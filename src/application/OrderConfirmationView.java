package application;

import dao.BookDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Cart;
import model.CartItem;
import model.Customer;

public class OrderConfirmationView {

    public void show(
            Stage stage,
            Customer customer,
            Cart cart,
            double total,
            Runnable backAction
    ) {

        /* ================= BUSINESS LOGIC (CHECKOUT) ================= */

        // 1️⃣ إنشاء الطلب
        int orderId = OrderDAO.createOrder(
                customer.getCustomerID(),
                total
        );

        if (orderId == -1) {
            new Alert(
                    Alert.AlertType.ERROR,
                    "Failed to create order. Please try again."
            ).showAndWait();
            return;
        }

        // 2️⃣ حفظ عناصر الطلب + إنقاص الكمية
        for (CartItem item : cart.getItems()) {

            // تأكد إن الكمية كافية (أمان)
            if (item.getQuantity() > item.getBook().getQuantity()) {
                new Alert(
                        Alert.AlertType.ERROR,
                        "Not enough stock for book: " +
                                item.getBook().getTitle()
                ).showAndWait();
                return;
            }

            // حفظ عنصر الطلب
            OrderItemDAO.insertItem(orderId, item);

            // إنقاص الكمية من الكتاب
            BookDAO.decreaseQuantity(
                    item.getBook().getBookID(),
                    item.getQuantity()
            );
        }

        // 3️⃣ تفريغ السلة بعد نجاح الشراء
        cart.clear();

        /* ================= UI ================= */

        Label title = new Label("✅ Order Confirmed");
        title.getStyleClass().add("sb-title");

        Label customerLabel = new Label(
                "Customer: " + customer.getFullName()
        );

        Label totalLabel = new Label(
                "Total Paid: $ " + String.format("%.2f", total)
        );

        Button backBtn = new Button("Back to Shop");
        backBtn.getStyleClass().addAll("sb-pill", "sb-primary");
        backBtn.setOnAction(s -> backAction.run());

        VBox root = new VBox(
                20,
                title,
                customerLabel,
                totalLabel,
                backBtn
        );
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("sb-page");

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle("Order Confirmation");
        stage.show();
    }
}