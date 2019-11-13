package com.mk.multiscalemodeling.project1.io.jsonModels;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BorderDataModel {

    private String boundryId;
    private Color colorOfConnectedGrain;
}
