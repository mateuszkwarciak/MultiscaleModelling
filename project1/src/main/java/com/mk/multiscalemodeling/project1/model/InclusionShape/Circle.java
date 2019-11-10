package com.mk.multiscalemodeling.project1.model.InclusionShape;

import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.Inclusion;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Circle implements InclusionType {

    private SimulationManager simulationManager;
    
    public Circle() {
        simulationManager = JavaFxBridge.applicationContext.getBean(SimulationManager.class);
    }
    @Override
    public void genearteInclusion(Cell startCell, Inclusion inclusion, int sizeOfInclusion) {
        int oX = startCell.getX();
        int oY = startCell.getY();
        
        for (int i = oX - sizeOfInclusion; i <= oX + sizeOfInclusion; i++) {
            for (int j = oY - sizeOfInclusion; j <= oY + sizeOfInclusion; j++) {
                if (simulationManager.isInSimulationRange(i, j)) {
                    int oXi = (int) Math.pow(Math.abs(oX - i), 2);
                    int oYj = (int) Math.pow(Math.abs(oY - j), 2);
                    double diagonal = oXi + oYj - Math.pow(sizeOfInclusion, 2);
                    
                    log.info("oxi {} oyj {} diagonal {}", oXi, oYj, diagonal);
                    if (diagonal < sizeOfInclusion) {
                        log.info("poszlo i {} j {}", i,j);
                        tryMarkCell(simulationManager.getCells()[i][j], inclusion);
                    }
                }
            }
        }
        
    }

}
