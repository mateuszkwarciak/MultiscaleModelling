package com.mk.multiscalemodeling.project1.model.neighbourdhood;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.Grain;
import com.mk.multiscalemodeling.project1.model.GrainStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShapeControl implements Neighbourhood {

    private int shapeControllRatio;
    
    public ShapeControl(int shapeControllRatio) {
        this.shapeControllRatio = shapeControllRatio;
    }

    @Override
    public Grain tryToMatchTheGrain(Cell targetCell, Cell[][] cells) {
        List<Cell> counterclockwiseNeighbors = getCounterclockwiseNeighbors(targetCell, cells);
       
        return rule1(counterclockwiseNeighbors);
    }

    private Grain rule1(List<Cell> counterclockwiseNeighbors) {
        Map<Grain, List<Cell>> grain2Cell = sort(counterclockwiseNeighbors);
        if (grain2Cell.keySet().size() > 4) {
            return rule2(counterclockwiseNeighbors);
        }
        
        for (Entry<Grain, List<Cell>> entry : grain2Cell.entrySet()) {
            if (entry.getValue().size() >= 5 && entry.getKey() != null && entry.getKey().getStatus().equals(GrainStatus.GRAIN)) {
                int counter = 0;
                int j;
                
                // 2 cycles 
                for (int i = - 8; i < 8; i++) {
                   j = (i < 0) ? i + 8 : i;
                   
                   if (counterclockwiseNeighbors.get(j).getGrain() != null &&  counterclockwiseNeighbors.get(j).getGrain().equals(entry.getKey())) {
                       counter++;
                   } else {
                       if (counter >= 5) {
                           //Matched
                           log.trace("Rule 1 - Matched gain {}", entry.getKey());
                           return entry.getKey();
                       }   
                       counter = 0;
                   }  
                }  
            }
        }
        
        return rule2(counterclockwiseNeighbors);
    }
    
    private Grain rule2(List<Cell> counterclockwiseNeighbors) {
        List<Cell> nearestMoore = new ArrayList<>(Arrays.asList(
                counterclockwiseNeighbors.get(0),
                counterclockwiseNeighbors.get(2),
                counterclockwiseNeighbors.get(4),
                counterclockwiseNeighbors.get(6)
                ));
        
        Map<Grain, List<Cell>> grain2Cell = sort(nearestMoore);   
        if (grain2Cell.keySet().size() > 2) {
            return rule3(counterclockwiseNeighbors);
        }
        
        for (Entry<Grain, List<Cell>> entry : grain2Cell.entrySet()) {
            if (entry.getValue().size() >= 3 && entry.getKey() != null && entry.getKey().getStatus().equals(GrainStatus.GRAIN)) {
                //Matched
                log.trace("Rule 2 - Matched gain {}", entry.getKey());
                return entry.getKey();
            }
        }
        
        return rule3(counterclockwiseNeighbors);
    }
    
    private Grain rule3(List<Cell> counterclockwiseNeighbors) {
        List<Cell> furtherMoore = new ArrayList<>(Arrays.asList(
                counterclockwiseNeighbors.get(1),
                counterclockwiseNeighbors.get(3),
                counterclockwiseNeighbors.get(5),
                counterclockwiseNeighbors.get(7)
                ));
        
        Map<Grain, List<Cell>> grain2Cell = sort(furtherMoore);
        if (grain2Cell.keySet().size() > 2) {
            return rule4(counterclockwiseNeighbors);
        }
        
        for (Entry<Grain, List<Cell>> entry : grain2Cell.entrySet()) {
            if (entry.getValue().size() >= 3 && entry.getKey() != null && entry.getKey().getStatus().equals(GrainStatus.GRAIN)) {
                //Matched
                log.trace("Rule 3 - Matched gain {}", entry.getKey());
                return entry.getKey();
            }
        }
        
        return rule4(counterclockwiseNeighbors);
    }
    
    private Grain rule4(List<Cell> counterclockwiseNeighbors) {
        Random rand = new Random();
        int randomNumber = 1 + rand.nextInt(100);
        
        if (randomNumber <= shapeControllRatio) {
            Map<Grain, List<Cell>> grain2Cell = sort(counterclockwiseNeighbors);
            Grain mostOccurrencesGrain = null;
            int maxNumberOfCells = 0;
            
            for (Entry<Grain, List<Cell>> entry : grain2Cell.entrySet()) {
                if (entry.getKey() != null && entry.getKey().getStatus().equals(GrainStatus.GRAIN)) {
                    int noCellToCompare = entry.getValue().size();
                    if (noCellToCompare > maxNumberOfCells) {
                        mostOccurrencesGrain = entry.getKey();
                    }
                }
            }
            
            log.trace("Rule 4 - Matched gain {}", mostOccurrencesGrain);
            return mostOccurrencesGrain;
        }
        
        log.trace("Rule 4 - No match");
        return null;
    }
    
    private List<Cell> getCounterclockwiseNeighbors(Cell targetCell, Cell[][] cells) {
        List<Cell> result = new ArrayList<>();
        int x = targetCell.getX();
        int y = targetCell.getY();
        
        result.add(cells[x][y - 1]); //12 o'clock
        result.add(cells[x - 1][y - 1]); //10.30 o'clock
        result.add(cells[x - 1][y]); //9 o'clock
        result.add(cells[x - 1][y + 1]); //7.30 o'clock
        result.add(cells[x][y + 1]); //6 o'clock
        result.add(cells[x + 1][y + 1]); //4.30 o'clock
        result.add(cells[x + 1][y]); //3 o'clock
        result.add(cells[x + 1][y - 1]); //1.30 o'clock
        
        return result;
    }
    
    private Map<Grain, List<Cell>> sort(List<Cell> counterclockwiseNeighbors) {
        Map<Grain, List<Cell>> result = new HashMap<>();
        counterclockwiseNeighbors.stream().forEachOrdered((e) -> {
            if (result.containsKey(e.getGrain())) {
                result.get(e.getGrain()).add(e);
            } else {
                result.put(e.getGrain(), new ArrayList<>(Collections.singletonList(e)));
            }
        });
        
        return result;
    }
}
