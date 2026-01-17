package application;

import dao.BookDAO;
import dao.CategoryDAO;
import dao.CustomerDAO;
import dao.OrderDAO;
import dao.SupplierDAO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Book;
import model.Category;
import model.Customer;
import model.Supplier;
import model.TopCustomer;
import dao.PurchaseDAO;
import dao.PurchaseItemDAO;
import dao.StaffDAO;
import dao.SupplierDAO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import model.Purchase;
import model.PurchaseItemRow;
import model.Staff;
import model.Supplier;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

public class AdminDashboardView {

	private final TableView<Book> booksTable = new TableView<>();
	private final TableView<Customer> customersTable = new TableView<>();
	private final TableView<Category> categoriesTable = new TableView<>();
	private final TableView<Supplier> suppliersTable = new TableView<>();
	private final TableView<model.Purchase> purchasesTable = new TableView<>();
	private final TableView<model.MonthlyFinanceRow> financeTable = new TableView<>();
	private final TableView<model.MonthlyFinanceRow> negativeMonthsTable = new TableView<>();

	public void show(Stage stage, Runnable onBackToHome) {

		/* ================= HEADER ================= */

		ImageView logo = new ImageView(

				new Image(getClass().getResource("/logo.png").toExternalForm()));
		logo.setFitHeight(45);
		logo.setPreserveRatio(true);

		Label title = new Label("SABASTIA BookShop - Admin");
		title.getStyleClass().add("sb-logo-text");

		VBox logoBox = new VBox(4, logo, title);
		logoBox.setAlignment(Pos.CENTER_LEFT);

		Label welcome = new Label("Welcome, Admin");
		welcome.getStyleClass().add("sb-welcome");

		Button backBtn = new Button("← Back");
		backBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		backBtn.setOnAction(e -> onBackToHome.run());

		Button logoutBtn = new Button("Logout");
		logoutBtn.getStyleClass().addAll("sb-pill", "sb-accent");
		logoutBtn.setOnAction(e -> {
			Session.logout();
			onBackToHome.run();
		});

		HBox actions = new HBox(10, welcome, backBtn, logoutBtn);
		actions.setAlignment(Pos.CENTER_RIGHT);

		BorderPane header = new BorderPane();
		header.setLeft(logoBox);
		header.setRight(actions);
		header.setPadding(new Insets(12));
		header.getStyleClass().add("sb-page");

		/* ================= TABS ================= */

		TabPane tabs = new TabPane();
		tabs.getTabs().addAll(buildBooksTab(), buildCustomersTab(), buildCategoriesTab(), buildReportsTab(),
				buildFinanceTab(), buildSuppliersTab(), buildPurchasesTab());
		tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		BorderPane root = new BorderPane();
		root.setTop(header);
		root.setCenter(tabs);
		root.getStyleClass().add("sb-page");

		Scene scene = new Scene(root, 1200, 800);
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

		stage.setScene(scene);
		stage.setTitle("Admin Dashboard");
		stage.show();
	}

	/* ================= BOOKS TAB ================= */

	private Tab buildBooksTab() {

		Tab tab = new Tab("Books");

		TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getBookID()));

		TableColumn<Book, String> titleCol = new TableColumn<>("Title");
		titleCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTitle()));

		TableColumn<Book, String> authorCol = new TableColumn<>("Author");
		authorCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getAuthor()));

		TableColumn<Book, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPrice()));

		TableColumn<Book, Integer> qtyCol = new TableColumn<>("Qty");
		qtyCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getQuantity()));

		TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
		categoryCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
				c.getValue().getCategory() == null ? "" : c.getValue().getCategory().getCategoryName()));

		TableColumn<Book, String> imageCol = new TableColumn<>("Image Path");
		imageCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
				c.getValue().getImagePath() == null ? "" : c.getValue().getImagePath()));

		booksTable.getColumns().setAll(idCol, titleCol, authorCol, priceCol, qtyCol, categoryCol, imageCol);
		booksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Button addBtn = new Button("Add Book");
		Button editBtn = new Button("Edit Book");
		Button deleteBtn = new Button("Delete Book");
		Button refreshBtn = new Button("Refresh");

		addBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		editBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		refreshBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		deleteBtn.getStyleClass().addAll("sb-pill", "sb-accent");

		addBtn.setOnAction(e -> {
			Book b = showBookDialog(null);
			if (b != null) {
				BookDAO.insertBook(b);
				refreshBooks();
			}
		});

		editBtn.setOnAction(e -> {
			Book selected = booksTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				new Alert(Alert.AlertType.WARNING, "Select a book").show();
				return;
			}
			Book updated = showBookDialog(selected);
			if (updated != null) {
				BookDAO.updateBook(updated);
				refreshBooks();
			}
		});

		deleteBtn.setOnAction(e -> {
			Book selected = booksTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				new Alert(Alert.AlertType.WARNING, "Select a book").show();
				return;
			}
			BookDAO.deleteBook(selected.getBookID());
			refreshBooks();
		});

		refreshBtn.setOnAction(e -> refreshBooks());

		HBox actions = new HBox(10, addBtn, editBtn, deleteBtn, refreshBtn);
		actions.setPadding(new Insets(10));

		VBox box = new VBox(15, actions, booksTable);
		box.setPadding(new Insets(15));
		box.getStyleClass().add("sb-card");

		tab.setContent(box);
		refreshBooks();
		return tab;
	}

	private void refreshBooks() {
		booksTable.getItems().setAll(BookDAO.getAllBooks());
	}

	/* ================= ADD / EDIT BOOK DIALOG ================= */

	private Book showBookDialog(Book existing) {

		Dialog<Book> dialog = new Dialog<>();
		dialog.setTitle(existing == null ? "Add Book" : "Edit Book");

		ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

		TextField title = new TextField();
		TextField author = new TextField();
		TextField price = new TextField();
		TextField qty = new TextField();
		DatePicker date = new DatePicker(LocalDate.now());

		ComboBox<Category> categoryBox = new ComboBox<>();
		categoryBox.getItems().addAll(CategoryDAO.getAllCategories());

		TextField imagePath = new TextField();
		imagePath.setPromptText("Enter image path (e.g. images/book1.png)");

		if (existing != null) {
			title.setText(existing.getTitle());
			author.setText(existing.getAuthor());
			price.setText(String.valueOf(existing.getPrice()));
			qty.setText(String.valueOf(existing.getQuantity()));
			date.setValue(existing.getAddedDate());
			categoryBox.setValue(existing.getCategory());
			imagePath.setText(existing.getImagePath());
		}

		GridPane g = new GridPane();
		g.setPadding(new Insets(20));
		g.setHgap(10);
		g.setVgap(10);

		g.addRow(0, new Label("Title:"), title);
		g.addRow(1, new Label("Author:"), author);
		g.addRow(2, new Label("Price:"), price);
		g.addRow(3, new Label("Quantity:"), qty);
		g.addRow(4, new Label("Added Date:"), date);
		g.addRow(5, new Label("Category:"), categoryBox);
		g.addRow(6, new Label("Image Path:"), imagePath);

		dialog.getDialogPane().setContent(g);

		dialog.setResultConverter(btn -> {
			if (btn != saveBtn)
				return null;

			if (title.getText().isBlank() || author.getText().isBlank() || categoryBox.getValue() == null
					|| imagePath.getText().isBlank()) {

				new Alert(Alert.AlertType.ERROR, "All fields including Category and Image Path are required")
						.showAndWait();
				return null;
			}

			double p;
			int q;
			try {
				p = Double.parseDouble(price.getText());
				q = Integer.parseInt(qty.getText());
			} catch (Exception e) {
				new Alert(Alert.AlertType.ERROR, "Invalid Price / Quantity").showAndWait();
				return null;
			}

			return new Book(existing == null ? 0 : existing.getBookID(), title.getText(), author.getText(), p, q,
					date.getValue(), categoryBox.getValue(), imagePath.getText());
		});

		return dialog.showAndWait().orElse(null);
	}

	/* ================= CUSTOMERS TAB ================= */

	private Tab buildCustomersTab() {

		Tab tab = new Tab("Customers");

		TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCustomerID()));

		TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFullName()));

		TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getEmail()));

		customersTable.getColumns().setAll(idCol, nameCol, emailCol);
		customersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Button deleteBtn = new Button("Delete Customer");
		deleteBtn.getStyleClass().addAll("sb-pill", "sb-accent");
		deleteBtn.setOnAction(e -> {

			Customer selected = customersTable.getSelectionModel().getSelectedItem();

			if (selected == null) {
				new Alert(Alert.AlertType.WARNING, "Please select a customer first").show();
				return;
			}

			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
			confirm.setHeaderText("Delete Customer");
			confirm.setContentText("Are you sure you want to delete:\n" + selected.getFullName());

			confirm.showAndWait().ifPresent(res -> {
				if (res == ButtonType.OK) {
					CustomerDAO.deleteCustomerWithUser(selected.getCustomerID(), selected.getUserID());
					refreshCustomers();
				}
			});
		});

		Button refreshBtn = new Button("Refresh");
		refreshBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		HBox actions = new HBox(10, deleteBtn, refreshBtn);
		actions.setPadding(new Insets(10));

		VBox box = new VBox(10, actions, customersTable);
		box.setPadding(new Insets(10));
		box.getStyleClass().add("sb-card");

		tab.setContent(box);
		customersTable.getItems().setAll(CustomerDAO.getAllCustomers());
		return tab;
	}

	private void refreshCustomers() {
		customersTable.getItems().setAll(CustomerDAO.getAllCustomers());
	}

	/* ================= CATEGORIES TAB ================= */

	private Tab buildCategoriesTab() {

		Tab tab = new Tab("Categories");

		TableColumn<Category, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCategoryID()));

		TableColumn<Category, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCategoryName()));

		TableColumn<Category, Integer> countCol = new TableColumn<>("Books Count");
		countCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getBookCount()));
		countCol.setPrefWidth(140);

		categoriesTable.getColumns().setAll(idCol, nameCol, countCol);
		categoriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Button addBtn = new Button("Add Category");
		addBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		addBtn.setOnAction(e -> {
			Category newCat = showAddCategoryDialog();
			if (newCat != null) {
				CategoryDAO.insertCategory(newCat);
				refreshCategories();
			}
		});

		Button deleteBtn = new Button("Delete Category");
		deleteBtn.getStyleClass().addAll("sb-pill", "sb-accent");

		deleteBtn.setOnAction(e -> {
			Category selected = categoriesTable.getSelectionModel().getSelectedItem();

			if (selected == null) {
				new Alert(Alert.AlertType.WARNING, "Select a category first").show();
				return;
			}

			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
			confirm.setHeaderText("Delete Category");
			confirm.setContentText(
					"Delete category: " + selected.getCategoryName() + "\n(Only if no books are linked)");

			confirm.showAndWait().ifPresent(res -> {
				if (res == ButtonType.OK) {
					CategoryDAO.deleteCategory(selected.getCategoryID());
					refreshCategories();
				}
			});
		});

		Button refreshBtn = new Button("Refresh");
		refreshBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		HBox actions = new HBox(10, addBtn, deleteBtn, refreshBtn);
		actions.setPadding(new Insets(10));

		VBox box = new VBox(10, actions, categoriesTable);
		box.setPadding(new Insets(10));

		box.getStyleClass().add("sb-card");

		tab.setContent(box);

		List<Category> list = CategoryDAO.getAllCategoriesWithBookCount();
		categoriesTable.getItems().setAll(list);
		return tab;
	}

	private void refreshCategories() {
		categoriesTable.getItems().setAll(CategoryDAO.getAllCategoriesWithBookCount());
	}

	private Category showAddCategoryDialog() {

		Dialog<Category> dialog = new Dialog<>();
		dialog.setTitle("Add Category");

		ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

		TextField nameField = new TextField();
		nameField.setPromptText("Category Name");

		TextArea descField = new TextArea();
		descField.setPromptText("Description");
		descField.setPrefRowCount(3);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20));

		grid.add(new Label("Name:"), 0, 0);
		grid.add(nameField, 1, 0);
		grid.add(new Label("Description:"), 0, 1);
		grid.add(descField, 1, 1);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(btn -> {
			if (btn != saveBtn)
				return null;

			if (nameField.getText().isBlank()) {
				new Alert(Alert.AlertType.ERROR, "Category name is required").showAndWait();
				return null;
			}

			return new Category(0, nameField.getText().trim(), descField.getText().trim());
		});

		return dialog.showAndWait().orElse(null);
	}

	private Tab buildReportsTab() {
		Tab tab = new Tab("Reports");

		// ===== Low Stock =====
		Label lowStockTitle = new Label("Low Stock Books (Qty < 10)");
		lowStockTitle.getStyleClass().add("sb-title");

		TableView<Book> lowStockTable = new TableView<>();

		TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getBookID()));

		TableColumn<Book, String> titleCol = new TableColumn<>("Title");
		titleCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTitle()));

		TableColumn<Book, String> qtyCol = new TableColumn<>("Qty");
		qtyCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(String.valueOf(c.getValue().getQuantity())));

		TableColumn<Book, String> catCol = new TableColumn<>("Category");
		catCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
				c.getValue().getCategory() == null ? "" : c.getValue().getCategory().getCategoryName()));

		lowStockTable.getColumns().setAll(idCol, titleCol, catCol, qtyCol);
		lowStockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Button refreshLow = new Button("Refresh Low Stock");
		refreshLow.getStyleClass().addAll("sb-pill", "sb-primary");

		Runnable loadLow = () -> lowStockTable.getItems().setAll(BookDAO.getLowStockBooks(10));
		refreshLow.setOnAction(e -> loadLow.run());
		loadLow.run();

		VBox lowStockCard = new VBox(10, lowStockTitle, refreshLow, lowStockTable);
		lowStockCard.setPadding(new Insets(15));
		lowStockCard.getStyleClass().add("sb-card");

		// ===== Orders =====
		Label ordersTitle = new Label("Customer Orders");
		ordersTitle.getStyleClass().add("sb-title");

		DatePicker from = new DatePicker();
		from.setPromptText("From");
		DatePicker to = new DatePicker();
		to.setPromptText("To");

		Button filterBtn = new Button("Filter");
		filterBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		Button clearBtn = new Button("Clear");
		clearBtn.getStyleClass().addAll("sb-pill", "sb-accent");

		TableView<model.Order> ordersTable = new TableView<>();

		TableColumn<model.Order, Integer> orderIdCol = new TableColumn<>("OrderID");
		orderIdCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getOrderId()));

		TableColumn<model.Order, String> custCol = new TableColumn<>("Customer");
		custCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCustomerName()));

		TableColumn<model.Order, LocalDate> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getOrderDate()));

		TableColumn<model.Order, Double> totalCol = new TableColumn<>("Total");
		totalCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTotal()));

		ordersTable.getColumns().setAll(orderIdCol, custCol, dateCol, totalCol);
		ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Runnable loadOrders = () -> {
			ordersTable.getItems().setAll(dao.OrderDAO.getAllOrders(from.getValue(), to.getValue()));
		};

		filterBtn.setOnAction(e -> loadOrders.run());
		clearBtn.setOnAction(e -> {
			from.setValue(null);
			to.setValue(null);
			loadOrders.run();
		});
		loadOrders.run();

		// ===== Suppliers by City =====
		Label supplierTitle = new Label("Suppliers by City");
		supplierTitle.getStyleClass().add("sb-title");

		TextField cityField = new TextField();
		cityField.setPromptText("Enter city name");

		Button searchSupplierBtn = new Button("Search");
		searchSupplierBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		TableView<Supplier> supplierTable = new TableView<>();

		TableColumn<Supplier, Integer> supIdCol = new TableColumn<>("ID");
		supIdCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSupplierID()));

		TableColumn<Supplier, String> supNameCol = new TableColumn<>("Name");
		supNameCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSupplierName()));

		TableColumn<Supplier, String> supCityCol = new TableColumn<>("City");
		supCityCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCity()));

		TableColumn<Supplier, String> supPhoneCol = new TableColumn<>("Phone");
		supPhoneCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPhone()));

		supplierTable.getColumns().setAll(supIdCol, supNameCol, supCityCol, supPhoneCol);
		supplierTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		searchSupplierBtn.setOnAction(e -> {
			supplierTable.getItems().setAll(SupplierDAO.getSuppliersByCity(cityField.getText()));
		});

		HBox supplierFilters = new HBox(10, new Label("City:"), cityField, searchSupplierBtn);
		supplierFilters.setAlignment(Pos.CENTER_LEFT);

		VBox supplierCard = new VBox(10, supplierTitle, supplierFilters, supplierTable);
		supplierCard.setPadding(new Insets(15));
		supplierCard.getStyleClass().add("sb-card");

		// ===== Staff List =====
		Label staffTitle = new Label("Staff Members");
		staffTitle.getStyleClass().add("sb-title");

		Button loadStaffBtn = new Button("Load Staff");
		loadStaffBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		TableView<Staff> staffTable = new TableView<>();

		TableColumn<Staff, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFullName()));

		TableColumn<Staff, String> positionCol = new TableColumn<>("Position");
		positionCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPosition()));

		TableColumn<Staff, Double> salaryCol = new TableColumn<>("Salary");
		salaryCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSalary()));

		staffTable.getColumns().setAll(nameCol, positionCol, salaryCol);
		staffTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		loadStaffBtn.setOnAction(e -> {
			staffTable.getItems().setAll(StaffDAO.getAllStaffSortedByName());
		});

		VBox staffCard = new VBox(10, staffTitle, loadStaffBtn, staffTable);
		staffCard.setPadding(new Insets(15));
		staffCard.getStyleClass().add("sb-card");

		// ===== Books by Category =====
		Label booksTitle = new Label("Books by Category");
		booksTitle.getStyleClass().add("sb-title");

		Button loadBooksBtn = new Button("Load Books");
		loadBooksBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		TableView<Book> booksTable = new TableView<>();

		TableColumn<Book, String> bookTitleCol = new TableColumn<>("Title");
		bookTitleCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTitle()));

		TableColumn<Book, String> authorCol = new TableColumn<>("Author");
		authorCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getAuthor()));

		TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
		categoryCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
				c.getValue().getCategory() == null ? "" : c.getValue().getCategory().getCategoryName()));

		TableColumn<Book, Integer> qtyCo = new TableColumn<>("Quantity");
		qtyCo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getQuantity()));

		booksTable.getColumns().setAll(bookTitleCol, authorCol, categoryCol, qtyCo);
		booksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// تحميل البيانات عند الضغط على الزر
		loadBooksBtn.setOnAction(e -> {
			booksTable.getItems().setAll(BookDAO.getBooksSortedByCategory());
		});

		VBox booksCard = new VBox(10, booksTitle, loadBooksBtn, booksTable);
		booksCard.setPadding(new Insets(15));
		booksCard.getStyleClass().add("sb-card");

		// ===== Orders by Customer =====
		Label customerOrdersTitle = new Label("Orders by Customer");
		customerOrdersTitle.getStyleClass().add("sb-title");

		TextField customerField = new TextField();
		customerField.setPromptText("Enter customer name");

		Button searchCustomerBtn = new Button("Search");
		searchCustomerBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		TableView<model.Order> customerOrdersTable = new TableView<>();

		TableColumn<model.Order, Integer> orderIdCo = new TableColumn<>("OrderID");
		orderIdCo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getOrderId()));

		TableColumn<model.Order, LocalDate> dateCo = new TableColumn<>("Order Date");
		dateCo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getOrderDate()));

		TableColumn<model.Order, Double> totalCo = new TableColumn<>("Total");
		totalCo.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTotal()));

		customerOrdersTable.getColumns().setAll(orderIdCo, dateCo, totalCo);
		customerOrdersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		searchCustomerBtn.setOnAction(e -> {
			customerOrdersTable.getItems().setAll(OrderDAO.getOrdersByCustomer(customerField.getText()));
		});

		HBox customerFilters = new HBox(10, new Label("Customer:"), customerField, searchCustomerBtn);
		customerFilters.setAlignment(Pos.CENTER_LEFT);

		VBox customerOrdersCard = new VBox(10, customerOrdersTitle, customerFilters, customerOrdersTable);
		customerOrdersCard.setPadding(new Insets(15));
		customerOrdersCard.getStyleClass().add("sb-card");

		// ===== Unsold Books =====
		Label unsoldTitle = new Label("Unsold Books");
		unsoldTitle.getStyleClass().add("sb-title");

		TableView<Book> unsoldTable = new TableView<>();

		TableColumn<Book, Integer> unsoldIdCol = new TableColumn<>("ID");
		unsoldIdCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getBookID()));

		TableColumn<Book, String> unsoldTitleCol = new TableColumn<>("Title");
		unsoldTitleCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTitle()));

		TableColumn<Book, String> unsoldAuthorCol = new TableColumn<>("Author");
		unsoldAuthorCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getAuthor()));

		TableColumn<Book, String> unsoldCatCol = new TableColumn<>("Category");
		unsoldCatCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
				c.getValue().getCategory() == null ? "" : c.getValue().getCategory().getCategoryName()));

		unsoldTable.getColumns().setAll(unsoldIdCol, unsoldTitleCol, unsoldAuthorCol, unsoldCatCol);
		unsoldTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Button refreshUnsold = new Button("Refresh Unsold Books");
		refreshUnsold.getStyleClass().addAll("sb-pill", "sb-primary");

		Runnable loadUnsold = () -> unsoldTable.getItems().setAll(BookDAO.getUnsoldBooks());
		refreshUnsold.setOnAction(e -> loadUnsold.run());
		loadUnsold.run();

		VBox unsoldCard = new VBox(10, unsoldTitle, refreshUnsold, unsoldTable);
		unsoldCard.setPadding(new Insets(15));
		unsoldCard.getStyleClass().add("sb-card");

		Label topCustTitle = new Label("Top 5 Customers (by Total Purchases)");
		topCustTitle.getStyleClass().add("sb-title");

		// TableView
		TableView<TopCustomer> topCustTable = new TableView<>();

		// Columns
		TableColumn<TopCustomer, String> nameC = new TableColumn<>("Customer");
		nameC.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFullName()));

		TableColumn<TopCustomer, Double> totalC = new TableColumn<>("Total Spent");
		totalC.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTotalSpent()));

		// Add columns to table
		topCustTable.getColumns().setAll(nameC, totalC);
		topCustTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Refresh button
		Button refreshTop = new Button("Refresh Top Customers");
		refreshTop.getStyleClass().addAll("sb-pill", "sb-primary");

		// Load data
		Runnable loadTop = () -> topCustTable.getItems().setAll(CustomerDAO.getTopCustomers());
		refreshTop.setOnAction(e -> loadTop.run());
		loadTop.run();

		// VBox container
		VBox topCustCard = new VBox(10, topCustTitle, refreshTop, topCustTable);
		topCustCard.setPadding(new Insets(15));
		topCustCard.getStyleClass().add("sb-card");

		HBox filters = new HBox(10, new Label("From:"), from, new Label("To:"), to, filterBtn, clearBtn);
		filters.setAlignment(Pos.CENTER_LEFT);

		VBox ordersCard = new VBox(10, ordersTitle, filters, ordersTable);
		ordersCard.setPadding(new Insets(15));
		ordersCard.getStyleClass().add("sb-card");

		VBox page = new VBox(15, lowStockCard, ordersCard, supplierCard, staffCard, booksCard, customerOrdersCard,
				unsoldCard, topCustCard);
		page.setPadding(new Insets(15));
		ScrollPane sp = new ScrollPane(page);
		sp.setFitToWidth(true);
		tab.setContent(sp);
		return tab;
	}

	private Tab buildFinanceTab() {

		Tab tab = new Tab("Finance");

		Label title = new Label("Monthly Finance Summary");
		title.getStyleClass().add("sb-title");

		Button refreshBtn = new Button("Refresh Finance");
		refreshBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		// ====== Finance main table ======
		TableColumn<model.MonthlyFinanceRow, String> monthCol = new TableColumn<>("Month");
		monthCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getMonth()));

		TableColumn<model.MonthlyFinanceRow, Double> salesCol = new TableColumn<>("Sales Revenue");
		salesCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSalesRevenue()));

		TableColumn<model.MonthlyFinanceRow, Double> purchasesCol = new TableColumn<>("Purchase Expenses");
		purchasesCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPurchaseExpenses()));

		TableColumn<model.MonthlyFinanceRow, Double> profitCol = new TableColumn<>("Profit / Loss");
		profitCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getProfit()));

		TableColumn<model.MonthlyFinanceRow, Double> marginCol = new TableColumn<>("Profit Margin %");
		marginCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getProfitMargin()));

		financeTable.getColumns().setAll(monthCol, salesCol, purchasesCol, profitCol, marginCol);
		financeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// ✅ تلوين الصفوف الخاسرة بالجدول الرئيسي
		financeTable.setRowFactory(tv -> new TableRow<>() {
			@Override
			protected void updateItem(model.MonthlyFinanceRow item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setStyle("");
				} else if (item.getProfit() < 0) {
					setStyle("-fx-background-color: #ffd6d6;");
				} else {
					setStyle("");
				}
			}
		});

		// ====== Negative months table ======
		Label negTitle = new Label("Months with Negative Profit");
		negTitle.getStyleClass().add("sb-title");

		TableColumn<model.MonthlyFinanceRow, String> nMonthCol = new TableColumn<>("Month");
		nMonthCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getMonth()));

		TableColumn<model.MonthlyFinanceRow, Double> nProfitCol = new TableColumn<>("Profit / Loss");
		nProfitCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getProfit()));

		TableColumn<model.MonthlyFinanceRow, Double> nMarginCol = new TableColumn<>("Profit Margin %");
		nMarginCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getProfitMargin()));

		negativeMonthsTable.getColumns().setAll(nMonthCol, nProfitCol, nMarginCol);
		negativeMonthsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// ====== Load methods ======
		Runnable loadFinance = () -> {
			financeTable.getItems().setAll(dao.ReportDAO.getMonthlyFinance());
			negativeMonthsTable.getItems().setAll(dao.ReportDAO.getNegativeProfitMonths());
		};

		// ====== Most profitable category ======
		Label bestTitle = new Label("Most Profitable Category");
		bestTitle.getStyleClass().add("sb-title");

		Label categoryLbl = new Label("-");
		categoryLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		Label revenueLbl = new Label("-");
		revenueLbl.setStyle("-fx-font-size: 16px;");

		Button refreshBestBtn = new Button("Refresh");
		refreshBestBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		Runnable loadBestCategory = () -> {
			Category c = dao.ReportDAO.getMostProfitableCategory();

			if (c != null) {
				categoryLbl.setText("📚 Category: " + c.getCategoryName());
				revenueLbl.setText("💰 Revenue: $" + String.format("%.2f", c.getRevenue()));
			} else {
				categoryLbl.setText("No data");
				revenueLbl.setText("");
			}
		};

		refreshBestBtn.setOnAction(e -> loadBestCategory.run());
		loadBestCategory.run();

		VBox card3 = new VBox(10, bestTitle, categoryLbl, revenueLbl, refreshBestBtn);
		card3.setPadding(new Insets(15));
		card3.getStyleClass().add("sb-card");

		refreshBtn.setOnAction(e -> loadFinance.run());
		loadFinance.run();

		VBox card1 = new VBox(10, title, refreshBtn, financeTable);
		card1.setPadding(new Insets(15));
		card1.getStyleClass().add("sb-card");

		VBox card2 = new VBox(10, negTitle, negativeMonthsTable);
		card2.setPadding(new Insets(15));
		card2.getStyleClass().add("sb-card");

		VBox page = new VBox(15, card1, card2, card3);
		page.setPadding(new Insets(15));

		ScrollPane sp = new ScrollPane(page);
		sp.setFitToWidth(true);

		tab.setContent(sp);
		return tab;
	}

	private Tab buildSuppliersTab() {

		Tab tab = new Tab("Suppliers");

		TableColumn<Supplier, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSupplierID()));

		TableColumn<Supplier, String> nameCol = new TableColumn<>("Supplier Name");
		nameCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSupplierName()));

		TableColumn<Supplier, String> cityCol = new TableColumn<>("City");
		cityCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCity()));

		TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getEmail()));

		TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone");
		phoneCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPhone()));

		TableColumn<Supplier, String> contactCol = new TableColumn<>("Contact Person");
		contactCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getContactPerson()));

		suppliersTable.getColumns().setAll(idCol, nameCol, cityCol, emailCol, phoneCol, contactCol);
		suppliersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Button addBtn = new Button("Add Supplier");
		Button editBtn = new Button("Edit Supplier");
		Button deleteBtn = new Button("Delete Supplier");
		Button refreshBtn = new Button("Refresh");

		addBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		editBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		refreshBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		deleteBtn.getStyleClass().addAll("sb-pill", "sb-accent");

		addBtn.setOnAction(e -> {
			Supplier s = showSupplierDialog(null);
			if (s != null) {
				SupplierDAO.insertSupplier(s);
				refreshSuppliers();
			}
		});

		editBtn.setOnAction(e -> {
			Supplier selected = suppliersTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				new Alert(Alert.AlertType.WARNING, "Select a supplier").show();
				return;
			}
			Supplier updated = showSupplierDialog(selected);
			if (updated != null) {
				updated.setSupplierID(selected.getSupplierID());
				SupplierDAO.updateSupplier(updated);
				refreshSuppliers();
			}
		});

		deleteBtn.setOnAction(e -> {
			Supplier selected = suppliersTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				new Alert(Alert.AlertType.WARNING, "Select a supplier").show();
				return;
			}

			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
			confirm.setHeaderText("Delete Supplier");
			confirm.setContentText("Delete: " + selected.getSupplierName() + " ?");
			confirm.showAndWait().ifPresent(res -> {
				if (res == ButtonType.OK) {
					SupplierDAO.deleteSupplier(selected.getSupplierID());
					refreshSuppliers();
				}
			});
		});

		refreshBtn.setOnAction(e -> refreshSuppliers());

		HBox actions = new HBox(10, addBtn, editBtn, deleteBtn, refreshBtn);
		actions.setPadding(new Insets(10));

		VBox box = new VBox(15, actions, suppliersTable);
		box.setPadding(new Insets(15));
		box.getStyleClass().add("sb-card");

		tab.setContent(box);
		refreshSuppliers();
		return tab;
	}

	private void refreshSuppliers() {
		suppliersTable.getItems().setAll(SupplierDAO.getAllSuppliers());
	}

	private Supplier showSupplierDialog(Supplier existing) {

		Dialog<Supplier> dialog = new Dialog<>();
		dialog.setTitle(existing == null ? "Add Supplier" : "Edit Supplier");

		ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

		TextField name = new TextField();
		TextField city = new TextField();
		TextField email = new TextField();
		TextField phone = new TextField();
		TextField contact = new TextField();

		name.setPromptText("Supplier Name");
		city.setPromptText("City");
		email.setPromptText("Email");
		phone.setPromptText("Phone");
		contact.setPromptText("Contact Person");

		if (existing != null) {
			name.setText(existing.getSupplierName());
			city.setText(existing.getCity());
			email.setText(existing.getEmail());
			phone.setText(existing.getPhone());
			contact.setText(existing.getContactPerson());
		}

		GridPane g = new GridPane();
		g.setPadding(new Insets(20));
		g.setHgap(10);
		g.setVgap(10);

		g.addRow(0, new Label("Name:"), name);
		g.addRow(1, new Label("City:"), city);
		g.addRow(2, new Label("Email:"), email);
		g.addRow(3, new Label("Phone:"), phone);
		g.addRow(4, new Label("Contact Person:"), contact);

		dialog.getDialogPane().setContent(g);

		dialog.setResultConverter(btn -> {
			if (btn != saveBtn)
				return null;

			if (name.getText().isBlank()) {
				new Alert(Alert.AlertType.ERROR, "Supplier Name is required").showAndWait();
				return null;
			}

			Supplier s = new Supplier();
			s.setSupplierName(name.getText().trim());
			s.setCity(city.getText().trim());
			s.setEmail(email.getText().trim());
			s.setPhone(phone.getText().trim());
			s.setContactPerson(contact.getText().trim());
			return s;
		});

		return dialog.showAndWait().orElse(null);
	}

	private Tab buildPurchasesTab() {

		Tab tab = new Tab("Purchases");

		// ====== Table columns ======
		TableColumn<Purchase, Integer> idCol = new TableColumn<>("PurchaseID");
		idCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPurchaseID()));

		TableColumn<Purchase, String> supplierCol = new TableColumn<>("Supplier");
		supplierCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
				c.getValue().getSupplier() == null ? "" : c.getValue().getSupplier().getSupplierName()));

		TableColumn<Purchase, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(String.valueOf(c.getValue().getPurchaseDate())));

		TableColumn<Purchase, Double> totalCol = new TableColumn<>("Total Cost");
		totalCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getTotalCost()));

		purchasesTable.getColumns().setAll(idCol, supplierCol, dateCol, totalCol);
		purchasesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Button newPurchaseBtn = new Button("New Purchase");
		newPurchaseBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		Button refreshBtn = new Button("Refresh");
		refreshBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		newPurchaseBtn.setOnAction(e -> {
			boolean ok = showNewPurchaseDialog();
			if (ok)
				refreshPurchases();
		});

		refreshBtn.setOnAction(e -> refreshPurchases());

		HBox actions = new HBox(10, newPurchaseBtn, refreshBtn);
		actions.setPadding(new Insets(10));

		VBox box = new VBox(10, actions, purchasesTable);
		box.setPadding(new Insets(15));
		box.getStyleClass().add("sb-card");

		tab.setContent(box);

		refreshPurchases();
		return tab;
	}

	private void refreshPurchases() {
		purchasesTable.getItems().setAll(PurchaseDAO.getAllPurchases());
	}

	private boolean showNewPurchaseDialog() {

		Dialog<Boolean> dialog = new Dialog<>();
		dialog.setTitle("New Purchase");

		ButtonType saveBtn = new ButtonType("Save Purchase", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

		ComboBox<Supplier> supplierBox = new ComboBox<>();
		supplierBox.getItems().setAll(SupplierDAO.getAllSuppliers());

		// StaffID: خليها ثابتة 1 (مدير) أو اعملها TextField
		TextField staffIdField = new TextField("1");

		DatePicker date = new DatePicker(java.time.LocalDate.now());

		// Items table (UI)
		TableView<PurchaseItemRow> itemsTable = new TableView<>();

		TableColumn<PurchaseItemRow, String> bookCol = new TableColumn<>("Book");
		bookCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(
				c.getValue().getBook() == null ? "" : c.getValue().getBook().getTitle()));

		TableColumn<PurchaseItemRow, Integer> qtyCol = new TableColumn<>("Qty");
		qtyCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getQuantity()));

		TableColumn<PurchaseItemRow, Double> unitCol = new TableColumn<>("Unit Cost");
		unitCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getUnitCost()));

		TableColumn<PurchaseItemRow, Double> subCol = new TableColumn<>("Subtotal");
		subCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getSubtotal()));

		itemsTable.getColumns().setAll(bookCol, qtyCol, unitCol, subCol);
		itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		itemsTable.setPrefHeight(220);

		// Add item controls
		ComboBox<Book> bookBox = new ComboBox<>();
		bookBox.getItems().setAll(BookDAO.getAllBooks()); // لازم BookDAO يرجّع category/image مثل ما ظبطنا

		TextField qty = new TextField();
		qty.setPromptText("Qty");

		TextField unitCost = new TextField();
		unitCost.setPromptText("Unit Cost");

		Button addItemBtn = new Button("Add Item");
		addItemBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		Button removeItemBtn = new Button("Remove Selected");
		removeItemBtn.getStyleClass().addAll("sb-pill", "sb-accent");

		addItemBtn.setOnAction(e -> {
			if (bookBox.getValue() == null) {
				new Alert(Alert.AlertType.ERROR, "Choose a book").showAndWait();
				return;
			}
			int q;
			double u;
			try {
				q = Integer.parseInt(qty.getText().trim());
				u = Double.parseDouble(unitCost.getText().trim());
				if (q <= 0 || u <= 0)
					throw new RuntimeException();
			} catch (Exception ex) {
				new Alert(Alert.AlertType.ERROR, "Qty and Unit Cost must be valid numbers").showAndWait();
				return;
			}

			itemsTable.getItems().add(new PurchaseItemRow(bookBox.getValue(), q, u));
			qty.clear();
			unitCost.clear();
		});

		removeItemBtn.setOnAction(e -> {
			PurchaseItemRow sel = itemsTable.getSelectionModel().getSelectedItem();
			if (sel != null)
				itemsTable.getItems().remove(sel);
		});

		GridPane top = new GridPane();
		top.setHgap(10);
		top.setVgap(10);
		top.setPadding(new Insets(15));

		top.addRow(0, new Label("Supplier:"), supplierBox);
		top.addRow(1, new Label("StaffID:"), staffIdField);
		top.addRow(2, new Label("Date:"), date);

		HBox addRow = new HBox(10, new Label("Book:"), bookBox, new Label("Qty:"), qty, new Label("Unit:"), unitCost,
				addItemBtn, removeItemBtn);
		addRow.setAlignment(Pos.CENTER_LEFT);
		addRow.setPadding(new Insets(0, 15, 10, 15));

		VBox content = new VBox(10, top, addRow, itemsTable);
		dialog.getDialogPane().setContent(content);

		dialog.setResultConverter(btn -> {
			if (btn != saveBtn)
				return false;

			if (supplierBox.getValue() == null) {
				new Alert(Alert.AlertType.ERROR, "Supplier is required").showAndWait();
				return false;
			}

			int staffId;
			try {
				staffId = Integer.parseInt(staffIdField.getText().trim());
			} catch (Exception ex) {
				new Alert(Alert.AlertType.ERROR, "StaffID must be a number").showAndWait();
				return false;
			}

			if (itemsTable.getItems().isEmpty()) {
				new Alert(Alert.AlertType.ERROR, "Add at least one item").showAndWait();
				return false;
			}

			// Save with transaction
			try (var con = db.DBConnection.getConnection()) {
				con.setAutoCommit(false);

				double total = itemsTable.getItems().stream().mapToDouble(PurchaseItemRow::getSubtotal).sum();

				int purchaseId = PurchaseDAO.insertPurchaseReturnId(con, supplierBox.getValue().getSupplierID(),
						staffId, date.getValue(), total);

				for (PurchaseItemRow row : itemsTable.getItems()) {
					int bookId = row.getBook().getBookID();

					PurchaseItemDAO.insertPurchaseItem(con, purchaseId, bookId, row.getQuantity(), row.getUnitCost());

					// ✅ update stock
					BookDAO.addStock(con, bookId, row.getQuantity());
				}

				con.commit();
				return true;

			} catch (Exception ex) {
				ex.printStackTrace();
				new Alert(Alert.AlertType.ERROR, "Failed to save purchase. Check console.").showAndWait();
				return false;
			}
		});

		return dialog.showAndWait().orElse(false);
	}

}