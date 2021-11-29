package com.main.Billing.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.main.Billing.entity.Bill;
import com.main.Billing.entity.PostingJournal;
import com.main.Billing.service.BillingService;
import com.main.Billing.service.MainWindowService;
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
import javafx.scene.control.TextField;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateBillController implements Initializable {

	@Autowired
	MainWindowService mainService;
	@Autowired
	PostingJournalService postingService;
	@Autowired
	BillingService billingService;

	private Integer slipNumber;
	private String partyName, vehicleName, fuelType, date;
	private Double quantity, rate, cash;
	private boolean isUpdate;

	@FXML
	private Button c_save, c_delete;

	@FXML
	private TextField c_slip_number, c_vehicle_number, c_quantity, c_rate, c_amount, c_cash, c_total_amount;

	@FXML
	private ComboBox<String> c_fuel_type, c_party_name;

	@FXML
	private DatePicker c_posted_date;

	public CreateBillController(Integer slipNumber, String partyName, String vehicleName, String fuelType, String date,
			Double quantity, Double rate, Double cash) {
		this.slipNumber = slipNumber;
		this.partyName = partyName;
		this.vehicleName = vehicleName;
		this.fuelType = fuelType;
		this.date = date;
		this.quantity = quantity;
		this.rate = rate;
		this.cash = cash;
		this.isUpdate = true;
	}

	public CreateBillController() {
		isUpdate = false;
	}

	public void onCreateClear() {
		c_slip_number.setText("");
		c_party_name.setValue(null);
		c_fuel_type.setValue(null);
		c_amount.setText("");
		c_vehicle_number.setText("");
		c_rate.setText("");
		c_quantity.setText("");
		c_posted_date.setValue(null);
		c_cash.setText("");
		c_total_amount.setText("");
		c_save.setText("Save");
		c_delete.setDisable(true);
		c_delete.setVisible(false);
		isUpdate = false;

	}

	public void onCreateDelete(ActionEvent event) {
		try {
			Alert alert = Utility.getAlert("Confirmation", "Delete !!", "Do you want to delete the record?", null,
					AlertType.CONFIRMATION);
			if (!alert.getResult().equals(ButtonType.OK)) {
				return;
			}
			postingService.deletingPostingJournalForBill(Integer.parseInt(c_slip_number.getText()));
			billingService.deleteBillBySlipNumber(Integer.parseInt(c_slip_number.getText()));
		} catch (Exception ex) {
			Utility.getAlert("Error", "Error in deleting bill", ex.getMessage(), null, AlertType.ERROR);
		} finally {
			
		}
		onCreateClear();
	}

	public void onCreateSave(ActionEvent event) {

		if (c_slip_number.getText().equals("") || c_slip_number.getText().equals("0")
				|| !validate(c_slip_number.getText())) {
			Utility.getAlert("Error", "Please insert correct value.",
					"Please provide correct value for Slip Number field.", null, AlertType.ERROR);
			return;
		}

		if (c_vehicle_number.getText().equals("")) {
			Utility.getAlert("Error", "Please insert correct value.",
					"Please provide correct value for Slip Number field.", null, AlertType.ERROR);
			return;
		}

		if (c_party_name.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.", "Please provide correct value for Name field.",
					null, AlertType.ERROR);
			return;
		}

		if (c_fuel_type.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.",
					"Please provide correct value for Fuel Type field.", null, AlertType.ERROR);
			return;
		}

		if (c_posted_date.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.", "Please provide value for Posted Date field.",
					null, AlertType.ERROR);
			return;
		}

		if (c_quantity.getText().equals("") || c_quantity.getText().equals("0") || !validate(c_quantity.getText())) {
			Utility.getAlert("Error", "Please insert correct value.",
					"Please provide correct value for Quantity field.", null, AlertType.ERROR);
			return;
		}

		if (c_rate.getText().equals("") || c_rate.getText().equals("0") || c_rate.getText().equals("1")
				|| !validate(c_rate.getText())) {
			Utility.getAlert("Error", "Please insert correct value.", "Please provide correct value for Rate field.",
					null, AlertType.ERROR);
			return;
		}

		if (!c_cash.getText().equals("") && !validate(c_cash.getText())) {
			Utility.getAlert("Error", "Please insert correct value.", "Please provide correct value for Cash field.",
					null, AlertType.ERROR);
			return;
		} else if (c_cash.getText().equals("")) {
			c_cash.setText("0.00");
		}

		calculateValues();
		calculateTotal();

		try {
			Bill bill = new Bill(Integer.valueOf(c_slip_number.getText()), c_party_name.getValue(),
					c_vehicle_number.getText(), Date.valueOf(c_posted_date.getValue()), c_fuel_type.getValue(),
					Double.valueOf(c_quantity.getText()), Double.valueOf(c_rate.getText()),
					Double.valueOf(c_amount.getText()), Double.valueOf(c_cash.getText()),
					Double.valueOf(c_total_amount.getText()), "Y", "Posted");
			billingService.createOrUpdateBill(bill, isUpdate);
			
			PostingJournal pj = new PostingJournal(Integer.valueOf(c_slip_number.getText()), null,
					Date.valueOf(c_posted_date.getValue()), Double.parseDouble(c_total_amount.getText()), null, "Y", "Sale", "Credit Note", c_party_name.getValue());
			postingService.createOrUpdatePostingJournalForBill(pj, isUpdate);
		} catch (Exception ex) {
			Utility.getAlert("Error", "Error in saving bill", "", null, AlertType.ERROR);
		} finally {
		}
		onCreateClear();
	}

	public void calculateValues() {
		double amount, rate;

		if (c_amount.getText().equals("") || c_amount.getText().equals("0")) {
			amount = 0;
		} else {
			if (!validate(c_amount.getText())) {
				return;
			}
			amount = Double.parseDouble(c_amount.getText());
		}

		if (c_rate.getText().equals("") || c_rate.getText().equals("0") || !validate(c_rate.getText())) {
			rate = 1;
		} else {
			if (!validate(c_rate.getText())) {
				return;
			}
			rate = Double.parseDouble(c_rate.getText());
		}

		double basic = amount / rate;
		basic = round(basic, 2);
		c_quantity.setText(String.valueOf(basic));
		calculateTotal();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		c_delete.setDisable(true);
		c_delete.setVisible(false);
		Utility.setDateFormat(c_posted_date);

		c_posted_date.setValue(LocalDate.now());

		c_fuel_type.getItems().setAll("Diesel", "Petrol", "Lube");
		loadPartyName();
		if (isUpdate) {
			initializeValue();
			calculateValues();
			calculateTotal();
		}
	}

	private void loadPartyName() {
		try {
			ObservableList<String> partyNames = FXCollections.observableArrayList();
			partyNames.clear();
			mainService.findAllParty(null, partyNames);
			c_party_name.setItems(partyNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calculateTotal() {
		double cash;

		if (c_cash.getText().equals("") || c_cash.getText().equals("0")) {
			cash = 0;
		} else {
			if (!validate(c_cash.getText())) {
				return;
			}
			cash = Double.parseDouble(c_cash.getText());
		}

		double amount = (!c_amount.getText().equals("")) ? Double.parseDouble(c_amount.getText()) : 0;

		double basic = amount + cash;
		basic = round(basic, 2);
		c_total_amount.setText(String.valueOf(basic));
	}

	public void initializeValue() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		c_slip_number.setText(slipNumber.toString());
		c_party_name.setValue(partyName);
		c_posted_date.setValue(LocalDate.parse(date, dateTimeFormatter));
		c_vehicle_number.setText(vehicleName);
		c_fuel_type.setValue(fuelType);
		c_amount.setText(String.valueOf(quantity));
		c_rate.setText(String.valueOf(rate));
		c_cash.setText(String.valueOf(cash));

		c_slip_number.setDisable(true);
		c_save.setText("Update");
		c_delete.setDisable(false);
		c_delete.setVisible(true);
	}

	public void calculateQuantity() {
		double quantity, rate;

		if (c_quantity.getText().equals("") || c_quantity.getText().equals("0")) {
			quantity = 0;
		} else {
			if (!validate(c_quantity.getText())) {
				return;
			}
			quantity = Double.parseDouble(c_quantity.getText());
		}

		if (c_rate.getText().equals("") || c_rate.getText().equals("0") || !validate(c_rate.getText())) {
			rate = 1;
		} else {
			if (!validate(c_rate.getText())) {
				return;
			}
			rate = Double.parseDouble(c_rate.getText());
		}

		double basic = quantity * rate;
		basic = round(basic, 2);
		c_amount.setText(String.valueOf(basic));
		calculateTotal();
	}

	private boolean validate(String text) {
		return text.matches("-?\\d+(?:\\.\\d+)?");
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
