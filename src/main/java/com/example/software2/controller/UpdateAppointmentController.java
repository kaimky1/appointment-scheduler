package com.example.software2.controller;

import com.example.software2.MainApplication;
import com.example.software2.helper.ControllerHelper;
import com.example.software2.helper.JDBC;
import com.example.software2.model.Appointments;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class UpdateAppointmentController implements Initializable {
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
    private TextField updateAppointmentID;

    @FXML
    private ComboBox<String> userComboBox;

    /**
     * on update button, we are saving the new appointment.
     */
    @FXML
    void onUpdateAppointmentAction(ActionEvent event) {
        try{
            int id = Integer.parseInt(updateAppointmentID.getText());
            String title = titleField.getText();
            String type = typeField.getText();
            String description = descriptionField.getText();
            String location = locationField.getText();
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
            String sql = "UPDATE appointments SET Title = ?, Type = ? , Description = ?, Location = ?, Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, location);
            preparedStatement.setString(5, start);
            preparedStatement.setString(6, end);
            preparedStatement.setString(7, customerID);
            preparedStatement.setString(8, userID);
            preparedStatement.setInt(9, contactID);
            preparedStatement.setInt(10, id);

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
     * Navigated to the main-menu.fxml
     */
    @FXML
    void onUpdateAppointmentCancelButton(ActionEvent event) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-menu.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Grabbing the appointment and prefilling out the form.
     * @param appointment type Appointments
     */
    public void sendAppointment(Appointments appointment) {
        LocalDateTime start = LocalDateTime.parse(ControllerHelper.convertToLocalTime(String.valueOf(appointment.getStart())));
        LocalDateTime end = LocalDateTime.parse(ControllerHelper.convertToLocalTime(String.valueOf(appointment.getEnd())));
        updateAppointmentID.setEditable(false);
        updateAppointmentID.setDisable(true);
        updateAppointmentID.setText(Integer.toString(appointment.getAppointmentID()));
        titleField.setText(appointment.getTitle());
        typeField.setText(appointment.getType());
        descriptionField.setText(appointment.getDescription());
        locationField.setText(appointment.getLocation());
        startDatePicker.setValue(start.toLocalDate());
        startTimeComboBox.setValue(String.valueOf(start.toLocalTime()));
        endDatePicker.setValue(end.toLocalDate());
        endTimeComboBox.setValue(String.valueOf(end.toLocalTime()));
        customerComboBox.setValue(String.valueOf(appointment.getCustomerID()));
        userComboBox.setValue(String.valueOf(appointment.getUserID()));
        contactComboBox.setValue(appointment.getContact());
    }

    /**
     *
     * Initializing the controller.
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
    }
}

