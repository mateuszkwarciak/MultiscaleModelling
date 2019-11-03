package com.mk.multiscalemodeling.project1.model;

import javafx.scene.paint.Color;

public class Inclusion extends Grain {
   
    private GrainStatus status = GrainStatus.INCLUSION;
    private static Color COLOR = Color.BLACK;
    
    @Override
    public GrainStatus getStatus() {
        return status;
    }

    @Override
    public Color getColor() {
        return COLOR;
    }

}
