package com.mk.multiscalemodeling.project1.simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mk.multiscalemodeling.project1.model.Border;
import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Grain;
import com.mk.multiscalemodeling.project1.model.GrainImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BorderService {

    private List<Border> borders;
    
    private SimulationManager simulationManager;
    
    public BorderService(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
        borders = new ArrayList<>();
    }
    
    public void addBorder(Set<GrainImpl> grains, int width) {
        if (grains != null) {
            grains.stream().forEach((grain) -> {addBorder(grain, width);});
        }
    }
    
    public void addBorder(GrainImpl grain, int width) {
        if (grain.getBorder() != null) {
            return;
        }
        
        Border border = new Border(grain);
        grain.setBorder(border);
        borders.add(border);
        for (int i = 0; i < width; i ++) {
            List<Cell> cellsOnBorder = new ArrayList<>();
            grain.getCells().stream().forEach((cell) -> {
                if (checkIfOnEdge(cell)) {
                    cellsOnBorder.add(cell);
                }
            });
            markCellsOnBorder(border, cellsOnBorder);
        }
        log.info("Added border for grain");
    }
    
    public void removeAllBorders() {
        while(!borders.isEmpty()) {
            removeBorder(borders.get(0));
        }
        log.info("");
    }
    
    public void removeBorder(Set<GrainImpl> grains) {
        if (grains != null) {
            grains.stream().forEach((grain) -> {removeBorder(grain.getBorder());});
        }
        log.info("Border set removed");
    }
    
    public void removeBorder(Border borderToRemove) {
        log.debug("Removing border");
        if (borderToRemove == null) {
            return;
        }
        
        List<Cell> cellsToEarse = borderToRemove.getCells(); 
        while (!cellsToEarse.isEmpty()) {
            cellsToEarse.remove(0).removeFromGrain();
        }
        
        borders.remove(borderToRemove);
        
        log.info("Border removed");
    }
    
    private void markCellsOnBorder(Border border, List<Cell> cellsToMark) {
        while (!cellsToMark.isEmpty()) {
            Cell cell = cellsToMark.remove(0);
            cell.setGrain((Grain) border);
            cell.setStatus(CellStatus.BORDER);
        }
    }
    
    private boolean checkIfOnEdge(Cell cell) {
        if (edgeCondition(cell, simulationManager.getFromAbove(cell)) || (edgeCondition(cell, simulationManager.getFromLeft(cell))) ||
                (edgeCondition(cell, simulationManager.getFromBelow(cell))) || (edgeCondition(cell, simulationManager.getFromRight(cell)))) {
            return true;
        }
        return false;
    }
    
    private boolean edgeCondition(Cell cell, Cell cellToCompare) {
        return (cellToCompare != null && ((cellToCompare.getGrain() == null) ||  
                (cellToCompare.getGrain() != null && (!cell.getGrain().getColor().equals(cellToCompare.getGrain().getColor())))));         
    }
}
