package com.mk.multiscalemodeling.project1.controllers.utils;

import org.springframework.util.StringUtils;

import javafx.util.StringConverter;

public class PositiveIntegerStringConverter extends StringConverter<Integer> {

    @Override
    public String toString(Integer value) {
        if (value == null) {
            return "";
        }
        
        return Integer.toString(value);
    }

    @Override
    public Integer fromString(String value) {      
        if (StringUtils.isEmpty(value)) {
            return 0;
        }
        
        value.trim();
        
        try {
            Integer result = Integer.valueOf(value);
            
            return (result > 0) ? result : 0;
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

}
