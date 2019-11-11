package com.mk.multiscalemodeling.project1.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Grain;
import com.mk.multiscalemodeling.project1.model.GrainImpl;
import com.mk.multiscalemodeling.project1.model.Inclusion;
import com.mk.multiscalemodeling.project1.model.InclusionShape.InclusionType;
import com.mk.multiscalemodeling.project1.model.neighbourdhood.Neighbourhood;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Component
public class SimulationManager {

    private SimulationStatus simulationStatus;
    private GrainsManager grainsManager;
    
    @Getter 
    private int dimX;
    @Getter 
    private int dimY;
    
    @Getter
    private boolean fullGrown;
    
    @Getter
    @Setter
    private Cell cells[][];
    
    public void init(SimulationStatus simulationStatus, int dimX, int dimY) {
        this.simulationStatus = simulationStatus;
        this.dimX = dimX;
        this.dimY = dimY;
        
        grainsManager = JavaFxBridge.applicationContext.getBean(GrainsManager.class);
        
        if (this.simulationStatus.equals(SimulationStatus.NEW)) {
            initCells();
            log.info("Initialize simulation with params dimX{}, dimY {}", dimX, dimY);
        } else {
            log.info("Initialize loaded simulation with params dimX{}, dimY {}", dimX, dimY);
        }
    }
    
    public void addNucleonsToSimulation(int count) {
        List<Grain> nucleonsToAdd = grainsManager.createNeuclons(count);
        
        log.debug("Adding {} neuclons to simulation", count);
        
        Random rand = new Random();
        while (!nucleonsToAdd.isEmpty()) {
            // add 1 to move away from the absorbing edge
            int randomX = 1 + rand.nextInt(dimX);
            int randomY = 1 + rand.nextInt(dimY);
            
            Cell selectedCell = cells[randomX][randomY];
            
            if (selectedCell.getStatus().equals(CellStatus.EMPTY)) {
                selectedCell.setStatus(CellStatus.OCCUPIED);
                selectedCell.setGrain(nucleonsToAdd.remove(0));;
                log.trace("Nucleon added to cell array ({},{})", randomX, randomY);
            }
        }
    }
    
    public void addInclusionsToSimulation(int count, int sizeOfInclusion, InclusionType shape) {
        log.info("Adding {} inclusions to simulation", count);
        
        Random rand = new Random();
        List<Inclusion> inclusions = grainsManager.createInclusion(count);
        
        while (!inclusions.isEmpty()) {
            // add 1 to move away from the absorbing edge
            int randomX = 1 + rand.nextInt(dimX);
            int randomY = 1 + rand.nextInt(dimY);

            Cell selectedCell = cells[randomX][randomY];
            if (selectedCell.getStatus().equals(CellStatus.EMPTY)) {
                shape.genearteInclusion(selectedCell, inclusions.remove(0), sizeOfInclusion);
                log.debug("Adding inclusions to simulation(on empty cell) ({}, {})", randomX, randomY);
            } else if (selectedCell.getStatus().equals(CellStatus.OCCUPIED)) {
                if (checkIfOnGrainsEdge(selectedCell)) {
                    shape.genearteInclusion(selectedCell, inclusions.remove(0), sizeOfInclusion);
                    log.debug("Adding inclusions to simulation (on grains edge) ({}, {})", randomX, randomY);
                }
            }

            log.trace("Inclusion added to cell array ({},{})", randomX, randomY);
        }

    }
    
    private boolean checkIfOnGrainsEdge(Cell cellToCheck) {
        int x = cellToCheck.getX();
        int y = cellToCheck.getY();
        
        if (cellToCheck.getGrain() == null) {
            return false;
        } 
        
        Grain grain = cellToCheck.getGrain();
        for (int i = (x - 1); i <= (x + 1); i++) {
            for (int j = (y - 1); j <= (y + 1); j++) {
                if ((i == x && y == j) || !isInSimulationRange(i, j) ) {
                    continue;
                }

                Cell cell = cells[i][j];
                if (cell.getStatus().equals(CellStatus.ABSORBING) || cell.getStatus().equals(CellStatus.INCLUSION)) {
                    continue;
                }
                
                if (cell.getStatus().equals(CellStatus.EMPTY) || (!cell.getGrain().equals(grain))) {
                    return true;
                }
            }
        }
        
        return false;
    } 
    
    public boolean simulateGrowth(Neighbourhood neighbourhoodType) {
        boolean hasGrown = false;
        List<Pair<Cell, Grain>> cellsToUpdate = new ArrayList<>();
        log.debug("Perform 1 iteration of simulation");
        for (int i = 1; i < dimX + 1; i ++) {
            for (int j = 1; j < dimY + 1; j++) {
                if (cells[i][j].getStatus().equals(CellStatus.EMPTY)) {
                    Grain matchedGrain = neighbourhoodType.tryToMatchTheGrain(cells[i][j], cells);
                    if (matchedGrain != null) {
                        cellsToUpdate.add(Pair.of(cells[i][j], matchedGrain));
                    }
                }
            }
        }
        
        hasGrown = !cellsToUpdate.isEmpty();
        
        log.trace("Update cells");
        cellsToUpdate.stream().forEach((e) -> {
            updateCells(e.getLeft(), e.getRight());
        });
        
        return hasGrown;
    }
    
    public boolean isInSimulationRange(int i, int j) {
        return ((i > 0) && (i < (dimX + 2)) && (j > 0) && (j < (dimY + 2)));
    }
    
    public void clearSimulation() {
        grainsManager.clearAll();
        initCells();
    }
    
    public void removeSelectedGrains(Set<GrainImpl> grainsToRemove) {
        grainsManager.removeGrains(grainsToRemove);
    }
    
    public void removeExceptSelectedGrains(Set<GrainImpl> selectedGrains) {
        grainsManager.removeExceptSelected(selectedGrains);
    }
    
    public void mergeSelectedGrains(Set<GrainImpl> selectedGrains) {
        grainsManager.mergeSelectedGrains(selectedGrains);
    }
    
    private void updateCells(Cell cellToUpdate, Grain grain) {
        cellToUpdate.setGrain(grain);
        cellToUpdate.setStatus(CellStatus.OCCUPIED);      
    }
    
    private boolean checkIfFullyGrown() {
        boolean emptyCell = false;
        for (int i = 1; i <+ dimX; i ++) {
            for (int j = 1; j <+ dimY; j++) {
                if (cells[i][j].getStatus().equals(CellStatus.EMPTY)) {
                    emptyCell = true;
                }
            }
        }
        
        return fullGrown = !emptyCell;
    }
    
    private void initCells() {
        cells = new Cell[dimX + 2][dimY + 2];
        for (int i = 0; i < (dimX + 2); i ++) {
            for (int j = 0; j < (dimY + 2); j++) {
                if  (i == 0 || j == 0 || i == (dimX + 1) || j == (dimY + 1)) {
                    cells[i][j] = new Cell(CellStatus.ABSORBING, i, j);
                } else {
                    cells[i][j] = new Cell(CellStatus.EMPTY, i, j);
                }
            }
        }
    }
}
