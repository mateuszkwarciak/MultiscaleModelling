package com.mk.multiscalemodeling.project1.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.io.Exporter;
import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Grain;
import com.mk.multiscalemodeling.project1.model.GrainImpl;
import com.mk.multiscalemodeling.project1.model.GrainStatus;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
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
    @FXML private JFXToggleButton energyDistributionToggle;
    
    private SimulationParametersController parametersController;
    private SimulationManager simulationManager;

    private int CELL_SIZE = 3;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parametersController = JavaFxBridge.applicationContext.getBean(SimulationParametersController.class);
        simulationManager = JavaFxBridge.applicationContext.getBean(SimulationManager.class);
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/simulationParameters.fxml"));
            loader.setController(parametersController);
            
            AnchorPane mainPaneForParameters = (AnchorPane) loader.load();

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(mainPaneForParameters);
            
            parametersPane.setSidePane(scrollPane);

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
        
        canvas.setOnMouseClicked((e) -> {
            int x = (int) e.getX() / CELL_SIZE;
            int y = (int) e.getY() / CELL_SIZE;
            x++;
            y++;
            
            Cell selectedCell = simulationManager.getCells()[x][y];
            log.info("Selected cell X: {}, Y:{}, Color: {}, Status: {}, Grain Status: {}", selectedCell.getX(), selectedCell.getY(), 
                    (selectedCell.getGrain() != null) ? selectedCell.getGrain().getColor() : null, selectedCell.getStatus(),
                            (selectedCell.getGrain() != null) ? selectedCell.getGrain().getStatus() : null);
              
            if (parametersController.getEditModeStatus()) {
                //Select Grain
                if (e.getButton() == MouseButton.PRIMARY) {
                    Grain selectedGrain = selectedCell.getGrain();
                    if (selectedGrain != null && !selectedGrain.getStatus().equals(GrainStatus.INCLUSION)) {
                        if (!selectedGrain.getStatus().equals(GrainStatus.BORDER)) {
                            parametersController.addSelectedGrain((GrainImpl) selectedGrain);
                        }
                    }
                //Add inclusion
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    if (selectedCell.getStatus().equals(CellStatus.EMPTY)) {
                        simulationManager.addNucleonToSimulation(selectedCell);
                        drawCellsOnCanvas();
                        log.info("Neuclon added into simulation");
                    }
                }
            }
            
            
        });
        
        saveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            saveAction();
        });
        
        exitBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            exitAction();
        });
        
        energyDistributionToggle.setOnAction(e -> {
            if (energyDistributionToggle.isSelected()) {
                drawEnergyOnCanvas();
            } else {
                drawCellsOnCanvas();
            }
        });

    }
        
    public void setCanvasSize(int width, int hight) { 
        canvas.setWidth(width * CELL_SIZE);
        canvas.setHeight(hight * CELL_SIZE);
                
        log.info("RESIZE: dimX {}, dimY {}", canvas.getWidth(), canvas.getHeight());
        earseCanvas();
    }
    
    public void drawCellsOnCanvas() {
        earseCanvas();
        energyDistributionToggle.setSelected(false);
        draw(canvas, CELL_SIZE);
    }
    
    public void drawEnergyOnCanvas() {
        earseCanvas();
        energyDistributionToggle.setSelected(true);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Pair<Double, Double> minMax = simulationManager.getMinMaxEnergy();
        
        if (minMax == null) {
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            return;
        }
        
        double energyRange = minMax.getRight() - minMax.getLeft();
        double energyDt = energyRange / 255.0;
        double minEnergy = minMax.getLeft();
        
        Cell[][] cells = simulationManager.getCells();
        for (int i = 1; i < simulationManager.getDimX() + 1; i++) {
            for (int j = 1; j < simulationManager.getDimY() + 1; j++) {  
                Cell cell = cells[i][j];
                
                if (cell.getGrain() == null || cell.getEnergy() == -1) {
                    gc.setFill(Color.WHITE);
                } else if (cell.getEnergy() == 0.0) {
                    gc.setFill(Color.RED);
                } else {
                    int energyColor = (int)((cell.getEnergy() - minEnergy) / energyDt);
                    gc.setFill(Color.rgb(0, 128, energyColor));
                }
                
                gc.fillRect((i - 1) * CELL_SIZE, (j - 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        
    }
    
    private void drawToImage(Canvas canvasToImage) {
        draw(canvasToImage, 1);
    }
    
    private void draw(Canvas canvasToDraw, int cellSize) {
        GraphicsContext gc = canvasToDraw.getGraphicsContext2D();
        Cell[][] cells = simulationManager.getCells();
        
        // it starts from 1 to move away from the absorbing edge
        for (int i = 1; i < simulationManager.getDimX() + 1; i++) {
            for (int j = 1; j < simulationManager.getDimY() + 1; j++) {
                // draw if cell not empty
                if (cells[i][j].getStatus().equals(CellStatus.OCCUPIED) || cells[i][j].getStatus().equals(CellStatus.INCLUSION)
                        || cells[i][j].getStatus().equals(CellStatus.BORDER)) {
                    gc.setFill(cells[i][j].getGrain().getColor());
                    // subtract 1 to avoid drawing white frame 
                    gc.fillRect((i - 1) * cellSize, (j - 1) * cellSize, cellSize, cellSize);
                }   
            }
        }
    }
    
    private void earseCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Cell.EMPTY_CELL_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    private void exitAction() {
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
    
    private void saveAction() {
        JFXAlert<String> alert = new JFXAlert<>((Stage) scrollPane.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        
        JFXButton saveAsTxtBtn = new JFXButton("Save as JSON");
        saveAsTxtBtn.setOnMouseClicked((e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json files (*.json)", "*json"));
            Exporter exporter = new Exporter();
            
            File selectedFile = fileChooser.showSaveDialog((Stage) scrollPane.getScene().getWindow());
            if (selectedFile != null) {
                try {
                    exporter.saveAsJson(selectedFile);
                    log.info("Simulation saved to: {}", selectedFile.getPath());
                } catch (IOException ioe) {
                    log.info("Error during saving simulation into image");
                }
                alert.close();
            }
            
        } );
        
        JFXButton saveAsImageBtn = new JFXButton("Save as image");
        saveAsImageBtn.setOnMouseClicked((e) -> {
            Canvas canvasToSave = new Canvas(simulationManager.getDimX(), simulationManager.getDimY());
            drawToImage(canvasToSave);
            Exporter exporter = new Exporter();
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*png"));
            
            File selectedFile = fileChooser.showSaveDialog((Stage) scrollPane.getScene().getWindow());
            if (selectedFile != null) {
                try {
                    exporter.saveAsImage(canvasToSave, selectedFile);
                    log.info("Simulation saved to: {}", selectedFile.getPath());
                } catch (IOException ioe) {
                    log.info("Error during saving simulation into image");
                }
                alert.close();
            }
        });
       
        JFXButton cancelBtn = new JFXButton("Cancel");
        cancelBtn.setOnMouseClicked((e) -> {
            alert.close();
        });
        
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("Save simulation as:"));
        layout.setActions(saveAsTxtBtn, saveAsImageBtn, cancelBtn);
        alert.setContent(layout);
        
        alert.show();
    }

    public void clearAlert() {
        JFXAlert<String> alert = new JFXAlert<>((Stage) scrollPane.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("Clear"));
        
        JFXButton clearAll = new JFXButton("Clear all");
        clearAll.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            log.info("Perform Clear All action");
            parametersController.clearListOfSelectedGrains();
            simulationManager.clearSimulation();
            drawCellsOnCanvas();
            parametersController.refreshGBOccupationRatio();
            alert.close();
        });
        
        JFXButton removeSelected = new JFXButton("Remove selected grains");
        removeSelected.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            log.info("Perform Clear selected grains action");
            parametersController.removeSelectedGrainsFromSimulation(false);
            parametersController.clearListOfSelectedGrains();
            drawCellsOnCanvas();
            parametersController.refreshGBOccupationRatio();
            alert.close();
        });
        
        JFXButton removeNotSelected = new JFXButton("Remove not selected grains");
        removeNotSelected.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            log.info("Perform Clear not selected grains action");
            parametersController.removeAllGrainsExceptSelected(false);
            drawCellsOnCanvas();
            parametersController.refreshGBOccupationRatio();
            alert.close();
        });
        
        JFXButton removeSelectedWithBorder = new JFXButton("Remove selected grains (with border)");
        removeSelectedWithBorder.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            log.info("Perform Remove selected grains (without border) action");
            parametersController.removeSelectedGrainsFromSimulation(true);
            parametersController.clearListOfSelectedGrains();
            drawCellsOnCanvas();
            parametersController.refreshGBOccupationRatio();
            alert.close();
        });
        
        JFXButton removeNotSelectedWithBorder = new JFXButton("Remove not selected grains (with border)");
        removeNotSelectedWithBorder.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            log.info("Perform Remove not selected grains (without border) action");
            parametersController.removeAllGrainsExceptSelected(true);
            drawCellsOnCanvas();
            parametersController.refreshGBOccupationRatio();
            alert.close();
        });
        
        JFXButton cancel = new JFXButton("Cancel");
        cancel.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            log.info("Perform Clear Cancel action");
            alert.close();
        });
        
        layout.setActions(clearAll, removeSelected, removeNotSelected, removeSelectedWithBorder, removeNotSelectedWithBorder, cancel);
        alert.setContent(layout);
        
        alert.show();
    }
    
    
    public void showAlert(String heading, String message) {
        JFXAlert<String> alert = new JFXAlert<>((Stage) scrollPane.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(heading));
        layout.setBody(new HBox(new Label(message)));
        
        JFXButton okBtn = new JFXButton("Ok");
        okBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            alert.close();
        });

        layout.setActions(okBtn);
        alert.setContent(layout);
        
        alert.show();
    }
    
}
