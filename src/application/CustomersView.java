package application;

import dao.CustomerDAO;
import model.Customer;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CustomersView {

    private TableView<Customer> table = new TableView<>();
    private ObservableList<Customer> data;

    public Tab getTab() {

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
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        data = FXCollections.observableArrayList(CustomerDAO.getAllCustomers());
        table.setItems(data);

        Button bAdd = new Button("➕ Add");
        Button bEdit = new Button("✏ Edit");
        Button bDelete = new Button("🗑 Delete");
        Button bRefresh = new Button("🔄 Refresh");

        HBox bar = new HBox(10, bAdd, bEdit, bDelete, bRefresh);
        bar.setPadding(new Insets(10));

        BorderPane l = new BorderPane();
        l.setTop(bar);
        l.setCenter(table);

        bRefresh.setOnAction(e -> refresh());

        bAdd.setOnAction(e -> openAddWindow());
        bEdit.setOnAction(e -> openEditWindow());
        bDelete.setOnAction(e -> deleteCustomer());

        Tab tab = new Tab("Customers", l);
        tab.setClosable(false);
        return tab;
    }


    private void refresh() {
        data.setAll(CustomerDAO.getAllCustomers());
    }

    private void deleteCustomer() {
        Customer sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) 
        	  return;

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Delete Customer");
        a.setHeaderText("Are you sure ?");
        a.setContentText(sel.getFullName());

        Optional<ButtonType> res = a.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            CustomerDAO.deleteCustomer(sel.getCustomerID());
            refresh();
        }
    }

    private void openAddWindow() {
        Stage wd = new Stage();
        wd.setTitle("Add Customer");

        TextField tName = new TextField();
        TextField tEmail = new TextField();
        TextField tPhone = new TextField();
        TextField tAddress = new TextField();
        TextField tCity = new TextField();

        tName.setPromptText("Full Name");
        tEmail.setPromptText("Email");
        tPhone.setPromptText("Phone");
        tAddress.setPromptText("Address");
        tCity.setPromptText("City");

        Button bSave = new Button("Save");

        bSave.setOnAction(e -> {
            Customer c = new Customer(
                    0,
                    tName.getText(),
                    tEmail.getText(),
                    tPhone.getText(),
                    tAddress.getText(),
                    tCity.getText(),
                    java.time.LocalDate.now()
            );

            CustomerDAO.insertCustomer(c);
            refresh();
            wd.close();
        });

        VBox lt = new VBox(10,
                new Label("Add New Customer"),
                tName, tEmail, tPhone, tAddress, tCity,
                bSave
        );
        lt.setPadding(new Insets(15));

        wd.setScene(new Scene(lt, 320, 350));
        wd.show();
    }

    private void openEditWindow() {
        Customer c = table.getSelectionModel().getSelectedItem();
        if (c == null)
        	  return;

        Stage i = new Stage();
        i.setTitle("Edit Customer");

        TextField tName = new TextField(c.getFullName());
        TextField tEmail = new TextField(c.getEmail());
        TextField tPhone = new TextField(c.getPhone());
        TextField tAddress = new TextField(c.getAddress());
        TextField tCity = new TextField(c.getCity());

        Button bUpdate = new Button("Update");

        bUpdate.setOnAction(e -> {
            c.setFullName(tName.getText());
            c.setEmail(tEmail.getText());
            c.setPhone(tPhone.getText());
            c.setAddress(tAddress.getText());
            c.setCity(tCity.getText());

            CustomerDAO.updateCustomer(c);
            refresh();
            i.close();
        });

        VBox l = new VBox(10,
                new Label("Edit Customer"),
                tName, tEmail, tPhone, tAddress, tCity,
                bUpdate
        );
        l.setPadding(new Insets(15));

        i.setScene(new Scene(l, 320, 350));
        i.show();
    }
}
