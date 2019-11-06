package com.mk.multiscalemodeling.project1.model.neighbourdhood;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;

import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Grain;

public class VonNeuman implements Neighbourhood {

    private static Random random = new Random();
    
    @Override
    public Grain tryToMatchTheGrain(Cell targetCell, Cell[][] cells) {        
        Cell up = cells[targetCell.getX()][targetCell.getY() - 1];
        Cell right = cells[targetCell.getX() - 1][targetCell.getY()];
        Cell down = cells[targetCell.getX()][targetCell.getY() + 1];
        Cell left = cells[targetCell.getX() + 1][targetCell.getY()];
        
        List<Grain> matchedGrains = new ArrayList<>();
        if (up.getStatus().equals(CellStatus.OCCUPIED)) {
            matchedGrains.add(up.getGrain());
        }
        if (right.getStatus().equals(CellStatus.OCCUPIED)) {
            matchedGrains.add(right.getGrain());
        }
        if (down.getStatus().equals(CellStatus.OCCUPIED)) {
            matchedGrains.add(down.getGrain());
        }
        if (left.getStatus().equals(CellStatus.OCCUPIED)) {
            matchedGrains.add(left.getGrain());
        }
        
        if (matchedGrains.size() == 1) {
            return matchedGrains.get(0);
        } else if (matchedGrains.size() > 1) {
            matchedGrains.get(random.nextInt(matchedGrains.size()));
        }
        return null;
    }

}
