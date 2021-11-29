package com.main.Billing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.main.Billing.utility.PreAuthenticator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@SpringBootApplication
@ComponentScan
public class App extends Application {
	private static Logger log = LoggerFactory.getLogger(App.class);
	private ConfigurableApplicationContext springContext;
	private Parent rootNode;

	@Override
	public void init() {
		log.debug("Application Starting.....");
		try {
			springContext = SpringApplication.run(App.class);
		} catch (Exception e) {
			log.error("Error caught in Initilization....", e);
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
			fxmlLoader.setControllerFactory(springContext::getBean);
			rootNode = fxmlLoader.load();
			stage.setScene(new Scene(rootNode));
			stage.setTitle("Shaheed Filling Station");
			stage.resizableProperty().setValue(Boolean.FALSE);
			stage.getIcons().add(new Image(getClass().getResource("/HP_Logo.jpeg").toString()));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void stop() throws Exception {
		PreAuthenticator.unLock();
		int exitCode = SpringApplication.exit(springContext, (ExitCodeGenerator) () -> 0);
		System.exit(exitCode);
	}
}
