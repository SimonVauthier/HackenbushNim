/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetig;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author simon
 */
public class FXMLDocumentController implements Initializable {
    
    private static final double radius = 6;
    
    @FXML
    BorderPane edition;
    @FXML
    BorderPane menuPrincipal;
    @FXML
    BorderPane commentJouer;
    @FXML
    Pane dessin;
    @FXML
    Line sol;
    @FXML
    ToggleButton selection;
    @FXML
    ChoiceBox couleur;
    @FXML
    HBox mode;
    @FXML
    HBox type;
    @FXML
    ToolBar menuCreationHackenbush;
    @FXML
    ToggleGroup toggle;
    @FXML
    ToolBar menuCreationNim;
    @FXML
    HBox menuJeu;
    @FXML
    HBox ia;
    
    @FXML
    Slider slider;
    @FXML
    Slider slider2;
    
    @FXML
    ToggleButton point;
    @FXML
    ToggleButton droite;
    @FXML
    ToggleButton deplacer;
    @FXML
    ToggleButton boucle;
    
    @FXML
    Button start;
    @FXML
    Label tour;
    
    @FXML
    HBox speed;
    @FXML
    Slider sliderSpeed;
    
    private List<Circle> listCircle = new ArrayList<>();
    private List<Line> listLine = new ArrayList<>();
    private List<Group> listGroup = new ArrayList<>();
    private List<CubicCurve> listCub = new ArrayList<>();

    private List<Path> listNim = new ArrayList<>();
    private List<Circle> listPoint = new ArrayList<>();
    private boolean type_jeu;
    private List<List<Circle>> listPoints = new ArrayList<>();
    private int lastNbrTiges = 0;
    private int[] heap;// = new int[10]; /* /!\ ne fonctionne pas en remplaçant "10" par "(int) slider.getMax()" */
    
    private boolean gameStarted = false;
    private int coup = 0;
    
    
    /* modifie les éléments pour avoir un visuel si oui ou non ils sont selectionnés (dans une List) */
    void detect() {
        for(Node n : dessin.getChildren()){
            if(n instanceof Circle){
                if(listCircle.contains((Circle) n)){
                    ((Circle) n).setRadius(radius*2/3);
                }else{
                    ((Circle) n).setRadius(radius);
                }
            }
            if(n instanceof Line){
                if(listLine.contains((Line) n)){
                    n.setOpacity(0.5);
                }else{
                    n.setOpacity(1);
                }
            }
            if(n instanceof CubicCurve){
                if(listCub.contains((CubicCurve) n)){
                    n.setOpacity(0.5);
                }else{
                    n.setOpacity(1);
                }
            }
        }
    }
    
    
    //-----------------------------------------------------------
    //
    //                  Fonctions d'affichage
    //
    //-----------------------------------------------------------
    @FXML
    void retourMenu(){
        if(edition.isVisible() && !mode.isVisible() && dessin.getChildren().size() != 1){
            Alert alerte = new Alert(AlertType.WARNING, "Cette action supprimera le dessin actuel. \nContinuer ?", ButtonType.YES, ButtonType.NO);
            alerte.showAndWait();
            if(alerte.getResult() == ButtonType.NO){
                return;
            }
        }
        edition.setVisible(false);
        menuPrincipal.setVisible(true);
        commentJouer.setVisible(false);
        resetEdition();
        tour.setText("ROUGE");
        tour.setTextFill(Color.RED);
        listCircle.clear();
        listLine.clear();
        listGroup.clear();
        listCub.clear();
        listPoint.clear();
        listPoints.clear();
        listNim.clear();
    }
    
    private void resetEdition(){
        dessin.getChildren().clear();
        dessin.getChildren().add(sol);
        mode.setVisible(false);
        type.setVisible(false);
        ia.setVisible(false);
        menuCreationHackenbush.setVisible(false);
        menuCreationHackenbush.setDisable(false);
        if(toggle.getSelectedToggle() != null){
            toggle.getSelectedToggle().setSelected(false);
        }
        couleur.getSelectionModel().selectFirst();
        menuCreationNim.setVisible(false);
        menuCreationNim.setDisable(false);
        slider.setValue(1);
        slider2.setValue(slider2.getMax());
        menuJeu.setVisible(false);
        speed.setVisible(false);
        coup = 0;
        gameStarted = false;
    }
    
    @FXML
    void quitter(){
        Platform.exit();
    }
    
    @FXML
    void buttonEdition(){
        edition.setVisible(true);
        menuPrincipal.setVisible(false);
        commentJouer.setVisible(false);
        mode.setVisible(true);
    }
    
    @FXML
    void buttonCommentJouer(){
        edition.setVisible(false);
        menuPrincipal.setVisible(false);
        commentJouer.setVisible(true);
    }
    
    @FXML
    void start(){
        type.setVisible(true);
        gameStarted = true;
        start.setDisable(true);
        if(menuCreationNim.isVisible()){
            menuCreationNim.setDisable(true);
        }
        if(menuCreationHackenbush.isVisible()){
            menuCreationHackenbush.setDisable(true);
        }
    }
    
    @FXML
    void startH(){
        start();
        matrice();
        if(toggle.getSelectedToggle() != null){
            toggle.getSelectedToggle().setSelected(false);
        }
    }
    
    @FXML
    void HNormal(){
        startH();
        type.setVisible(false);
        type_jeu = true;
        if(!starter.isEmpty()){
            menuCreationHackenbush.setVisible(false);
            menuJeu.setVisible(true);
        }
    }
    @FXML
    void HMisere(){
        startH();
        type.setVisible(false);
        type_jeu = false;
        if(!starter.isEmpty()){
            menuCreationHackenbush.setVisible(false);
            menuJeu.setVisible(true);
        }
    }
    
    @FXML
    void NN2IA(){
        start();
        typeNormal();
        iavia();
    }
    @FXML
    void NNPIA(){
        start();
        typeNormal();
        pvia();
    }
    @FXML
    void NN2P(){
        start();
        typeNormal();
        pvp();
    }
    @FXML
    void NM2IA(){
        start();
        typeMisere();
        iavia();
    }
    @FXML
    void NMPIA(){
        start();
        typeMisere();
        pvia();
    }
    @FXML
    void NM2P(){
        start();
        typeMisere();
        pvp();
    }
    
    
    @FXML
    void playHackenbush(){
        retourMenu();
        buttonEdition();
        mode.setVisible(false);
        menuCreationHackenbush.setVisible(true);
    }
    
    @FXML
    void playNim(){
        retourMenu();
        buttonEdition();
        mode.setVisible(false);
        menuCreationNim.setVisible(true);
    }
    
    @FXML
    void reset(){
        dessin.getChildren().clear();
        dessin.getChildren().add(sol);
        if(toggle.getSelectedToggle() != null){
            toggle.getSelectedToggle().setSelected(false);
        }
    }
    
    void affichageType(){
        menuCreationHackenbush.setVisible(false);
        type.setVisible(false);
        if(menuCreationNim.isVisible()){
            ia.setVisible(true);
        }else{
            menuJeu.setVisible(true);
        }
    }
    @FXML
    void typeMisere(){
        affichageType();
        type_jeu = false;
    }
    @FXML
    void typeNormal(){
        affichageType();
        type_jeu = true;
    }
    void lancerPartieNim(){
        ia.setVisible(false);
        menuCreationNim.setVisible(false);
    }
       
    @FXML
    void iavia(){
        speed.setVisible(true);
        lancerPartieNim();
        pvsia = false;
    }
    
    @FXML
    void pvia(){
        lancerPartieNim();
        int n = (int) slider.getValue();
        affichageApresIA(n);
        pvsia = true;
    }
    @FXML
    void pvp(){
        pvsia = false;
        lancerPartieNim();
        int n = (int) slider.getValue();
        menuJeu.setVisible(true);
        affichageApresIA(n);
    }
    
    private boolean pvsia;
    
    //----------------------------------------------------
    //
    //                  Affichage fichier d'aide
    //
    //----------------------------------------------------
    
    @FXML
    void aideHackenbush(){
        StringBuilder builder = new StringBuilder();
        InputStream stream = getClass().getResourceAsStream("aideHackenbush.txt");
        Scanner scanner = new Scanner(stream);
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
            builder.append(System.lineSeparator());
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().setPrefSize(800,700);
        alert.setResizable(true);
        alert.setContentText(builder.toString());
        
        alert.showAndWait();
    }
    @FXML
    void aideNim(){
        StringBuilder builder = new StringBuilder();
        InputStream stream = getClass().getResourceAsStream("aideNim.txt");
        Scanner scanner = new Scanner(stream);
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
            builder.append(System.lineSeparator());
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().setPrefSize(800,200);
        alert.setResizable(true);
        alert.setContentText(builder.toString());
        
        alert.showAndWait();
    }
    @FXML
    void aPropos(){
        StringBuilder builder = new StringBuilder();
        InputStream stream = getClass().getResourceAsStream("aPropos.txt");
        Scanner scanner = new Scanner(stream);
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
            builder.append(System.lineSeparator());
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().setPrefSize(600,300);
        alert.setResizable(true);
        alert.setContentText(builder.toString());
        
        alert.showAndWait();
    }
    
    //-------------------------------------------------------------------
    //
    //                      Hackenbush
    //
    //-------------------------------------------------------------------
    private Circle createPoint(MouseEvent e) {
        Circle c = new Circle();
        /* on créer uniquement un Point2D, qui ne sort pas du cadre du dessin */
        if(e.getY() > sol.getEndY() - radius || e.getY() < dessin.getBorder().getInsets().getTop() + radius){
            if(e.getY() > sol.getEndY() - radius){
                c.setCenterY(sol.getEndY() - radius);
            }else{
                c.setCenterY(dessin.getBorder().getInsets().getTop() + radius);
            }
        }else{
            c.setCenterY(e.getY());
        }
        if(e.getX() < dessin.getBorder().getInsets().getLeft() + radius || e.getX() > dessin.getWidth() - dessin.getBorder().getInsets().getRight() - radius){
            if(e.getX() < dessin.getBorder().getInsets().getLeft() + radius){
                c.setCenterX(dessin.getBorder().getInsets().getLeft() + radius);
            }else{
                c.setCenterX(dessin.getWidth() - dessin.getBorder().getInsets().getRight() - radius);
            }
        }else{
            c.setCenterX(e.getX());
        }
        return c;
    }
    
    /* déclaration des évènements possible pour une Line */
    private void mouseEventArete(Line n){
        n.setOnMouseClicked((MouseEvent z) -> {
            if(!gameStarted){
                if(!selection.isSelected()){
                    String s = couleur.getSelectionModel().selectedItemProperty().get().toString();
                    switch(s){
                        case "Vert" :
                            n.setStroke(Color.GREEN);
                            break;
                        case "Rouge" :
                            n.setStroke(Color.RED);
                            break;
                        case "Bleu" :
                            n.setStroke(Color.BLUE);
                            break;
                        default:
                            break;
                    }
                }else{
                    if(listLine.contains(n)){
                        listLine.remove(n);
                        n.setOpacity(1);
                    }else{
                        listLine.add(n);
                        n.setOpacity(0.5);
                    }
                }
            }else{
                if(coup%2 + 1 == 1){
                    if(n.getStroke() == Color.BLUE){
                        Alert a = new Alert(AlertType.WARNING, "Vous ne pouvez pas prendre une arète bleue.", ButtonType.OK);
                        a.showAndWait();
                    }else{
                        tour.setText("BLEU");
                        tour.setTextFill(Color.BLUE);
                        coup++;
                        dessin.getChildren().remove(n);
                    }
                }else{
                    if(n.getStroke() == Color.RED){
                        Alert a = new Alert(AlertType.WARNING, "Vous ne pouvez pas prendre une arète rouge.", ButtonType.OK);
                        a.showAndWait();
                    }else{
                        tour.setText("ROUGE");
                        tour.setTextFill(Color.RED);
                        coup++;
                        dessin.getChildren().remove(n);
                    }
                }
            }
        });
        n.setOnMouseEntered((MouseEvent z) -> {
            n.setStrokeWidth(n.getStrokeWidth()*3/2);
        });
        n.setOnMouseExited((MouseEvent z) -> {
            n.setStrokeWidth(n.getStrokeWidth()*2/3);
        });
    }
    /* même chose pour les boucles (CubicCurve) */
    private void mouseEventArete(CubicCurve n){
        n.setOnMouseClicked((MouseEvent z) -> {
            if(!gameStarted){
                if(!selection.isSelected()){
                    String s = couleur.getSelectionModel().selectedItemProperty().get().toString();
                    switch(s){
                        case "Vert" :
                            n.setStroke(Color.GREEN);
                            break;
                        case "Rouge" :
                            n.setStroke(Color.RED);
                            break;
                        case "Bleu" :
                            n.setStroke(Color.BLUE);
                            break;
                        default:
                            break;
                    }
                }else{
                    if(listCub.contains(n)){
                        listCub.remove(n);
                        n.setOpacity(1);
                    }else{
                        listCub.add(n);
                        n.setOpacity(0.5);
                    }
                }
            }else{
                if(coup%2 + 1 == 1){
                    if(n.getStroke() == Color.BLUE){
                        Alert a = new Alert(AlertType.WARNING, "Vous ne pouvez pas prendre une arète bleue.", ButtonType.OK);
                        a.showAndWait();
                    }else{
                        tour.setText("BLEU");
                        tour.setTextFill(Color.BLUE);
                        coup++;
                        dessin.getChildren().remove(n);
                    }
                }else{
                    if(n.getStroke() == Color.RED){
                        Alert a = new Alert(AlertType.WARNING, "Vous ne pouvez pas prendre une arète rouge.", ButtonType.OK);
                        a.showAndWait();
                    }else{
                        tour.setText("ROUGE");
                        tour.setTextFill(Color.RED);
                        coup++;
                        dessin.getChildren().remove(n);
                    }
                }
            }
        });
        n.setOnMouseEntered((MouseEvent z) -> {
            n.setStrokeWidth(n.getStrokeWidth()*3/2);
        });
        n.setOnMouseExited((MouseEvent z) -> {
            n.setStrokeWidth(n.getStrokeWidth()*2/3);
        });
        n.setOnMouseDragged((MouseEvent z) -> {
            /* en fonction de l'emplacement de la souris, on oriente le noeud plutot que de le déplacer */
            if(Math.abs(z.getX() - n.getStartX()) > Math.abs(z.getY() - n.getStartY())){
                if(z.getX() > n.getStartX()){
                    n.controlX1Property().bind(n.startXProperty().add(10*radius));
                    n.controlY1Property().bind(n.startYProperty().add(10*radius));
                    n.controlX2Property().bind(n.startXProperty().add(10*radius));
                    n.controlY2Property().bind(n.startYProperty().subtract(10*radius));
                }else{
                    n.controlX1Property().bind(n.startXProperty().subtract(10*radius));
                    n.controlY1Property().bind(n.startYProperty().add(10*radius));
                    n.controlX2Property().bind(n.startXProperty().subtract(10*radius));
                    n.controlY2Property().bind(n.startYProperty().subtract(10*radius));
                }
            }else{
                if(z.getY() > n.getStartY()){
                    n.controlX1Property().bind(n.startXProperty().add(10*radius));
                    n.controlY1Property().bind(n.startYProperty().add(10*radius));
                    n.controlX2Property().bind(n.startXProperty().subtract(10*radius));
                    n.controlY2Property().bind(n.startYProperty().add(10*radius));
                }else{
                    n.controlX1Property().bind(n.startXProperty().add(10*radius));
                    n.controlY1Property().bind(n.startYProperty().subtract(10*radius));
                    n.controlX2Property().bind(n.startXProperty().subtract(10*radius));
                    n.controlY2Property().bind(n.startYProperty().subtract(10*radius));
                }
            }
        });
    }
    
    /* même chose pour les groupes */
    private void mouseEventArete(Group n){
        n.setOnMouseEntered((MouseEvent z) -> {
            for(Node e : n.getChildren()){
                if(e instanceof Line){
                    ((Line) e).setStrokeWidth(((Line) e).getStrokeWidth()*3/2);
                }else{
                    if(e instanceof CubicCurve){
                        ((CubicCurve) e).setStrokeWidth(((CubicCurve) e).getStrokeWidth()*3/2);
                    }
                }
            }
        });
        n.setOnMouseExited((MouseEvent z) -> {
            for(Node e : n.getChildren()){
                if(e instanceof Line){
                    ((Line) e).setStrokeWidth(((Line) e).getStrokeWidth()*2/3);
                }else{
                    if(e instanceof CubicCurve){
                        ((CubicCurve) e).setStrokeWidth(((CubicCurve) e).getStrokeWidth()*2/3);
                    }
                }
            }
        });
        n.setOnMouseClicked((MouseEvent z) -> {
            if(selection.isSelected()){
                if(listGroup.contains(n)){
                    listGroup.remove(n);
                    for(Node e : n.getChildren()){
                        e.setOpacity(1);
                    }
                }else{
                    listGroup.add(n);
                    for(Node e : n.getChildren()){
                        e.setOpacity(0.5);
                    }
                }
            }
        });
    }
    
    /* destruction des évènements possibles */
    private void removeMouseEventArete(Line n){
        n.setOnMouseClicked(null);
        n.setOnMouseEntered(null);
        n.setOnMouseExited(null);
    }
    private void removeMouseEventArete(CubicCurve n){
        n.setOnMouseClicked(null);
        n.setOnMouseEntered(null);
        n.setOnMouseExited(null);
        n.setOnMouseDragged(null);
    }
    private void removeMouseEventArete(Group n){
        n.setOnMouseClicked(null);
        n.setOnMouseEntered(null);
        n.setOnMouseExited(null);
        n.setOnMouseDragged(null);
    }
    
    /* évènements principaux d'un point */
    private void mouseEventPoint(Circle c){
        MakeDraggable.makeDraggable(c, dessin, sol, false, true, listCircle);
        c.setOnMouseClicked((MouseEvent z) -> {
            if(droite.isSelected()){
                if(listCircle.contains(c)){
                    listCircle.remove(c);
                    c.setRadius(radius);
                }else{
                    listCircle.add(c);
                    c.setRadius(radius*2/3);
                }
            }else{
                if(boucle.isSelected()){
                    if(listCircle.size() >= 1){
                        for(int i = 0 ; i < listCircle.size() ; i++){
                            listCircle.get(i).setRadius(1);
                        }
                        listCircle.clear();
                    }
                    listCircle.add(c);
                    boucle();
                    listCircle.remove(c);
                }
            }
        });
    }
    
    private EventHandler<MouseEvent> handlerPoint = (MouseEvent e) -> {
        Circle c = createPoint(e);
        c.setFill(Color.WHITE);
        c.setStroke(Color.BLACK);
        c.setStrokeWidth(radius/3);
        c.setRadius(radius);
        mouseEventPoint(c);
        dessin.getChildren().add(c);
    };
    
    private EventHandler<MouseEvent> handlerDroite = (MouseEvent e) -> {
        Line d = new Line();
        if(listCircle.size() > 2){
            listCircle.clear();
            detect();
        }
        if(listCircle.size() == 2){
            d.startXProperty().bindBidirectional(listCircle.get(0).centerXProperty());
            d.startYProperty().bindBidirectional(listCircle.get(0).centerYProperty());
            d.endXProperty().bindBidirectional(listCircle.get(1).centerXProperty());
            d.endYProperty().bindBidirectional(listCircle.get(1).centerYProperty());
            listCircle.clear();
            detect();
            d.setStrokeWidth(radius*2/3);
            dessin.getChildren().add(d);
        }
        String c = couleur.getSelectionModel().selectedItemProperty().get().toString();
        switch(c){
            case "Vert" :
                d.setStroke(Color.GREEN);
                break;
            case "Rouge" :
                d.setStroke(Color.RED);
                break;
            case "Bleu" :
                d.setStroke(Color.BLUE);
                break;
            default:
                break;
        }
        mouseEventArete(d);
    };
    
    /* créé une boucle, c'est à dire une arete reliant un sommet à lui même */
    void boucle(){
        CubicCurve c = new CubicCurve();
        c.startXProperty().bind(listCircle.get(0).centerXProperty());
        c.startYProperty().bind(listCircle.get(0).centerYProperty());
        c.endXProperty().bind(c.startXProperty());
        c.endYProperty().bind(c.startYProperty());
        c.controlX1Property().bind(c.startXProperty().add(10*radius));
        c.controlY1Property().bind(c.startYProperty().add(10*radius));
        c.controlX2Property().bind(c.startXProperty().add(10*radius));
        c.controlY2Property().bind(c.startYProperty().subtract(10*radius));
        c.setStrokeWidth(radius*2/3);
        c.setFill(Color.WHITE);
        String d = couleur.getSelectionModel().selectedItemProperty().get().toString();
        switch(d){
            case "Vert" :
                c.setStroke(Color.GREEN);
                break;
            case "Rouge" :
                c.setStroke(Color.RED);
                break;
            case "Bleu" :
                c.setStroke(Color.BLUE);
                break;
            default:
                break;
        }
        mouseEventArete(c);
        dessin.getChildren().add(c);
    }
    
    @FXML
    void group() {
        /* on veut grouper plusieurs éléments, qu'ils soient 
        * de type Line, CubicCurve ou Group
        */
        if(listLine.size() + listCub.size() + listGroup.size() > 1){
            Group g = new Group();
            /* s'il y a déjà un groupe, on ajoute au nouveau groupe les éléments contenus
            * dans les anciens groupes
            */
            for(int i = 0; i < listGroup.size(); i++){
                for(int j = 0; j < listGroup.get(i).getChildren().size(); j++){
                    if(listGroup.get(i).getChildren().get(j) instanceof Line){
                        listLine.add((Line) listGroup.get(i).getChildren().get(j));
                    }else{
                        if(listGroup.get(i).getChildren().get(j) instanceof CubicCurve){
                            listCub.add((CubicCurve) listGroup.get(i).getChildren().get(j));
                        }
                    }
                }
            }
            /* on ajoute au groupe chacun des éléments "individuels" */
            for(int i = 0; i < listLine.size(); i++){
                g.getChildren().add(listLine.get(i));
                listLine.get(i).setOpacity(1);
                removeMouseEventArete(listLine.get(i));
            }
            for(int i = 0; i < listCub.size(); i++){
                g.getChildren().add(listCub.get(i));
                listCub.get(i).setOpacity(1);
                removeMouseEventArete(listCub.get(i));
            }
            /* on retire tous les éléments du groupe du dessin */
            dessin.getChildren().removeAll(listLine);
            listLine.clear();
            dessin.getChildren().removeAll(listCub);
            listCub.clear();
            /* on retire également tous les groupes */
            for(int i = 0 ; i < listGroup.size() ; i++){
                for(int j = 0 ; j < listGroup.get(i).getChildren().size() ; j++){
                    dessin.getChildren().remove(listGroup.get(i).getChildren().get(j));
                }
            }
            listGroup.clear();
            mouseEventArete(g);
            MakeDraggable.makeDraggableGroup(g, dessin, radius, sol);
            /* puis on ajoute le groupe complet au dessin */
            dessin.getChildren().add(g);
        }else{
            System.out.println("Sélectionnez plusieurs éléments");
        }
    };
    
    @FXML
    void degroup(){
        for(int i = 0; i < listGroup.size(); i++){
            removeMouseEventArete(listGroup.get(i));
            /* on prend chaque élément du groupe et on l'ajoute à une list
            * d'éléments qui deviendront indépendants
            */
            for(Node n : listGroup.get(i).getChildren()){
                n.setOpacity(1);
                if(n instanceof Line){
                    /* on ajoute les évènements classiques pour les Line */
                    mouseEventArete((Line) n);
                    listLine.add((Line) n);
                }else{
                    if(n instanceof CubicCurve){
                        /* on ajoute les évènements classiques pour les CubicCurve */
                        mouseEventArete((CubicCurve) n);
                        listCub.add((CubicCurve) n);
                    }
                }
            }
            /* on ajoute chaque élément des listes au dessin */
            for(int j = 0 ; j < listLine.size() ; j++){
                dessin.getChildren().add(listLine.get(j));
            }
            listLine.clear();
            for(int j = 0 ; j < listCub.size() ; j++){
                dessin.getChildren().add(listCub.get(j));
            }
            listCub.clear();
            /* on retire le groupe du dessin ainsi que chaque élément le constituant */
            dessin.getChildren().remove(listGroup.get(i));
            listGroup.get(i).getChildren().clear();
        }
        
    }
    
    @FXML
    void dupliquer(){
        if(listGroup.size() != 1){
            System.out.println("Selectionnez un seul groupe");
            return;
        }
        /* on retire les éléments de la liste et on remet
        * son visuel d'élément non selectionné.
        */
        for(int i = 0 ; i < listLine.size() ; i++){
            listLine.get(i).setOpacity(1);
        }
        listLine.clear();
        for(int i = 0 ; i < listCub.size() ; i++){
            listCub.get(i).setOpacity(1);
        }
        listCub.clear();
        
        /* on commence par dupliquer tous les points nécessaire*/
        List<Circle> newPoint = new ArrayList<>();
        for(Node n : listGroup.get(0).getChildren()){
            if(n instanceof Line){
                Line m = (Line) n;
                Circle c1 = new Circle(m.getStartX(), m.getStartY(), radius);
                Circle c2 = new Circle(m.getEndX(), m.getEndY(), radius);
                if(!newPoint.contains(c1)){
                    /* on ajoute le point uniquement s'il n'a pas déjà été ajouté */
                    newPoint.add(c1);
                    mouseEventPoint(c1);
                    c1.setFill(Color.WHITE);
                    c1.setStroke(Color.BLACK);
                    c1.setStrokeWidth(radius/3);
                }
                if(!newPoint.contains(c2)){
                    /* on ajoute le point uniquement s'il n'a pas déjà été ajouté */
                    newPoint.add(c2);
                    mouseEventPoint(c2);
                    c2.setFill(Color.WHITE);
                    c2.setStroke(Color.BLACK);
                    c2.setStrokeWidth(radius/3);
                }
            }else{
                if(n instanceof CubicCurve){
                    CubicCurve m = (CubicCurve) n;
                    Circle c = new Circle(m.getStartX(), m.getStartY(), radius);
                    if(!newPoint.contains(c)){
                        /* on ajoute le point uniquement s'il n'a pas déjà été ajouté */
                        newPoint.add(c);
                        mouseEventPoint(c);
                        c.setFill(Color.WHITE);
                        c.setStroke(Color.BLACK);
                        c.setStrokeWidth(radius/3);
                    }
                }
            }
        }
        /* on ajoute tous les points nécessaires à la copie au dessin */
        for(int i = 0 ; i < newPoint.size() ; i++){
            dessin.getChildren().add(newPoint.get(i));
        }
        /* à partir des points créés, on détecte si un ligne relie deux d'entre eux, et on bind 
        * chaque ligne pour qu'elle commence et termine toujours sur un point
        */
        for(Node n : listGroup.get(0).getChildren()){
            if(n instanceof Line){
                Line m = new Line();
                for(int i = 0 ; i < newPoint.size() ; i++){
                    if(newPoint.get(i).getCenterX() == ((Line) n).getStartX() && newPoint.get(i).getCenterY() == ((Line) n).getStartY()){
                        m.startXProperty().bind(newPoint.get(i).centerXProperty());
                        m.startYProperty().bind(newPoint.get(i).centerYProperty());
                    }else{
                        if(newPoint.get(i).getCenterX() == ((Line) n).getEndX() && newPoint.get(i).getCenterY() == ((Line) n).getEndY()){
                            m.endXProperty().bind(newPoint.get(i).centerXProperty());
                            m.endYProperty().bind(newPoint.get(i).centerYProperty());
                        }
                    }
                }
                m.setStrokeWidth(radius*2/3);
                m.setFill(((Line) n).getFill());
                m.setStroke(((Line) n).getStroke());
                mouseEventArete(m);
                dessin.getChildren().add(m);
                listLine.add(m);
            }else{
                if(n instanceof CubicCurve){
                    CubicCurve m = new CubicCurve();
                    for(int i = 0 ; i < newPoint.size() ; i++){
                        if(newPoint.get(i).getCenterX() == ((CubicCurve) n).getStartX() && newPoint.get(i).getCenterY() == ((CubicCurve) n).getStartY()){
                            m.startXProperty().bind(newPoint.get(i).centerXProperty());
                            m.startYProperty().bind(newPoint.get(i).centerYProperty());
                            m.endXProperty().bind(newPoint.get(i).centerXProperty());
                            m.endYProperty().bind(newPoint.get(i).centerYProperty());
                            
                            /* problème : ne recopie pas l'orientation de la boucle... */
                            m.controlX1Property().bind(m.startXProperty().add(10*radius));
                            m.controlY1Property().bind(m.startYProperty().add(10*radius));
                            m.controlX2Property().bind(m.startXProperty().add(10*radius));
                            m.controlY2Property().bind(m.startYProperty().subtract(10*radius));
                            
                            m.setStrokeWidth(radius*2/3);
                            m.setFill(Color.WHITE);
                            m.setStroke(((CubicCurve) n).getStroke());
                        }
                    }
                    m.setFill(((CubicCurve) n).getFill());
                    mouseEventArete(m);
                    dessin.getChildren().add(m);
                    listCub.add(m);
                }
            }
        }
        /* tous les éléments du groupe original sont remis sous la forme
        * d'éléments non sélectionnés
        */
        for(int i = 0 ; i < listGroup.get(0).getChildren().size() ; i++){
            listGroup.get(0).getChildren().get(i).setOpacity(1);
        }
        listGroup.clear();
        
        /* on décale la copie afin de la distinguer de son origine */
        for(int i = 0 ; i < newPoint.size() ; i++){
            newPoint.get(i).setCenterX(newPoint.get(i).getCenterX() + 3*radius);
            newPoint.get(i).setCenterY(newPoint.get(i).getCenterY() + 3*radius);
        }
        
        /* on ne peut dupliquer qu'un groupe. Au fur et à mesure de la construction
        * de chaque nouvel élément, on l'a ajouté à la liste nécessaire pour grouper
        * les éléments. Ainsi, la copie d'un groupe restera un groupe
        */
        group();
    }
    
    
    //-----------------------------------------------------
    //
    //              Graphe (non implémenté)
    //
    //-----------------------------------------------------
    
    private List<Circle> listSommets = new ArrayList<>();
    private List<Line> listAretes = new ArrayList<>();
    private List<CubicCurve> listCubic = new ArrayList<>();
    private List<Circle> starter = new ArrayList<>();
    private Map<Circle, Integer> numeroPoint = new HashMap<>();
    private int[][] graph;
    private void matrice(){
        starter.clear();
        listCubic.clear();
        listAretes.clear();
        listSommets.clear();
        listGroup.clear();
        int nbr_sommets = 0;
        /* les groupes créés doivent être dégroupés pour retrouver des aretes indépendantes */
        for(Node n : dessin.getChildren()){
            if(n instanceof Group){
                listGroup.add((Group) n);
            }
        }
        degroup();
        
        /* on stocke chaque élément sur le dessin */
        for(Node n : dessin.getChildren()){
            if(n instanceof Circle){
                numeroPoint.put((Circle) n, nbr_sommets);
                nbr_sommets++;
                listSommets.add((Circle) n);
                if(((Circle) n).getCenterY() == sol.getEndY() - radius){
                    starter.add((Circle) n);
                }
            }else{
                if(n instanceof CubicCurve){
                    if(!((Line) n).getId().equals(sol.getId())){
                        listCubic.add((CubicCurve) n);
                    }
                }else{
                    if(n instanceof Line){
                        listAretes.add((Line) n);
                    }
                }
            }
        }
        /* on vérifie qu'il y a au moins un point au sol. S'il n'y en a pas, 
        * on met un message d'erreur.
        */
        if(starter.isEmpty()){
            Alert a = new Alert(AlertType.WARNING, "Aucun sommet ne touche le sol.", ButtonType.OK);
            a.showAndWait();
            type.setVisible(false);
            gameStarted = false;
            menuCreationHackenbush.setDisable(false);
            menuCreationHackenbush.setVisible(true);
            menuJeu.setVisible(false);
            return;
        }
        
        /* tous les points le long du sol sont reliés deux à deux */
        for(int i = 0 ; i < starter.size()-1 ; i++){
            for(int j = i+1 ; j < starter.size() ; j++){
                Line l = new Line();
                l.setStartX(starter.get(i).getCenterX());
                l.setStartY(starter.get(i).getCenterY());
                l.setEndX(starter.get(j).getCenterX());
                l.setEndY(starter.get(j).getCenterY());
                listAretes.add(l);
            }
        }
        
        /* on construit une matrice carrée disant combien de sommets vont de i à j (la matrice est symétrique) */
        graph = new int[nbr_sommets][nbr_sommets];
        for(int i = 0 ; i < listSommets.size() ; i++){
            Circle c = new Circle(listSommets.get(i).getCenterX(), listSommets.get(i).getCenterY(), listSommets.get(i).getRadius());
            c.setFill(listSommets.get(i).getFill());
            c.setStroke(listSommets.get(i).getStroke());
            c.setStrokeWidth(listSommets.get(i).getStrokeWidth());
            dessin.getChildren().remove(listSommets.get(i));
            dessin.getChildren().add(c);
        }
        for(int i = 0 ; i < nbr_sommets ; i++){
            /* si l'élément est un CubicCurve, il est relié à lui même (boucle) */
            for(int k = 0 ; k < listCubic.size() ; k++){
                if(listCubic.get(k).getStartX() == listSommets.get(i).getCenterX() && listCubic.get(k).getStartY() == listSommets.get(i).getCenterY()){
                    graph[i][i]++;
                }
            }
            /* si l'élément est une Line, elle relie i et j
            * le graphe étant non orienté, la matrice est symétrique ie graph[i][j] = graph[j][i]
            */
            for(int j = i+1 ; j < nbr_sommets ; j++){
                for(int k = 0 ; k < listAretes.size() ; k++){
                    if((listAretes.get(k).getStartX() == listSommets.get(i).getCenterX() && listAretes.get(k).getStartY() == listSommets.get(i).getCenterY() 
                    && listAretes.get(k).getEndX() == listSommets.get(j).getCenterX() && listAretes.get(k).getEndY() == listSommets.get(j).getCenterY())
                  || (listAretes.get(k).getStartX() == listSommets.get(j).getCenterX() && listAretes.get(k).getStartY() == listSommets.get(j).getCenterY() 
                    && listAretes.get(k).getEndX() == listSommets.get(i).getCenterX() && listAretes.get(k).getEndY() == listSommets.get(i).getCenterY())){
                        graph[i][j]++;
                        graph[j][i]++;
                    }
                }
            }
        }
    }
    
    /* je ne sais pas comment résoudre / implémenter la connexité du graphe 
    * la puissance de la matrice semble être une solution mais non viable
    */
    
    
/* ---------------------------------------------------------------------------------------------------------------------- *\
 *                                                                                                                        *
 *                                                    NIM                                                                 *
 *                                                                                                                        *
 *                                                                                                                        *
\*------------------------------------------------------------------------------------------------------------------------*/
    
    private void nim() {
        start.setDisable(false);
        /* si le slider à changé d'unité, on recréé (new n) tiges */
        if(lastNbrTiges == 0 || lastNbrTiges != (int) slider.getValue()){
            listPoints.clear();
            dessin.getChildren().clear();
            dessin.getChildren().add(sol);
            lastNbrTiges = (int) slider.getValue();
        }else{
            /* sinon on ne fait rien */
            return;
        }
        /* on créé le tableau pour pouvoir jouer au jeu de Nim */
        heap = new int[(int) slider.getValue()];
        
        /* calculs pour avoir des tiges occupant tout l'espace (découpage en grille) */
        double L_dessin = sol.getEndY() - radius - (dessin.getBorder().getInsets().getTop() + radius);
        double l_dessin = dessin.getWidth() - (dessin.getBorder().getInsets().getRight() + radius) - (dessin.getBorder().getInsets().getLeft() + radius);
        slider2.setDisable(true);
        slider2.setValue(slider2.getMax());
        double taille_colonne = l_dessin/lastNbrTiges;
        double taille_ligne = L_dessin/(slider2.getMax()+1);
        for(int i = 0 ; i < slider.getValue() ; i++){
            Path p = new Path();
            p.setId("path"+i);
            p.setStroke(Color.GREEN);
            p.setStrokeWidth(radius*2/3);
            p.setOnMouseEntered((MouseEvent z) -> {
                p.setStrokeWidth(p.getStrokeWidth()*3/2);
            });
            p.setOnMouseExited((MouseEvent z) -> {
                p.setStrokeWidth(p.getStrokeWidth()*2/3);
            });
            p.setOnMouseClicked((MouseEvent z) -> {
                if(p.getStroke() == Color.GREEN){
                    p.setStroke(Color.GREENYELLOW);
                    if(listNim.size() == 1){
                        listNim.get(0).setStroke(Color.GREEN);
                    }
                    slider2.setDisable(false);
                    slider2.setValue(p.getElements().size());
                    listNim.clear();
                    listNim.add(p);
                }else{
                    p.setStroke(Color.GREEN);
                    listNim.remove(p);
                    slider2.setDisable(true);
                }
            });
            List<Circle> listPoint = new ArrayList<>();
            for(int j = 0 ; j <= slider2.getMax() ; j++){
                Circle c = new Circle();
                if(j == 0){
                    c.setCenterX((Math.random() + i)*taille_colonne + dessin.getBorder().getInsets().getLeft() + radius);
                    c.setCenterY(L_dessin - j*taille_ligne + dessin.getBorder().getInsets().getTop() + radius);
                }else{
                    c.setCenterX((Math.random() + i)*taille_colonne + dessin.getBorder().getInsets().getLeft() + radius);
                    c.setCenterY(L_dessin - (Math.random() + j)*taille_ligne + dessin.getBorder().getInsets().getTop() + radius);
                }
                c.setFill(Color.WHITE);
                c.setStroke(Color.BLACK);
                c.setStrokeWidth(radius/3);
                c.setRadius(radius);
                /* on rend le déplacement possible pour chaque noeud */
                if(j == 0){
                    /* on empêche le noeud collé au sol de se déplacer en y */
                    MakeDraggable.makeDraggable(c, dessin, sol, true, false, listCircle);
                }else{
                    /* on empêche le noeud de se coller au sol */
                    MakeDraggable.makeDraggable(c, dessin, sol, false, false, listCircle);
                }
                dessin.getChildren().add(c);
                if(j == 0){
                    MoveTo m = new MoveTo();
                    m.xProperty().bind(c.centerXProperty());
                    m.yProperty().bind(c.centerYProperty());
                    p.getElements().add(m);
                }else{
                    LineTo l = new LineTo();
                    l.xProperty().bind(c.centerXProperty());
                    l.yProperty().bind(c.centerYProperty());
                    p.getElements().add(l);
                }
                listPoint.add(c);
            }
            listPoints.add(listPoint);
            dessin.getChildren().add(p);
            heap[i] = p.getElements().size()-1;
        }
    }
    
    private void modifColonne(){
        String element = listNim.get(0).getId();
        int i;
        /* oui c'est moche ... je n'ai pas trouvé d'autres solutions */
        switch(element){
            case "path0":
                i = 0;
                break;
            case "path1" :
                i = 1;
                break;
            case "path2" :
                i = 2;
                break;
            case "path3" :
                i = 3;
                break;
            case "path4" :
                i = 4;
                break;
            case "path5" :
                i = 5;
                break;
            case "path6" :
                i = 6;
                break;
            case "path7" :
                i = 7;
                break;
            case "path8" :
                i = 8;
                break;
            case "path9" :
                i = 9;
                break;
            default :
                i = -1;
                break;
        }
        if(i == -1){
            System.out.println("Erreur");
            return;
        }
        /* on retire l'élément sélectionner ainsi que les points qui lui sont associé */
        dessin.getChildren().remove(listNim.get(0));
        List<Circle> pt = listPoints.get(i);
        for(int j = 0 ; j < pt.size () ; j++){
            dessin.getChildren().remove(listPoints.get(i).get(j));
        }
        /* on recréé un chemin passant par les anciens points mais s'arrêtant au niveau désiré */
        Path p = new Path();
        p.setId("path"+i);
        p.setStroke(Color.GREENYELLOW);
        p.setStrokeWidth(radius*2/3);
        p.setOnMouseEntered((MouseEvent z) -> {
            p.setStrokeWidth(p.getStrokeWidth()*3/2);
        });
        p.setOnMouseExited((MouseEvent z) -> {
            p.setStrokeWidth(p.getStrokeWidth()*2/3);
        });
        p.setOnMouseClicked((MouseEvent z) -> {
            if(p.getStroke() == Color.GREEN){
                p.setStroke(Color.GREENYELLOW);
                if(listNim.size() == 1){
                    listNim.get(0).setStroke(Color.GREEN);
                }
                slider2.setDisable(false);
                slider2.setValue(p.getElements().size()-1);
                listNim.clear();
                listNim.add(p);
            }else{
                p.setStroke(Color.GREEN);
                listNim.remove(p);
                slider2.setDisable(true);
            }
        });
        for(int j = 0 ; j <= slider2.getValue() ; j++){
            Circle c = pt.get(j);
            dessin.getChildren().add(c);
            if(j == 0){
                MoveTo m = new MoveTo();
                m.xProperty().bind(c.centerXProperty());
                m.yProperty().bind(c.centerYProperty());
                p.getElements().add(m);
            }else{
                LineTo l = new LineTo();
                l.xProperty().bind(c.centerXProperty());
                l.yProperty().bind(c.centerYProperty());
                p.getElements().add(l);
            }
        }
        dessin.getChildren().add(p);
        listNim.remove(listNim.get(0));
        listNim.add(p);
        heap[i] = p.getElements().size()-1;
    }
    
    /* fonction permettant l'affichage après chaque intervention */
    void affichageApresIA(int n){
        /* on efface le dessin pour le reconstruire */
        dessin.getChildren().clear();
        dessin.getChildren().add(sol);
        for(int i = 0 ; i < n ; i++){
            int mem_i = i;
            List<Circle> pt = listPoints.get(i);
            if(heap[i] != 0){
                /* on recréer tous les points en retirant les évènements souris */
                for(int j = 0 ; j <= heap[i] ; j++){
                    Circle c = new Circle();
                    c.setCenterX(pt.get(j).getCenterX());
                    c.setCenterY(pt.get(j).getCenterY());
                    c.setRadius(pt.get(j).getRadius());
                    c.setFill(pt.get(j).getFill());
                    c.setStroke(pt.get(j).getStroke());
                    c.setStrokeWidth(pt.get(j).getStrokeWidth());
                    dessin.getChildren().add(c);
                }
                /* pour pouvoir jouer, il ne faut pas 1 Path mais plein de Line */
                for(int j = 0 ; j < heap[i] ; j++){
                    Line l = new Line();
                    int mem_j = j;
                    l.setStartX(pt.get(j).getCenterX());
                    l.setStartY(pt.get(j).getCenterY());
                    l.setEndX(pt.get(j+1).getCenterX());
                    l.setEndY(pt.get(j+1).getCenterY());
                    l.setStroke(Color.GREEN);
                    l.setStrokeWidth(radius*2/3);
                    l.setOnMouseEntered((MouseEvent e) -> {
                        l.setStroke(Color.YELLOWGREEN);
                    });
                    l.setOnMouseExited((MouseEvent e) -> {
                        l.setStroke(Color.GREEN);
                    });
                    l.setOnMouseClicked((MouseEvent e) -> {
                        /* le joueur joue */
                        Nim.Move move = new Nim.Move(mem_i, mem_j);
                        Nim.applyMove(move, heap);
                        coup++;
                        if(Nim.isFinished(heap)){
                            finNim();
                        }else{
                            if(!pvsia){
                                if((coup+1)%2 == 0){
                                    tour.setText("BLEU");
                                    tour.setTextFill(Color.BLUE);
                                }else{
                                    tour.setText("ROUGE");
                                    tour.setTextFill(Color.RED);
                                }
                            }else{
                                coup++;
                                Nim nim = new Nim(type_jeu);
                                move = nim.nextMove(heap);
                                Nim.applyMove(move, heap);
                            }
                            affichageApresIA(n);
                            if(Nim.isFinished(heap)){
                                finNim();
                            }else{
                                if(!pvsia){
                                    if((coup+1)%2 == 0){
                                        tour.setText("BLEU");
                                        tour.setTextFill(Color.BLUE);
                                    }else{
                                        tour.setText("ROUGE");
                                        tour.setTextFill(Color.RED);
                                    }
                                }
                            }
                        }
                    });
                    dessin.getChildren().add(l);
                }
            }
        }
    }
    
    private void finNim(){
        if((coup+1)%2 == 0){
            tour.setText("BLEU");
            tour.setTextFill(Color.BLUE);
        }else{
            tour.setText("ROUGE");
            tour.setTextFill(Color.RED);
        }
        dessin.getChildren().clear();
        dessin.getChildren().add(sol);
        if(type_jeu){
            System.out.println("Joueur " + ((coup+1)%2+1) + " gagnant dans Nim normal");
            Alert alerte = new Alert(AlertType.INFORMATION, "Le joueur " + ((coup+1)%2+1) + " gagne ! (Nim normal)\nRetourner au menu ?", ButtonType.YES, ButtonType.NO);
            alerte.showAndWait();
            if(alerte.getResult() == ButtonType.YES){
                retourMenu();
            }else{
                quitter();
            }
        }else{
            System.out.println("Joueur " + (coup%2+1) + " gagnant dans Nim misère");
            Alert alerte = new Alert(AlertType.INFORMATION, "Le joueur " + (coup%2+1) + " gagne ! (Nim misère)\nRetourner au menu ?", ButtonType.YES, ButtonType.NO);
            alerte.showAndWait();
            if(alerte.getResult() == ButtonType.YES){
                retourMenu();
            }else{
                quitter();
            }
        }
        dessin.getChildren().clear();
    }
    
    
    /* problème : l'écran ne s'actualise une fois la fonction terminée
    * ie à la fin du while. On ne suit donc pas l'évolution du jeu ...
    * l'implémentation de la vitesse n'est donc pas opérationnelle
    */
    @FXML
    void goIAvsIA() throws InterruptedException {
        int n = (int) slider.getValue();
        Nim nim1 = new Nim(type_jeu);
        Nim nim2 = new Nim(type_jeu);
        Nim.Move coup;
        int i = 0;
        
        while(true){
            i++;
            coup = nim1.nextMove(heap);
            if(coup == Nim.Move.EMPTY){
                if(type_jeu){
                    System.out.println("Joueur 1 perdant dans Nim normal");
                    Alert alerte = new Alert(AlertType.INFORMATION, "Le joueur 2 gagne ! (Nim normal)\nRetourner au menu ?", ButtonType.YES, ButtonType.NO);
                    alerte.showAndWait();
                    if(alerte.getResult() == ButtonType.YES){
                        retourMenu();
                    }else{
                        quitter();
                    }
                }else{
                    System.out.println("Joueur 1 gagnant dans Nim misère");
                    Alert alerte = new Alert(AlertType.INFORMATION, "Le joueur 1 gagne ! (Nim misère)\nRetourner au menu ?", ButtonType.YES, ButtonType.NO);
                    alerte.showAndWait();
                    if(alerte.getResult() == ButtonType.YES){
                        retourMenu();
                    }else{
                        quitter();
                    }
                }
                speed.setVisible(false);
                break;
            }
            Nim.applyMove(coup, heap);
            Nim.printNim(heap);
            //Thread.sleep(speed.getValue()*1000);
            Thread.sleep(1000);
            affichageApresIA(n);
            coup = nim2.nextMove(heap);
            if(coup == Nim.Move.EMPTY){
                if(type_jeu){
                    System.out.println("Joueur 2 perdant dans Nim normal");
                    Alert alerte = new Alert(AlertType.INFORMATION, "Le joueur 1 gagne ! (Nim normal)\nRetourner au menu ?", ButtonType.YES, ButtonType.NO);
                    alerte.showAndWait();
                    if(alerte.getResult() == ButtonType.YES){
                        retourMenu();
                    }else{
                        quitter();
                    }
                }else{
                    System.out.println("Joueur 2 gagnant dans Nim misère");
                    Alert alerte = new Alert(AlertType.INFORMATION, "Le joueur 2 gagne ! (Nim misère)\nRetourner au menu ?", ButtonType.YES, ButtonType.NO);
                    alerte.showAndWait();
                    if(alerte.getResult() == ButtonType.YES){
                        retourMenu();
                    }else{
                        quitter();
                    }
                }
                break;
            }
            Nim.applyMove(coup, heap);
            Nim.printNim(heap);
            //pause();
            //Thread.sleep(speel.getValue()*1000);
            affichageApresIA(n);
        }
    }
    
    @FXML
    Menu menuJouer;
    @FXML
    Menu menuHackenbush;
    @FXML
    Menu menuNim;
    @FXML
    MenuItem aideNim;
    @FXML
    MenuItem aideHackenbush;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /* dans Hackenbush, on sélectionne une couleur initiale */
        couleur.getSelectionModel().selectFirst();
        sol.setStartX(15);
        sol.endXProperty().bind(dessin.widthProperty().subtract(sol.startXProperty()));
        sol.startYProperty().bind(dessin.heightProperty().subtract(20));
        sol.endYProperty().bind(sol.startYProperty());
        /* évènement clics de la souris sur le dessin */
        dessin.onMouseClickedProperty().bind(
                Bindings.when(point.selectedProperty()).then(handlerPoint).otherwise(
                Bindings.when(droite.selectedProperty()).then(handlerDroite).otherwise(
                        (EventHandler<MouseEvent>) null)));
        /* un slider varie sur l'ensemble Double. Comme nous voulons des Int
        * nous utilisons Math.floor (partie entière inférieure) pour arrondir
        */
        slider.setOnMouseDragged((MouseEvent z) -> {
            double d = slider.getValue();
            if(d - Math.floor(d) < 0.5){
                slider.setValue(Math.floor(d));
            }else{
                slider.setValue(Math.floor(d) + 1);
            }
            if(d != slider.getValue()){
                nim();
            }
        });
        /* on définit la même fonction pour un simple clic sur le slider */
        slider.onMouseClickedProperty().bind(slider.onMouseDraggedProperty());
        slider2.setOnMouseDragged((MouseEvent z) -> {
            double d = slider2.getValue();
            if(d - Math.floor(d) < 0.5){
                slider2.setValue(Math.floor(d));
            }else{
                slider2.setValue(Math.floor(d) + 1);
            }
            if(d != slider2.getValue()){
                modifColonne();
            }
        });
        slider2.onMouseClickedProperty().bind(slider2.onMouseDraggedProperty());
        slider2.setValue(slider2.getMax());
        
        /* automatisme sur le MenuBar en haut de la fenêtre */
        menuJouer.disableProperty().bind(edition.visibleProperty().not().or(mode.visibleProperty()).or(menuJeu.visibleProperty()));
        menuNim.disableProperty().bind(menuCreationNim.visibleProperty().not().or(start.disableProperty()));
        menuHackenbush.disableProperty().bind(menuCreationHackenbush.visibleProperty().not());
        aideNim.disableProperty().bind(menuCreationNim.visibleProperty().not());
        aideHackenbush.disableProperty().bind(menuCreationHackenbush.visibleProperty().not());
    }
}
