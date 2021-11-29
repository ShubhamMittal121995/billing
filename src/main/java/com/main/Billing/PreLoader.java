package com.main.Billing;

import javafx.application.Preloader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PreLoader extends Preloader {
	ProgressIndicator bar;
	Stage stage;

	private Scene createPreloaderScene() {
		bar = new ProgressIndicator();
		bar.setPadding(new Insets(20, 20, 20, 20));
		Label label = new Label("Please Wait... Application Starting");
		label.setFont(new Font(18));
		label.setPadding(new Insets(20, 0, 20, 70));
		BorderPane p = new BorderPane();
		p.setTop(label);
		p.setCenter(bar);
		return new Scene(p, 400, 300);
	}

	public void start(Stage stage) throws Exception {
		this.stage = stage;
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(createPreloaderScene());
		stage.show();
	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification evt) {
		if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
			stage.hide();
		}
	}
}
