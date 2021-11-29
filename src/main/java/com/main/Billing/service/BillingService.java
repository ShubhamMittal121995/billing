package com.main.Billing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.main.Billing.entity.Bill;
import com.main.Billing.repository.BillRepositoryImpl;
import com.main.Billing.utility.Utility;

import javafx.scene.control.Alert.AlertType;

@Service
public class BillingService {
	@Autowired
	BillRepositoryImpl billRepository;

	public void deleteBillBySlipNumber(int slipNumber) {
		int row = billRepository.deleteBillById(slipNumber);
		if(row > 0) {
			Utility.beep();
			Utility.getAlert("Information", "Deleted !!", "Bill Deleted successfully!!!", null,
					AlertType.INFORMATION);
		}
	}

	public void createOrUpdateBill(Bill bill, boolean isUpdate) {
		if(isUpdate) {
			billRepository.updateBillByEntity(bill);
		} else 
			billRepository.createBillByEntity(bill);			
		Utility.beep();
		Utility.getAlert("Information", isUpdate ? "Updated !!":"Inserted !!", isUpdate ? "Bill Updated Sucessfully.": "New Bill inserted successfully!!!", null,
				AlertType.INFORMATION);
	}	
}
