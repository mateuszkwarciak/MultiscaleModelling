package com.mk.multiscalemodeling.project1.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.mk.multiscalemodeling.project1.JavaFxBridge;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StartController implements Initializable{

	@FXML private AnchorPane rootPane;
    @FXML private JFXButton newBtn;
    @FXML private JFXButton loadBtn;
    @FXML private JFXButton optionsBtn;
    @FXML private JFXButton exitBtn;
    @FXML private StackPane dialogPane;
    
    private JFXDialog dialog;
    
    private Stage popUpStage;
    
    private static int MINIMUM_MATRIX_SIZE = 300;
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	dialogPane.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
    		closeDialogPane();
		});
	}
    
    @FXML
    void newAction(ActionEvent event) {
    	//generateNewProjectConfigurationDialog();
    	showNewProjectPopUpWindow();
    }
    
	private void showNewProjectPopUpWindow() {
		generateNewProjectLoyaut();
	}

	private void generateNewProjectLoyaut() {
		VBox newProjectLayout = new VBox();
		newProjectLayout.setPadding(new Insets(20, 30, 20, 30));

		Text headingText = new Text("New configuration");
		Text bodyText = new Text("Set simulation size");
		
		JFXTextField hightField = new JFXTextField();
		hightField.setLabelFloat(true);
		hightField.setPromptText("Hight");
		
		
    	JFXTextField widthField = new JFXTextField("Width");
		
    	JFXButton createBtn = new JFXButton("Create");
    	JFXButton closeBtn = new JFXButton("Close");
    	
    	createBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
    		//TODO USUNAC PO NAPRAWIE BLEDU Z TEXT FIELDS
    		goToSimulationScheme();
    		popUpStage.close();
    		Stage rootStage = (Stage) rootPane.getScene().getWindow();
    		rootStage.close();
    		
			/*
			 * int matrixDimY = readTextFieldValue(hightField); int matrixDimX =
			 * readTextFieldValue(widthField);
			 * 
			 * if (matrixDimY < MINIMUM_MATRIX_SIZE) {
			 * hightField.setText("Wrong value! Should be an integer greater then 299"); }
			 * 
			 * if (matrixDimX < MINIMUM_MATRIX_SIZE) {
			 * widthField.setText("Wrong value! Should be an integer greater then 299"); }
			 * 
			 * if (matrixDimY >= MINIMUM_MATRIX_SIZE && matrixDimX >= MINIMUM_MATRIX_SIZE) {
			 * //utworz nowy projekt goToSimulationScheme();
			 * 
			 * log.info("Start new project: "); popUpStage.close(); }
			 */
    		
    	});
    	
    	closeBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
    		log.debug("PopUpWindow closed");
    		popUpStage.close();
    	});
    	//hightField, widthField,
    	newProjectLayout.getChildren().addAll(headingText, bodyText, createBtn, closeBtn);
    	
		displayPopUpWindow(newProjectLayout);
	}
    
    @FXML
    void loadAction(ActionEvent event) {
    	
    }

    @FXML
    void optionsAction(ActionEvent event) {
    	
    }
    
    @FXML
    void exitAction(ActionEvent event) {
    	
    }
    
    private double popUpWindowOffsetX = 0;
    private double popUpWindowOffsetY = 0;
    
    private void displayPopUpWindow(Parent layout) {
		popUpStage = new Stage();
		popUpStage.initModality(Modality.APPLICATION_MODAL);
		popUpStage.initStyle(StageStyle.UNDECORATED);

		Scene popUpScene = new Scene(layout, 300, 300);
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
        	
        	Stage simulationStage = new Stage();
			Parent root = fxmlLoader.load();
			
			Scene simulationScene = new Scene(root);
			
			simulationStage.setTitle(JavaFxBridge.APPLICATION_TITLE);
			simulationStage.setScene(simulationScene);
			simulationStage.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.warn("Error during opening simulation window: {}", e);
		}
    }
    
    private void buildAndDisplayDialog(JFXDialogLayout layout) {
    	setDialogPaneDisableStatus(false);
    	
    	dialog = new JFXDialog(dialogPane, layout, JFXDialog.DialogTransition.CENTER);
    	dialog.show();
    }
   
        
    private JFXDialogLayout buildLayout(Node layoutHeading, Pane bodyPane, JFXButton leftButton, JFXButton rightButton) {
    	JFXDialogLayout dialogLayout = new JFXDialogLayout();
    	
    	dialogLayout.setHeading(layoutHeading);
    	dialogLayout.setBody(bodyPane);
    	dialogLayout.setActions(leftButton, rightButton);
    	
    	return dialogLayout;
    }
    
    private void generateNewProjectConfigurationDialog() {
    	Text headingText = new Text("New configuration");
    	
    	VBox bodyPane = new VBox();   	
    	Text bodyText = new Text("Set simulation size");
    	JFXTextField hightField = new JFXTextField("Hight");
    	JFXTextField widthField = new JFXTextField("Widthaaa");
    	
    	bodyPane.getChildren().addAll(bodyText, widthField);
    	
    	JFXButton create = new JFXButton("Create");
    	JFXButton exit = new JFXButton("Exit");
    	
    	create.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
    		int matrixDimY = readTextFieldValue(hightField);
    		int matrixDimX = readTextFieldValue(widthField);
    		
    		if (matrixDimY < MINIMUM_MATRIX_SIZE) {
    			hightField.setText("Wrong value! Should be an integer greater then 299");
    		}
    		
    		if (matrixDimX < MINIMUM_MATRIX_SIZE) {
    			widthField.setText("Wrong value! Should be an integer greater then 299");
    		} 
    		
    		if (matrixDimY >= MINIMUM_MATRIX_SIZE && matrixDimX >= MINIMUM_MATRIX_SIZE) {
    			//otworz nowy prijekt
    			
    			log.info("Start new project: ");
    			closeDialogPane();
    		}
    		
    	});
    	
    	exit.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
    		closeDialogPane();
    	});
    	
    	JFXDialogLayout newProjectConfigurationLayout = buildLayout(headingText, bodyPane, create, exit);

    	newProjectConfigurationLayout.setMinHeight(300.0);
    	
    	buildAndDisplayDialog(newProjectConfigurationLayout);
    }
        
    private int readTextFieldValue(JFXTextField textField) {
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
    
    private void closeDialogPane() {
		dialog.close();
		dialogPane.setDisable(true);
		
    }
    
    private void setDialogPaneDisableStatus(boolean status) {
    	dialogPane.setDisable(false);
    }
}
