package dao;

import db.DBConnection;
import model.CategoryRevenueRow;
import model.MonthlyFinanceRow;
import model.SupplierCostRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Category;

public class ReportDAO {

    public static List<MonthlyFinanceRow> getMonthlyFinance() {
        List<MonthlyFinanceRow> list = new ArrayList<>();

        String sql = """
            WITH months AS (
                SELECT DATE_FORMAT(OrderDate, '%Y-%m') AS m FROM Orders
                UNION
                SELECT DATE_FORMAT(PurchaseDate, '%Y-%m') AS m FROM Purchase
            ),
            sales AS (
                SELECT DATE_FORMAT(OrderDate, '%Y-%m') AS m, SUM(TotalAmount) AS totalSales
                FROM Orders
                GROUP BY m
            ),
            purchases AS (
                SELECT DATE_FORMAT(PurchaseDate, '%Y-%m') AS m, SUM(TotalCost) AS totalPurchases
                FROM Purchase
                GROUP BY m
            )
            SELECT 
                months.m AS Month,
                IFNULL(sales.totalSales, 0) AS SalesRevenue,
                IFNULL(purchases.totalPurchases, 0) AS PurchaseExpenses,
                (IFNULL(sales.totalSales,0) - IFNULL(purchases.totalPurchases,0)) AS Profit,
                CASE 
                    WHEN IFNULL(sales.totalSales,0) = 0 THEN 0
                    ELSE ROUND(((IFNULL(sales.totalSales,0) - IFNULL(purchases.totalPurchases,0)) / IFNULL(sales.totalSales,0)) * 100, 2)
                END AS ProfitMargin
            FROM months
            LEFT JOIN sales ON sales.m = months.m
            LEFT JOIN purchases ON purchases.m = months.m
            ORDER BY months.m
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new MonthlyFinanceRow(
                        rs.getString("Month"),
                        rs.getDouble("SalesRevenue"),
                        rs.getDouble("PurchaseExpenses"),
                        rs.getDouble("Profit"),
                        rs.getDouble("ProfitMargin")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    
    public static Category getMostProfitableCategory() {

        String sql = """
            SELECT 
                c.CategoryID,
                c.CategoryName,
                c.Description,
                SUM(oi.Subtotal) AS Revenue
            FROM OrderItem oi
            JOIN Book b ON oi.BookID = b.BookID
            JOIN Category c ON b.CategoryID = c.CategoryID
            GROUP BY c.CategoryID, c.CategoryName, c.Description
            ORDER BY Revenue DESC
            LIMIT 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                Category c = new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName"),
                        rs.getString("Description")
                );
                c.setRevenue(rs.getDouble("Revenue"));
                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static List<MonthlyFinanceRow> getNegativeProfitMonths() {
        List<MonthlyFinanceRow> all = getMonthlyFinance();
        List<MonthlyFinanceRow> neg = new ArrayList<>();
        for (MonthlyFinanceRow r : all) {
            if (r.getProfit() < 0) neg.add(r);
        }
        return neg;
    }

    // 6) Most profitable category based on SALES revenue
    public static CategoryRevenueRow getTopCategoryBySalesRevenue() {
        String sql = """
            SELECT c.CategoryID, c.CategoryName,
                   SUM(COALESCE(oi.Subtotal, oi.Quantity * b.Price)) AS revenue
            FROM Orders o
            JOIN OrderItem oi ON oi.OrderID = o.OrderID
            JOIN Book b ON b.BookID = oi.BookID
            JOIN Category c ON c.CategoryID = b.CategoryID
            GROUP BY c.CategoryID, c.CategoryName
            ORDER BY revenue DESC
            LIMIT 1;
        """;

        try (Connection con = db.DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new CategoryRevenueRow(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName"),
                        rs.getDouble("revenue")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 7) Supplier with highest total purchase cost
    public static SupplierCostRow getTopSupplierByPurchaseCost() {
        String sql = """
            SELECT s.SupplierID, s.SupplierName,
                   SUM(pi.Quantity * pi.UnitCost) AS cost
            FROM Purchase pu
            JOIN Supplier s ON s.SupplierID = pu.SupplierID
            JOIN PurchaseItem pi ON pi.PurchaseID = pu.PurchaseID
            GROUP BY s.SupplierID, s.SupplierName
            ORDER BY cost DESC
            LIMIT 1;
        """;

        try (Connection con = db.DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new SupplierCostRow(
                        rs.getInt("SupplierID"),
                        rs.getString("SupplierName"),
                        rs.getDouble("cost")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}