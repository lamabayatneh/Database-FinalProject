package application;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.CartItem;

import java.time.LocalDate;
import java.util.List;

public class OrderConfirmationView {

	public void show(Stage stage, int orderId, List<CartItem> items, double total, Runnable backAction) {

		Label title = new Label("✅ Order Confirmed");
		title.setStyle("-fx-font-size:24px; -fx-font-weight:bold;");

		Label orderInfo = new Label("Order #" + orderId + "   |   Date: " + LocalDate.now());

		VBox header = new VBox(5, title, orderInfo);

		Label customerLabel = new Label("👤 Customer: " + Session.currentCustomer.getFullName());

		TableView<CartItem> table = new TableView<>();
		table.setItems(FXCollections.observableArrayList(items));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<CartItem, String> colBook = new TableColumn<>("Book");
		colBook.setCellValueFactory(
				c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getBook().getTitle()));

		TableColumn<CartItem, Integer> colQty = new TableColumn<>("Qty");
		colQty.setCellValueFactory(
				c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantity()).asObject());

		TableColumn<CartItem, Double> colPrice = new TableColumn<>("Price");
		colPrice.setCellValueFactory(
				c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getBook().getPrice()).asObject());

		TableColumn<CartItem, Double> colSub = new TableColumn<>("Subtotal");
		colSub.setCellValueFactory(
				c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getSubtotal()).asObject());

		table.getColumns().addAll(colBook, colQty, colPrice, colSub);

		Label totalLabel = new Label("💰 Total: $" + total);
		totalLabel.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

		Button doneBtn = new Button("🏠 Back to Store");
		doneBtn.setStyle("""
				    -fx-background-color:#3498db;
				    -fx-text-fill:white;
				    -fx-font-size:15px;
				    -fx-padding:10 30;
				    -fx-background-radius:8;
				""");

		doneBtn.setOnAction(e -> backAction.run());

		HBox footer = new HBox(doneBtn);
		footer.setAlignment(Pos.CENTER_RIGHT);

		VBox card = new VBox(15, header, customerLabel, table, totalLabel, footer);

		card.setPadding(new Insets(25));
		card.setStyle("""
				    -fx-background-color:white;
				    -fx-background-radius:15;
				    -fx-effect:dropshadow(gaussian,#cccccc,20,0.3,0,5);
				""");

		BorderPane root = new BorderPane(card);
		root.setPadding(new Insets(30));
		root.setStyle("""
				    -fx-background-color:linear-gradient(to bottom,#f5f7fa,#e0e6ed);
				""");

		stage.setScene(new Scene(root, 900, 600));
		stage.setTitle("Order Confirmation");
	}
}
