package com.mk.multiscalemodeling.project1.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.GrainImpl;
import com.mk.multiscalemodeling.project1.model.GrainStatus;
import com.mk.multiscalemodeling.project1.model.Inclusion;
import com.mk.multiscalemodeling.project1.simulation.GrainsManager;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;
import com.mk.multiscalemodeling.project1.simulation.SimulationStatus;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Importer {
    
    private GrainsManager grainsManager;
    private SimulationManager simulationManager;
    
    public Importer() {
        grainsManager = JavaFxBridge.applicationContext.getBean(GrainsManager.class);
        simulationManager = JavaFxBridge.applicationContext.getBean(SimulationManager.class);
    }
    
    public void importFromImage(File file) throws IOException {
        Image image = new Image(file.toURI().toString());
        PixelReader pixelReader = image.getPixelReader();
        
        Map<Color, GrainImpl> color2grain = new HashMap<>();
        List<GrainImpl> grains = new ArrayList<>();
        
        List<Inclusion> inclusions = new ArrayList<>();
        Inclusion collectiveInclusion = new Inclusion();
        inclusions.add(collectiveInclusion);
        
        int width = (int) image.getWidth();
        int hight = (int) image.getHeight();
        log.info("Loading simulation with parameters width: {} hight: {}", width, hight);
        
        Cell cells[][] = new Cell[width + 2][hight + 2];
        
        for (int i = 0; i < (width + 2); i ++) {
            for (int j = 0; j < (hight + 2); j++) {
                if  (i == 0 || j == 0 || i == (width + 1) || j == (hight + 1)) {
                    cells[i][j] = new Cell(CellStatus.ABSORBING, i, j);
                } else {
                    Color color = pixelReader.getColor(i - 1, j - 1);
                    
                    //Empty cells
                    if (color.equals(Cell.EMPTY_CELL_COLOR)) {
                        cells[i][j] = new Cell(CellStatus.EMPTY, i, j);
                    //Inclusions
                    } else if (color.equals(Inclusion.COLOR)) {
                        cells[i][j] = new Cell(CellStatus.OCCUPIED, i, j);
                        cells[i][j].setGrain(collectiveInclusion);
                    //Grains
                    } else {
                        GrainImpl grain = null;
                        //if grain of this color does not exist
                        if(!color2grain.containsKey(color)) {
                            //create new grain
                            grain = new GrainImpl(GrainStatus.GRAIN, color);
                            
                            //save
                            color2grain.put(color, grain);
                            grains.add(grain);
                        } else {
                            grain = color2grain.get(color);
                        }
                        
                        cells[i][j] = new Cell(CellStatus.OCCUPIED, i, j);
                        cells[i][j].setGrain(grain);
                    }
                }
            }
        }
        
        grainsManager.setColor2grain(color2grain);
        grainsManager.setGrains(grains);
        grainsManager.setInclusions(inclusions);
        
        simulationManager.setCells(cells);
        simulationManager.init(SimulationStatus.LOADED, width, hight);
        
        log.info("Simulation loaded");
    }
    
}
