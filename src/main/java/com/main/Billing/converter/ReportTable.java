package com.main.Billing.converter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReportTable {
	public SimpleStringProperty rt_party, rt_from_date, rt_to_date, rt_report_name, rt_location;
	public SimpleIntegerProperty rt_no;

	public ReportTable(int no, String party, String fromDate, String toDate, String reportName, String location) {
		this.rt_no = new SimpleIntegerProperty(no);
		this.rt_from_date = new SimpleStringProperty(fromDate);
		this.rt_party = new SimpleStringProperty(party);
		this.rt_to_date = new SimpleStringProperty(toDate);
		this.rt_report_name = new SimpleStringProperty(reportName);
		this.rt_location = new SimpleStringProperty(location);
	}

	public String getParty() {
		return rt_party.getValue();
	}

	public String getFromDate() {
		return rt_from_date.getValue();
	}

	public String getToDate() {
		return rt_to_date.getValue();
	}

	public String getReportName() {
		return rt_report_name.getValue();
	}

	public String getLocation() {
		return rt_location.getValue();
	}

	public Integer getId() {
		return rt_no.getValue();
	}

	public StringProperty rt_partyProperty() {
		return rt_party;
	}

	public StringProperty rt_from_dateProperty() {
		return rt_from_date;
	}

	public StringProperty rt_to_dateProperty() {
		return rt_to_date;
	}

	public StringProperty rt_report_nameProperty() {
		return rt_report_name;
	}

	public StringProperty rt_locationProperty() {
		return rt_location;
	}

	public IntegerProperty rt_noProperty() {
		return rt_no;
	}

}
