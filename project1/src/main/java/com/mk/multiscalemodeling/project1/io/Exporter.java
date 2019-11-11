package com.mk.multiscalemodeling.project1.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.io.jsonModels.CellDataModel;
import com.mk.multiscalemodeling.project1.io.jsonModels.GrainDataModel;
import com.mk.multiscalemodeling.project1.io.jsonModels.JsonSImulationModel;
import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Inclusion;
import com.mk.multiscalemodeling.project1.simulation.GrainsManager;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Exporter {
    
    private GrainsManager grainsManager;
    private SimulationManager simulationManager;
    
    public Exporter() {
        grainsManager = JavaFxBridge.applicationContext.getBean(GrainsManager.class);
        simulationManager = JavaFxBridge.applicationContext.getBean(SimulationManager.class);
    }
    
    public void saveAsImage(Canvas canvasToSave, File file) throws IOException {
        if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("png")) {
            file = new File(file.toString() + ".png");
        }
        
        WritableImage writableImage = new WritableImage((int) canvasToSave.getWidth(), (int) canvasToSave.getHeight()); 
        ImageIO.write(SwingFXUtils.fromFXImage(canvasToSave.snapshot(null, writableImage), null), "png", file);
        log.info("Saved image to file: {}", file.getName());
    }

    public void saveAsJson(File file) throws IOException {
        if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
            file = new File(file.toString() + ".json");
        }
        log.info("1");
        JsonSImulationModel dataToSave = new JsonSImulationModel();
        dataToSave.setWidth(simulationManager.getDimX());
        dataToSave.setHight(simulationManager.getDimY());
        log.info("2");
        dataToSave.setGrains(grainsManager.getGrains().stream().map((e) -> new GrainDataModel(e.getColor(), e.getStatus()))
                .collect(Collectors.toList()));
        log.info("2.5");
        dataToSave.setInclusionsId(new ArrayList<>(grainsManager.getId2Inclusion().keySet()));
        dataToSave.setCells(new ArrayList<>());
        log.info("3");
        Cell[][] cells = simulationManager.getCells();
        for (int i = 1; i < simulationManager.getDimX() + 1; i++) {
            for (int j = 1; j < simulationManager.getDimY() + 1; j++) {
                Cell cell = cells[i][j];
                Color cellColor = (cell.getGrain() != null) ? cell.getGrain().getColor() : Cell.EMPTY_CELL_COLOR;
       
                CellDataModel cellDataModel;
                if (cell.getStatus().equals(CellStatus.INCLUSION)) {
                    cellDataModel = new CellDataModel(cell.getX(), cell.getY(), cellColor, cell.getStatus(), 
                            ((Inclusion) cell.getGrain()).getInclusionId());
                } else {
                    cellDataModel = new CellDataModel(cell.getX(), cell.getY(), cellColor, cell.getStatus(), null);
                }
                
                dataToSave.getCells().add(cellDataModel);
            }
        }
        
        String json = new Gson().toJson(dataToSave);
        
        PrintWriter writer = new PrintWriter(file);
        writer.println(json);
        writer.close();
        
        log.info("Saved simulation to file: {}", file.getName());
    }
    
}
