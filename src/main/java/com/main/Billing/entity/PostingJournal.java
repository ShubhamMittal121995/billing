package com.main.Billing.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "POSTING_JOURNAL")
public class PostingJournal {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "posting_id")
	private Integer postingId;
	@Column(name = "slip_number")
	private Integer slipNumber;
	@Column(name = "payment_id")
	private Integer paymentId;
	@Column(name = "party_name")
	private String partyName;
	@Column(name = "posted_date")
	private Date postedDate;
	@Column(name = "debit_amount")
	private Double debitAmount;
	@Column(name = "credit_amount")
	private Double creditAmount;
	@Column(name = "record_active_ind")
	private String recordActiveInd;
	@Column(name = "mode")
	private String mode;
	@Column(name= "reference")
	private String reference;

	public PostingJournal(Integer slipNumber, Integer paymentId, Date postedDate, Double debitAmount,
			Double creditAmount, String recordActiveInd, String mode, String reference, String partyName) {
		super();
		this.slipNumber = slipNumber;
		this.paymentId = paymentId;
		this.postedDate = postedDate;
		this.debitAmount = debitAmount;
		this.creditAmount = creditAmount;
		this.recordActiveInd = recordActiveInd;
		this.mode = mode;
		this.reference = reference;
		this.partyName = partyName;
	}

	public PostingJournal() {
		super();
	}

	public Integer getPostingId() {
		return postingId;
	}

	public void setPostingId(Integer postingId) {
		this.postingId = postingId;
	}

	public Integer getSlipNumber() {
		return slipNumber;
	}

	public void setSlipNumber(Integer slipNumber) {
		this.slipNumber = slipNumber;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public Date getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}

	public Double getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(Double debitAmount) {
		this.debitAmount = debitAmount;
	}

	public Double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getRecordActiveInd() {
		return recordActiveInd;
	}

	public void setRecordActiveInd(String recordActiveInd) {
		this.recordActiveInd = recordActiveInd;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	@Override
	public String toString() {
		return "PostingJournal [postingId=" + postingId + ", slipNumber=" + slipNumber + ", paymentId=" + paymentId
				+ ", postedDate=" + postedDate + ", debitAmount=" + debitAmount + ", creditAmount=" + creditAmount
				+ ", recordActiveInd=" + recordActiveInd + "]";
	}

}
