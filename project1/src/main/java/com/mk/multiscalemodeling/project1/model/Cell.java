package com.mk.multiscalemodeling.project1.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Cell {

    private CellStatus status;
    private Grain grain;
    private int x;
    private int y;

    public Cell(CellStatus status, int x, int y) {
        this.status = status;
        this.x = x;
        this.y = y;
    }
}
