package com.mk.multiscalemodeling.project1.io.jsonModels;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class JsonSImulationModel {

    int width;
    int hight;
    
    List<GrainDataModel> grains;
    List<BorderDataModel> borders;
    List<String> inclusionsId;
    List<CellDataModel> cells;
    
}
