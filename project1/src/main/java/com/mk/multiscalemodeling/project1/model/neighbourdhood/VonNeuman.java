package com.mk.multiscalemodeling.project1.model.neighbourdhood;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Grain;

public class VonNeuman implements Neighbourhood {
    
    @Override
    public Grain tryToMatchTheGrain(Cell targetCell, Cell[][] cells) {        
        Cell up = cells[targetCell.getX()][targetCell.getY() - 1];
        Cell right = cells[targetCell.getX() - 1][targetCell.getY()];
        Cell down = cells[targetCell.getX()][targetCell.getY() + 1];
        Cell left = cells[targetCell.getX() + 1][targetCell.getY()];
        
        Map<Grain, AtomicInteger> grain2Count = new HashMap<>(); 
        if (up.getStatus().equals(CellStatus.OCCUPIED)) {
            increment(grain2Count, up.getGrain());
        }
        if (right.getStatus().equals(CellStatus.OCCUPIED)) {
            increment(grain2Count, right.getGrain());
        }
        if (down.getStatus().equals(CellStatus.OCCUPIED)) {
            increment(grain2Count, down.getGrain());
        }
        if (left.getStatus().equals(CellStatus.OCCUPIED)) {
            increment(grain2Count, left.getGrain());
        }
        
        if (grain2Count.size() > 0) {
            Entry<Grain, AtomicInteger> mostOccurrences = grain2Count.entrySet().iterator().next();
            for (Entry<Grain, AtomicInteger> entry: grain2Count.entrySet()) {
                if (entry.getValue().get() > mostOccurrences.getValue().get()) {
                    mostOccurrences = entry;
                }
            }
            return mostOccurrences.getKey();
        }
        
        return null;
    }
    
    private void increment(Map<Grain, AtomicInteger> grain2Count, Grain grainToAdd) {
        AtomicInteger numberOfoOccurrences = grain2Count.get(grainToAdd);
        if (numberOfoOccurrences == null) {
            grain2Count.put(grainToAdd, new AtomicInteger(1));
            return;
        }
        numberOfoOccurrences.incrementAndGet();
        
    }

}
