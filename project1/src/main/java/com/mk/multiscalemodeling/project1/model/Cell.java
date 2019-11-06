package com.mk.multiscalemodeling.project1.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Cell {

    @Getter 
    @Setter
    private CellStatus status;
    
    @Getter
    private Grain grain;
    
    @Getter 
    @Setter
    private int x;

    @Getter 
    @Setter
    private int y;

    public Cell(CellStatus status, int x, int y) {
        this.status = status;
        this.x = x;
        this.y = y;
    }
    
    public void setGrain(Grain grain) {
        if (this.grain != null) {
            grain.deregisterCell(this);
        }
        
        this.grain = grain;
        this.grain.registerCell(this);
    }
}
