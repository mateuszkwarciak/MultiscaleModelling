package com.mk.multiscalemodeling.project1;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.mk.multiscalemodeling.project1.model.GrainsManager;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

@SpringBootConfiguration
@ComponentScan({"com.mk.multiscalemodeling.project1.model", "com.mk.multiscalemodeling.project1.simulation"})
public class Project1Configuration {

	@Bean
	public GrainsManager getGrainsManager() {
		return new GrainsManager();
	}

	@Bean
	public SimulationManager getSimulationManager() {
	    return new SimulationManager();
	}
	
}
