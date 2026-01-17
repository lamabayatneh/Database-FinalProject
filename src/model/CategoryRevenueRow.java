package model;

public class CategoryRevenueRow {
	private final int categoryId;
	private final String categoryName;
	private final double revenue;

	public CategoryRevenueRow(int categoryId, String categoryName, double revenue) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.revenue = revenue;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public double getRevenue() {
		return revenue;
	}
}