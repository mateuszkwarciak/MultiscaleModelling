package com.mk.multiscalemodeling.project1.model;

import java.util.UUID;

import javafx.scene.paint.Color;

public class Inclusion extends Grain {
   
    public static Color COLOR = Color.BLACK;
    
    private GrainStatus status = GrainStatus.INCLUSION;
    
    private String inclusionId;
    
    public Inclusion() {
        inclusionId = UUID.randomUUID().toString();
    }
    
    public Inclusion(String inclusionId) {
        this.inclusionId = inclusionId;
    }
    
    @Override
    public GrainStatus getStatus() {
        return status;
    }

    @Override
    public Color getColor() {
        return COLOR;
    }

    public String getInclusionId() {
        return inclusionId;
    }

}
