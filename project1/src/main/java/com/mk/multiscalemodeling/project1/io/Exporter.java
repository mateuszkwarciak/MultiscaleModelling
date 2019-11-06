package com.mk.multiscalemodeling.project1.io;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Exporter {
    
    public static void saveAsImage(Canvas canvasToSave, File file) throws IOException {
        if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("png")) {
            file = new File(file.toString() + ".png");
        }
        
        WritableImage writableImage = new WritableImage((int) canvasToSave.getWidth(), (int) canvasToSave.getHeight()); 
        ImageIO.write(SwingFXUtils.fromFXImage(canvasToSave.snapshot(null, writableImage), null), "png", file);
        log.info("Saved image to file: {}", file.getPath());
    }

    public static boolean saveAsTxt() throws IOException {
        return true;
    }
    
}
