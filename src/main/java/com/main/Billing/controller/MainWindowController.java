package com.main.Billing.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.main.Billing.converter.BillTable;
import com.main.Billing.converter.PartyTable;
import com.main.Billing.converter.PaymentTable;
import com.main.Billing.converter.ReportTable;
import com.main.Billing.entity.Bill;
import com.main.Billing.entity.Party;
import com.main.Billing.entity.Payment;
import com.main.Billing.entity.ReportInfo;
import com.main.Billing.service.MainWindowService;
import com.main.Billing.utility.EmailHelper;
import com.main.Billing.utility.PropertyUtil;
import com.main.Billing.utility.Utility;

import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.JRException;

@Component
public class MainWindowController implements Initializable {

	@Autowired
	MainWindowService service;
	@Autowired
	ApplicationContext context;

	@FXML
	private Button party_save, search_save, editButton, paymentEdit, report_find, report_clear, report_open, report_send ;

	@FXML
	private TextField search_slip_no, r_no;

	@FXML
	private ComboBox<String> search_party_name, p_party, r_party;

	@FXML
	private DatePicker search_from_date, search_to_date, p_date, p_to_date, r_from_date, r_to_date;

	@FXML
	private TableView<BillTable> search_table;

	@FXML
	private TableColumn<BillTable, String> s_slip_no, s_party_name, s_vehicle_number, s_posted_date, s_fuel_type,
			s_rate, s_amount, s_quantity, s_cash, s_total_amount;

	@FXML
	private TableView<PartyTable> party_table;

	@FXML
	private TableColumn<PartyTable, String> tc_party_name, tc_owner_name, tc_contact, tc_address, tc_email;

	@FXML
	private TableView<PaymentTable> payment_table;

	@FXML
	private TableColumn<PaymentTable, String> pc_party, pc_date, pc_mode, pc_remark, pc_amount, pc_id;

	@FXML
	private TextField party_name, owner_name, contact_number, email;

	@FXML
	private TextArea address;

	@FXML
	private Label lCount, lAmount, lCash, lTotalAmount;
	
	@FXML
	private TableView<ReportTable> report_table;
	
	@FXML
	private TableColumn<ReportTable, String> rt_party, rt_no, rt_from_date, rt_to_date, rt_report_name, rt_location;

	ObservableList<BillTable> data = FXCollections.observableArrayList();
	ObservableList<PartyTable> partydata = FXCollections.observableArrayList();
	ObservableList<PaymentTable> paymentdata = FXCollections.observableArrayList();
	ObservableList<ReportTable> reportdata = FXCollections.observableArrayList();

	private boolean isUpdateParty = false;
	private static Map<String, String> partyEmailMap = new HashMap<String, String>();

	public void onSearchFind() {
		Long slipNumber = null;
		String pName = null;
		String date = null;
		String toDate = null;

		if (!search_slip_no.getText().trim().equals("") && !search_slip_no.getText().equals("0")
				&& Long.parseLong(search_slip_no.getText()) != 0) {
			slipNumber = Long.valueOf(search_slip_no.getText());
			search_from_date.setValue(null);
		}

		if (search_party_name.getValue() != null) {
			pName = search_party_name.getValue();
		}

		if (search_from_date.getValue() != null) {
			date = search_from_date.getValue().toString();
		}

		if (search_to_date.getValue() != null) {
			toDate = search_to_date.getValue().toString();
		}
		// Utility.piStage.show();
		onSearchDataClear();
		data.clear();
		int tCount = 0;
		double tAmount = 0.00;
		double tCash = 0.00;
		double tTotalAmount = 0.00;

		List<Bill> resultList = service.findBillByProperties(slipNumber, pName, date, toDate);
		if (resultList != null) {
			for (Bill bill : resultList) {
				tCount++;
				tAmount += bill.getAmount();
				tCash += bill.getCash();
				tTotalAmount += bill.getTotalAmount();
				BillTable bt = new BillTable(bill.getSlipNumber(), bill.getPartyName(), bill.getVehicleNumber(),
						Utility.getFormattedDate(bill.getPostedDate()), bill.getFuelType(), bill.getRate(),
						bill.getAmount(), bill.getQuantity(), bill.getCash(), bill.getTotalAmount());
				data.add(bt);
			}
		}

		if (data.size() == 0) {
			search_table.setPlaceholder(new Label("No visible columns and/or data exist."));
		} else {
			lCount.setText(tCount + "");
			lAmount.setText(tAmount + "");
			lCash.setText(tCash + "");
			lTotalAmount.setText(tTotalAmount + "");
		}
		// Utility.piStage.close();
	}

	public void onSearchClear() {
		search_slip_no.setText("");
		search_party_name.setValue(null);
		search_from_date.setValue(null);
		search_to_date.setValue(null);
		editButton.setDisable(true);
		editButton.setVisible(false);
		onSearchDataClear();
	}

	public void onSearchDataClear() {
		data.clear();
		lCount.setText("");
		lAmount.setText("");
		lCash.setText("");
		lTotalAmount.setText("");
	}

	public void generateReport(ActionEvent event) {
		if (search_party_name.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.",
					"Please provide correct value for Party Name field.", null, AlertType.ERROR);
			return;
		}

		if (search_from_date.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.",
					"Please provide correct value for From Date field.", null, AlertType.ERROR);
			return;
		}

		if (search_to_date.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.", "Please provide correct value for To Date field.",
					null, AlertType.ERROR);
			return;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
		String partyName = search_party_name.getValue();
		String fromDate = search_from_date.getValue().toString();
		String toDate = search_to_date.getValue().toString();
		String fileName = partyName + "_" + formatter.format(new Date()) + ".pdf";
		String filePath = PropertyUtil.DOWNLOAD_PDF_PATH + "/" + fileName;

		Map<String, String> input = new HashMap<String, String>();
		input.put("partyName", partyName);
		input.put("fromDate", fromDate);
		input.put("toDate", toDate);
		input.put("fileName", fileName);
		input.put("filePath", filePath);
		Stage stage = new Stage();
		try {
			ProgressIndicator PI = new ProgressIndicator();
			StackPane root = new StackPane();
			root.getChildren().add(PI);
			Scene scene = new Scene(root, 300, 200);
			stage.setScene(scene);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.show();
			if (service.generateReportByCriteria(input, partyEmailMap)) {
				Utility.getAlert("Information", "Report Created..", "Report Generated. Please Check in Report Tab", null, AlertType.INFORMATION);
			}
			stage.close();
		} catch (ColumnBuilderException | FileNotFoundException | ClassNotFoundException | JRException e) {
			Utility.getAlert("Error", "Error in Dynamic Jasper", e.getMessage(), null, AlertType.ERROR);
		} finally {
			stage.close();
		}
	}
	
	public void onTableClick() {
		if (search_table.getSelectionModel().getSelectedItem() == null)
			return;
		else {
			editButton.setVisible(true);
			editButton.setDisable(false);
		}
	}

	public void generateNewInvoice() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/CreateBill.fxml"));

			fxmlLoader.setController(context.getBean(CreateBillController.class));

			Scene scene = new Scene(fxmlLoader.load());
			Stage stage = new Stage();
			stage.setTitle("Create Bill..");
			stage.setScene(scene);
			stage.initStyle(StageStyle.UTILITY);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
			System.out.println("TRYING!");
			stage.setOnCloseRequest(event -> {
				System.out.println("Close Requested");
				onSearchDataClear();
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void editInvoice() {
		try {
			if (search_table.getSelectionModel().getSelectedItem() == null)
				return;
			Integer slipNumber = search_table.getSelectionModel().getSelectedItem().getSlipNumber();
			String partyName = search_table.getSelectionModel().getSelectedItem().getPartyName();
			String vehicleName = search_table.getSelectionModel().getSelectedItem().getVehicleNumber();
			String fuelType = search_table.getSelectionModel().getSelectedItem().getFuelType();
			String date = search_table.getSelectionModel().getSelectedItem().getPostedDate();
			Double quantity = search_table.getSelectionModel().getSelectedItem().getAmount();
			Double rate = search_table.getSelectionModel().getSelectedItem().getRate();
			Double cash = search_table.getSelectionModel().getSelectedItem().getCash();

			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/CreateBill.fxml"));
			CreateBillController bean = (CreateBillController) context.getBean("createBillController", slipNumber,
					partyName, vehicleName, fuelType, date, quantity, rate, cash);
			fxmlLoader.setController(bean);
			Scene scene = new Scene(fxmlLoader.load(), 640, 480);
			Stage stage = new Stage();
			stage.setTitle("Update Bill..");
			stage.setScene(scene);
			stage.initStyle(StageStyle.UTILITY);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
			System.out.println("TRYING!");
			stage.setOnCloseRequest(event -> {
				System.out.println("Close Requested");
				onSearchFind();
				editButton.setDisable(true);
				editButton.setVisible(false);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Search Tab Action Ended Party Tab Action Started
	 */
	public void onPartySave() {
		try {
			if (party_name.getText().trim().equals("")) {
				Utility.beep();
				Utility.getAlert("Error", "Please insert value for Party Name",
						"Please provide correct value for Party Name field.", null, AlertType.ERROR);
				return;
			}
			Party party = new Party(party_name.getText().trim(), contact_number.getText(), owner_name.getText(),
					address.getText(), email.getText());
			service.createOrUpdateParty(party, isUpdateParty);
		} catch (Exception ex) {
			if (ex.getMessage().contains("Unique"))
				Utility.getAlert("Error", "Error in saving or updating party", "Party with Party Name already Exist.",
						null, AlertType.ERROR);
			else
				Utility.getAlert("Error", "Error in saving or updating party", "", null, AlertType.ERROR);
		}
		onPartyClear();
		onPartyReload();
	}

	public void onPartyClear() {
		party_name.setText("");
		owner_name.setText("");
		contact_number.setText("");
		address.setText("");
		email.setText("");
		isUpdateParty = false;
		party_name.setDisable(false);
		party_save.setText("Save");
	}

	public void onPartyReload() {
		loadPartyName();
	}

	public void onPartyTableClick() {
		if (party_table.getSelectionModel().getSelectedItem() == null)
			return;
		party_name.setText(party_table.getSelectionModel().getSelectedItem().getPartyName());
		owner_name.setText(party_table.getSelectionModel().getSelectedItem().getOwnerName());
		email.setText(party_table.getSelectionModel().getSelectedItem().getEmail());
		contact_number.setText(party_table.getSelectionModel().getSelectedItem().getContactNumber());
		address.setText(party_table.getSelectionModel().getSelectedItem().getAddress());
		isUpdateParty = true;
		party_name.setDisable(true);
		party_save.setText("Update");
	}

	/*
	 * Party Tab Action Ended Payment Tab Action Started
	 */

	public void onPaymentTableClick() {
		if (payment_table.getSelectionModel().getSelectedItem() == null)
			return;
		else {
			paymentEdit.setDisable(false);
			paymentEdit.setVisible(true);
		}
	}

	public void onPaymentFind() {
		String pName = null;
		String date = null;
		String toDate = null;

		if (p_party.getValue() != null) {
			pName = p_party.getValue();
		}

		if (p_date.getValue() != null) {
			date = p_date.getValue().toString();
			if(p_to_date.getValue() != null) {
				toDate = p_to_date.getValue().toString();
			}
		}
		try {
			onPaymentDataClear();
			paymentdata.clear();
			List<Payment> resultList = service.findPaymentByProperties(pName, date, toDate);
			if (resultList != null) {
				for (Payment payment : resultList) {
					PaymentTable pt = new PaymentTable(payment.getPartyName(),
							Utility.getFormattedDate(payment.getPostedDate()), payment.getMode(), payment.getRemarks(),
							payment.getAmount(), payment.getPaymentId());
					paymentdata.add(pt);
				}
			}
			if (paymentdata.size() == 0) {
				payment_table.setPlaceholder(new Label("No visible columns and/or data exist."));
			}
		} catch (Exception ex) {
			Utility.getAlert("Error", "Error in Search Find", ex.getMessage(), null, AlertType.ERROR);
		} finally {
		}
	}

	public void onPaymentClear() {
		p_date.setValue(null);
		p_party.setValue(null);
		paymentEdit.setDisable(true);
		paymentEdit.setVisible(false);
		onPaymentDataClear();
	}

	public void onPaymentDataClear() {
		paymentdata.clear();
	}

	public void onPaymentNew() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/CreatePayment.fxml"));
			fxmlLoader.setController(context.getBean(CreatePaymentController.class));
			Scene scene = new Scene(fxmlLoader.load());
			Stage stage = new Stage();
			stage.setTitle("Create Payment..");
			stage.setScene(scene);
			stage.initStyle(StageStyle.UTILITY);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
			System.out.println("TRYING!");
			stage.setOnCloseRequest(event -> {
				System.out.println("Close Requested");
				onPaymentDataClear();
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onPaymentEdit() {
		try {
			if (payment_table.getSelectionModel().getSelectedItem() == null)
				return;
			Integer id = payment_table.getSelectionModel().getSelectedItem().getId();
			String partyName = payment_table.getSelectionModel().getSelectedItem().getParty();
			String mode = payment_table.getSelectionModel().getSelectedItem().getMode();
			String remarks = payment_table.getSelectionModel().getSelectedItem().getRemark();
			String date = payment_table.getSelectionModel().getSelectedItem().getDate();
			Double payment = payment_table.getSelectionModel().getSelectedItem().getAmount();

			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/CreatePayment.fxml"));

			CreatePaymentController controller = context.getBean(CreatePaymentController.class, id, partyName, remarks,
					mode, date, payment);
			fxmlLoader.setController(controller);
			Scene scene = new Scene(fxmlLoader.load());
			Stage stage = new Stage();
			stage.setTitle("Update Payment..");
			stage.setScene(scene);
			stage.initStyle(StageStyle.UTILITY);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
			System.out.println("TRYING!");
			stage.setOnCloseRequest(event -> {
				System.out.println("Close Requested");
				onPaymentFind();
				paymentEdit.setVisible(false);
				paymentEdit.setDisable(true);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onPaymentExport() {

		if (p_party.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.",
					"Please provide correct value for Party Name field.", null, AlertType.ERROR);
			return;
		}

		if (p_date.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.",
					"Please provide correct value for From Date field.", null, AlertType.ERROR);
			return;
		}

		if (p_to_date.getValue() == null) {
			Utility.getAlert("Error", "Please insert correct value.", "Please provide correct value for To Date field.",
					null, AlertType.ERROR);
			return;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
		String partyName = p_party.getValue();
		String fromDate = p_date.getValue().toString();
		String toDate = p_to_date.getValue().toString();
		String fileName = "Payment_"+partyName + "_" + formatter.format(new Date()) + ".pdf";
		String filePath = PropertyUtil.DOWNLOAD_PDF_PATH + "/" + fileName;

		Map<String, String> input = new HashMap<String, String>();
		input.put("partyName", partyName);
		input.put("fromDate", fromDate);
		input.put("toDate", toDate);
		input.put("fileName", fileName);
		input.put("filePath", filePath);
		Stage stage = new Stage();
		try {
			ProgressIndicator PI = new ProgressIndicator();
			StackPane root = new StackPane();
			root.getChildren().add(PI);
			Scene scene = new Scene(root, 300, 200);
			stage.setScene(scene);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.show();
			if (service.generatePaymentReportByCriteria(input)) {
				Utility.getAlert("Information", "Payment Ledger Created", "Payment Report Exported Sucessfully..", null, AlertType.INFORMATION);
			}
			stage.close();
		} catch (ColumnBuilderException | FileNotFoundException | ClassNotFoundException | JRException e) {
			Utility.getAlert("Error", "Error in Dynamic Jasper", e.getMessage(), null, AlertType.ERROR);
		} finally {
			stage.close();
		}
	
	}

	/*
	 * On Init of Application StartUp
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Utility.setDateFormat(search_from_date);
		Utility.setDateFormat(search_to_date);
		Utility.setDateFormat(p_date);
		Utility.setDateFormat(p_to_date);
		Utility.setDateFormat(r_from_date);
		Utility.setDateFormat(r_to_date);

		search_table.setPlaceholder(new Label("No visible columns and/or data exist."));
		payment_table.setPlaceholder(new Label("No visible columns and/or data exist."));
		report_table.setPlaceholder(new Label("No visible columns and/or data exist."));

		s_slip_no.setCellValueFactory(new PropertyValueFactory<>("slip_no"));
		s_party_name.setCellValueFactory(new PropertyValueFactory<>("party_name"));
		s_vehicle_number.setCellValueFactory(new PropertyValueFactory<>("vehicle_number"));
		s_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		s_amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
		s_fuel_type.setCellValueFactory(new PropertyValueFactory<>("fuel_type"));
		s_rate.setCellValueFactory(new PropertyValueFactory<>("rate"));
		s_posted_date.setCellValueFactory(new PropertyValueFactory<>("posted_date"));
		s_cash.setCellValueFactory(new PropertyValueFactory<>("cash"));
		s_total_amount.setCellValueFactory(new PropertyValueFactory<>("total_amount"));
		search_table.setItems(data);

		tc_party_name.setCellValueFactory(new PropertyValueFactory<>("p_party_name"));
		tc_owner_name.setCellValueFactory(new PropertyValueFactory<>("p_owner_name"));
		tc_contact.setCellValueFactory(new PropertyValueFactory<>("p_contact_number"));
		tc_address.setCellValueFactory(new PropertyValueFactory<>("p_address"));
		tc_email.setCellValueFactory(new PropertyValueFactory<>("p_email"));
		party_table.setItems(partydata);

		pc_party.setCellValueFactory(new PropertyValueFactory<>("pc_party"));
		pc_remark.setCellValueFactory(new PropertyValueFactory<>("pc_remark"));
		pc_mode.setCellValueFactory(new PropertyValueFactory<>("pc_mode"));
		pc_date.setCellValueFactory(new PropertyValueFactory<>("pc_date"));
		pc_id.setCellValueFactory(new PropertyValueFactory<>("pc_id"));
		pc_amount.setCellValueFactory(new PropertyValueFactory<>("pc_amount"));
		payment_table.setItems(paymentdata);
		
		rt_party.setCellValueFactory(new PropertyValueFactory<>("rt_party"));
		rt_from_date.setCellValueFactory(new PropertyValueFactory<>("rt_from_date"));
		rt_to_date.setCellValueFactory(new PropertyValueFactory<>("rt_to_date"));
		rt_report_name.setCellValueFactory(new PropertyValueFactory<>("rt_report_name"));
		rt_location.setCellValueFactory(new PropertyValueFactory<>("rt_location"));
		rt_no.setCellValueFactory(new PropertyValueFactory<>("rt_no"));
		report_table.setItems(reportdata);

		pc_id.setVisible(false);
		loadPartyName();

		editButton.setDisable(true);
		editButton.setVisible(false);
		paymentEdit.setDisable(true);
		paymentEdit.setVisible(false);
		report_open.setDisable(true);
		report_open.setVisible(false);
		report_send.setDisable(true);
		report_send.setVisible(false);
	}

	private void loadPartyName() {
		try {
			ObservableList<String> partyNames = FXCollections.observableArrayList();
			partydata.clear();
			partyNames.clear();
			partyNames.add("");
			partydata.addAll(service.findAllParty(partyEmailMap, partyNames));
			search_party_name.setItems(partyNames);
			p_party.setItems(partyNames);
			r_party.setItems(partyNames);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void backupdbtosql() {
		try {
			if(Utility.createBackupToSql()) {
				Utility.getAlert("Information", "Backup created!", "Sucessfully Created !!", null, AlertType.INFORMATION);
			}
			loadPartyName();
		} catch (Exception e) {
			Utility.getAlert("Error", "Backup not created!", "Please contact with admin", null, AlertType.ERROR);
		}
	}

	public void restoreSqlToDb() {
		FileChooser fileChooser = new FileChooser();
		Stage stage = new Stage();
		File selectedFile = fileChooser.showOpenDialog(stage);
		try {
			if (selectedFile.toString() != null) {
				if (Utility.restoreBackupToDB(selectedFile.toString())) {
					Utility.getAlert("Information", "Backup restored!", "Sucessfully Restored !!", null,
							AlertType.INFORMATION);
				}
				loadPartyName();
			}
		} catch (Exception ex) {
			Utility.getAlert("Error", "Data not restored!", "Please contact with admin", null, AlertType.ERROR);
			return;
		}
	}
	
	public void sendAnEmail(String filePath, String emailAddress, String fromDate, String toDate) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/SendEmail.fxml"));
			
			SendEmailController bean = context.getBean(SendEmailController.class, filePath, emailAddress, fromDate, toDate);
			fxmlLoader.setController(bean);
			Scene scene = new Scene(fxmlLoader.load());
			Stage stage = new Stage();
			stage.setTitle("Sending Email..");
			stage.setScene(scene);
			stage.initStyle(StageStyle.UTILITY);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
			System.out.println("TRYING!");
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}	
	}
	
	public void onReportFind() {
		Long number = null;
		String pName = null;
		String date = null;
		String toDate = null;

		if (!r_no.getText().trim().equals("") && !r_no.getText().equals("0")
				&& Long.parseLong(r_no.getText()) != 0) {
			number = Long.valueOf(r_no.getText());
			r_from_date.setValue(null);
		}

		if (r_party.getValue() != null) {
			pName = r_party.getValue();
		}

		if (r_from_date.getValue() != null) {
			date = r_from_date.getValue().toString();
		}

		if (r_to_date.getValue() != null) {
			toDate = r_to_date.getValue().toString();
		}
		
		onReportDataClear();
		reportdata.clear();

		List<ReportInfo> resultList = service.findReportByProperties(number, pName, date, toDate);
		if (resultList != null) {
			for (ReportInfo report: resultList) {
				ReportTable rt = new ReportTable(report.getId(), report.getPartyName(), Utility.getFormattedDate(report.getFromDate()), Utility.getFormattedDate(report.getToDate()), report.getFileName(), report.getFilePath()); 
				reportdata.add(rt);
			}
		}
		if (reportdata.size() == 0) 
			report_table.setPlaceholder(new Label("No visible columns and/or data exist."));
	}
	
	public void onReportClear() {
		r_no.setText("");
		r_party.setValue(null);
		r_from_date.setValue(null);
		r_to_date.setValue(null);
		report_open.setDisable(true);
		report_open.setVisible(false);
		report_send.setVisible(false);
		report_send.setDisable(true);
		onReportDataClear();
	}
	
	public void onReportDataClear() {
		reportdata.clear();
	}
	
	public void onReportOpen() {
		if (report_table.getSelectionModel().getSelectedItem() == null)
			return;
		String filePath = report_table.getSelectionModel().getSelectedItem().getLocation();
		Utility.displayPDF(filePath);
	}
	
	public void onReportSend() {
		if (report_table.getSelectionModel().getSelectedItem() == null)
			return;
		String partyName = report_table.getSelectionModel().getSelectedItem().getParty();
		String fromDate = report_table.getSelectionModel().getSelectedItem().getFromDate();
		String toDate = report_table.getSelectionModel().getSelectedItem().getToDate();
		String filePath = report_table.getSelectionModel().getSelectedItem().getLocation();
		
		Alert alert = Utility.getAlert("Confirmation", "Send !!", "Do you want to send the PDF?", null,
				AlertType.CONFIRMATION);
		if (alert.getResult().equals(ButtonType.OK)) {
			if (partyEmailMap.containsKey(partyName) && partyEmailMap.get(partyName) == "") {
				Utility.getAlert("Error", "Party Email not Provide", "Please update Party Email Address.", null,
						AlertType.ERROR);
			} else if (PropertyUtil.EMAIL == null && PropertyUtil.PASSWORD == null) {
				sendAnEmail(filePath, partyEmailMap.get(partyName), fromDate, toDate);
			} else {
				Map<String, String> input = new HashMap<String, String>();
				input.put("partyName", partyName);
				input.put("fromDate", fromDate);
				input.put("toDate", toDate);
				input.put("filePath", filePath);
				input.put("fromEmail", PropertyUtil.EMAIL);
				input.put("fromPassword", PropertyUtil.PASSWORD);
				input.put("toClientAddress", partyEmailMap.get(partyName));
				EmailHelper helper = new EmailHelper();
				helper.sendPDFToClient(input, null);
			}
		}
	}
	
	public void onReportTableClick() {
		if (report_table.getSelectionModel().getSelectedItem() == null)
			return;
		else {
			report_open.setDisable(false);
			report_open.setVisible(true);
			report_send.setVisible(true);
			report_send.setDisable(false);
		}
		
	}
}
