package com.mk.multiscalemodeling.project1.io.jsonModels;

import com.mk.multiscalemodeling.project1.model.GrainStatus;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class GrainDataModel {

    Color color;
    GrainStatus grainStatus;
}
