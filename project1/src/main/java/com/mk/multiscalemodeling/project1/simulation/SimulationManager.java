package com.mk.multiscalemodeling.project1.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Grain;
import com.mk.multiscalemodeling.project1.model.neighbourdhood.Neighbourhood;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Component
public class SimulationManager {

    private SimulationStatus simulationStatus;
    private GrainsManager grainsManager;
    
    @Getter private int dimX;
    @Getter private int dimY;
    
    private Cell cells[][];
    
    public void init(SimulationStatus simulationStatus, int dimX, int dimY) {
        this.simulationStatus = simulationStatus;
        this.dimX = dimX;
        this.dimY = dimY;
        
        grainsManager = JavaFxBridge.applicationContext.getBean(GrainsManager.class);
        
        initCells();
        log.info("Initialize sumulation with params dimX{}, dimY {}", dimX, dimY);
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
    
    public boolean simulateGrowth(Neighbourhood neighbourhoodType) {
        boolean hasGrown = false;
        List<Pair<Cell, Grain>> cellsToUpdate = new ArrayList<>();
         
        for (int i = 1; i < dimX; i ++) {
            for (int j = 1; j < dimY; j++) {
                if (cells[i][j].getStatus().equals(CellStatus.EMPTY)) {
                    Grain matchedGrain = neighbourhoodType.tryToMatchTheGrain(cells[i][j], cells);
                    if (matchedGrain != null) {
                        cellsToUpdate.add(Pair.of(cells[i][j], matchedGrain));
                    }
                }
            }
        }
        
        hasGrown = !cellsToUpdate.isEmpty();

        cellsToUpdate.stream().forEach((e) -> {
            updateCells(e.getLeft(), e.getRight());
        });
        
        return hasGrown;
    }
    
    public Cell[][] getCells() {
        return cells;
    }
    
    private void updateCells(Cell cellToUpdate, Grain grain) {
        cellToUpdate.setGrain(grain);
        cellToUpdate.setStatus(CellStatus.OCCUPIED);  
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
