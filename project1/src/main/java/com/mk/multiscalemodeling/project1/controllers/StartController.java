package com.mk.multiscalemodeling.project1.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.controllers.utils.PositiveIntegerStringConverter;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;
import com.mk.multiscalemodeling.project1.simulation.SimulationStatus;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StartController implements Initializable{

	@FXML private AnchorPane rootPane;
    @FXML private JFXButton newBtn;
    @FXML private JFXButton loadBtn;
    @FXML private JFXButton optionsBtn;
    @FXML private JFXButton exitBtn;
    @FXML private StackPane dialogPane;
 
    private Stage popUpStage;
    
    private static final int MINIMUM_MATRIX_SIZE = 300;  
    private static final String NEW_WINDOW_HEADER = "New configuration";
    private static final String NEW_WINDOW_BODY = "Set simulation size:";
    private static final String NEW_WINDOW_ERROR = "Should be an integer greater then 299";
    
    @Value("${project1.developModeEnabled}")
    private boolean developModeEnabled;
    
    //@Autowired
    private SimulationManager simulationManager;
    
    //@Autowired
    private SimulationController simulationController;
    
    @FXML
    void newAction(ActionEvent event) {
        prepareAndShowNewProjectPopUpWindow();
    }
    
    @FXML
    void loadAction(ActionEvent event) {
    	
    }

    @FXML
    void optionsAction(ActionEvent event) {
    	
    }
    
    @FXML
    void exitAction(ActionEvent event) {
        closeProgram();
    }
    
    private void prepareAndShowNewProjectPopUpWindow() {
        VBox newProjectLayout = new VBox();
        newProjectLayout.setPadding(new Insets(20, 30, 20, 30));
        newProjectLayout.setSpacing(15.0);
        
        Text headingText = new Text(NEW_WINDOW_HEADER);
        Text bodyText = new Text(NEW_WINDOW_BODY);
        
        Label errorLbl = new Label();       
        Separator headingBodySeparator = new Separator();
        
        JFXTextField hightField = new JFXTextField();
        hightField.setLabelFloat(true);
        hightField.setPromptText("Hight");
        hightField.setTextFormatter(new TextFormatter<>(new PositiveIntegerStringConverter()));
        hightField.focusTraversableProperty().set(false);
        
        JFXTextField widthField = new JFXTextField();
        widthField.setLabelFloat(true);
        widthField.setPromptText("Width");
        widthField.setTextFormatter(new TextFormatter<>(new PositiveIntegerStringConverter()));
        widthField.focusTraversableProperty().set(false);
        
        if (developModeEnabled) {
            hightField.setText("300");
            widthField.setText("300");
        }
        
        JFXButton createBtn = new JFXButton("Create");
        JFXButton closeBtn = new JFXButton("Close");
        createBtn.focusTraversableProperty().set(false);
        closeBtn.focusTraversableProperty().set(false);
        
        createBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            errorLbl.setText("");
            
            int widthOfSimulation = readIntegerFromField(hightField);
            int hightOfSimulation = readIntegerFromField(widthField);

            if (widthOfSimulation < MINIMUM_MATRIX_SIZE || hightOfSimulation < MINIMUM_MATRIX_SIZE) {
                errorLbl.setText(NEW_WINDOW_ERROR);
            }

            if (widthOfSimulation >= MINIMUM_MATRIX_SIZE && hightOfSimulation >= MINIMUM_MATRIX_SIZE) {
                log.info("Start new simulation: ");
                
                simulationManager.init(SimulationStatus.NEW, widthOfSimulation, hightOfSimulation);
                //simulationController.setCanvasSize(widthOfSimulation, hightOfSimulation);
                
                goToSimulationScheme();
                
                simulationController.setCanvasSize(widthOfSimulation, hightOfSimulation);
                
                closeStartWindow();
            }
        });
        
        closeBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            log.debug("PopUpWindow closed");
            popUpStage.close();
        });

        AnchorPane buttonPane = new AnchorPane();
        buttonPane.setPrefHeight(100);
        buttonPane.getChildren().addAll(closeBtn, createBtn);
        
        AnchorPane.setLeftAnchor(closeBtn, 0.0);
        AnchorPane.setRightAnchor(createBtn, 0.0);
        AnchorPane.setBottomAnchor(closeBtn, 11.0);
        AnchorPane.setBottomAnchor(createBtn, 11.0);
        
        newProjectLayout.getChildren().addAll(headingText, headingBodySeparator, bodyText, errorLbl, hightField, widthField, buttonPane);
        
        displayPopUpWindow(newProjectLayout, 300, 280);
    }
    
    private double popUpWindowOffsetX = 0;
    private double popUpWindowOffsetY = 0;
    
    private void displayPopUpWindow(Parent layout, int width, int height) {
		popUpStage = new Stage();
		popUpStage.initModality(Modality.APPLICATION_MODAL);
		popUpStage.initStyle(StageStyle.UNDECORATED);

		Scene popUpScene = new Scene(layout, width, height);
		popUpScene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				popUpWindowOffsetX = event.getSceneX();
				popUpWindowOffsetY = event.getSceneY();
			}
		});
		popUpScene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				popUpStage.setX(event.getScreenX() - popUpWindowOffsetX);
				popUpStage.setY(event.getScreenY() - popUpWindowOffsetY);
			}
		});

		popUpStage.setScene(popUpScene);
		popUpStage.show();
    }
    
    private void goToSimulationScheme() {	
    	try {
    		FXMLLoader fxmlLoader = new FXMLLoader();
        	fxmlLoader.setLocation(getClass().getResource("/fxml/simulation.fxml"));
        	fxmlLoader.setController(simulationController);
        	
        	Stage simulationStage = new Stage();
			Parent root = fxmlLoader.load();
			
			Scene simulationScene = new Scene(root);
			
			simulationStage.setTitle(JavaFxBridge.APPLICATION_TITLE);
			simulationStage.setMinHeight(650);
			simulationStage.setMinWidth(800);
			simulationStage.setScene(simulationScene);
			simulationStage.show();
		} catch (IOException e) {
			log.warn("Error during opening simulation window: {}", e);
		}
    }
            
    private int readIntegerFromField(JFXTextField textField) {
        //TODO add handling of textFormatter
    	String textFromField = textField.getText();

    	if (StringUtils.isEmpty(textFromField)) {
    		log.trace("Text field from button {} is empty", textField.getId());
    		return 0;
    	}
    	
    	try {
    		int fieldValue = Integer.parseInt(textFromField);
    		log.trace("Text field from button {} is {}", textField.getId(), fieldValue);
    		return fieldValue;
    	} catch (NumberFormatException nfe) {
    		log.info("Cannot read text field value from {}", textField.getId());
    		return 0;
    	}
    }
    
    private void closeStartWindow() {
        log.info("Closing StartWindow");
        popUpStage.close();
        Stage rootStage = (Stage) rootPane.getScene().getWindow();
        rootStage.close();
    }
    
    private void closeProgram() {
        closeStartWindow();
        log.info("Closing program");
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        simulationController = JavaFxBridge.applicationContext.getBean(SimulationController.class);
        simulationManager = JavaFxBridge.applicationContext.getBean(SimulationManager.class);
    }

}
