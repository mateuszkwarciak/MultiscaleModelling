package com.mk.multiscalemodeling.project1.io.jsonModels;

import java.util.List;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class JsonDataModel {

    int width;
    int hight;
    
    List<Color> grainsColors;
    
    List<CellDataModel> cells;
    
}
