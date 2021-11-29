package com.main.Billing.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.main.Billing.utility.EmailHelper;
import com.main.Billing.utility.Utility;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SendEmailController implements Initializable{
	
	@FXML
	TextField email;
	
	@FXML
	PasswordField password;
	
	private String filePath, toClientAddress, fromDate, toDate;
	private Stage stage = null;
	
	public SendEmailController(String filePath, String toClientAddress, String fromDate, String toDate) {
		this.filePath = filePath;
		this.toClientAddress = toClientAddress;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}
	
	public SendEmailController() {
		super();
	}

	public void sendingEmail(ActionEvent event) {
		String fromEmail = null;
		String fromPassword = null;
		
		if(!email.getText().equals("") && email.getText().length() > 0 && isEmail(email.getText()))  {
			fromEmail = email.getText();
		} else {
			Utility.getAlert("Error", "Email Address is not valid", "Please correct it.", null, AlertType.ERROR);
			return;
		}
		
		if(!password.getText().equals("") && password.getText().length() > 0) {
			fromPassword = password.getText();
		} else {
			Utility.getAlert("Error", "Password is Empty", "Please correct it.", null, AlertType.ERROR);
			return;
		}
		
		if(!isEmail(toClientAddress)) {
			Utility.getAlert("Error", "Party Email Address is not valid", "Please correct it", null, AlertType.ERROR);
			return;
		}
		
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		if(fromEmail != null && fromPassword != null) {
			EmailHelper helper = new EmailHelper();
			Map<String,String> inputMap = new HashMap<String,String>();
			inputMap.put("fromEmail", fromEmail);
			inputMap.put("fromPassword", fromPassword);
			inputMap.put("toClientAddress", toClientAddress);
			inputMap.put("fromDate", fromDate);
			inputMap.put("toDate", toDate);
			inputMap.put("filePath", filePath);
			helper.sendPDFToClient(inputMap, stage);
		} else {
			Utility.getAlert("Error", "Please insert correct value.", "Either email or password is null", null, AlertType.ERROR);
		}
		
	}
	
	public boolean isEmail(String value) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";

		Pattern pat = Pattern.compile(emailRegex);
		if (value == null)
			return false;
		return pat.matcher(value).matches();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
}

