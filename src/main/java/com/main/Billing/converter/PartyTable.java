package com.main.Billing.converter;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PartyTable {
	public final SimpleStringProperty tc_party_name, tc_owner_name, tc_contact, tc_address, tc_email;

	public PartyTable(String name, String owner, String address, String contact, String email) {
		this.tc_party_name = new SimpleStringProperty(name);
		this.tc_owner_name = new SimpleStringProperty(owner);
		this.tc_contact = new SimpleStringProperty(contact);
		this.tc_address = new SimpleStringProperty(address);
		this.tc_email = new SimpleStringProperty(email);
	}

	public final String getPartyName() {
		return tc_party_name.getValue();
	}

	public final String getOwnerName() {
		return tc_owner_name.getValue();
	}

	public final String getContactNumber() {
		return tc_contact.getValue();
	}

	public final String getAddress() {
		return tc_address.getValue();
	}

	public final String getEmail() {
		return tc_email.getValue();
	}

	public final StringProperty p_party_nameProperty() {
		return tc_party_name;
	}

	public final StringProperty p_owner_nameProperty() {
		return tc_owner_name;
	}

	public final StringProperty p_contact_numberProperty() {
		return tc_contact;
	}

	public final StringProperty p_addressProperty() {
		return tc_address;
	}

	public final StringProperty p_emailProperty() {
		return tc_email;
	}
}
