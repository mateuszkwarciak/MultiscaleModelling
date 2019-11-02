package com.mk.multiscalemodeling.project1.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import com.mk.multiscalemodeling.project1.Project1Configuration;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SimulationController implements Initializable {

    @FXML private JFXHamburger parametersBtn;
    @FXML private ImageView saveBtn;
    @FXML private ImageView exitBtn;
    @FXML private ScrollPane scrollPane;
    @FXML private StackPane paneForCanvas;
    @FXML private JFXDrawer parametersPane;
    @FXML private Canvas canvas;
    
    private SimulationParametersController parametersController;
    private SimulationManager simulationManager;

    private Color CANVAS_BACKGROUND = Color.GHOSTWHITE;
    
    
    private static final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Project1Configuration.class);

    int canvasDimX, canvasDimY;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parametersController = applicationContext.getBean(SimulationParametersController.class);
        simulationManager = applicationContext.getBean(SimulationManager.class);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/simulationParameters.fxml"));
            loader.setController(parametersController);
            
            ScrollPane mainPaneForParameters = (ScrollPane) loader.load();

            parametersPane.setSidePane(mainPaneForParameters);

        } catch (IOException e) {
            log.error("Error during controller initialization. {}", e);
            Platform.exit();
        }

        HamburgerBackArrowBasicTransition showParametersBtnTask = new HamburgerBackArrowBasicTransition(parametersBtn);
        showParametersBtnTask.setRate(-1);
        parametersBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            showParametersBtnTask.setRate(showParametersBtnTask.getRate() * -1);
            showParametersBtnTask.play();
            
            if (parametersPane.isOpened()) {
                parametersPane.close();
                parametersPane.setDisable(true);
            } else {
                parametersPane.setDisable(false);
                parametersPane.open();
            }
        });
        
        paneForCanvas.prefWidthProperty().bind(scrollPane.widthProperty());
        paneForCanvas.prefHeightProperty().bind(scrollPane.heightProperty());
        
        saveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            
        });
        
        exitBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            generateExitAlert();
        });
    }
        
    public void setCanvasSize(int width, int hight) {
        this.canvasDimX = width;
        this.canvasDimY = hight;
        
        canvas.setWidth(canvasDimX);
        canvas.setHeight(canvasDimY);
                
        log.info("RESIZE: dimX {}, dimY {}", canvasDimX, canvasDimY);
        earseCanvas();
    }
    
    private void earseCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(CANVAS_BACKGROUND);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    private void generateExitAlert() {
        JFXAlert<String> alert = new JFXAlert<>((Stage) scrollPane.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("Exit"));
        layout.setBody(new HBox(new Label("Are you sure?")));
        
        JFXButton yesBtn = new JFXButton("Yes");
        yesBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            Platform.exit();
        });
        
        JFXButton noBtn = new JFXButton("No");
        noBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            alert.close();
        });
        
        layout.setActions(yesBtn, noBtn);
        alert.setContent(layout);
        
        alert.show();
    }
    
    private void prepareAndShowSaveAlert() {
        JFXAlert<String> alert = new JFXAlert<>((Stage) scrollPane.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("Save"));
        HBox layoutBody = new HBox();
        
        
        
        JFXButton pathBtn = new JFXButton("Select path");
             
        pathBtn.setOnMouseClicked((e) -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directoryToSave = directoryChooser.showDialog((Stage) scrollPane.getScene().getWindow());
        });
        
        DirectoryChooser directoryChooser = new DirectoryChooser();
        
        layoutBody.getChildren().addAll(new Label("Chose location"), pathBtn);
        layout.setBody(layoutBody);
        
        JFXButton yesBtn = new JFXButton("Yes");
        yesBtn.setOnMouseClicked((e) -> {
            Platform.exit();
        });
        
        JFXButton noBtn = new JFXButton("No");
        noBtn.setOnMouseClicked((e) -> {
            alert.close();
        });
        
        layout.setActions(yesBtn, noBtn);
        alert.setContent(layout);
        
        alert.show();
    }
}
