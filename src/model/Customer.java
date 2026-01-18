package model;

import java.time.LocalDate;

public class Customer {

	private int customerID;
	private String fullName;
	private String email;
	private String phone;
	private String address;
	private String city;
	private LocalDate registrationDate;
	private int userID;

	public Customer(int customerID, int userID, String fullName, String email, String phone, String address,
			String city, LocalDate registrationDate) {
		super();
		this.customerID = customerID;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.city = city;
		this.registrationDate = registrationDate;
		this.userID = userID;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

}