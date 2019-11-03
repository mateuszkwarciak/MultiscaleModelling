package com.mk.multiscalemodeling.project1.simulation;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Grain;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Component
public class SimulationManager {

    private SimulationStatus simulationStatus;
    
    @Autowired
    private GrainsManager grainsManager;
    
    @Getter private int dimX;
    @Getter private int dimY;
    
    private Cell cells[][];
    
    public void init(SimulationStatus simulationStatus, int dimX, int dimY) {
        this.simulationStatus = simulationStatus;
        this.dimX = dimX;
        this.dimY = dimY;
        
        initCells();
        log.trace("Initialize sumulation with params{}", this.toString());
    }
    
    public void addNucleonsToSimulation(int count) {
        List<Grain> nucleonsToAdd = grainsManager.createNeuclons(count);

        Random rand = new Random();
        while (!nucleonsToAdd.isEmpty()) {
            // add 1 to move away from the absorbing edge
            int randomX = 1 + rand.nextInt(dimX);
            int randomY = 1 + rand.nextInt(dimY);
            
            Cell selectedCell = cells[randomX][randomY];
            
            if (selectedCell.getStatus().equals(CellStatus.EMPTY)) {
                selectedCell.setStatus(CellStatus.OCCUPIED);
                selectedCell.setGrain(nucleonsToAdd.remove(0));;
            }
        }
    }
    
    public Cell[][] getCells() {
        return cells;
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
