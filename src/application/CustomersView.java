package application;

import dao.CustomerDAO;
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
import model.Customer;
import java.time.LocalDate;
import java.util.Optional;

public class CustomersView {

	private TableView<Customer> table = new TableView<>();
	private ObservableList<Customer> data = FXCollections.observableArrayList(CustomerDAO.getAllCustomers());

	private FilteredList<Customer> filteredData = new FilteredList<>(data, c -> true);

	public BorderPane getView() {

		TableColumn<Customer, Integer> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(new PropertyValueFactory<>("customerID"));

		TableColumn<Customer, String> colName = new TableColumn<>("Name");
		colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));

		TableColumn<Customer, String> colEmail = new TableColumn<>("Email");
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

		TableColumn<Customer, String> colPhone = new TableColumn<>("Phone");
		colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

		TableColumn<Customer, String> colCity = new TableColumn<>("City");
		colCity.setCellValueFactory(new PropertyValueFactory<>("city"));

		table.getColumns().addAll(colId, colName, colEmail, colPhone, colCity);
		table.setItems(filteredData);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TextField searchField = new TextField();
		searchField.setPromptText("🔍 Search customer (name, email, city...)");
		searchField.setPrefWidth(250);

		searchField.textProperty().addListener((obs, oldVal, newVal) -> {
			String keyword = newVal.toLowerCase();

			filteredData.setPredicate(c -> {
				if (keyword.isEmpty())
					return true;

				return c.getFullName().toLowerCase().contains(keyword) || c.getEmail().toLowerCase().contains(keyword)
						|| c.getCity().toLowerCase().contains(keyword) || c.getPhone().toLowerCase().contains(keyword);
			});
		});

		Button add = new Button("➕ Add");
		Button edit = new Button("✏ Edit");
		Button delete = new Button("🗑 Delete");

		add.setOnAction(e -> openAddWindow(Session.currentUser.getId()));
		edit.setOnAction(e -> openEditWindow());
		delete.setOnAction(e -> deleteCustomer());

		HBox actions = new HBox(15, add, edit, delete, searchField);
		actions.setPadding(new Insets(10));

		BorderPane layout = new BorderPane();
		layout.setTop(actions);
		layout.setCenter(table);

		return layout;
	}

	private void openAddWindow(int userId) {
		Stage win = new Stage();
		win.setTitle("Add Customer");

		Label title = new Label("➕ Add New Customer");
		title.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

		TextField name = new TextField();
		TextField email = new TextField();
		TextField phone = new TextField();
		TextField address = new TextField();
		TextField city = new TextField();

		name.setPromptText("Full Name");
		email.setPromptText("Email");
		phone.setPromptText("Phone");
		address.setPromptText("Address");
		city.setPromptText("City");

		name.setMaxWidth(250);
		email.setMaxWidth(250);
		phone.setMaxWidth(250);
		address.setMaxWidth(250);
		city.setMaxWidth(250);

		Label status = new Label();
		status.setStyle("-fx-text-fill: red;");

		Button save = new Button("💾 Save");
		save.setStyle("""
				    -fx-background-color: #2ecc71;
				    -fx-text-fill: white;
				    -fx-font-weight: bold;
				    -fx-background-radius: 8;
				    -fx-padding: 8 20 8 20;
				""");

		save.setOnMouseEntered(e -> save.setStyle("""
				    -fx-background-color: #27ae60;
				    -fx-text-fill: white;
				    -fx-font-weight: bold;
				    -fx-background-radius: 8;
				    -fx-padding: 8 20 8 20;
				"""));
		save.setOnMouseExited(e -> save.setStyle("""
				    -fx-background-color: #2ecc71;
				    -fx-text-fill: white;
				    -fx-font-weight: bold;
				    -fx-background-radius: 8;
				    -fx-padding: 8 20 8 20;
				"""));

		save.setOnAction(e -> {
			if (name.getText().isEmpty() || email.getText().isEmpty()) {
				status.setText("⚠ Name and Email are required");
				return;
			}

			Customer c = new Customer(0, userId, name.getText().trim(), email.getText().trim(), phone.getText().trim(),
					address.getText().trim(), city.getText().trim(), LocalDate.now());

			CustomerDAO.insertCustomer(c);
			refresh();
			win.close();
		});

		VBox box = new VBox(10, title, name, email, phone, address, city, status, save);
		box.setPadding(new Insets(20));
		box.setAlignment(Pos.CENTER);
		box.setStyle("""
				    -fx-background-color: #ffffff;
				    -fx-background-radius: 12;
				    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);
				""");

		StackPane root = new StackPane(box);
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2);");

		win.setScene(new Scene(root, 350, 450));
		win.show();
	}

	private void openEditWindow() {

		Customer c = table.getSelectionModel().getSelectedItem();
		if (c == null)
			return;

		Stage win = new Stage();

		TextField name = new TextField(c.getFullName());
		TextField email = new TextField(c.getEmail());
		TextField phone = new TextField(c.getPhone());
		TextField address = new TextField(c.getAddress());
		TextField city = new TextField(c.getCity());

		Button update = new Button("Update");

		update.setOnAction(e -> {
			c.setFullName(name.getText());
			c.setEmail(email.getText());
			c.setPhone(phone.getText());
			c.setAddress(address.getText());
			c.setCity(city.getText());

			CustomerDAO.updateCustomer(c);
			refresh();
			win.close();
		});

		VBox box = new VBox(10, name, email, phone, address, city, update);
		box.setPadding(new Insets(15));

		win.setScene(new Scene(box, 300, 350));
		win.setTitle("Edit Customer");
		win.show();
	}

	private void deleteCustomer() {

		Customer c = table.getSelectionModel().getSelectedItem();
		if (c == null)
			return;

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Customer");
		alert.setHeaderText("Are you sure?");
		alert.setContentText(c.getFullName());

		Optional<ButtonType> res = alert.showAndWait();
		if (res.isPresent() && res.get() == ButtonType.OK) {
			CustomerDAO.deleteCustomer(c.getCustomerID());
			refresh();
		}
	}

	private void refresh() {
		data.setAll(CustomerDAO.getAllCustomers());
	}

}
