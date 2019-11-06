package com.mk.multiscalemodeling.project1.model;

import javafx.scene.paint.Color;

public class GrainImpl extends Grain {

    private GrainStatus status;
    private Color color;
    
    public GrainImpl(GrainStatus status, Color color) {
        this.status = status;
        this.color = color;
    }

    @Override
    public GrainStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(GrainStatus status) {
        this.status = status;   
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }
    
  
  

}
