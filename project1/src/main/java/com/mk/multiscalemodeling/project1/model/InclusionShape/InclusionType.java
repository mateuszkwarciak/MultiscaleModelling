package com.mk.multiscalemodeling.project1.model.InclusionShape;

import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.CellStatus;
import com.mk.multiscalemodeling.project1.model.Inclusion;

public interface InclusionType {

    public void genearteInclusion(Cell startCell, Inclusion inclusion, int sizeOfInclusion);
    
    default void tryMarkCell(Cell cell, Inclusion inclusion) {
        if (!cell.getStatus().equals(CellStatus.ABSORBING)) {
            cell.setGrain(inclusion);
            cell.setStatus(CellStatus.INCLUSION);
        } 
    }
}
