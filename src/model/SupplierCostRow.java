package model;

public class SupplierCostRow {
	private final int supplierId;
	private final String supplierName;
	private final double cost;

	public SupplierCostRow(int supplierId, String supplierName, double cost) {
		this.supplierId = supplierId;
		this.supplierName = supplierName;
		this.cost = cost;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public double getCost() {
		return cost;
	}
}