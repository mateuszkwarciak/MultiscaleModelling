package com.mk.multiscalemodeling.project1.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.mk.multiscalemodeling.project1.model.Grain;
import com.mk.multiscalemodeling.project1.model.GrainImpl;
import com.mk.multiscalemodeling.project1.model.GrainStatus;
import com.mk.multiscalemodeling.project1.model.Inclusion;

import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GrainsManager {

    private Map<Color, Grain> color2grain = new HashMap<>();
    private List<Grain> grains = new ArrayList<>();
    private List<Grain> inclusions = new ArrayList<>();

    public List<Grain> createNeuclons(int count) {
        if (count < 0) {
            log.warn("Wrong number of neuclons to add ({})", count);
        }
        
        List<Grain> neuclons = new ArrayList<>();
        IntStream.rangeClosed(0, count).forEach(e -> {
            neuclons.add(createNeuclon());
        });
        
        return neuclons;
    }

    public Grain createNeuclon() {
        Color neuclonColor = getRandomColor();
        while (color2grain.containsKey(neuclonColor)) {
            neuclonColor = getRandomColor();
        }
        
        GrainImpl neuclon = new GrainImpl(GrainStatus.NEUCLON, neuclonColor);
        
        color2grain.put(neuclonColor, neuclon);
        grains.add(neuclon);
        
        return neuclon;
    }
    
    public List<Grain> createInclusion(int count) {
        if (count < 0) {
            log.warn("Wrong number of inclusions to add ({})", count);
        }
        
        List<Grain> inclusions = new ArrayList<>();
        IntStream.rangeClosed(0, count).forEach(e -> {
            inclusions.add(createInclusion());
        });
        
        return inclusions;
    }
    
    public Grain createInclusion() {        
        Inclusion inclusion = new Inclusion();
        inclusions.add(inclusion);
        
        return inclusion;
    }
    
    public List<Grain> getGrains() {
        return grains;
    }
    
    public List<Grain> getInclusions() {
        return inclusions;
    }
    
    public Grain getGrainByColor(Color color) {
        return color2grain.get(color);
    }
    
    /**
     * This function gets 2 grains and merging them with each other. The smaller grain is absorbed by larger grain
     * 
     * @param first grain
     * @param second grain
     * @return remained grain
     */
    public Grain mergeGrains(Grain first, Grain second) {
        //TODO:
        return null;
    }
    
    private Color getRandomColor() {
        Random randomizer = new Random();
        int red = randomizer.nextInt(255);
        int green = randomizer.nextInt(255);
        int blue = randomizer.nextInt(255);
        
        return Color.rgb(red, green, blue);
    }
}
