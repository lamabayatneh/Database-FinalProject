package model;

import java.time.LocalDate;

public class Purchase {
	private int purchaseID;
	private Supplier supplier;
	private int staffID;
	private LocalDate purchaseDate;
	private double totalCost;

	public Purchase(int purchaseID, Supplier supplier, int staffID, LocalDate purchaseDate, double totalCost) {
		this.purchaseID = purchaseID;
		this.supplier = supplier;
		this.staffID = staffID;
		this.purchaseDate = purchaseDate;
		this.totalCost = totalCost;
	}

	public int getPurchaseID() {
		return purchaseID;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public int getStaffID() {
		return staffID;
	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public double getTotalCost() {
		return totalCost;
	}
}