package com.mk.multiscalemodeling.project1.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.fxml.Initializable;

public class SimulationParametersController implements Initializable{

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
