package com.mk.multiscalemodeling.project1.simulation.mc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Grain;
import com.mk.multiscalemodeling.project1.model.GrainStatus;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

public class MonteCarloController {

    SimulationManager simulationManager;
    
    Random rand = new Random();
    
    public MonteCarloController(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
    }
    
    public Grain resolveCellMembership(Cell selectedCell, double grainBoundaryEnergy) {
        List<Cell> neighbourhoodCells = getMooreNeighbourhood(selectedCell);
        if (CollectionUtils.isEmpty(neighbourhoodCells) || (selectedCell.getGrain() != null 
                && selectedCell.getGrain().getStatus().equals(GrainStatus.FROZEN))) {
            return selectedCell.getGrain();
        }
        
        Grain orginalGrain = selectedCell.getGrain();
        List<Grain> uniqueGrains = new ArrayList<>(neighbourhoodCells.stream().map(cell -> cell.getGrain()).filter(Objects::nonNull)
                .filter(grain -> !grain.equals(orginalGrain)).collect(Collectors.toSet()));
        
        if (CollectionUtils.isEmpty(uniqueGrains)) {
            return orginalGrain;
        }
        Grain grainForSubstitution = uniqueGrains.get(rand.nextInt(uniqueGrains.size()));
        
        double energyBefore = 8;
        double energyAfter = 8;
        for (Cell neighbor : neighbourhoodCells) {
            if (neighbor.getGrain().equals(orginalGrain)) {
                energyBefore--; 
            } else if (neighbor.getGrain().equals(grainForSubstitution)) {
                energyAfter--;
            } 
        } 
        
        energyBefore = energyBefore * grainBoundaryEnergy;
        energyAfter = energyAfter * grainBoundaryEnergy;
        
        if ((energyAfter - energyBefore) <= 0) {
            return grainForSubstitution;
        } else {
            return orginalGrain;
        }  
    }

    private List<Cell> getMooreNeighbourhood(Cell cell) {
        List<Cell> mooreNeighbourhood = new ArrayList<>();
        
        Cell neighbor = simulationManager.getFromAbove(cell);
        if (neighborCondition(neighbor)) {
            mooreNeighbourhood.add(neighbor);
        }
        
        neighbor = simulationManager.getFromAboveLeft(cell);
        if (neighborCondition(neighbor)) {
            mooreNeighbourhood.add(neighbor);
        }
        
        neighbor = simulationManager.getFromLeft(cell);
        if (neighborCondition(neighbor)) {
            mooreNeighbourhood.add(neighbor);
        }
        
        neighbor = simulationManager.getFromBelowLeft(cell);
        if (neighborCondition(neighbor)) {
            mooreNeighbourhood.add(neighbor);
        }
        
        neighbor = simulationManager.getFromBelow(cell);
        if (neighborCondition(neighbor)) {
            mooreNeighbourhood.add(neighbor);
        }
        
        neighbor = simulationManager.getFromBelowRight(cell);
        if (neighborCondition(neighbor)) {
            mooreNeighbourhood.add(neighbor);
        }
        
        neighbor = simulationManager.getFromRight(cell);
        if (neighborCondition(neighbor)) {
            mooreNeighbourhood.add(neighbor);
        }
        
        neighbor = simulationManager.getFromAboveRight(cell);
        if (neighborCondition(neighbor)) {
            mooreNeighbourhood.add(neighbor);
        }
        
        return mooreNeighbourhood;
    }
    
    private boolean neighborCondition(Cell neighbour) {
        return (neighbour != null && neighbour.getStatus().equals(CellStatus.OCCUPIED) 
                && neighbour.getGrain() != null && neighbour.getGrain().getStatus().equals(GrainStatus.GRAIN));
    }
}
