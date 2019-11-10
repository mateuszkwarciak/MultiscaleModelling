package com.mk.multiscalemodeling.project1.model.InclusionShape;

import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.Inclusion;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Square implements InclusionType {

    private SimulationManager simulationManager;
    
    public Square() {
        simulationManager = JavaFxBridge.applicationContext.getBean(SimulationManager.class);
    }
    
    @Override
    public void genearteInclusion(Cell startCell, Inclusion inclusion, int sizeOfInclusion) {  
        double side = ((double) sizeOfInclusion) / Math.sqrt(2.0);
        int halfSizeA;
        int halfSizeB;
        if (side <= 1) {
            halfSizeA = 1;
            halfSizeB = 0;
        } else if (side <= 2) {
            halfSizeA = 1;
            halfSizeB = 1;
        } else {
            int roundedSide = (int) Math.round(side);
            halfSizeA = (int) roundedSide / 2;
            
            if (((int) roundedSide % halfSizeA) == 1) {
                halfSizeB = halfSizeA + 1;
            } else {
                halfSizeB = halfSizeA;
            }  
        }
         
        log.trace("SideLength: {}, halfA: {}, halfB: {}", side, halfSizeA, halfSizeB);
        
        for (int i = startCell.getX() - halfSizeA; i < startCell.getX() + halfSizeB; i++) {
            for (int j = startCell.getY() - halfSizeA; j < startCell.getY() + halfSizeB; j++) {
                if (simulationManager.isInSimulationRange(i, j)) {
                    log.info("I{}, J {}", i, j);
                    tryMarkCell(simulationManager.getCells()[i][j], inclusion);
                }
            }
        }
        
    }

}
