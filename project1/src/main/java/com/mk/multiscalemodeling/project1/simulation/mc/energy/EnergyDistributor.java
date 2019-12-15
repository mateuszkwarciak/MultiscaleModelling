package com.mk.multiscalemodeling.project1.simulation.mc.energy;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnergyDistributor {

    private SimulationManager simulationManager;
    
    private Random random = new Random();
    
    public EnergyDistributor(SimulationManager simulationManager) {
        this.simulationManager = simulationManager;
    }
    
    public void distributeEnergy(List<Cell> cells, EnergyType energyType, double threshold, double energyInside, double energyGB) {
        switch (energyType) {
        case HOMOGENOUS:
            log.info("Distribute energy with homogenous way (threshold: {}", threshold);
            distributeHomogoneus(cells, threshold, energyInside);
            break;
        case HETEROGENOUS:
            log.info("Distribute energy with heterogeneus way (threshold: {}", threshold);
            distributeHeterogeneus(cells, threshold, energyInside, energyGB);
            break;            
        default:
            log.warn("Unnown energy type");
        }
    }
    
    private void distributeHomogoneus(List<Cell> cells, double threshold, double energy) {
        for (Cell cell: cells) {
            double energyDispersion = -threshold + random.nextDouble() * threshold * 2;
            cell.setEnergy(energy + energy * energyDispersion);
        }
    }
    
    private void distributeHeterogeneus(List<Cell> cells, double threshold, double energyInside, double energyGB) {
        log.info("thr: {}, enIns {}, enDB {}", threshold, energyInside, energyGB);
        for (Cell cell: cells) {
            double energyDispersion = -threshold + random.nextDouble() * threshold * 2;
            if(simulationManager.checkIfOnGrainsEdge(cell)) {
                cell.setEnergy(energyGB + energyGB * energyDispersion); 
            } else {
                cell.setEnergy(energyInside + energyInside * energyDispersion);
            }
        }
    }

    public Pair<Double, Double> getMinMaxEnergy(List<Cell> listOfNonAbsorbingCells) {
        if (CollectionUtils.isEmpty(listOfNonAbsorbingCells)) {
            return null;
        }
        
        double min = listOfNonAbsorbingCells.get(0).getEnergy();
        double max = listOfNonAbsorbingCells.get(0).getEnergy();
        
        for (Cell cell: listOfNonAbsorbingCells) {
            if (cell.getEnergy() < min) {
                min = cell.getEnergy();
            }
            
            if (cell.getEnergy() > max) {
                max = cell.getEnergy();
            }
        }
        
        log.info("Returned min {} and max {} values of energy", min, max);
        return Pair.of(min, max);
    }
}
