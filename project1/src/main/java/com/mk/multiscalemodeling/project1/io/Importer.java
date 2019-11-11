package com.mk.multiscalemodeling.project1.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.io.jsonModels.JsonSImulationModel;
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
    
    public void importFromJson(File file) throws IOException{
        InputStream inputStream = new FileInputStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        
        String singleLine = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        
        while(singleLine != null) {
            stringBuilder.append(singleLine);
            singleLine = bufferedReader.readLine();
        }
        
        bufferedReader.close();
        
        String json = stringBuilder.toString();
        
        JsonSImulationModel dataToImport = new Gson().fromJson(json, JsonSImulationModel.class);
        
        Map<Color, GrainImpl> color2grain = new HashMap<>();
        List<GrainImpl> grains = new ArrayList<>();
        Map<String, Inclusion> id2Inclusion = new HashMap<>();
        List<Inclusion> inclusions = new ArrayList<>();
        
        int width = (int) dataToImport.getWidth();
        int hight = (int) dataToImport.getHight();
        log.info("Loading simulation with parameters width: {} hight: {}", width, hight);
        
        dataToImport.getGrains().stream().forEach((e) -> {
            GrainStatus status = e.getGrainStatus();
            Color loadedColor = Color.color(e.getColor().getRed(), e.getColor().getGreen(), e.getColor().getBlue());
            GrainImpl grain = new GrainImpl(status, loadedColor);
            color2grain.put(loadedColor, grain);
            grains.add(grain);
        });
        
        dataToImport.getInclusionsId().stream().forEach((e) -> {
            Inclusion inclusion = new Inclusion(e);
            inclusions.add(inclusion);
            id2Inclusion.put(e, inclusion);
        });
        
        Cell cells[][] = new Cell[width + 2][hight + 2];
        for (int i = 0; i < (width + 2); i ++) {
            for (int j = 0; j < (hight + 2); j++) {
                if  (i == 0 || j == 0 || i == (width + 1) || j == (hight + 1)) {
                    cells[i][j] = new Cell(CellStatus.ABSORBING, i, j);
                }
            }
        }
        
        dataToImport.getCells().stream().forEach((e) -> {
            int x = e.getX();
            int y = e.getY();
            Cell cell = new Cell(e.getStatus(), x, y);
            
            if (e.getStatus().equals(CellStatus.OCCUPIED)) {
                //Grain
                Color color = Color.color(e.getColor().getRed(), e.getColor().getGreen(), e.getColor().getBlue());
                cell.setGrain(color2grain.get(color)); 
            } else if (e.getStatus().equals(CellStatus.INCLUSION)) {
                //Inclusion
                Inclusion inclusion = id2Inclusion.get(e.getInclusionId());
                cell.setGrain(inclusion);
            }
            
            cells[x][y] = cell;
        });
        
        grainsManager.setColor2grain(color2grain);
        grainsManager.setGrains(grains);
        grainsManager.setInclusions(inclusions);
        
        simulationManager.setCells(cells);
        simulationManager.init(SimulationStatus.LOADED, width, hight);
        
        log.info("Simulation loaded");
    }
    
}
