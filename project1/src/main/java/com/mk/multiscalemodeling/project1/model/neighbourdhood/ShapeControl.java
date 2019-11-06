package com.mk.multiscalemodeling.project1.model.neighbourdhood;

import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.Grain;

public class ShapeControl implements Neighbourhood {

    private int shapeControllRatio;
    
    public ShapeControl(int shapeControllRatio) {
        this.shapeControllRatio = shapeControllRatio;
    }

    @Override
    public Grain tryToMatchTheGrain(Cell targetCell, Cell[][] cells) {
        // TODO Auto-generated method stub
        return null;
    }

}
