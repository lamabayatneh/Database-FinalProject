package application;

import dao.BookDAO;
import dao.CategoryDAO;
import dao.CustomerDAO;
import dao.UserDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Book;
import model.Category;
import model.User;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

public class MainApp extends Application {

	private final FlowPane booksPane = new FlowPane(20, 20);
	private final TextField searchField = new TextField();

	private final Label sectionTitle = new Label();

	@Override
	public void start(Stage stage) {
		showHome(stage);
	}

	private void showHome(Stage stage) {

		/* ================= HEADER ================= */

		ImageView logo = new ImageView();
		URL logoUrl = getClass().getResource("/logo.png");
		if (logoUrl != null) {
			logo.setImage(new Image(logoUrl.toExternalForm()));
		}
		logo.setFitHeight(60);
		logo.setPreserveRatio(true);

		Label shopName = new Label("SABASTIA BookShop");
		shopName.getStyleClass().add("sb-logo-text");

		VBox logoBox = new VBox(6, logo, shopName);
		logoBox.setAlignment(Pos.CENTER);

		Label welcome = new Label();
		welcome.getStyleClass().add("sb-welcome");

		Button adminBtn = new Button("Admin Panel");
		adminBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		adminBtn.setOnAction(e -> new AdminDashboardView().show(stage, () -> showHome(stage)));

		Button cartBtn = new Button("🛒 Cart");
		cartBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		cartBtn.setOnAction(e -> new CartView().show(stage, () -> showHome(stage)));

		Button logoutBtn = new Button("Logout");
		logoutBtn.getStyleClass().addAll("sb-pill", "sb-accent");
		logoutBtn.setOnAction(e -> {
			Session.logout();
			showHome(stage);
		});

		Button loginHeaderBtn = new Button("Login");
		loginHeaderBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		Button registerHeaderBtn = new Button("Create Account");
		registerHeaderBtn.getStyleClass().addAll("sb-pill", "sb-accent");

		HBox actions = new HBox(10, welcome, adminBtn, cartBtn, logoutBtn, loginHeaderBtn, registerHeaderBtn);
		actions.setAlignment(Pos.CENTER_RIGHT);

		BorderPane header = new BorderPane();
		header.setCenter(logoBox);
		header.setRight(actions);
		header.setPadding(new Insets(15));
		header.getStyleClass().add("sb-header");

		/* ================= LOGIN CARD ================= */

		VBox loginCard = buildLoginCard(stage, () -> showHome(stage));
		loginCard.setVisible(false);
		loginCard.setManaged(false);

		loginHeaderBtn.setOnAction(e -> {
			boolean show = !loginCard.isVisible();
			loginCard.setVisible(show);
			loginCard.setManaged(show);
		});

		registerHeaderBtn.setOnAction(e -> new RegisterView().show(stage));

		/* ================= SEARCH ================= */

		searchField.setPromptText("Search by title or author...");
		searchField.setPrefWidth(420);
		searchField.getStyleClass().add("sb-text-field");

		Button searchBtn = new Button("Search");
		searchBtn.getStyleClass().addAll("sb-pill", "sb-primary");

		Button clearBtn = new Button("Clear");
		clearBtn.getStyleClass().addAll("sb-pill", "sb-accent");

		searchBtn.setOnAction(e -> doSearch());
		clearBtn.setOnAction(e -> {
			searchField.clear();
			sectionTitle.setText("All Books");
			loadAllBooks();
		});

		HBox searchBox = new HBox(10, searchField, searchBtn, clearBtn);
		searchBox.setAlignment(Pos.CENTER);
		searchBox.setPadding(new Insets(10, 10, 0, 10));

		FlowPane categoriesPane = new FlowPane(15, 15);
		categoriesPane.setAlignment(Pos.CENTER);
		categoriesPane.setPadding(new Insets(10));

		Button allBtn = new Button("All");
		allBtn.getStyleClass().add("sb-category");
		allBtn.setOnAction(e -> {
			sectionTitle.setText("All Books");
			loadAllBooks();
		});

		Button latestBtn = new Button("الأحدث");
		latestBtn.getStyleClass().add("sb-category");
		latestBtn.setOnAction(e -> {
			sectionTitle.setText("وصل حديثًا");
			loadLatestBooks();
		});

		categoriesPane.getChildren().addAll(allBtn, latestBtn);

		for (Category c : CategoryDAO.getAllCategories()) {
			Button btn = new Button(c.getCategoryName());
			btn.getStyleClass().add("sb-category");
			btn.setOnAction(e -> {
				sectionTitle.setText(c.getCategoryName());
				loadBooksByCategory(c.getCategoryName());
			});
			categoriesPane.getChildren().add(btn);
		}

		Label authorsLbl = new Label(" : المؤلفون");
		authorsLbl.getStyleClass().add("sb-filter-label");

		ComboBox<String> authorsBox = new ComboBox<>();
		authorsBox.setPromptText("اختر مؤلف");
		authorsBox.getStyleClass().add("sb-category");
		authorsBox.setPrefWidth(220);

		Set<String> authors = BookDAO.getAllBooks().stream().map(Book::getAuthor).filter(a -> a != null && !a.isBlank())
				.collect(Collectors.toSet());

		authorsBox.getItems().addAll(authors);

		authorsBox.setOnAction(e -> {
			String author = authorsBox.getValue();
			if (author != null) {
				sectionTitle.setText("كتب " + author);
				loadBooksByAuthor(author);
			}
		});

		Button bestBtn = new Button("الأكثر مبيعًا");
		bestBtn.getStyleClass().add("sb-category");
		bestBtn.setOnAction(e -> {
			sectionTitle.setText("الأكثر مبيعًا");
			loadBestSellers();
		});

		HBox secondRow = new HBox(20, authorsLbl, authorsBox, bestBtn);
		secondRow.setAlignment(Pos.CENTER);
		secondRow.setPadding(new Insets(5, 0, 10, 0));

		/* ================= BOOKS ================= */

		sectionTitle.getStyleClass().add("sb-section-title");
		sectionTitle.setText("All Books");

		booksPane.setPadding(new Insets(20));
		booksPane.setPrefWrapLength(1100);

		loadAllBooks();

		VBox centerContent = new VBox(15, searchBox, categoriesPane, secondRow, sectionTitle, booksPane);
		centerContent.setAlignment(Pos.TOP_CENTER);

		ScrollPane scroll = new ScrollPane(centerContent);
		scroll.setFitToWidth(true);

		/* ================= ROOT ================= */

		BorderPane root = new BorderPane();
		root.setTop(header);
		root.setCenter(scroll);
		root.setRight(loginCard);
		root.getStyleClass().add("sb-page");

		Scene scene = new Scene(root, 1200, 800);
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

		stage.setTitle("Sabastia BookShop");
		stage.setScene(scene);
		stage.show();


		/* ================= SESSION ================= */

		boolean loggedIn = Session.isLoggedIn();
		boolean isAdmin = loggedIn && "ADMIN".equalsIgnoreCase(Session.currentUser.getRole());

		welcome.setVisible(loggedIn);
		welcome.setManaged(loggedIn);
		welcome.setText(loggedIn ? "Welcome, " + Session.currentUser.getUsername() : "");

		adminBtn.setVisible(isAdmin);
		adminBtn.setManaged(isAdmin);

		cartBtn.setVisible(loggedIn && !isAdmin);
		cartBtn.setManaged(loggedIn && !isAdmin);

		logoutBtn.setVisible(loggedIn);
		logoutBtn.setManaged(loggedIn);

		loginHeaderBtn.setVisible(!loggedIn);
		loginHeaderBtn.setManaged(!loggedIn);

		registerHeaderBtn.setVisible(!loggedIn);
		registerHeaderBtn.setManaged(!loggedIn);
	}

	/* ================= LOGIN CARD ================= */

	private VBox buildLoginCard(Stage stage, Runnable onSuccess) {

		Image logoImage = new Image(getClass().getResourceAsStream("/logo.png"));
		ImageView logo = new ImageView(logoImage);
		logo.setFitWidth(80);
		logo.setFitHeight(80);
		logo.setPreserveRatio(true);

		Label title = new Label("Login");
		title.getStyleClass().add("sb-title");

		TextField username = new TextField();
		username.setPromptText("Username");
		username.getStyleClass().add("sb-text-field");

		PasswordField password = new PasswordField();
		password.setPromptText("Password");
		password.getStyleClass().add("sb-text-field");

		Button loginBtn = new Button("Login");
		loginBtn.getStyleClass().addAll("sb-pill", "sb-primary");
		loginBtn.setPrefWidth(220);

		Button registerBtn = new Button("Create Account");
		registerBtn.getStyleClass().addAll("sb-pill", "sb-accent");
		registerBtn.setPrefWidth(220);

		Label status = new Label();
		status.setStyle("-fx-text-fill:red;");

		loginBtn.setOnAction(e -> {
			User user = UserDAO.login(username.getText(), password.getText());

			if (user == null) {
				status.setText("Invalid credentials");
				return;
			}

			Session.currentUser = user;

			if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
				Session.currentCustomer = CustomerDAO.getCustomerByUserId(user.getId());
			} else {
				Session.currentCustomer = null;
			}

			onSuccess.run();
		});

		registerBtn.setOnAction(e -> new RegisterView().show(stage));

		VBox card = new VBox(12, logo, title, username, password, loginBtn, registerBtn, status);
		card.getStyleClass().add("sb-card");
		card.setAlignment(Pos.CENTER);
		card.setPrefWidth(300);

		return card;
	}

	/* ================= LOADERS ================= */

	private void loadLatestBooks() {
		booksPane.getChildren().clear();
		for (Book b : BookDAO.getLatestBooks(12)) {
			booksPane.getChildren().add(BookUI.createBookCard(b));
		}
	}

	private void loadAllBooks() {
		booksPane.getChildren().clear();
		for (Book b : BookDAO.getAllBooks()) {
			booksPane.getChildren().add(BookUI.createBookCard(b));
		}
	}

	private void loadBooksByCategory(String categoryName) {
		booksPane.getChildren().clear();
		for (Book b : BookDAO.getBooksByCategory(categoryName)) {
			booksPane.getChildren().add(BookUI.createBookCard(b));
		}
	}

	private void loadBooksByAuthor(String author) {
		booksPane.getChildren().clear();
		for (Book b : BookDAO.getBooksByAuthor(author)) {
			booksPane.getChildren().add(BookUI.createBookCard(b));
		}
	}

	private void loadBestSellers() {
		booksPane.getChildren().clear();

		for (Book b : BookDAO.getBestSellerBooks(3, 10)) {
			booksPane.getChildren().add(BookUI.createBookCard(b));
		}
	}

	private void doSearch() {
		String key = searchField.getText().trim();
		if (key.isEmpty()) {
			sectionTitle.setText("All Books");
			loadAllBooks();
			return;
		}

		sectionTitle.setText("Search Results");
		booksPane.getChildren().clear();
		for (Book b : BookDAO.searchBooks(key)) {
			booksPane.getChildren().add(BookUI.createBookCard(b));
		}
	}

	public static void main(String[] args) {
		launch();
	}
}