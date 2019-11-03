package com.mk.multiscalemodeling.project1.model;

import javafx.scene.paint.Color;

public class Inclusion extends Grain {
   
    public static Color COLOR = Color.BLACK;
    
    private GrainStatus status = GrainStatus.INCLUSION;
    
    @Override
    public GrainStatus getStatus() {
        return status;
    }

    @Override
    public Color getColor() {
        return COLOR;
    }

}
