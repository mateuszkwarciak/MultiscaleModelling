package com.mk.multiscalemodeling.project1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxBridge extends Application {

    private static final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Project1Configuration.class);
    
	private Parent rootNode;
    private FXMLLoader fxmlLoader;
    
    public static final String APPLICATION_TITLE = "Kryształki";
    
	public static void main(String[] args) {
        launch(args);
    }
    
    @Override
	public void init() throws Exception {
    	fxmlLoader = new FXMLLoader();
    	fxmlLoader.setControllerFactory(clazz -> applicationContext.getBean(clazz));
		super.init();
	}

	@Override
    public void start(Stage primaryStage) throws Exception {
    	fxmlLoader.setLocation(getClass().getResource("/fxml/start.fxml"));
        rootNode = fxmlLoader.load();
        
        primaryStage.setTitle(APPLICATION_TITLE);
        
        Scene scene = new Scene(rootNode, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
