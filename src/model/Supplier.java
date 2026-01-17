package model;

public class Supplier {
	private int supplierID;
	private String supplierName;
	private String city;
	private String email;
	private String phone;
	private String contactPerson;

	public Supplier() {
	}

	public Supplier(int supplierID, String supplierName, String city, String email, String phone,
			String contactPerson) {
		this.supplierID = supplierID;
		this.supplierName = supplierName;
		this.city = city;
		this.email = email;
		this.phone = phone;
		this.contactPerson = contactPerson;
	}

	public int getSupplierID() {
		return supplierID;
	}

	public void setSupplierID(int supplierID) {
		this.supplierID = supplierID;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	// مهم لعرض الاسم في ComboBox
	@Override
	public String toString() {
		return supplierName == null ? "" : supplierName;
	}
}