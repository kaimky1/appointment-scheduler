package com.example.software2.controller;

import com.example.software2.MainApplication;
import com.example.software2.helper.ControllerHelper;
import com.example.software2.helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AddAppointmentController implements Initializable {
    Stage stage;
    Parent scene;
    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private ComboBox<String> customerComboBox;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> endTimeComboBox;

    @FXML
    private TextField addAppointmentID;

    @FXML
    private TextField locationField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<String> startTimeComboBox;

    @FXML
    private TextField titleField;

    @FXML
    private TextField typeField;

    @FXML
    private ComboBox<String> userComboBox;

    /**
     * On click of add button, it will insert the information from the form into the database.
     */

    @FXML
    void onAddAppointmentAction(ActionEvent event) {
        try{
            int id = createId();
            String title = titleField.getText();
            String description = descriptionField.getText();
            String location = locationField.getText();
            String type = typeField.getText();
            String start = ControllerHelper.formatDateTime(startDatePicker, startTimeComboBox);
            String end = ControllerHelper.formatDateTime(endDatePicker, endTimeComboBox);
            String customerID = customerComboBox.getValue();
            String userID = userComboBox.getValue();
            Integer contactID = ControllerHelper.findContactID(contactComboBox.getValue());


            if (!ControllerHelper.businessHours(LocalTime.parse(startTimeComboBox.getValue()), LocalTime.parse(endTimeComboBox.getValue()))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Appointments must be scheduled between 8 am and 10 pm ET.");
                alert.showAndWait();
                return;
            }

            if (ControllerHelper.overlappingAppointments(customerID, start, end)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The customer already has a scheduled appointment during this time");
                alert.showAndWait();
                return;
            }
            JDBC.makeConnection();
            String sql = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, location);
            preparedStatement.setString(5, type);
            preparedStatement.setString(6, start);
            preparedStatement.setString(7, end);
            preparedStatement.setString(8, customerID);
            preparedStatement.setString(9, userID);
            preparedStatement.setInt(10, contactID);


            preparedStatement.executeUpdate();
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-menu.fxml")));
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Enter valid values: " + e);
            alert.showAndWait();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * The cancel button navigates to the main-menu.fxml.
     */
    @FXML
    void onAddAppointmentCancelButton(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-menu.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Initializes the add appointment controller.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> contactNames = ControllerHelper.getContactNames();
        contactComboBox.setItems(contactNames);
        ObservableList<String> userIds = ControllerHelper.getUserIDs();
        userComboBox.setItems(userIds);
        ObservableList<String> customerIDs = ControllerHelper.getCustomerIDs();
        customerComboBox.setItems(customerIDs);
        ObservableList<String> times = ControllerHelper.getTimes();
        startTimeComboBox.setItems(times);
        endTimeComboBox.setItems(times);
        addAppointmentID.setText("Auto Gen - Disabled");
        addAppointmentID.setEditable(false);
        addAppointmentID.setDisable(true);
    }

    private int createId() {
        int appointmentID = 0;
        try {
            JDBC.makeConnection();
            String sql = "SELECT MAX(Appointment_ID) AS LargestAppointmentID FROM appointments";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                appointmentID = resultSet.getInt("LargestAppointmentID") + 1;
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
        return appointmentID;
    }
}

