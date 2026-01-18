package model;

public class MonthlyFinanceRow {
	private String month;
	private double salesRevenue;
	private double purchaseExpenses;
	private double profit;
	private double profitMargin;

	public MonthlyFinanceRow(String month, double salesRevenue, double purchaseExpenses, double profit,
			double profitMargin) {
		this.month = month;
		this.salesRevenue = salesRevenue;
		this.purchaseExpenses = purchaseExpenses;
		this.profit = profit;
		this.profitMargin = profitMargin;
	}

	public String getMonth() {
		return month;
	}

	public double getSalesRevenue() {
		return salesRevenue;
	}

	public double getPurchaseExpenses() {
		return purchaseExpenses;
	}

	public double getProfit() {
		return profit;
	}

	public double getProfitMargin() {
		return profitMargin;
	}
}