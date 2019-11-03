package com.mk.multiscalemodeling.project1.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.controllers.utils.PositiveIntegerStringConverter;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextFormatter;

@Component
public class SimulationParametersController implements Initializable{

    @FXML private JFXTextField noNucleonsField;
    @FXML private JFXTextField noInclusionsField;
    @FXML private JFXTextField sizeInclusionsField;
    @FXML private JFXComboBox<?> typeInclusionsComboBox;
    @FXML private JFXComboBox<?> neighbourhoodTypeComboBox;
    @FXML private JFXSlider shapeRatioSlider;
        
    private SimulationController simulationController;
    private SimulationManager simulationManager;
      
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        simulationController = JavaFxBridge.applicationContext.getBean(SimulationController.class);
        simulationManager = JavaFxBridge.applicationContext.getBean(SimulationManager.class);
        
        noNucleonsField.setTextFormatter(new TextFormatter<>(new PositiveIntegerStringConverter()));
        noInclusionsField.setTextFormatter(new TextFormatter<>(new PositiveIntegerStringConverter()));
        sizeInclusionsField.setTextFormatter(new TextFormatter<>(new PositiveIntegerStringConverter()));
    }
    
    @FXML
    void nucleatingAction(ActionEvent event) {
        int numberOfNucleons = (int) noNucleonsField.getTextFormatter().getValue();
        
        // do not allow create more then 300 neuclons at once
        if (numberOfNucleons > 300) {
            noNucleonsField.setText("300");
            return;
        }
        
        simulationManager.addNucleonsToSimulation(numberOfNucleons);
        simulationController.drawCellsOnCanvas(simulationManager.getCells());
    }
    
    @FXML
    void addInclusionsAction(ActionEvent event) {

    }

    @FXML
    void clearAction(ActionEvent event) {

    }

    @FXML
    void startStepByStepAction(ActionEvent event) {

    }
    
    @FXML
    void startImmediateAction(ActionEvent event) {

    }

    void setSimulationController(SimulationController simulationController) {
        this.simulationController = simulationController;
    }
    
}
