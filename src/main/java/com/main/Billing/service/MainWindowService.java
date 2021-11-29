package com.main.Billing.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.main.Billing.converter.PartyTable;
import com.main.Billing.entity.Bill;
import com.main.Billing.entity.Party;
import com.main.Billing.entity.Payment;
import com.main.Billing.entity.ReportInfo;
import com.main.Billing.repository.BillRepositoryImpl;
import com.main.Billing.repository.PartyRepositoryImpl;
import com.main.Billing.repository.ReportInfoImpl;
import com.main.Billing.utility.PDFReportCreator;
import com.main.Billing.utility.PropertyUtil;
import com.main.Billing.utility.Utility;

import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class MainWindowService {

	@Autowired
	PartyRepositoryImpl partyRepository;
	@Autowired
	BillRepositoryImpl billRepository;
	@Autowired
	ReportInfoImpl reportRepository;

	public void createOrUpdateParty(Party party, boolean isUpdateParty) {
		if (isUpdateParty) {
			partyRepository.updateParty(party);
		} else
			partyRepository.createParty(party);
		Utility.beep();
		Utility.getAlert("Information", isUpdateParty ? "Updated !!" : "Inserted !!",
				isUpdateParty ? "Party Updated Sucessfully." : "New Party inserted successfully!!!", null,
				AlertType.INFORMATION);
	}

	public ObservableList<PartyTable> findAllParty(Map<String, String> partyEmailMap,
			ObservableList<String> partyNames) {
		ObservableList<PartyTable> partydata = FXCollections.observableArrayList();
		List<Party> partyList = partyRepository.findAllParty();
		if (partyList != null) {
			for (Party party : partyList) {
				PartyTable partyTable = new PartyTable(party.getPartyName(), party.getOwnerName(), party.getAddress(),
						party.getContactNumber(), party.getEmail());

				if (partyEmailMap != null)
					partyEmailMap.put(party.getPartyName(), party.getEmail());

				partyNames.add(party.getPartyName());
				partydata.add(partyTable);
			}
		}
		return partydata;
	}

	public List<Bill> findBillByProperties(Long slipNumber, String pName, String date, String toDate) {
		StringBuilder sb = new StringBuilder("SELECT b FROM Bill b ");
		sb.append("WHERE b.recordActiveInd = 'Y' and ");
		if (slipNumber != null)
			sb.append(" b.slipNumber = '" + slipNumber + "' and ");
		if (pName != null)
			sb.append(" b.partyName = '" + pName + "' and ");
		if (date != null) {
			if (toDate != null)
				sb.append(" b.postedDate >= '" + date + "' and b.postedDate <= '" + toDate + "' and ");
			else
				sb.append(" b.postedDate = '" + date + "' and ");
		}
		String sql = sb.toString().substring(0, sb.toString().length() - 4);
		sql += " order by posted_date";

		return billRepository.findBillByQuery(sql);
	}

	public List<Payment> findPaymentByProperties(String pName, String date, String toDate) {
		StringBuilder sb = new StringBuilder("SELECT p FROM Payment p ");
		sb.append("WHERE p.activeInd = 'Y' and ");
		if (pName != null)
			sb.append(" p.partyName = '" + pName + "' and ");
		if (date != null) {
			if(toDate != null) {
				sb.append(" p.postedDate >= '" + date + "' and p.postedDate <= '"+toDate+"' and ");
			} else 
				sb.append(" p.postedDate = '" + date + "' and ");
		}	
		String query = sb.toString().substring(0, sb.toString().length() - 4);
		query += " order by p.postedDate";

		return billRepository.findPaymentByQuery(query);
	}

	public boolean generateReportByCriteria(Map<String, String> input, Map<String, String> partyEmailMap)
			throws FileNotFoundException, JRException, ColumnBuilderException, ClassNotFoundException {
		PDFReportCreator creator = new PDFReportCreator();
		JasperPrint jp;
		String filePath = input.get("filePath").toString();
		jp = creator.getReport(input);

		Alert alert = Utility.getAlert("Confirmation", "Download !!", "Do you want to download the PDF?", null,
				AlertType.CONFIRMATION);
		if (alert.getResult().equals(ButtonType.OK)) {
			File path = new File(PropertyUtil.DOWNLOAD_PDF_PATH);
			if (!path.isDirectory()) {
				path.mkdir();
			}
			JasperExportManager.exportReportToPdfStream(jp, new FileOutputStream(filePath));
			createEntryForReport(input, filePath);
			return true;
		}
		return false;
	}
	
	

	public void createEntryForReport(Map<String, String> input, String filePath) {
		ReportInfo reportInfo = new ReportInfo(input.get("partyName").toString(),
				Date.valueOf(input.get("fromDate").toString()), Date.valueOf((String) input.get("toDate")), filePath, input.get("fileName").toString());
		reportRepository.createReportInfo(reportInfo);
	}

	public boolean generatePaymentReportByCriteria(Map<String, String> input)
			throws ColumnBuilderException, ClassNotFoundException, JRException, FileNotFoundException {
		PDFReportCreator creator = new PDFReportCreator();
		JasperPrint jp;
		String filePath = input.get("filePath").toString();
		jp = creator.getPaymentReport(input);

		File path = new File(PropertyUtil.DOWNLOAD_PDF_PATH);
		if (!path.isDirectory()) {
			path.mkdir();
		}
		JasperExportManager.exportReportToPdfStream(jp, new FileOutputStream(filePath));
		createEntryForReport(input, filePath);
		return true;
	}

	public List<ReportInfo> findReportByProperties(Long number, String pName, String date, String toDate) {
		StringBuilder sb = new StringBuilder("SELECT r FROM ReportInfo r ");
		sb.append("WHERE ");
		if (number != null)
			sb.append(" r.id = '" + number + "' and ");
		if (pName != null)
			sb.append(" r.partyName = '" + pName + "' and ");
		if (date != null) {
			if (toDate != null)
				sb.append(" r.fromDate >= '" + date + "' and r.toDate <= '" + toDate + "' and ");
			else
				sb.append(" r.fromDate = '" + date + "' and ");
		}
		String sql = sb.toString().substring(0, sb.toString().length() - 4);
		sql += " order by r.id";

		return reportRepository.findReportByQuery(sql);
	}

}
