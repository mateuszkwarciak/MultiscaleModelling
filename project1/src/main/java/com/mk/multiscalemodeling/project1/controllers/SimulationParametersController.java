package com.mk.multiscalemodeling.project1.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.controllers.utils.PositiveIntegerStringConverter;
import com.mk.multiscalemodeling.project1.model.InclusionShape.Circle;
import com.mk.multiscalemodeling.project1.model.InclusionShape.InclusionType;
import com.mk.multiscalemodeling.project1.model.InclusionShape.Square;
import com.mk.multiscalemodeling.project1.model.neighbourdhood.Neighbourhood;
import com.mk.multiscalemodeling.project1.model.neighbourdhood.ShapeControl;
import com.mk.multiscalemodeling.project1.model.neighbourdhood.VonNeuman;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextFormatter;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SimulationParametersController implements Initializable{

    @FXML private JFXTextField noNucleonsField;
    @FXML private JFXTextField noInclusionsField;
    @FXML private JFXTextField sizeInclusionsField;
    @FXML private JFXComboBox<String> typeInclusionsComboBox;
    @FXML private JFXComboBox<String> neighbourhoodTypeComboBox;
    @FXML private JFXSlider shapeRatioSlider;
    
    private static final String VONNEUMAN_NEIGHBOURHOOD = "VonNeuman";
    private static final String SHAPE_CONTROL_NEIGHBOURHOOD = "Shape Control";
    
    private static final String INCLUSION_SQUARE = "Square";
    private static final String INCLUSION_CIRCLE = "Circle";
    
    private Timeline timeline;
    
    private SimulationController simulationController;
    private SimulationManager simulationManager;
      
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        simulationController = JavaFxBridge.applicationContext.getBean(SimulationController.class);
        simulationManager = JavaFxBridge.applicationContext.getBean(SimulationManager.class);
        
        noNucleonsField.setTextFormatter(new TextFormatter<>(new PositiveIntegerStringConverter()));
        noInclusionsField.setTextFormatter(new TextFormatter<>(new PositiveIntegerStringConverter()));
        sizeInclusionsField.setTextFormatter(new TextFormatter<>(new PositiveIntegerStringConverter()));
        
        neighbourhoodTypeComboBox.getItems().addAll(VONNEUMAN_NEIGHBOURHOOD, SHAPE_CONTROL_NEIGHBOURHOOD);
        typeInclusionsComboBox.getItems().addAll(INCLUSION_SQUARE, INCLUSION_CIRCLE);    
    }
    
    @FXML
    void nucleatingAction(ActionEvent event) {
        int numberOfNucleons = (int) noNucleonsField.getTextFormatter().getValue();
        
        if (numberOfNucleons == 0) {
            return;
        }
        
        // do not allow create more then 300 neuclons at once
        if (numberOfNucleons > 1000) {
            noNucleonsField.setText("1000");
            return;
        }
        
        simulationManager.addNucleonsToSimulation(numberOfNucleons);
        simulationController.drawCellsOnCanvas();
    }
    
    @FXML
    void addInclusionsAction(ActionEvent event) {
        int numberOfInclusions = (int) noInclusionsField.getTextFormatter().getValue();
        int sizeOfInclusions = (int) sizeInclusionsField.getTextFormatter().getValue();
        InclusionType inclusionShape = getInclusionShape();
        
        if (numberOfInclusions == 0 || sizeOfInclusions == 0 || inclusionShape == null) {
            return;
        }
        
        simulationManager.addInclusionsToSimulation(numberOfInclusions, sizeOfInclusions, inclusionShape);
        simulationController.drawCellsOnCanvas();
    }

    @FXML
    void clearAction(ActionEvent event) {

    }

    @FXML
    void startStepByStepAction(ActionEvent event) {
        Neighbourhood selectedNeighborhood = getNeighbourhood();
        if (selectedNeighborhood == null) {
            log.warn("Cannot run simulation. Select neighbourhood");
            return;
        }
        
        KeyFrame simulation = new KeyFrame(Duration.seconds(0.1), (e) -> {
            stepByStepSimulation(selectedNeighborhood);
            simulationController.drawCellsOnCanvas();
        });

        timeline = new Timeline(simulation);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    @FXML
    void startImmediateAction(ActionEvent event) {
        Neighbourhood selectedNeighborhood = getNeighbourhood();
        if (selectedNeighborhood == null) {
            log.warn("Cannot run simulation. Select neighbourhood");
            return;
        }
        
        int counter = 0;
        while(simulationManager.simulateGrowth(selectedNeighborhood)) {
            counter ++;
            log.info("Processing grain growth. Iteration no: {}", counter);
        }
        log.info("End of simulation. Nuber of iteration: {}", counter);
        
        simulationController.drawCellsOnCanvas();
    }
    
    private void stepByStepSimulation(Neighbourhood selectedNeighborhood) {
        if(!simulationManager.simulateGrowth(selectedNeighborhood)) {
            if (timeline != null) {
                timeline.stop();
            }
        };
    }

    private Neighbourhood getNeighbourhood() {
        String selectedNeighbourhoodType = neighbourhoodTypeComboBox.getSelectionModel().getSelectedItem();
        if (StringUtils.isEmpty(selectedNeighbourhoodType)) {
            //TODO add alert
            return null;
        }
        
        switch (selectedNeighbourhoodType) {
        case VONNEUMAN_NEIGHBOURHOOD:
            return new VonNeuman();
        case SHAPE_CONTROL_NEIGHBOURHOOD:
            int shapeControlRatio = (int) shapeRatioSlider.getValue();
            return new ShapeControl(shapeControlRatio);
        default:
            return null;
        }
    }
    
    private InclusionType getInclusionShape() {
        String selectedInclusionShape = typeInclusionsComboBox.getSelectionModel().getSelectedItem();
        if (StringUtils.isEmpty(selectedInclusionShape)) {
            return null;
        }
        
        switch (selectedInclusionShape) {
        case INCLUSION_SQUARE:
            return new Square();
        case INCLUSION_CIRCLE:
            return new Circle(); 
        default:
            return null;
        }
    }
    
    void setSimulationController(SimulationController simulationController) {
        this.simulationController = simulationController;
    }
    
}
