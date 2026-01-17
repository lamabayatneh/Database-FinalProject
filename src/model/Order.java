package model;

import java.time.LocalDate;

public class Order {
	private int orderId;
	private int customerId;
	private String customerName;
	private LocalDate orderDate;
	private double total;

	public Order(int orderId, int customerId, String customerName, LocalDate orderDate, double total) {
		this.orderId = orderId;
		this.customerId = customerId;
		this.customerName = customerName;
		this.orderDate = orderDate;
		this.total = total;
	}

	public int getOrderId() {
		return orderId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public double getTotal() {
		return total;
	}
}