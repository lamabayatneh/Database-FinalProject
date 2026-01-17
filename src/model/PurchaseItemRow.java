package model;

public class PurchaseItemRow {
	private Book book;
	private int quantity;
	private double unitCost;

	public PurchaseItemRow(Book book, int quantity, double unitCost) {
		this.book = book;
		this.quantity = quantity;
		this.unitCost = unitCost;
	}

	public Book getBook() {
		return book;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getUnitCost() {
		return unitCost;
	}

	public double getSubtotal() {
		return quantity * unitCost;
	}
}