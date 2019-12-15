package com.mk.multiscalemodeling.project1.simulation.mc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Grain;
import com.mk.multiscalemodeling.project1.model.GrainStatus;
import com.mk.multiscalemodeling.project1.simulation.GrainsManager;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MCRecrystallisationController {

    private SimulationManager simulationManager;
    
    private Random rand = new Random();
    
    public MCRecrystallisationController(SimulationManager simulationManager, GrainsManager grainsManager) {
        this.simulationManager = simulationManager;
    }
    
    public void simulateRecristalization(RecrystallisedLocationType locationType, RecrystallisedNucleatingType nucleatingType, int noOfNucleons, 
            int noOfIterations, double grainBoundaryEnergy) {
        
        for (int i = 0; i < noOfIterations; i++) {
            int newNucleons = noOfNucleons;
            
            if (nucleatingType.equals(RecrystallisedNucleatingType.BEGINING) && i == 0) {
                simulationManager.addRecrystallisedNucleons(newNucleons, locationType);
            } else if (nucleatingType.equals(RecrystallisedNucleatingType.CONSTANT)) {
                simulationManager.addRecrystallisedNucleons(newNucleons, locationType);
            } else if (nucleatingType.equals(RecrystallisedNucleatingType.INCREASING)) {
                newNucleons += noOfNucleons;
                simulationManager.addRecrystallisedNucleons(newNucleons, locationType);
            }
            
            recristallise(grainBoundaryEnergy);
        }
    }

    
    private void recristallise(double grainBoundaryEnergy) {
        List<Cell> randomCells = simulationManager.getListOfRandomCells().stream()
                .filter(cell -> cell.getStatus().equals(CellStatus.OCCUPIED)).collect(Collectors.toList());
        List<Pair<Cell, Grain>> cell2update = new ArrayList<>();
        
        while (!randomCells.isEmpty()) {
            Cell cell = randomCells.remove(0);
            List<Cell> neighbourhood = getMooreNeighbourhoodWithRecristallisedGrains(cell);
            
            if (CollectionUtils.isEmpty(neighbourhood)) {
                log.trace("Cell {} hasn't recristalliesed neighbours", cell);
                continue;
            }
            
            Grain orginalGrain = cell.getGrain();
            Grain grainAfterReorientation = neighbourhood.get(rand.nextInt(neighbourhood.size())).getGrain();
            
            double energyBefore = 8;
            double energyAfter = 8;
            
            for (Cell neighbor : neighbourhood) {
                if (neighbor.getGrain().equals(orginalGrain)) {
                    energyBefore--; 
                } else if (neighbor.getGrain().equals(grainAfterReorientation)) {
                    energyAfter--;
                } 
            }
                    
            energyBefore = grainBoundaryEnergy * energyBefore + cell.getEnergy();
            energyAfter = grainBoundaryEnergy * energyAfter + cell.getEnergy();
            log.info("Energy before: {}, energy after: {}", energyBefore, energyAfter);
            if (energyAfter - energyBefore <= 0.0) {
                //recrystallization
                cell2update.add(Pair.of(cell, grainAfterReorientation));
            }
        }
        
        for (Pair<Cell, Grain> cell2Grain : cell2update) {
            cell2Grain.getKey().setGrain(cell2Grain.getRight());
            cell2Grain.getKey().setEnergy(0.0);
        }
    }
    
    private List<Cell> getMooreNeighbourhoodWithRecristallisedGrains(Cell cell) {
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
                && neighbour.getGrain() != null && neighbour.getGrain().getStatus().equals(GrainStatus.RECRYSTALLISED));
    }
}
