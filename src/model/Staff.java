package model;

import java.util.List;

public class Staff {
	private String fullName;
	private String position;
	private double salary;
	private List<Customer> customers;

	public Staff(String fullName, String position, double salary) {
		this.fullName = fullName;
		this.position = position;
		this.salary = salary;
	}

	public String getFullName() {
		return fullName;
	}

	public String getPosition() {
		return position;
	}

	public double getSalary() {
		return salary;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}
}
