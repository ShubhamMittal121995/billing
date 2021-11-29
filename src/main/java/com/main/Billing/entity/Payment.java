package com.main.Billing.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PAYMENT")
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="payment_id")
	private Integer paymentId;
	@Column(name="party_name")
	private String partyName;
	@Column(name="mode")
	private String mode;
	@Column(name="remarks")
	private String remarks;
	@Column(name="posted_date")
	private Date postedDate;
	@Column(name="active_ind")
	private String activeInd;
	@Column(name="amount")
	private Double amount;
	@Column(name="posted_status")
	private String postedStatus;

	public Payment() {
		super();
	}

	public Payment(String partyName, String mode, String remarks, Date postedDate, String activeInd,
			Double amount, String postedStatus) {
		super();
		this.partyName = partyName;
		this.mode = mode;
		this.remarks = remarks;
		this.postedDate = postedDate;
		this.activeInd = activeInd;
		this.amount = amount;
		this.postedStatus = postedStatus;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}

	public String getActiveInd() {
		return activeInd;
	}

	public void setActiveInd(String activeInd) {
		this.activeInd = activeInd;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getPostedStatus() {
		return postedStatus;
	}

	public void setPostedStatus(String postedStatus) {
		this.postedStatus = postedStatus;
	}

	@Override
	public String toString() {
		return "Payment [paymentId=" + paymentId + ", partyName=" + partyName + ", mode=" + mode + ", remarks="
				+ remarks + ", postedDate=" + postedDate + ", activeInd=" + activeInd + ", amount=" + amount
				+ ", postedStatus=" + postedStatus + "]";
	}
}
