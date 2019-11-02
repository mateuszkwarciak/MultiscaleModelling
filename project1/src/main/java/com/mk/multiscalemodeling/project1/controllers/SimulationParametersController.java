package com.mk.multiscalemodeling.project1.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

@Component
public class SimulationParametersController implements Initializable{

    @FXML private JFXTextField noGrainsField;
    @FXML private JFXButton nucleatingBtn;
    @FXML private JFXButton clearBtn;
    @FXML private JFXButton startStepByStepBtn;
    @FXML private JFXButton startImmediateBtn;
    @FXML private JFXTextField noInclusionsField;
    @FXML private JFXTextField sizeInclusionsField;
    @FXML private JFXComboBox<?> typeInclusionsComboBox;
    @FXML private JFXButton addInclusionsBtn;
    @FXML private JFXComboBox<?> neighbourhoodTypeComboBox;
    @FXML private JFXSlider shapeRatioSlider;
    
    @Autowired
    private SimulationController simulationController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        
    }

    void setSimulationController(SimulationController simulationController) {
        this.simulationController = simulationController;
    }
    
}
