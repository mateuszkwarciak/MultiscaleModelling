package com.mk.multiscalemodeling.project1.model.neighbourdhood;

import com.mk.multiscalemodeling.project1.model.Cell;
import com.mk.multiscalemodeling.project1.model.Grain;

public interface Neighbourhood {

	public Grain tryToMatchTheGrain(Cell targetCell, Cell[][] cells);
	
}
