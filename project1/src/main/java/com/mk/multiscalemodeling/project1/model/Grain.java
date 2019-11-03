package com.mk.multiscalemodeling.project1.model;

import javafx.scene.paint.Color;

public abstract class Grain {

    public abstract GrainStatus getStatus();
    public void setStatus(GrainStatus status) {};
    
    public abstract Color getColor();
    public void setColor(Color color) {};
    
}
