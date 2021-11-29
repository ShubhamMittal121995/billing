package com.main.Billing.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BILL")
public class Bill {
	@Id
	@Column(name="slip_number")
	private Integer slipNumber;
	@Column(name="party_name")
	private String partyName;
	@Column(name="vehicle_number")
	private String vehicleNumber;
	@Column(name="posted_date")
	private Date postedDate;
	@Column(name="fuel_type")
	private String fuelType;
	@Column(name="quantity")
	private Double quantity;
	@Column(name="rate")
	private Double rate;
	@Column(name="amount")
	private Double amount;
	@Column(name="cash")
	private Double cash;
	@Column(name="total_amount")
	private Double totalAmount;
	@Column(name="record_active_ind")
	private String recordActiveInd;
	@Column(name="posted_status")
	private String postedStatus;

	public Bill() {
		super();
	}

	@Override
	public String toString() {
		return "Bill [slipNumber=" + slipNumber + ", partyName=" + partyName + ", vehicleNumber=" + vehicleNumber
				+ ", postedDate=" + postedDate + ", fuelType=" + fuelType + ", quantity=" + quantity + ", rate=" + rate
				+ ", amount=" + amount + ", cash=" + cash + ", totalAmount=" + totalAmount + ", recordActiveInd="
				+ recordActiveInd + ", postedStatus=" + postedStatus + "]";
	}

	public Bill(Integer slipNumber, String partyName, String vehicleNumber, Date postedDate, String fuelType,
			Double quantity, Double rate, Double amount, Double cash, Double totalAmount, String recordActiveInd,
			String postedStatus) {
		super();
		this.slipNumber = slipNumber;
		this.partyName = partyName;
		this.vehicleNumber = vehicleNumber;
		this.postedDate = postedDate;
		this.fuelType = fuelType;
		this.quantity = quantity;
		this.rate = rate;
		this.amount = amount;
		this.cash = cash;
		this.totalAmount = totalAmount;
		this.recordActiveInd = recordActiveInd;
		this.postedStatus = postedStatus;
	}

	public Integer getSlipNumber() {
		return slipNumber;
	}

	public void setSlipNumber(Integer slipNumber) {
		this.slipNumber = slipNumber;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public Date getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = cash;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getRecordActiveInd() {
		return recordActiveInd;
	}

	public void setRecordActiveInd(String recordActiveInd) {
		this.recordActiveInd = recordActiveInd;
	}

	public String getPostedStatus() {
		return postedStatus;
	}

	public void setPostedStatus(String postedStatus) {
		this.postedStatus = postedStatus;
	}
}

