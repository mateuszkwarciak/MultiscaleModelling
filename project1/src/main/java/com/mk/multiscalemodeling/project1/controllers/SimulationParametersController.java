package com.mk.multiscalemodeling.project1.controllers;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.controllers.utils.PositiveIntegerStringConverter;
import com.mk.multiscalemodeling.project1.model.GrainImpl;
import com.mk.multiscalemodeling.project1.model.GrainStatus;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
    @FXML private JFXToggleButton editModeToggle;
    @FXML private VBox selectedGrainsBox;
    
    private Set<GrainImpl> selectedGrains;
    
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
        
        selectedGrains = new HashSet<>();
    }
    
    @FXML
    void nucleatingAction(ActionEvent event) {
        int numberOfNucleons = (int) noNucleonsField.getTextFormatter().getValue();
        
        if (numberOfNucleons == 0) {
            simulationController.showAlert("Warning", "Number of neuclons should be greater than 0");
            return;
        }
        
        // do not allow create more then 300 neuclons at once
        if (numberOfNucleons > 1000) {
            simulationController.showAlert("Warning", "Maximum allowed value is 1000");
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
            String msg = "Missing: ";
            msg += (numberOfInclusions == 0) ? " number of inclusions," : "";
            msg += (sizeOfInclusions == 0) ? " size of inclusions," : "";
            msg += (numberOfInclusions == 0) ? " inclusions shape" : "";
            simulationController.showAlert("Warning", msg);
            return;
        }
        
        simulationManager.addInclusionsToSimulation(numberOfInclusions, sizeOfInclusions, inclusionShape);
        simulationController.drawCellsOnCanvas();
    }

    @FXML
    void clearAction(ActionEvent event) {
        simulationController.clearAlert();
    }

    @FXML
    void startStepByStepAction(ActionEvent event) {
        Neighbourhood selectedNeighborhood = getNeighbourhood();
        if (selectedNeighborhood == null) {
            log.warn("Cannot run simulation. Select neighbourhood");
            simulationController.showAlert("Warning", "Select neighbourhood");
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
        int counter = 0;
        if (selectedNeighborhood == null) {
            log.warn("Cannot run simulation. Select neighbourhood");
            simulationController.showAlert("Warning", "Select neighbourhood");
            return;
        } else if (selectedNeighborhood instanceof ShapeControl) {
            log.debug("Performing 1 iteration grain growth with VonNeuman neighbourhood before growing with Shape Control");
            simulationManager.simulateGrowth(new VonNeuman());
            counter++;
        }
             
        while(simulationManager.simulateGrowth(selectedNeighborhood)) {
            counter ++;
            log.info("Processing grain growth. Iteration no: {}", counter);
        }
        log.info("End of simulation. Nuber of iteration: {}", counter);
        
        simulationController.drawCellsOnCanvas();
    }
    
    @FXML
    void activateSelectedAction(ActionEvent event) {
        for(GrainImpl grain : selectedGrains) {
            grain.setStatus(GrainStatus.GRAIN);
        } 
        refreshListOfSelectedGrains();
    }
    
    @FXML
    void deactivateSelectedAction(ActionEvent event) {
        for(GrainImpl grain : selectedGrains) {
            grain.setStatus(GrainStatus.FROZEN);
        }
        refreshListOfSelectedGrains();
    }
    
    @FXML
    void removeSelectionAction(ActionEvent event) {
        clearListOfSelectedGrains();
    }

    @FXML
    void mergeAction(ActionEvent event) {
        mergeSelectedGrains();
        simulationController.drawCellsOnCanvas();
    }
    
    private void stepByStepSimulation(Neighbourhood selectedNeighborhood) {
        if(!simulationManager.simulateGrowth(selectedNeighborhood)) {
            if (timeline != null) {
                log.info("End of simulation. No chenges in iteration");
                timeline.stop();
            }
        };
    }

    private Neighbourhood getNeighbourhood() {
        String selectedNeighbourhoodType = neighbourhoodTypeComboBox.getSelectionModel().getSelectedItem();
        if (StringUtils.isEmpty(selectedNeighbourhoodType)) {
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
    
    public void clearListOfSelectedGrains() {
        selectedGrains = new HashSet<>();
        refreshListOfSelectedGrains();
    }
    
    public void removeSelectedGrainsFromSimulation() {
        simulationManager.removeSelectedGrains(selectedGrains);
    }
    
    public void removeAllGrainsExceptSelected() {
        simulationManager.removeExceptSelectedGrains(selectedGrains);
    }
    
    public void mergeSelectedGrains() {
        simulationManager.mergeSelectedGrains(selectedGrains);
        clearListOfSelectedGrains();
    }
    
    public void addSelectedGrain(GrainImpl selectedGrain) {
        selectedGrains.add(selectedGrain);
        refreshListOfSelectedGrains();
    }
    
    public boolean getEditModeStatus() {
        return editModeToggle.isSelected();
    }
    
    private void refreshListOfSelectedGrains() {
        selectedGrainsBox.getChildren().clear();
        for (GrainImpl grain: selectedGrains) {
            Text grainLabel = new Text(grain.getColor().toString() + "   Status: " + grain.getStatus() + "   No. cells: " + grain.getCells().size());
            grainLabel.setFill(grain.getColor());
            grainLabel.setTextAlignment(TextAlignment.JUSTIFY);
            
            selectedGrainsBox.getChildren().add(grainLabel);
        }
    }
    
    void setSimulationController(SimulationController simulationController) {
        this.simulationController = simulationController;
    }
    
}
