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
    
    @Getter
    private Map<Color, GrainImpl> color2recrystallisedGrain = new HashMap<>();
    
    @Setter
    private List<GrainImpl> grains = new ArrayList<>();
    //TODO: merge inclusions and id2Inclusion into one collection
    @Setter
    private List<Inclusion> inclusions = new ArrayList<>();
    @Setter
    @Getter
    private Map<String, Inclusion> id2Inclusion = new HashMap<>();
    
    @Getter
    private List<GrainImpl> recrystallisedGrains = new ArrayList<>();
    
    @Getter
    BorderService borderService;

    public void init(SimulationManager simulationManager) {
        borderService = new BorderService(simulationManager);
    }
    
    public List<Grain> createNeuclons(int count, boolean rescrystallised) {
        if (count < 0) {
            log.warn("Wrong number of neuclons to add ({})", count);
        }
        
        List<Grain> neuclons = new ArrayList<>();
        if (rescrystallised) {
            IntStream.rangeClosed(0, count - 1).forEach(e -> {
                neuclons.add(createRecrystallisedNeuclon());
            });
        } else {
            IntStream.rangeClosed(0, count - 1).forEach(e -> {
                neuclons.add(createNeuclon());
            });
        }
 
        return neuclons;
    }

    public Grain createNeuclon() {
        Color neuclonColor = getRandomColorForGrain();
        while (color2grain.containsKey(neuclonColor)) {
            neuclonColor = getRandomColorForGrain();
        }
        
        GrainImpl neuclon = new GrainImpl(GrainStatus.GRAIN, neuclonColor);
        
        color2grain.put(neuclonColor, neuclon);
        grains.add(neuclon);
        
        return neuclon;
    }
    
    public Grain createRecrystallisedNeuclon() {
        Color recrystallisedNeuclonColor = getRandomColorForRecrystallisedGrain();
        while (color2recrystallisedGrain.containsKey(recrystallisedNeuclonColor)) {
            recrystallisedNeuclonColor = getRandomColorForGrain();
        }
        
        GrainImpl recrystallisedNeuclon = new GrainImpl(GrainStatus.RECRYSTALLISED, recrystallisedNeuclonColor);
        
        color2recrystallisedGrain.put(recrystallisedNeuclonColor, recrystallisedNeuclon);
        recrystallisedGrains.add(recrystallisedNeuclon);
        grains.add(recrystallisedNeuclon);
        
        return recrystallisedNeuclon;
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
        recrystallisedGrains = new ArrayList<>();
        color2grain = new HashMap<>();
        color2recrystallisedGrain = new HashMap<>();
    }
    
    public void removeGrains(Set<GrainImpl> grainsToRemove, boolean removeBorder) {
        for (GrainImpl grain : grainsToRemove) {
            
            if (removeBorder) {
                log.info("Usuwamy granice");
                borderService.removeBorder(((GrainImpl)grain).getBorder());      
            }
            
            List<Cell> cells = grain.getCells();
                while(!cells.isEmpty()) {
                    cells.get(0).removeFromGrain();
                }
            
            grains.remove(grain);
            color2grain.remove(grain.getColor());
            color2recrystallisedGrain.remove(grain.getColor());
            recrystallisedGrains.remove(grain);
        }
    }
    
    public void removeExceptSelected(Set<GrainImpl> selectedGrains, boolean removeBorder) {
        for (Grain grain : grains) {
            if (selectedGrains.contains(grain)) {
                continue;
            }
            
            if (removeBorder) {
                borderService.removeBorder(((GrainImpl)grain).getBorder());      
            }
            
            List<Cell> cells = grain.getCells();
            while (!cells.isEmpty()) {
                cells.get(0).removeFromGrain();
            }
            
            recrystallisedGrains.remove(grain);
            color2recrystallisedGrain.remove(grain.getColor());
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
    
    public void removeAllBorders() {
        borderService.removeAllBorders();
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
        
        removeGrains(selectedGrains, false);
    }
    
    public void clearAll() {
        removeAllGrains();
        removeAllInclusions();
    }
    
    private Color getRandomColorForGrain() {
        Random randomizer = new Random();
        int red = randomizer.nextInt(11);  //values from 0 to 10
        int green = randomizer.nextInt(245) + 11; // values from 11 to 255
        int blue = randomizer.nextInt(245) + 11; // values from 11 to 255
        
        return Color.rgb(red, green, blue);
    }
    
    private Color getRandomColorForRecrystallisedGrain() {
        Random randomizer = new Random();
        int red = randomizer.nextInt(245) + 11; // values from 11 to 255
        int green = randomizer.nextInt(11); //values from 0 to 10
        int blue = randomizer.nextInt(11); //values from 0 to 10
        
        return Color.rgb(red, green, blue);
    }
}
