package com.main.Billing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.main.Billing.entity.Payment;
import com.main.Billing.repository.PaymentRepositoryImpl;
import com.main.Billing.utility.Utility;

import javafx.scene.control.Alert.AlertType;

@Service
public class PaymentService {
	
	@Autowired
	PaymentRepositoryImpl paymentRepository;
	
	public void deletePaymentById(Integer paymentId) {
		int row = paymentRepository.deletePaymentById(paymentId);
		if(row > 0) {
			Utility.beep();
			Utility.getAlert("Information", "Deleted !!", "Payment Deleted successfully!!!", null,
					AlertType.INFORMATION);
		}
		
	}

	public Integer createPayment(Payment payment) {
		Integer id = paymentRepository.createPaymentByEntity(payment);			
		Utility.beep();
		Utility.getAlert("Information", "Inserted !!", "New Payment inserted successfully!!!", null,
				AlertType.INFORMATION);
		return id;
	}

	public void updatePayment(Integer paymentId, Payment payment) {
		Payment oldPayment = paymentRepository.findPaymentByPaymentId(paymentId);
		if(oldPayment != null) {
			payment.setPaymentId(oldPayment.getPaymentId());
			paymentRepository.updatePayment(payment);
			Utility.beep();
			Utility.getAlert("Information", "Updated !!", "Payment updated successfully!!!", null,
					AlertType.INFORMATION);
		} else {
			createPayment(payment);
		}
	}

}
