package com.mk.multiscalemodeling.project1.io.jsonModels;

import com.mk.multiscalemodeling.project1.model.CellStatus;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class CellDataModel {
    int x;
    int y;
    Color color;
    CellStatus status;
    
    //Optional values
    String inclusionId;
    String borderId;
}
