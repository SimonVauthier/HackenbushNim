/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;

/**
 *
 * @author zielonka adapté par Simon
 *
 *
 * La classe contient deux méthodes pour rendre un Node déplaçable à l'aide de
 * la souris.
 *
 * La méthode static rend délaçable un seul Node.
 *
 * La méthode non static permet de déplacer un groupe de Nodes.
 */
public class MakeDraggable {

    /* la méthode static qui permet rendre <<draggable>> un Node qui
    *  n'appartient à aucun groupe. Cette méthode n'est pas utilisée
    *  dans le programme.
     */
    public static void makeDraggable(Circle circle, Pane dessin, Line sol, boolean first, boolean hackenbush, List<Circle> l) {
        class T {

            double initialTranslateX, initialTranslateY,
                    anchorX, anchorY;
        }

        final T t = new T();

        if (circle == null) {
            System.err.println("makeDraggable node == null");
        }
        circle.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {

            /* trouver et memoriser la translation initiale de Node */
            t.initialTranslateX = circle.getCenterX();
            t.initialTranslateY = circle.getCenterY();
            /* trouver la position initial de la souris dans les coordonnees 
                 * du parent de Node
                 * et les memoriser dans t.anchor
             */
            
            Point2D point = circle.localToParent(event.getX(), event.getY());
            t.anchorX = point.getX();
            t.anchorY = point.getY();
            //event.consume();
        });

        circle.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if(!l.contains(circle)){
            /* trouver la position de la souris dans les coordonnees du parent
                 * effectuer le deplacement de Node pour suivre les mouvement 
                 * de la souris
             */
            Point2D point = circle.localToParent(event.getX(), event.getY());
            //circle.setCenterX(t.initialTranslateX - t.anchorX + point.getX());
            //circle.setCenterY(t.initialTranslateY - t.anchorY + point.getY());
            /*
            node.setTranslateX(t.initialTranslateX - t.anchorX + event.getX());
            node.setTranslateY(t.initialTranslateY - t.anchorY + event.getY());
             */
            if(!hackenbush){
                if(!first){
                    if(point.getY() + (t.initialTranslateY - t.anchorY) > sol.getEndY() - 2*circle.getRadius() || point.getY() + (t.initialTranslateY - t.anchorY) < dessin.getBorder().getInsets().getTop() + circle.getRadius()){
                        if(point.getY() + (t.initialTranslateY - t.anchorY) > sol.getEndY() - 2*circle.getRadius()){
                            circle.setCenterY(sol.getEndY() - 2*circle.getRadius());
                        }else{
                            circle.setCenterY(dessin.getBorder().getInsets().getTop() + circle.getRadius());
                        }
                    }else{
                        circle.setCenterY(t.initialTranslateY - t.anchorY + point.getY());
                    }
                }
            }else{
                if(point.getY() + (t.initialTranslateY - t.anchorY) > sol.getEndY() - circle.getRadius() || point.getY() + (t.initialTranslateY - t.anchorY) < dessin.getBorder().getInsets().getTop() + circle.getRadius()){
                    if(point.getY() + (t.initialTranslateY - t.anchorY) > sol.getEndY() - circle.getRadius()){
                        circle.setCenterY(sol.getEndY() - circle.getRadius());
                    }else{
                        circle.setCenterY(dessin.getBorder().getInsets().getTop() + circle.getRadius());
                    }
                }else{
                    circle.setCenterY(t.initialTranslateY - t.anchorY + point.getY());
                }
            }
            if(point.getX() + (t.initialTranslateX - t.anchorX) < dessin.getBorder().getInsets().getLeft() + circle.getRadius() || point.getX() + (t.initialTranslateX - t.anchorX) > dessin.getWidth() - dessin.getBorder().getInsets().getRight() - circle.getRadius()){
                if(point.getX() +( t.initialTranslateX - t.anchorX) < dessin.getBorder().getInsets().getLeft() + circle.getRadius()){
                    circle.setCenterX(dessin.getBorder().getInsets().getLeft() + circle.getRadius());
                }else{
                    circle.setCenterX(dessin.getWidth() - dessin.getBorder().getInsets().getRight() - circle.getRadius());
                }
            }else{
                circle.setCenterX(t.initialTranslateX - t.anchorX + point.getX());
            }
            }
                     //event.consume();
        });

        circle.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            //event.consume();
        });
    }

    /* 
    La méthode static qui rend un Node déplaçable avec tous les Nodes
    du groupe auquel il appartient.
     */
    public static void makeDraggableGroup(Group g, Pane p, double radius, Line sol) {

        class T {

            /*  initTranslate sert  pour mémoriser la valeur des propriétés
              translateX, translateY pour chaque Node qui appartient 
              au même groupe que le Node node qui est le paramètre de la méthode
              makeDraggableWithGroup().
            
              Les map est composé de couples (Node, Point2D) où les clés
              Node sont tous les Node qui ont le même parent que le node en
              paramètre de makeDraggableWithGroup().
            */
            
            Map<Node, Point2D> initTranslate;

            /*position initiale de la souris*/
            Point2D anchor;
            
            /*position maximale des cercles, afin de ne pas sortir du bord */
            double maxX;
            double maxY;
            double minX;
            double minY;
        }

        final T t = new T();
        t.initTranslate = new HashMap<>();

        /* l'évènement MouseEventMOUSE_PRESSED initialise T. */
        g.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            t.maxX = 0.0;
            t.maxY = 0.0;
            t.minX = p.getWidth();
            t.minY = p.getHeight();
            
            
            /* faire le parcours sur tous les Nodes appartenant au groupe 
            que node en paramètre de la méthode makeDraggableWithGroup.*/
            for (Node n : p.getChildren()) {
                if(n instanceof Circle){
                    for(Node e : g.getChildren()){
                        if(e instanceof Line){
                            if((((Circle) n).getCenterX() == ((Line) e).getStartX() && ((Circle) n).getCenterY() == ((Line) e).getStartY()) || (((Circle) n).getCenterX() == ((Line) e).getEndX() && ((Circle) n).getCenterY() == ((Line) e).getEndY())){
                                t.initTranslate.put(n, new Point2D(((Circle) n).getCenterX(), ((Circle) n).getCenterY()));
                                if(((Circle) n).getCenterX() < t.minX){
                                    t.minX = ((Circle) n).getCenterX();
                                }else{
                                    if(((Circle) n).getCenterX() > t.maxX){
                                        t.maxX = ((Circle) n).getCenterX();
                                    }
                                }
                                if(((Circle) n).getCenterY() < t.minY){
                                    t.minY = ((Circle) n).getCenterY();
                                }else{
                                    if(((Circle) n).getCenterY() > t.maxY){
                                        t.maxY = ((Circle) n).getCenterY();
                                    }
                                }
                            }
                        }else{
                            if(e instanceof CubicCurve){
                                if((((Circle) n).getCenterX() == ((CubicCurve) e).getStartX() && ((Circle) n).getCenterY() == ((CubicCurve) e).getStartY()) || (((Circle) n).getCenterX() == ((CubicCurve) e).getEndX() && ((Circle) n).getCenterY() == ((CubicCurve) e).getEndY())){
                                    t.initTranslate.put(n, new Point2D(((Circle) n).getCenterX(), ((Circle) n).getCenterY()));
                                    if(((Circle) n).getCenterX() < t.minX){
                                        t.minX = ((Circle) n).getCenterX();
                                    }else{
                                        if(((Circle) n).getCenterX() > t.maxX){
                                            t.maxX = ((Circle) n).getCenterX();
                                        }
                                    }
                                    if(((Circle) n).getCenterY() < t.minY){
                                        t.minY = ((Circle) n).getCenterY();
                                    }else{
                                        if(((Circle) n).getCenterY() > t.maxY){
                                            t.maxY = ((Circle) n).getCenterY();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            /* trouver la position initial de la souris dans les coordonnees 
                 * du parent de node
                 * et les memoriser dans t.anchor
             */
            t.anchor = g.localToParent(event.getX(), event.getY());

            //event.consume();
        });
        
        /* L'évènement MouseEvent.MOUSE_DRAGGED provoque le déplacement de tous les
           Nodes appartenant au Group  node.getParent() où node est le Node 
           qui reçoit cet évènement. 
        */
        
        
        g.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            /* trouver la position de la souris dans les coordonnees du parent
                 * effectuer le deplacement de Node pour suivre les mouvement 
                 * de la souris
             */
            Point2D point = g.localToParent(event.getX(), event.getY());

            /* déplacer tous les points qui sont reliés aux éléments
            * du groupe.
            */
            
            
            if((t.maxX - t.anchor.getX() + point.getX() > p.getWidth() - p.getBorder().getInsets().getRight() - radius)
             || (t.minX - t.anchor.getX() + point.getX() < p.getBorder().getInsets().getLeft() + radius)){
                /*if(t.maxX - t.anchor.getX() + point.getX() > p.getWidth() + p.getBorder().getInsets().getRight() - radius){
                    for(Node n : p.getChildren()){
                        if(n instanceof Circle && t.initTranslate.containsKey(n)){
                            ((Circle) n).setCenterX(p.getWidth() + p.getBorder().getInsets().getRight() - radius);
                        }
                    }
                }else{
                    for(Node n : p.getChildren()){
                        if(n instanceof Circle && t.initTranslate.containsKey(n)){
                            ((Circle) n).setCenterX(p.getBorder().getInsets().getLeft() + radius);
                        }
                    }
                }*/
            }else{
                for(Node n : p.getChildren()){
                    if(n instanceof Circle && t.initTranslate.containsKey(n)){
                        ((Circle) n).setCenterX(t.initTranslate.get(n).getX() - t.anchor.getX() + point.getX());
                    }
                }
            }
            if((t.maxY - t.anchor.getY() + point.getY() > sol.getStartY() - radius)
             || (t.minY - t.anchor.getY() + point.getY() < p.getBorder().getInsets().getTop() + radius)){
                /*if(t.maxY - t.anchor.getY() + point.getY() > sol.getStartY() - radius){
                    for(Node n : p.getChildren()){
                        if(n instanceof Circle && t.initTranslate.containsKey(n)){
                            ((Circle) n).setCenterY(sol.getStartY() - radius);
                        }
                    }
                }else{
                    for(Node n : p.getChildren()){
                        if(n instanceof Circle && t.initTranslate.containsKey(n)){
                            ((Circle) n).setCenterY(p.getBorder().getInsets().getTop() + radius);
                        }
                    }
                }*/
            }else{
                for(Node n : p.getChildren()){
                    if(n instanceof Circle && t.initTranslate.containsKey(n)){
                        ((Circle) n).setCenterY(t.initTranslate.get(n).getY() - t.anchor.getY() + point.getY());
                    }
                }
            }
            

            //event.consume();
        });
        

        g.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            //event.consume();
        });
    }
}
