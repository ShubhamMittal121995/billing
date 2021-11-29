package com.main.Billing.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PARTY")
public class Party {

	@Id
	@Column(name="party_name")
	private String party_name;
	@Column(name="contact_number")
	private String contact_number;
	@Column(name="owner_name")
	private String owner_name;
	@Column(name="address")
	private String address;
	@Column(name="email")
	private String email;

	public Party(String party_name, String contact_number, String owner_name, String address, String email) {
		super();
		this.party_name = party_name;
		this.contact_number = contact_number;
		this.owner_name = owner_name;
		this.address = address;
		this.email = email;
	}

	public Party() {
	}

	public String getPartyName() {
		return party_name;
	}

	public void setPartyName(String party_name) {
		this.party_name = party_name;
	}

	public String getContactNumber() {
		return contact_number;
	}

	public void setContactNumber(String contact_number) {
		this.contact_number = contact_number;
	}

	public String getOwnerName() {
		return owner_name;
	}

	public void setOwnerName(String owner_name) {
		this.owner_name = owner_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Party [party_name=" + party_name + ", contact_number=" + contact_number + ", owner_name=" + owner_name
				+ ", address=" + address + ", email=" + email + "]";
	};

}

