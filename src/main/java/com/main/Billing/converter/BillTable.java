package com.main.Billing.converter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BillTable {
	public final SimpleStringProperty s_party_name, s_vehicle_number, s_posted_date, s_fuel_type;

	public final SimpleDoubleProperty s_rate, s_amount, s_quantity, s_cash, s_total_amount;

	public final SimpleIntegerProperty s_slip_no;

	public BillTable(int slipNo, String partyName, String vehicleNumber, String postedDate, String fuelType, Double rate,
			Double amount, Double quantity, Double cash, Double totalAmount) {
		this.s_slip_no = new SimpleIntegerProperty(slipNo);
		this.s_party_name = new SimpleStringProperty(partyName);
		this.s_vehicle_number = new SimpleStringProperty(vehicleNumber);
		this.s_posted_date = new SimpleStringProperty(postedDate);
		this.s_fuel_type = new SimpleStringProperty(fuelType);
		this.s_quantity = new SimpleDoubleProperty(quantity);
		this.s_rate = new SimpleDoubleProperty(rate);
		this.s_amount = new SimpleDoubleProperty(amount);
		this.s_cash = new SimpleDoubleProperty(cash);
		this.s_total_amount = new SimpleDoubleProperty(totalAmount);
	}

	public final String getPartyName() {
		return s_party_name.getValue();
	}

	public final String getVehicleNumber() {
		return s_vehicle_number.getValue();
	}

	public final String getPostedDate() {
		return s_posted_date.getValue();
	}

	public final String getFuelType() {
		return s_fuel_type.getValue();
	}

	public final Double getQuantity() {
		return s_quantity.getValue();
	}

	public final Double getRate() {
		return s_rate.getValue();
	}

	public final Double getAmount() {
		return s_amount.getValue();
	}

	public final Integer getSlipNumber() {
		return s_slip_no.getValue();
	}

	public final Double getCash() {
		return s_cash.getValue();
	}

	public final Double getTotalAmount() {
		return s_total_amount.getValue();
	}

	public final IntegerProperty slip_noProperty() {
		return s_slip_no;
	}

	public final StringProperty party_nameProperty() {
		return s_party_name;
	}

	public final StringProperty vehicle_numberProperty() {
		return s_vehicle_number;
	}

	public final StringProperty fuel_typeProperty() {
		return s_fuel_type;
	}

	public final DoubleProperty quantityProperty() {
		return s_quantity;
	}

	public final DoubleProperty rateProperty() {
		return s_rate;
	}

	public final DoubleProperty amountProperty() {
		return s_amount;
	}

	public final StringProperty posted_dateProperty() {
		return s_posted_date;
	}

	public final DoubleProperty cashProperty() {
		return s_cash;
	}

	public final DoubleProperty total_amountProperty() {
		return s_total_amount;
	}
}
