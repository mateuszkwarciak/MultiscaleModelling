package com.mk.multiscalemodeling.project1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxBridge extends Application {

	private Parent rootNode;
    private FXMLLoader fxmlLoader;
    
	public static void main(String[] args) {
        launch(args);
    }
    
    @Override
	public void init() throws Exception {
    	fxmlLoader = new FXMLLoader();
		super.init();
	}

	@Override
    public void start(Stage primaryStage) throws Exception {
    	fxmlLoader.setLocation(getClass().getResource("/fxml/start.fxml"));
        rootNode = fxmlLoader.load();
        
        primaryStage.setTitle("Kryszta³ki");
        
        Scene scene = new Scene(rootNode, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
