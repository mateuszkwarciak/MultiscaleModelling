package com.mk.multiscalemodeling.project1;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootConfiguration
@ComponentScan({"com.mk.multiscalemodeling.project1.model", "com.mk.multiscalemodeling.project1.simulation", 
    "com.mk.multiscalemodeling.project1.controllers"})
@PropertySource(value = { "application.properties" })
public class Project1Configuration {

}
