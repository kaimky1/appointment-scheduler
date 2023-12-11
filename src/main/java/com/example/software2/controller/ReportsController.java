package com.example.software2.controller;

import com.example.software2.MainApplication;
import com.example.software2.helper.JDBC;
import com.example.software2.model.Customer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.software2.model.Appointments;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsController implements Initializable {
    Stage stage;
    Parent scene;
    @FXML
    private TableColumn<Appointments, String> customerType;

    @FXML
    private TableColumn<Appointments, String> appointmentMonth;

    @FXML
    private TableColumn<Appointments, String> appointmentType;

    @FXML
    private TableView<Appointments> appointmentsTableView;

    @FXML
    private TableColumn<Appointments, String> contact;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private TableColumn<Appointments, Integer> contactCustomerID;

    @FXML
    private TableColumn<Appointments, String> contactDescription;

    @FXML
    private TableColumn<Appointments, String> contactEndTime;

    @FXML
    private TableColumn<Appointments, Integer> contactID;

    @FXML
    private TableColumn<Appointments, String> contactLocation;

    @FXML
    private TableColumn<Appointments, String> contactStartTime;

    @FXML
    private TableColumn<Appointments, String> contactTitle;

    @FXML
    private TableColumn<Appointments, String> contactType;

    @FXML
    private TableColumn<Appointments, String> contactUserID;

    @FXML
    private TableView<Appointments> contactsTableView;

    @FXML
    private ComboBox<String> customerComboBox;

    @FXML
    private TableColumn<Appointments, String> customerDescription;

    @FXML
    private TableColumn<Appointments, String> customerEndDateTime;

    @FXML
    private TableColumn<Appointments, String> customerID;

    @FXML
    private TableColumn<Appointments, String> customerLocation;

    @FXML
    private TableColumn<Appointments, String> customerStateDateTime;

    @FXML
    private TableView<Appointments> customerTableView;

    @FXML
    private TableColumn<Appointments, String> customerTitle;

    @FXML
    private TableColumn<Appointments, String> customerUserID;

    @FXML
    private TableColumn<Appointments, Integer> numberOfAppointments;

    /**
     * Navigates to the login.fxml upon click.
     */
    @FXML
    void onLogoutButtonClicked(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("login.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Navigates to the main-menu.fxml upon button click.
     */
    @FXML
    void onMainMenuButtonClicked(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-menu.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Observable List of all appointments.
     */
    private ObservableList<Appointments> allAppointments;

    /**
     * Observable list of all appointments by contacts.
     */
    private ObservableList<Appointments> contactsAppointments;

    /**
     * Observable List of all appointments by customers.
     */
    private ObservableList<Appointments> customerAppointments;

    /**
     * Initialized when the reports controller is activated.
     * Lambda function listens for the action and gets the appointment based on the id.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> contactNames = getContactNames();
        contactComboBox.setItems(contactNames);
        contactComboBox.setOnAction(event -> {
            String selectedContact = contactComboBox.getValue();
            if (selectedContact != null) {
                int contactId = getContactIdByName(selectedContact);
                if (contactId != -1) {
                    getAppointments(contactId);
                }
            }
        });
        ObservableList<String> customerNames = getCustomerNames();
        customerComboBox.setItems(customerNames);
        customerComboBox.setOnAction(event -> {
            String selectedCustomer = customerComboBox.getValue();
            if (selectedCustomer != null) {
                int customerID = getCustomerIdByName(selectedCustomer);
                if (customerID != -1) {
                    getCustomerAppointments(customerID);
                }
            }
        });

        getCustomers();
        initializeTableView();
        setAppointmentsTableView();
    }

    /**
     * The initialize table view which sets up the tables.
     */
    private void initializeTableView() {
        allAppointments = Appointments.getAllAppointments();
        contactsAppointments = FXCollections.observableArrayList();
        customerAppointments = FXCollections.observableArrayList();

        setTableView(contactsTableView, contactsAppointments);
        setTableView(customerTableView, customerAppointments);
        setTableView(appointmentsTableView, allAppointments);
    }

    /**
     * Helps to set the table views for each table that is needed.
     * @param tableView the type of table view that is being set.
     * @param data the data that is being sent in.
     */
    private void setTableView(TableView<Appointments> tableView, ObservableList<Appointments> data) {
        tableView.setItems(data);
    }

    /**
     * Sets the appointments table view up by grabbing all the unique appointments, counting all appointments and
     * displaying it in the table.
     */
    private void setAppointmentsTableView() {
        Set<String> uniqueCombinations = new HashSet<>();

        List<Appointments> uniqueAppointments = allAppointments.stream()
                .filter(appointment -> uniqueCombinations.add(getCombinationKey(appointment)))
                .collect(Collectors.toList());

        appointmentsTableView.setItems(FXCollections.observableArrayList(uniqueAppointments));

        numberOfAppointments.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(Appointments.getAppointmentsCountForTypeAndMonth(
                        cellData.getValue().getType(),
                        String.valueOf(cellData.getValue().getStart().getMonthValue())
                )).asObject());

        appointmentMonth.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStart().getMonth().toString()));

        appointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    /**
     * Function to create a unique combination key.
     * @param appointment Type appointments
     * @return combination like "test-11"
     */
    private String getCombinationKey(Appointments appointment) {
        return appointment.getType() + "-" + appointment.getStart().getMonthValue();
    }

    /**
     * Sets the customer table view.
     */
    private void setCustomerTableView() {
        customerTableView.setItems(customerAppointments);
        customerID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        customerDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        customerLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        customerStateDateTime.setCellValueFactory(new PropertyValueFactory<>("start"));
        customerEndDateTime.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        customerType.setCellValueFactory(new PropertyValueFactory<>("type"));
        customerTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        contactsTableView.refresh();
    }

    /**
     * Sets the contacts table view.
     */
    private void setContactsTableView() {
        contactsTableView.setItems(contactsAppointments);
        contact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        contactID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        contactDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        contactLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactStartTime.setCellValueFactory(new PropertyValueFactory<>("start"));
        contactEndTime.setCellValueFactory(new PropertyValueFactory<>("end"));
        contactCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        contactUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        contactType.setCellValueFactory(new PropertyValueFactory<>("type"));
        contactTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
    }

    /**
     * Grabs all the customers.
     */
    private void getCustomers() {
        Appointments.clearAllCustomers();
        try {
            JDBC.makeConnection();
            String sql = "SELECT c.Customer_ID, c.Customer_Name, c.Address, c.Postal_Code, c.Phone, d.Division FROM customers c JOIN first_level_divisions d ON c.Division_ID = d.Division_ID";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("Customer_ID");
                String name = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postalCode = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                String division = resultSet.getString("Division");
                Customer customer = new Customer(id, name, address, postalCode, phone, division);
                if (!Appointments.getAllCustomers().contains(customer)) {
                    Appointments.addCustomer(customer);
                }
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
    }

    /**
     * Grabs the appointment where the selected contact equals the id.
     * @param selectedContact the unique id of the selected contact
     */
    private void getAppointments(int selectedContact) {
        contactsAppointments.clear();
        try {
            JDBC.makeConnection();
            String sql = "SELECT " +
                    "appointments.Appointment_ID, " +
                    "appointments.Title, " +
                    "appointments.Description, " +
                    "appointments.Location, " +
                    "appointments.Type, " +
                    "appointments.Start, " +
                    "appointments.End, " +
                    "appointments.User_ID, " +
                    "appointments.Contact_ID AS appointments_Contact_ID, " +
                    "appointments.Customer_ID, " +
                    "contacts.Contact_Name, " +
                    "contacts.Contact_ID AS contacts_Contact_ID " +
                    "FROM appointments " +
                    "INNER JOIN contacts ON contacts.Contact_ID = appointments.Contact_ID " +
                    "WHERE appointments.Contact_ID = ?";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setInt(1, selectedContact);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int appointmentID = resultSet.getInt("Appointment_ID");
                String title = resultSet.getString("Title");
                String description = resultSet.getString("Description");
                String location = resultSet.getString("Location");
                String contact = resultSet.getString("Contact_Name");
                String type = resultSet.getString("Type");
                Timestamp startTimestamp = resultSet.getTimestamp("Start");
                LocalDateTime start = startTimestamp.toLocalDateTime();
                Timestamp endTimestamp = resultSet.getTimestamp("End");
                LocalDateTime end = endTimestamp.toLocalDateTime();
                int customerID = resultSet.getInt("Customer_ID");
                int userID = resultSet.getInt("User_ID");
                Appointments appointment = new Appointments(appointmentID, title, description, location, contact, type, start, end, customerID, userID);

                if (customerID > 0) {
                    contactsAppointments.add(appointment);
                }
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
        setContactsTableView();
    }

    /**
     * Grabs the customer based on the customer ID.
     * @param selectedCustomer the unique ID of the selected customer
     */
    private void getCustomerAppointments(int selectedCustomer) {
        customerAppointments.clear();
        try {
            JDBC.makeConnection();
            String sql = "SELECT " +
                    "appointments.Appointment_ID, " +
                    "appointments.Title, " +
                    "appointments.Description, " +
                    "appointments.Location, " +
                    "appointments.Type, " +
                    "appointments.Start, " +
                    "appointments.End, " +
                    "appointments.User_ID, " +
                    "appointments.Contact_ID AS appointments_Contact_ID, " +
                    "appointments.Customer_ID, " +
                    "contacts.Contact_Name, " +
                    "contacts.Contact_ID AS contacts_Contact_ID " +
                    "FROM appointments " +
                    "INNER JOIN contacts ON contacts.Contact_ID = appointments.Contact_ID " +
                    "WHERE appointments.Customer_ID = ?";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setInt(1, selectedCustomer);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int appointmentID = resultSet.getInt("Appointment_ID");
                String title = resultSet.getString("Title");
                String description = resultSet.getString("Description");
                String location = resultSet.getString("Location");
                String contact = resultSet.getString("Contact_Name");
                String type = resultSet.getString("Type");
                Timestamp startTimestamp = resultSet.getTimestamp("Start");
                LocalDateTime start = startTimestamp.toLocalDateTime();
                Timestamp endTimestamp = resultSet.getTimestamp("End");
                LocalDateTime end = endTimestamp.toLocalDateTime();
                int customerID = resultSet.getInt("Customer_ID");
                int userID = resultSet.getInt("User_ID");
                Appointments appointment = new Appointments(appointmentID, title, description, location, contact, type, start, end, customerID, userID);

                if (customerID != 0) {
                    customerAppointments.add(appointment);
                }
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
        setCustomerTableView();
    }

    /**
     *
     * @return Grabs all the contact names.
     */
    private ObservableList<String> getContactNames() {
        List<String> contactList;
        try {
            JDBC.makeConnection();
            String sql = "SELECT Contact_Name FROM contacts";

            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            contactList = new ArrayList<>();
            while (resultSet.next()) {
                String contact = resultSet.getString("Contact_Name");
                contactList.add(contact);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
        return FXCollections.observableArrayList(contactList);
    }

    /**
     *
     * @return Grabs all the customer names.
     */
    private ObservableList<String> getCustomerNames() {
        List<String> customerNameList;
        try {
            JDBC.makeConnection();
            String sql = "SELECT Customer_Name FROM customers";

            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            customerNameList = new ArrayList<>();
            while (resultSet.next()) {
                String contact = resultSet.getString("Customer_Name");
                customerNameList.add(contact);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
        return FXCollections.observableArrayList(customerNameList);
    }

    /**
     * Returns contact id of the name that we send in.
     * @param contactName name of contact
     * @return the contact id
     */

    private int getContactIdByName(String contactName) {
        int contactId = -1;
        try {
            JDBC.makeConnection();
            String sql = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setString(1, contactName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                contactId = resultSet.getInt("Contact_ID");
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
        return contactId;
    }

    /**
     * Get customer id by name.
     * @param customerName customer name
     * @return customer id based on name
     */
    private int getCustomerIdByName(String customerName) {
        int customerID = -1;
        try {
            JDBC.makeConnection();
            String sql = "SELECT Customer_ID FROM customers WHERE Customer_Name = ?";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setString(1, customerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                customerID = resultSet.getInt("Customer_ID");
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
        return customerID;
    }
}
