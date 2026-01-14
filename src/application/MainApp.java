package application;

import java.util.List;
import java.util.Optional;

import dao.BookDAO;
import dao.CustomerDAO;
import dao.UserDAO;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Book;
import model.Customer;
import model.User;

public class MainApp extends Application {

	private FlowPane productsPane = new FlowPane(20, 20);

	@Override
	public void start(Stage stage) {
		showLogin(stage);
	}

	private void showCustomerUI(Stage stage) {

		Label logo = new Label("📚 Sabastia Book Shop");
		logo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

		TextField search = new TextField();
		search.setPromptText("Search for books...");
		search.setPrefWidth(300);

		Button backBtn = new Button("⬅ Back");
		backBtn.setOnAction(e -> start(stage));
		Button cartBtn = new Button("🛒 Cart");

		cartBtn.setOnAction(e -> {
			new CartView().show(stage, () -> showCustomerUI(stage));
		});

		HBox topBar = new HBox(20, logo, search, backBtn, cartBtn);
		topBar.setPadding(new Insets(15));

		VBox categories = new VBox(15);
		categories.setPadding(new Insets(15));
		categories.setPrefWidth(200);
		categories.setStyle("-fx-background-color: #f1f2f6;");

		Label catTitle = new Label("Categories");
		catTitle.setStyle("-fx-font-weight: bold;");

		Button allBtn = new Button("📚 All Books");
		Button progBtn = new Button("📘 Programming");
		Button dbBtn = new Button("📗 Databases");
		Button aiBtn = new Button("🤖 AI");
		Button netBtn = new Button("🌐 Networks");

		allBtn.setOnAction(e -> loadAllBooks());
		progBtn.setOnAction(e -> loadCategory("Programming"));
		dbBtn.setOnAction(e -> loadCategory("Databases"));
		aiBtn.setOnAction(e -> loadCategory("AI"));
		netBtn.setOnAction(e -> loadCategory("Networks"));

		categories.getChildren().addAll(catTitle, allBtn, progBtn, dbBtn, aiBtn, netBtn);

		BorderPane layout = new BorderPane();
		layout.setTop(topBar);
		layout.setLeft(categories);
		layout.setCenter(productsPane);

		stage.setScene(new Scene(layout, 1200, 700));
		stage.setTitle("Sabastia Book Shop");
	}

	private void loadAllBooks() {

		productsPane.getChildren().clear();

		List<Book> books = BookDAO.getAllBooks();
		for (Book b : books) {
			productsPane.getChildren().add(BookUI.createBookCard(b));
		}
	}

	private void loadCategory(String category) {

		productsPane.getChildren().clear();

		List<Book> books = BookDAO.getBooksByCategory(category);
		for (Book b : books) {
			productsPane.getChildren().add(BookUI.createBookCard(b));
		}
	}

	private void showAdminUI(Stage stage) {

		TabPane tabPane = new TabPane();
		TableView<Book> table = new TableView<>();
		ObservableList<Book> data = FXCollections.observableArrayList(BookDAO.getAllBooks());

		TableColumn<Book, Integer> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(new PropertyValueFactory<>("bookID"));

		TableColumn<Book, String> colTitle = new TableColumn<>("Title");
		colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));

		TableColumn<Book, String> colAuthor = new TableColumn<>("Author");
		colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

		TableColumn<Book, Double> colPrice = new TableColumn<>("Price");
		colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

		TableColumn<Book, Integer> colQty = new TableColumn<>("Qty");
		colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		table.getColumns().addAll(colId, colTitle, colAuthor, colPrice, colQty);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Button btnAdd = new Button("➕ Add");
		Button btnEdit = new Button("✏ Edit");
		Button btnDelete = new Button("🗑 Delete");

		btnAdd.setOnAction(e -> BookUI.openAddBookWindow(data));

		btnEdit.setOnAction(e -> {
			Book selected = table.getSelectionModel().getSelectedItem();
			if (selected != null)
				BookUI.openEditBookWindow(selected, data);

		});

		btnDelete.setOnAction(e -> {
			Book selected = table.getSelectionModel().getSelectedItem();
			if (selected == null)
				return;

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Delete Book");
			alert.setHeaderText("Are you sure?");
			alert.setContentText(selected.getTitle());

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				BookDAO.deleteBook(selected.getBookID());
				data.setAll(BookDAO.getAllBooks());
			}
		});

		BorderPane booksLayout = new BorderPane();
		Tab booksTab = new Tab("📚 Books");
		booksTab.setClosable(false);
		booksTab.setContent(booksLayout);
		TextField searchField = new TextField();
		searchField.setPromptText("🔍 Search by title or author...");
		searchField.setPrefWidth(250);
		FilteredList<Book> filteredData = new FilteredList<>(data, b -> true);
		table.setItems(filteredData);

		searchField.textProperty().addListener((obs, oldVal, newVal) -> {
			String keyword = newVal.toLowerCase();

			filteredData.setPredicate(book -> {
				if (keyword.isEmpty())
					return true;

				return book.getTitle().toLowerCase().contains(keyword)
						|| book.getAuthor().toLowerCase().contains(keyword);
			});
		});

		HBox booksTopBar = new HBox(15, btnAdd, btnEdit, btnDelete, searchField);
		booksTopBar.setPadding(new Insets(10));
		booksLayout.setTop(booksTopBar);
		booksLayout.setCenter(table);

		CustomersView customersView = new CustomersView();
		Tab customersTab = new Tab("👤 Customers");
		customersTab.setClosable(false);
		customersTab.setContent(customersView.getView());

		tabPane.getTabs().addAll(booksTab, customersTab);

		Button btnBack = new Button("⬅ Back");
		btnBack.setOnAction(e -> start(stage));

		Label title = new Label("🛠 Admin Panel");
		title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

		HBox topBar = new HBox(15, btnBack, title);
		topBar.setPadding(new Insets(10));

		BorderPane root = new BorderPane();
		root.setTop(topBar);
		root.setCenter(tabPane);

		stage.setScene(new Scene(root, 1100, 650));
		stage.setTitle("Admin Panel");
		stage.show();
	}

	void showLogin(Stage stage) {

		Button registerBtn = new Button("Create Account");

		registerBtn.setOnAction(e -> {
			new RegisterView().show(stage);
		});

		Label title = new Label("🔐 Login");
		title.setStyle("-fx-font-size:22px; -fx-font-weight:bold;");

		TextField username = new TextField();
		username.setPromptText("Username");
		username.setMaxWidth(250);

		PasswordField password = new PasswordField();
		password.setPromptText("Password");
		password.setMaxWidth(250);

		Button loginBtn = new Button("Login");
		loginBtn.setPrefWidth(250);
		password.setOnAction(e -> loginBtn.fire());

		username.setStyle("""
				    -fx-padding: 10;
				    -fx-font-size: 14px;
				""");

		password.setStyle("""
				    -fx-padding: 10;
				    -fx-font-size: 14px;
				""");

		loginBtn.setStyle("""
				    -fx-background-color: #2c3e50;
				    -fx-text-fill: white;
				    -fx-font-size: 15px;
				    -fx-background-radius: 8;
				""");

		loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
				"-fx-background-color:#1abc9c; -fx-text-fill:white; -fx-font-size:15px; -fx-background-radius:8;"));

		loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
				"-fx-background-color:#2c3e50; -fx-text-fill:white; -fx-font-size:15px; -fx-background-radius:8;"));

		Label status = new Label();
		status.setStyle("-fx-text-fill: red;");

		loginBtn.setOnAction(e -> {

			if (username.getText().isEmpty() || password.getText().isEmpty()) {
				status.setText("⚠ Please enter username and password");
				return;
			}

			User user = UserDAO.login(username.getText().trim(), password.getText().trim());

			if (user == null) {
				status.setText("❌ Invalid username or password");
				return;
			}

			Session.currentUser = user;

			if ("ADMIN".equalsIgnoreCase(user.getRole())) {

				showAdminUI(stage);

			} else {

				Customer customer = CustomerDAO.getCustomerByUserId(user.getId());

				if (customer == null) {
					status.setText("❌ Customer record not found");
					return;
				}

				Session.currentCustomer = customer;
				Session.currentUser = user;

				showCustomerUI(stage);
			}
		});

		VBox card = new VBox(15, title, username, password, loginBtn, status, registerBtn);
		card.setPadding(new Insets(30));
		card.setMaxWidth(350);
		card.setStyle("""
				    -fx-background-color: white;
				    -fx-background-radius: 12;
				    -fx-effect: dropshadow(gaussian, #cccccc, 15, 0.3, 0, 5);
				""");

		BorderPane root = new BorderPane(card);
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2);");
		BorderPane.setAlignment(card, Pos.CENTER);

		stage.setScene(new Scene(root, 400, 350));
		stage.setTitle("Sabastia System - Login");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
