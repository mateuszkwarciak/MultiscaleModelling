package com.mk.multiscalemodeling.project1.model;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Cell {

    public static final Color EMPTY_CELL_COLOR = Color.GHOSTWHITE;
    
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
            this.grain.deregisterCell(this);
        }
        
        this.grain = grain;
        if (this.grain != null) {
            this.grain.registerCell(this);
        }
    }
    
    public void removeFromGrain() {
        if (grain != null) {
            grain.deregisterCell(this);
            grain = null;
            status = CellStatus.EMPTY;
        }
    }
}
