package com.mk.multiscalemodeling.project1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import javafx.scene.paint.Color;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DummyTests {

    @Test
    public void dummy() {
        Color color1 = Color.rgb(3, 3, 3);
        Color color2 = Color.rgb(3, 3, 3);
        
        Map<Color, String> map = new HashMap();
        map.put(color1, "kolor 1");
        String wartosc = map.putIfAbsent(color2, "Kolor 2");
        log.info(wartosc);
        log.info(map.entrySet().toString());
    }
    
    private Color getRandomColor() {
        Random randomizer = new Random();
        int red = randomizer.nextInt(255);
        int green = randomizer.nextInt(255);
        int blue = randomizer.nextInt(255);
        
        return Color.rgb(red, green, blue);
    }
    
}
