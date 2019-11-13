package com.mk.multiscalemodeling.project1.model;

import java.util.UUID;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

public class Border extends Grain {

    public static final Color COLOR = Color.rgb(3, 3, 3);
    
    @Getter
    private Grain grain;
    private GrainStatus status;
    
    @Getter
    private String borderId;
    
    public Border(Grain grain) {
        this.grain = grain;
        status = GrainStatus.BORDER;
        borderId = UUID.randomUUID().toString();
    }
    
    public Border(Grain grain, String borderId) {
        status = GrainStatus.BORDER;
        this.grain = grain;
        this.borderId = borderId;
    }
    
    @Override
    public GrainStatus getStatus() {
        return status;
    }

    @Override
    public Color getColor() {
        return COLOR;
    }

}
