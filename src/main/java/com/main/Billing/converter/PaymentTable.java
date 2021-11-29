package com.main.Billing.converter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PaymentTable {
	public final SimpleStringProperty pc_party, pc_date, pc_mode, pc_remark;
	public final SimpleDoubleProperty pc_amount;
	public final SimpleIntegerProperty pc_id;

	public PaymentTable(String partyName, String date, String mode, String remark, Double amount, int pc_id) {
		this.pc_party = new SimpleStringProperty(partyName);
		this.pc_date = new SimpleStringProperty(date);
		this.pc_mode = new SimpleStringProperty(mode);
		this.pc_remark = new SimpleStringProperty(remark);
		this.pc_amount = new SimpleDoubleProperty(amount);
		this.pc_id = new SimpleIntegerProperty(pc_id);
	}

	public final String getParty() {
		return pc_party.getValue();
	}

	public final String getDate() {
		return pc_date.getValue();
	}

	public final String getMode() {
		return pc_mode.getValue();
	}

	public final String getRemark() {
		return pc_remark.getValue();
	}

	public final Double getAmount() {
		return pc_amount.getValue();
	}

	public final Integer getId() {
		return pc_id.getValue();
	}

	public final StringProperty pc_partyProperty() {
		return pc_party;
	}

	public final StringProperty pc_dateProperty() {
		return pc_date;
	}

	public final StringProperty pc_modeProperty() {
		return pc_mode;
	}

	public final StringProperty pc_remarkProperty() {
		return pc_remark;
	}

	public final IntegerProperty pc_idProperty() {
		return pc_id;
	}

	public final DoubleProperty pc_amountProperty() {
		return pc_amount;
	}
}
