package com.mk.multiscalemodeling.project1.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimulationController implements Initializable {

    @FXML private JFXHamburger parametersBtn;
    @FXML private ImageView saveBtn;
    @FXML private ImageView exitBtn;
    @FXML private ScrollPane paneForCanvas;
    @FXML private JFXDrawer parametersPane;
    
    @Autowired
    private SimulationParametersController parametersController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //parametersController = new SimulationParametersController();
            //parametersController.setSimulationController(this);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/simulationParameters.fxml"));
            //loader.setController(parametersController);
            
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
    }

}
