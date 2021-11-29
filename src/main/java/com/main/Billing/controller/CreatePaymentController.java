package com.main.Billing.controller;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.main.Billing.entity.Payment;
import com.main.Billing.entity.PostingJournal;
import com.main.Billing.service.MainWindowService;
import com.main.Billing.service.PaymentService;
import com.main.Billing.service.PostingJournalService;
import com.main.Billing.utility.Utility;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreatePaymentController implements Initializable {

	@Autowired
	MainWindowService mainService;
	@Autowired
	PaymentService paymentService;
	@Autowired
	PostingJournalService postingService;
	
	private Integer paymentId;
	private String partyName, remark, paymentMode, date;
	private Double paymentAmount;
	private boolean isUpdate;

	@FXML
	private Button delete, save;

	@FXML
	private TextField amount;

	@FXML
	private TextArea remarks;

	@FXML
	private ComboBox<String> party_name, mode;

	@FXML
	private DatePicker posted_date;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	public CreatePaymentController(Integer paymentId, String partyName, String remark, String paymentMode, String date,
			Double paymentAmount) {
		this.paymentId = paymentId;
		this.partyName = partyName;
		this.remark = remark;
		this.paymentMode = paymentMode;
		this.date = date;
		this.paymentAmount = paymentAmount;
		isUpdate = true;
	}

	public CreatePaymentController() {
		isUpdate = false;
	}

	public void onCreateClear() {
		party_name.setValue(null);
		mode.setValue(null);
		amount.setText("");
		remarks.setText("");
		posted_date.setValue(null);
		delete.setDisable(true);
		save.setText("Save");
		delete.setVisible(false);
		isUpdate = false;
	}

	public void onCreateSave(ActionEvent event) {

		if (party_name.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.",
					"Please provide correct value for Party Name field.", null, AlertType.ERROR);
			return;
		}

		if (mode.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.", "Please provide correct value for Mode field.",
					null, AlertType.ERROR);
			return;
		}

		if (posted_date.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.", "Please provide value for Posted Date field.",
					null, AlertType.ERROR);
			return;
		}

		if (amount.getText().equals("") || amount.getText().equals("0") || !validate(amount.getText())) {
			Utility.getAlert("Error", "Please insert correct value.", "Please provide correct value for Amount field.",
					null, AlertType.ERROR);
			return;
		}

		try {	
			Payment payment = new Payment(party_name.getValue(), mode.getValue(), remarks.getText(), Date.valueOf(posted_date.getValue()), "Y", Double.parseDouble(amount.getText()), "Posted");
			if(isUpdate && paymentId != null) {
				paymentService.updatePayment(paymentId, payment);
			} else 
				paymentId = paymentService.createPayment(payment);
			
			PostingJournal pj = new PostingJournal(null, paymentId,
					Date.valueOf(posted_date.getValue()),null, Double.parseDouble(amount.getText()), "Y", mode.getValue(), remarks.getText(), party_name.getValue());
			postingService.createOrUpdatePostingJournalForPayment(pj, isUpdate);

		} catch (Exception ex) {
			Utility.getAlert("Error", "Error in saving/updating payment", ex.getMessage(), null, AlertType.ERROR);
		} finally {
		}
		onCreateClear();
	}

	public void onCreateDelete(ActionEvent event) {
		try {
			Alert alert = Utility.getAlert("Confirmation", "Delete !!", "Do you want to delete the record?", null,
					AlertType.CONFIRMATION);
			if (!alert.getResult().equals(ButtonType.OK)) {
				return;
			}
			paymentService.deletePaymentById(paymentId);
			postingService.deletingPostingJournalForPayment(paymentId);
		} catch (Exception ex) {
			Utility.getAlert("Error", "Error in deleting payment", ex.getMessage(), null, AlertType.ERROR);
		} finally {
		}
		onCreateClear();
	}

	private void loadPartyName() {
		try {
			ObservableList<String> partyNames = FXCollections.observableArrayList();
			partyNames.clear();
			mainService.findAllParty(null, partyNames);
			party_name.setItems(partyNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean validate(String text) {
		return text.matches("-?\\d+(?:\\.\\d+)?");
	}

	public void initializeValue() {
		party_name.setValue(partyName);
		posted_date.setValue(LocalDate.parse(date, dateTimeFormatter));
		mode.setValue(paymentMode);
		amount.setText(String.valueOf(paymentAmount));
		remarks.setText(remark);

		save.setText("Update");
		delete.setDisable(false);
		delete.setVisible(true);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		delete.setDisable(true);
		delete.setVisible(false);
		Utility.setDateFormat(posted_date);
		mode.getItems().setAll("Cash", "Cheque");
		loadPartyName();
		if (isUpdate) {
			initializeValue();
		}
	}
}
