package model;

public class TopCustomer {
	private String fullName;
	private double totalSpent;

	public TopCustomer(String fullName, double totalSpent) {
		this.fullName = fullName;
		this.totalSpent = totalSpent;
	}

	public String getFullName() {
		return fullName;
	}

	public double getTotalSpent() {
		return totalSpent;
	}
}
