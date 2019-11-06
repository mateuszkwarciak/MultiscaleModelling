package com.mk.multiscalemodeling.project1.model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

public abstract class Grain {

    @Getter
    @Setter
    public List<Cell> cells = new ArrayList<>();
    
    public abstract GrainStatus getStatus();
    public void setStatus(GrainStatus status) {};
    
    public abstract Color getColor();
    public void setColor(Color color) {};
    
    public void registerCell(Cell cell) {
        if (!cells.contains(cell)) {
            cells.add(cell);
        }
    }
    
    public void deregisterCell(Cell cell) {
        cells.remove(cell);
    }

}
