package com.mk.multiscalemodeling.project1.controllers.utils;

import org.springframework.util.StringUtils;

import javafx.util.StringConverter;

public class PositiveDoubleStringConverter extends StringConverter<Double>{

    @Override
    public String toString(Double value) {
        if (value == null) {
            return "";
        }
        
        return Double.toString(value);
    }

    @Override
    public Double fromString(String value) {
        if (StringUtils.isEmpty(value)) {
            return 0.0;
        }
        
        value.trim();
        
        try {
            Double result = Double.valueOf(value);
            
            return (result > 0) ? result : 0;
        } catch (NumberFormatException nfe) {
            return 0.0;
        }
    }

}
