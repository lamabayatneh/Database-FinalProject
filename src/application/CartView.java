package application;

import java.util.List;

import dao.BookDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.CartItem;

public class CartView {

	public void show(Stage stage, Runnable backAction) {

		Label title = new Label("🛒 Your Shopping Cart");
		title.setStyle("-fx-font-size:22px; -fx-font-weight:bold;");

		Button backBtn = new Button("⬅ Back");
		backBtn.setOnAction(e -> backAction.run());

		HBox topBar = new HBox(15, title, backBtn);
		topBar.setPadding(new Insets(15));
		topBar.setAlignment(Pos.CENTER_LEFT);

		TableView<CartItem> table = new TableView<>();
		table.setItems(FXCollections.observableArrayList(Session.cart.getItems()));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<CartItem, String> colTitle = new TableColumn<>("Book");
		colTitle.setCellValueFactory(
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

		table.getColumns().addAll(colTitle, colQty, colPrice, colSub);

		Label totalLabel = new Label("Total: $ " + Session.cart.getTotal());
		totalLabel.setStyle("""
				    -fx-font-size:18px;
				    -fx-font-weight:bold;
				""");
		if (Session.currentCustomer == null) {
			new Alert(Alert.AlertType.ERROR, "Please login first").show();
			return;
		}

		Button checkoutBtn = new Button("✅ Checkout");
		checkoutBtn.setStyle("""
				    -fx-background-color:#27ae60;
				    -fx-text-fill:white;
				    -fx-font-size:16px;
				    -fx-padding:10 30;
				    -fx-background-radius:10;
				""");

		checkoutBtn.setOnAction(e -> {

			if (Session.cart.getItems().isEmpty()) {
				new Alert(Alert.AlertType.WARNING, "Cart is empty!").show();
				return;
			}

			for (CartItem item : Session.cart.getItems()) {
				int available = BookDAO.getQuantity(item.getBook().getBookID());

				if (item.getQuantity() > available) {
					new Alert(Alert.AlertType.ERROR,
							"Not enough stock for: " + item.getBook().getTitle() + "\nAvailable: " + available).show();
					return;
				}
			}

			int orderId = OrderDAO.createOrder(Session.currentCustomer.getCustomerID(), Session.cart.getTotal());

			if (orderId == -1) {
				new Alert(Alert.AlertType.ERROR, "Checkout failed").show();
				return;
			}

			var orderedItems = List.copyOf(Session.cart.getItems());
			double total = Session.cart.getTotal();

			Session.cart.getItems().forEach(item -> {
				OrderItemDAO.insertItem(orderId, item);
				BookDAO.decreaseQuantity(item.getBook().getBookID(), item.getQuantity());
			});

			Session.cart.clear();

			new OrderConfirmationView().show(stage, orderId, orderedItems, total, backAction);

		});

		VBox bottomBox = new VBox(15, totalLabel, checkoutBtn);
		bottomBox.setAlignment(Pos.CENTER_RIGHT);
		bottomBox.setPadding(new Insets(15));

		VBox card = new VBox(15, table, bottomBox);
		card.setPadding(new Insets(20));
		card.setStyle("""
				    -fx-background-color:white;
				    -fx-background-radius:15;
				    -fx-effect:dropshadow(gaussian,#cccccc,20,0.3,0,5);
				""");

		BorderPane root = new BorderPane();
		root.setTop(topBar);
		root.setCenter(card);
		root.setPadding(new Insets(20));
		root.setStyle("""
				    -fx-background-color:linear-gradient(to bottom,#f5f7fa,#e0e6ed);
				""");

		stage.setScene(new Scene(root, 800, 500));
		stage.setTitle("Cart");
	}

}
