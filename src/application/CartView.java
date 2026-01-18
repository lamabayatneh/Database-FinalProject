package application;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.CartItem;

import java.net.URL;

public class CartView {

	public void show(Stage stage, Runnable backAction) {

		URL logoUrl = getClass().getResource("/logo.png");
		if (logoUrl == null) {
			throw new RuntimeException("logo.png not found in resources");
		}

		ImageView logo = new ImageView(new Image(logoUrl.toExternalForm()));
		logo.setFitHeight(40);
		logo.setPreserveRatio(true);

		Label shopName = new Label("SABASTIA BookShop");
		shopName.getStyleClass().add("sb-logo-text");

		VBox logoBox = new VBox(2, logo, shopName);
		logoBox.setAlignment(Pos.CENTER_LEFT);

		Label userLabel = new Label("Welcome, " + Session.currentUser.getUsername());
		userLabel.getStyleClass().add("sb-muted");

		BorderPane topBar = new BorderPane();
		topBar.setLeft(logoBox);
		topBar.setRight(userLabel);
		topBar.setPadding(new Insets(10, 20, 10, 20));

		/* ================= HEADER ================= */
		Button backBtn = new Button("← Back");
		backBtn.getStyleClass().addAll("sb-pill", "sb-accent");
		backBtn.setOnAction(s -> backAction.run());

		Label title = new Label("🛒 Your Shopping Cart");
		title.getStyleClass().add("sb-title");

		HBox header = new HBox(15, backBtn, title);
		header.setAlignment(Pos.CENTER_LEFT);

		/* ================= TABLE ================= */
		TableView<CartItem> table = new TableView<>();
		table.setItems(FXCollections.observableArrayList(Session.cart.getItems()));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setPrefHeight(350);

		TableColumn<CartItem, String> bookCol = new TableColumn<>("Book");
		bookCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBook().getTitle()));

		TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty");
		qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());

		TableColumn<CartItem, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getBook().getPrice()).asObject());

		TableColumn<CartItem, Double> subCol = new TableColumn<>("Subtotal");
		subCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSubtotal()).asObject());

		table.getColumns().addAll(bookCol, qtyCol, priceCol, subCol);

		Label totalLabel = new Label("Total: $ " + String.format("%.2f", Session.cart.getTotal()));
		totalLabel.getStyleClass().add("sb-title");

		Button checkoutBtn = new Button("✔ Checkout");
		checkoutBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		checkoutBtn.setOnAction(s -> {
			if (Session.currentCustomer == null) {
				new Alert(Alert.AlertType.ERROR, "No customer is selected / logged in. Please login again.")
						.showAndWait();
				return;
			}
			if (Session.cart == null || Session.cart.getItems().isEmpty()) {
				new Alert(Alert.AlertType.WARNING, "Your cart is empty.").showAndWait();
				return;
			}

			new OrderConfirmationView().show(stage, Session.currentCustomer, Session.cart, Session.cart.getTotal(),
					backAction);
		});

		VBox footer = new VBox(10, totalLabel, checkoutBtn);
		footer.setAlignment(Pos.CENTER_RIGHT);

		/* ================= CARD ================= */
		VBox card = new VBox(20, header, table, footer);
		card.getStyleClass().add("sb-card");
		card.setPadding(new Insets(20));

		/* ================= ROOT ================= */
		VBox root = new VBox(15, topBar, card);
		root.getStyleClass().add("sb-page");

		Scene scene = new Scene(root, 1000, 650);
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

		stage.setScene(scene);
		stage.setTitle("Cart");
		stage.show();
	}
}