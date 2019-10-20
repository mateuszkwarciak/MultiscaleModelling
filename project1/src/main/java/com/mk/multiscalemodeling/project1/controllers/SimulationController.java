package com.mk.multiscalemodeling.project1.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimulationController implements Initializable {

    private JFXDrawer parametersPanel;
    private JFXHamburger parametersBtn;
    
    private SimulationParametersController parametersController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            parametersController = new SimulationParametersController();
            parametersController.setSimulationController(this);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DRAWER FXMLl"));
            loader.setController(parametersController);
            
            AnchorPane mainPaneForParameters = (AnchorPane) loader.load();

            parametersPanel.setSidePane(mainPaneForParameters);

        } catch (IOException e) {
            log.error("Error during controller initialization. {}", e);
            Platform.exit();
        }

        HamburgerBackArrowBasicTransition showParametersBtnTask = new HamburgerBackArrowBasicTransition(parametersBtn);
        showParametersBtnTask.setRate(-1);
        parametersBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            showParametersBtnTask.setRate(showParametersBtnTask.getRate() * -1);
            showParametersBtnTask.play();
            
            if (parametersPanel.isOpened()) {
                parametersPanel.close();
                parametersPanel.setDisable(true);
            } else {
                parametersPanel.setDisable(false);
                parametersPanel.open();
            }
        });
    }

}
