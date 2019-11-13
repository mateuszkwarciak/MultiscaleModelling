package com.mk.multiscalemodeling.project1.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.mk.multiscalemodeling.project1.JavaFxBridge;
import com.mk.multiscalemodeling.project1.model.Border;
import com.mk.multiscalemodeling.project1.model.Cell;
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
public class GrainsManager {

    //TODO: merge color2grain and grains into one collection
    @Setter
    @Getter
    private Map<Color, GrainImpl> color2grain = new HashMap<>();
    @Setter
    private List<GrainImpl> grains = new ArrayList<>();
    //TODO: merge inclusions and id2Inclusion into one collection
    @Setter
    private List<Inclusion> inclusions = new ArrayList<>();
    @Setter
    @Getter
    private Map<String, Inclusion> id2Inclusion = new HashMap<>();
    
    private BorderService borderService;

    public void init(SimulationManager simulationManager) {
        borderService = new BorderService(simulationManager);
    }
    
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
        while (color2grain.containsKey(neuclonColor) || neuclonColor.equals(Inclusion.COLOR) || neuclonColor.equals(Border.COLOR)) {
            neuclonColor = getRandomColor();
        }
        
        GrainImpl neuclon = new GrainImpl(GrainStatus.GRAIN, neuclonColor);
        
        color2grain.put(neuclonColor, neuclon);
        grains.add(neuclon);
        
        return neuclon;
    }
    
    public List<Inclusion> createInclusion(int count) {
        if (count < 0) {
            log.warn("Wrong number of inclusions to add ({})", count);
        }
        
        List<Inclusion> inclusions = new ArrayList<>();
        IntStream.rangeClosed(0, count - 1).forEach(e -> {
            inclusions.add(createInclusion());
        });
        
        return inclusions;
    }
    
    public Inclusion createInclusion() {        
        Inclusion inclusion = new Inclusion();
        inclusions.add(inclusion);
        id2Inclusion.put(inclusion.getInclusionId(), inclusion);
        
        return inclusion;
    }
    
    public void addBorder(Set<GrainImpl> grains, int width) {
        borderService.addBorder(grains, width);
    }
    
    public void addBorderForAllGrains(int width) {
        borderService.addBorder(new HashSet<>(grains), width);
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
    
    public void removeAllGrains() {
        for (Grain grain : grains) {
            List<Cell> cells = grain.getCells();
            while (!cells.isEmpty()) {
                cells.get(0).removeFromGrain();
            }
        } 
        
        grains = new ArrayList<>();
        color2grain = new HashMap<>();
    }
    
    public void removeGrains(Set<GrainImpl> grainsToRemove) {
        for (GrainImpl grain : grainsToRemove) {
            List<Cell> cells = grain.getCells();
                while(!cells.isEmpty()) {
                    cells.get(0).removeFromGrain();
                }
            grains.remove(grain);
            color2grain.remove(grain.getColor());
        }
    }
    
    public void removeExceptSelected(Set<GrainImpl> selectedGrains) {
        for (Grain grain : grains) {
            if (selectedGrains.contains(grain)) {
                continue;
            }
            
            List<Cell> cells = grain.getCells();
            while (!cells.isEmpty()) {
                cells.get(0).removeFromGrain();
            }
            
        }
        
        grains = new ArrayList<>(selectedGrains);
        color2grain = grains.stream().collect(Collectors.toMap(GrainImpl::getColor, Function.identity()));
    }
    
    public void removeAllInclusions() {
        for (Inclusion inclusion : inclusions) {
            List<Cell> cells = inclusion.getCells();
            while (!cells.isEmpty()) {
                cells.get(0).removeFromGrain();
            }
        }
        
        inclusions = new ArrayList<>();
        id2Inclusion = new HashMap<>();
    }
    
    public void mergeSelectedGrains(Set<GrainImpl> selectedGrains) {
        if (selectedGrains.size() < 2) {
            return;
        }
        
        GrainImpl mainGrain = (GrainImpl) createNeuclon();
        
        for (GrainImpl grain : selectedGrains) {
            List<Cell> cells = grain.getCells();
            while(!cells.isEmpty()) {
                cells.get(0).setGrain(mainGrain);
            }
        }
        
        removeGrains(selectedGrains);
    }
    
    public void clearAll() {
        removeAllGrains();
        removeAllInclusions();
    }
    
    private Color getRandomColor() {
        Random randomizer = new Random();
        int red = randomizer.nextInt(255);
        int green = randomizer.nextInt(255);
        int blue = randomizer.nextInt(255);
        
        return Color.rgb(red, green, blue);
    }
}
