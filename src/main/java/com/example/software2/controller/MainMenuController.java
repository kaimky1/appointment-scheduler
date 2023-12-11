package com.example.software2.controller;
import com.example.software2.helper.ControllerHelper;
import com.example.software2.helper.JDBC;
import com.example.software2.model.Appointments;
import com.example.software2.model.Customer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.example.software2.MainApplication;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    Stage stage;
    Parent scene;

    @FXML
    private TableColumn<Customer, String> customerAddressColumn;

    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;

    @FXML
    private TableColumn<Customer, String> customerNameColumn;

    @FXML
    private TableColumn<Customer, String> customerPhoneNumberColumn;

    @FXML
    private TableColumn<Customer, String> customerPostalCodeColumn;

    @FXML
    private TableColumn<Customer, String> customerProvinceStateColumn;


    @FXML
    private TableView<Customer> customersTableView;

    @FXML
    private TableColumn<Appointments, String> appointmentContact;

    @FXML
    private TableColumn<Appointments, String> appointmentCustomerID;

    @FXML
    private TableColumn<Appointments, String> appointmentDescription;

    @FXML
    private TableColumn<Appointments, String> appointmentEndTime;

    @FXML
    private TableColumn<Appointments, String> appointmentID;

    @FXML
    private TableColumn<Appointments, String> appointmentLocation;

    @FXML
    private TableColumn<Appointments, String> appointmentStartTime;

    @FXML
    private TableColumn<Appointments, String> appointmentTitle;

    @FXML
    private TableColumn<Appointments, String> appointmentType;

    @FXML
    private TableColumn<Appointments, String> appointmentUserID;

    @FXML
    private TableView<Appointments> appointmentsTableView;

    /**
     * Function to filter appt by week when the radio button is clicked.
     * @param event
     */
    @FXML
    void weekFilterClicked(ActionEvent event) {
        Calendar calendar = Calendar.getInstance();
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        getAppointmentsByWeek(weekOfYear, year);
        setAppointmentTableView();
    }

    /**
     * Function to filter by month when the radio button is clicked.
     * @param event
     */

    @FXML
    void monthFilterClicked(ActionEvent event) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        getAppointmentsByMonth(month, year);
        setAppointmentTableView();
    }

    /**
     * Function to switch appointment view to all appointments when clicked.
     * @param event
     */
    @FXML
    void allFilterClicked(ActionEvent event) {
        getAppointments();
        setAppointmentTableView();
    }

    /**
     * Button to navigate to the customer-create-page.fxml
     * @param event
     * @throws IOException
     */
    @FXML
    void addCustomerButtonClicked(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("customer-create-page.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Button that deletes a customer from the SQL database.
     * @param event
     */

    @FXML
    void deleteCustomerButtonClicked(ActionEvent event) {
        Customer customer = customersTableView.getSelectionModel().getSelectedItem();
        try {
            JDBC.makeConnection();
            String sql = "DELETE customers, appointments FROM customers " +
                    "LEFT JOIN appointments ON customers.Customer_ID = appointments.Customer_ID " +
                    "WHERE customers.Customer_ID = ?";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setInt(1, customer.getId());
            preparedStatement.executeUpdate();
            getCustomers();
            setCustomerTableView();
            setAppointmentTableView();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Deleted Successfully");
            alert.showAndWait();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error Deleting Customer" + "\n\nError details: " + e.getMessage());
            alert.showAndWait();
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
    }

    /**
     * Button that will take you to the customer-update-page.fxml.
     * @param event
     * @throws IOException
     */
    @FXML
    void updateCustomerButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Objects.requireNonNull(MainApplication.class.getResource("customer-update-page.fxml")));
        loader.load();

        UpdateCustomerController updateCustomerController = loader.getController();

        if (customersTableView.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a customer");
            alert.showAndWait();
        }

        updateCustomerController.sendCustomer(customersTableView.getSelectionModel().getSelectedItem());

        stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Navigates to the reports-page.fxml once clicked.
     * @param event
     * @throws IOException
     */
    @FXML
    void onReportsButtonClicked(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("reports-page.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Navigates you back to the login.fxml page.
     * @param event
     * @throws IOException
     */
    @FXML
    void onLogoutButtonClicked(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("login.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Navigates you to the appointment-create-page.fxml to create an appt.
     * @param event
     * @throws IOException
     */
    @FXML
    void addAppointmentButtonClicked(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("appointment-create-page.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Deletes an appointment once the button is clicked.
     * @param event
     */
    @FXML
    void deleteAppointmentButtonClicked(ActionEvent event) {
        Appointments appointment = appointmentsTableView.getSelectionModel().getSelectedItem();
        try {
            JDBC.makeConnection();
            String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setInt(1, appointment.getAppointmentID());
            preparedStatement.executeUpdate();
            getAppointments();
            setAppointmentTableView();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment Deleted");
            alert.setHeaderText("Appointment " + appointment.getAppointmentID() + " has been deleted.");
            alert.setContentText("Appointment Type: " + appointment.getType());
            alert.showAndWait();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
    }

    /**
     * Allows you to edit an appointment by taking you to the appointment-update-page.fxml.
     * @param event
     * @throws IOException
     */
    @FXML
    void updateAppointmentButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Objects.requireNonNull(MainApplication.class.getResource("appointment-update-page.fxml")));
        loader.load();

        UpdateAppointmentController updateAppointmentController = loader.getController();

        if (appointmentsTableView.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select an appointment");
            alert.showAndWait();
        }

        updateAppointmentController.sendAppointment(appointmentsTableView.getSelectionModel().getSelectedItem());

        stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Once the main menu is initialized, we ensure to call the following functions:
     * getAppointments(), getCustomers();, setCustomerTableView();, and setAppointmentTableView();
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getAppointments();
        getCustomers();
        setCustomerTableView();
        setAppointmentTableView();
    }

    /**
     * Sets the appointment table.
     */
    private void setAppointmentTableView() {
        appointmentsTableView.setItems(Appointments.getAllAppointments());
        appointmentContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentStartTime.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentStartTime.setCellValueFactory(cellData -> {
            LocalDateTime item = cellData.getValue().getStart();
            return new SimpleStringProperty(item.toString());
        });
        appointmentEndTime.setCellValueFactory(cellData -> {
            LocalDateTime item = cellData.getValue().getEnd();
            String formattedStart = ControllerHelper.convertToLocalTime(String.valueOf(item));
            return new SimpleStringProperty(formattedStart);
        });
        appointmentCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        appointmentUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
    }

    /**
     * Sets the customer table.
     */
    private void setCustomerTableView() {
        customersTableView.setItems(Appointments.getAllCustomers());
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        customerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerProvinceStateColumn.setCellValueFactory(new PropertyValueFactory<>("provinceState"));
    }

    /**
     * Grabs all the customers from the SQL database.
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
     * Gets all the appointments from the SQL database.
     */
    private void getAppointments() {
        Appointments.clearAllAppointments();
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
                    "INNER JOIN contacts ON contacts.Contact_ID = appointments.Contact_ID";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);

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
                if (!Appointments.getAllAppointments().contains(appointment)) {
                    Appointments.addAppointment(appointment);
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
     * Filters appointments by week
     * @param weekOfYear integer that takes in the week of the year.
     * @param year integer that takes in the year.
     */

    private void getAppointmentsByWeek(int weekOfYear, int year) {
        Appointments.clearAllAppointments();
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
                    "WHERE WEEK(appointments.Start, 1) = ? AND YEAR(appointments.Start) = ?";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setInt(1, weekOfYear);
            preparedStatement.setInt(2, year);

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
                if (!Appointments.getAllAppointments().contains(appointment)) {
                    Appointments.addAppointment(appointment);
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
     * Grabs the appointments by month.
     * @param month Integer that takes in the month.
     * @param year Integer that takes in the year.
     */
    private void getAppointmentsByMonth(int month, int year) {
        Appointments.clearAllAppointments();
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
                    "WHERE MONTH(Start) = ? AND YEAR(Start) = ?";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setInt(1, month);
            preparedStatement.setInt(2, year);

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
                if (!Appointments.getAllAppointments().contains(appointment)) {
                    Appointments.addAppointment(appointment);
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
}
