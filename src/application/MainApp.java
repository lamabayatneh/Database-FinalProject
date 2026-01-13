package application;

import java.util.Optional;

import dao.BookDAO;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Book;

public class MainApp extends Application {

	TableView<Book> table = new TableView<>();

	@Override
	public void start(Stage stage) {

		TableColumn<Book, Integer> cId = new TableColumn<>("ID");
		cId.setCellValueFactory(new PropertyValueFactory<>("bookID"));

		TableColumn<Book, String> cTitle = new TableColumn<>("Title");
		cTitle.setCellValueFactory(new PropertyValueFactory<>("title"));

		TableColumn<Book, String> cAuthor = new TableColumn<>("Author");
		cAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

		TableColumn<Book, Double> cPrice = new TableColumn<>("Price");
		cPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

		TableColumn<Book, Integer> cQty = new TableColumn<>("Quantity");
		cQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		table.getColumns().addAll(cId, cTitle, cAuthor, cPrice, cQty);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		ObservableList<Book> data = FXCollections.observableArrayList(BookDAO.getAllBooks());
		table.setItems(data);

		Button bAdd = new Button("➕ Add Book");
		Button bEdit = new Button("✏ Edit Book");
		Button bDelete = new Button("🗑 Delete");
		Button bRefresh = new Button("🔄 Refresh");

		HBox r = new HBox(10, bAdd, bEdit, bDelete, bRefresh);
		r.setPadding(new Insets(10));

		BorderPane bLayout = new BorderPane();
		bLayout.setTop(r);
		bLayout.setCenter(table);

		Tab bTab = new Tab("Books", bLayout);
		bTab.setClosable(false);

		CustomersView cv = new CustomersView();

		TabPane tabs = new TabPane();
		tabs.getTabs().addAll(bTab, cv.getTab());

		Scene se = new Scene(tabs, 1000, 600);
		stage.setScene(se);
		stage.show();

		bRefresh.setOnAction(e -> {
			data.setAll(BookDAO.getAllBooks());
		});

		bAdd.setOnAction(e -> openAddWindow(data));

		bEdit.setOnAction(e -> {
			Book selected = table.getSelectionModel().getSelectedItem();

			if (selected == null) {
				System.out.println("Please select a book first!");
				return;
			}

			openEditWindow(selected, data);
		});

		bDelete.setOnAction(e -> {

			Book sel = table.getSelectionModel().getSelectedItem();

			if (sel == null) {
				System.out.println("Please select a book first!");
				return;
			}

			Alert al = new Alert(Alert.AlertType.CONFIRMATION);
			al.setTitle("Confirm Delete");
			al.setHeaderText("Are you sure you want to delete this book ?");
			al.setContentText(sel.getTitle());

			Optional<ButtonType> res = al.showAndWait();

			if (res.isPresent() && res.get() == ButtonType.OK) {
				BookDAO.deleteBook(sel.getBookID());
				data.setAll(BookDAO.getAllBooks());
			}
		});

	}

	private void openAddWindow(ObservableList<Book> data) {

		Stage w = new Stage();
		w.setTitle("Add New Book");

		TextField tTitle = new TextField();
		TextField tAuthor = new TextField();
		TextField tprice = new TextField();
		TextField tQty = new TextField();

		tTitle.setPromptText("Title");
		tAuthor.setPromptText("Author");
		tprice.setPromptText("Price");
		tQty.setPromptText("Quantity");

		Button bSave = new Button("Save");

		bSave.setOnAction(e -> {

			Book b = new Book(0, tTitle.getText(), tAuthor.getText(), Double.parseDouble(tprice.getText()),
					Integer.parseInt(tQty.getText()), java.time.LocalDate.now());

			BookDAO.insertBook(b);
			data.setAll(BookDAO.getAllBooks());
			w.close();
		});

		VBox lt = new VBox(10, new Label("Add New Book"), tTitle, tAuthor, tprice, tQty, bSave);

		lt.setPadding(new Insets(15));

		w.setScene(new Scene(lt, 300, 300));
		w.show();
	}

	private void openEditWindow(Book sel, ObservableList<Book> data) {

		Stage i = new Stage();
		i.setTitle("Edit Book");

		TextField tTitle = new TextField(sel.getTitle());
		TextField tAuthor = new TextField(sel.getAuthor());
		TextField tPrice = new TextField(String.valueOf(sel.getPrice()));
		TextField tQty = new TextField(String.valueOf(sel.getQuantity()));

		Button bSave = new Button("Update");

		bSave.setOnAction(e -> {

			sel.setTitle(tTitle.getText());
			sel.setAuthor(tAuthor.getText());
			sel.setPrice(Double.parseDouble(tPrice.getText()));
			sel.setQuantity(Integer.parseInt(tQty.getText()));

			BookDAO.updateBook(sel);
			data.setAll(BookDAO.getAllBooks());
			i.close();
		});

		VBox lt = new VBox(10, new Label("Edit Book"), tTitle, tAuthor, tPrice, tQty, bSave);

		lt.setPadding(new Insets(15));
		i.setScene(new Scene(lt, 300, 300));
		i.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
