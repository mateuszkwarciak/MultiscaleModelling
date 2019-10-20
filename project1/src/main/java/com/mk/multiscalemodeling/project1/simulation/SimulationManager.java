package com.mk.multiscalemodeling.project1.simulation;

import org.springframework.stereotype.Component;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Component
public class SimulationManager {

    private SimulationStatus simulationStatus;
    
    private int dimX;
    private int dimY;
    
    public void init(SimulationStatus simulationStatus, int dimX, int dimY) {
        this.simulationStatus = simulationStatus;
        this.dimX = dimX;
        this.dimY = dimY;
        
        log.trace("Initialize sumulation with params{}", this.toString());
    } 
}
