package com.mk.multiscalemodeling.project1.simulation;

import java.io.Serializable;
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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GrainsManager implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Setter
    @Getter
    private Map<Color, GrainImpl> color2grain = new HashMap<>();
    @Setter
    private List<GrainImpl> grains = new ArrayList<>();
    @Setter
    private List<Inclusion> inclusions = new ArrayList<>();

    public List<Grain> createNeuclons(int count) {
        if (count < 0) {
            log.warn("Wrong number of neuclons to add ({})", count);
        }
        
        List<Grain> neuclons = new ArrayList<>();
        IntStream.rangeClosed(0, count - 1).forEach(e -> {
            neuclons.add(createNeuclon());
        });
        
        return neuclons;
    }

    public Grain createNeuclon() {
        Color neuclonColor = getRandomColor();
        while (color2grain.containsKey(neuclonColor) || neuclonColor.equals(Inclusion.COLOR)) {
            neuclonColor = getRandomColor();
        }
        
        GrainImpl neuclon = new GrainImpl(GrainStatus.GRAIN, neuclonColor);
        
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
    
    public List<GrainImpl> getGrains() {
        return grains;
    }
    
    public List<Inclusion> getInclusions() {
        return inclusions;
    }
    
    public GrainImpl getGrainByColor(Color color) {
        return color2grain.get(color);
    }
    
    private Color getRandomColor() {
        Random randomizer = new Random();
        int red = randomizer.nextInt(255);
        int green = randomizer.nextInt(255);
        int blue = randomizer.nextInt(255);
        
        return Color.rgb(red, green, blue);
    }
}
