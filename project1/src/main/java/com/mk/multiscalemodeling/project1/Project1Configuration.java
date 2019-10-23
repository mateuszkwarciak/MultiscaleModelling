package com.mk.multiscalemodeling.project1;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.mk.multiscalemodeling.project1.controllers.SimulationController;
import com.mk.multiscalemodeling.project1.controllers.SimulationParametersController;
import com.mk.multiscalemodeling.project1.controllers.StartController;
import com.mk.multiscalemodeling.project1.simulation.GrainsManager;
import com.mk.multiscalemodeling.project1.simulation.SimulationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootConfiguration
@Configuration
@ComponentScan({"com.mk.multiscalemodeling.project1.model", "com.mk.multiscalemodeling.project1.simulation"})
@PropertySource(value = { "application.properties" })
public class Project1Configuration {

	@Bean
	public GrainsManager getGrainsManager() {
	    log.info("Initializing Grains Manager");
		return new GrainsManager();
	}

	@Bean
	public SimulationManager getSimulationManager() {
	    log.info("Initializing Simulation Manager");
	    return new SimulationManager();
	}
	
	@Bean
	public StartController getStartController() {
	    log.info("Initializing: StartController");
	    return new StartController();
	}
	
	@Bean
	public SimulationController getSimulationController() {
	    log.info("Initializing: SimulationController");
	    return new SimulationController();
	}
	
	@Bean
	public SimulationParametersController getSimulationParametersController() {
	    log.info("Initializing: SimulationParametersController");
	    return new SimulationParametersController();
	}
	
}
