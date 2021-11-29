package com.main.Billing.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REPORT_INFO")
public class ReportInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@Column(name="PARTY_NAME")
	private String partyName;
	@Column(name="FILE_NAME")
	private String fileName;
	@Column(name="FROM_DATE")
	private Date fromDate;
	@Column(name="TO_DATE")
	private Date toDate;
	@Column(name="FILE_LOCATION")
	private String filePath;
	
	public ReportInfo(String partyName, Date fromDate, Date toDate, String filePath, String fileName) {
		super();
		this.partyName = partyName;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.filePath = filePath;
		this.fileName = fileName;
	}

	public ReportInfo() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "ReportInfo [id=" + id + ", partyName=" + partyName + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", filePath=" + filePath + "]";
	}
}
