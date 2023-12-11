package com.example.software2.controller;

import com.example.software2.model.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.software2.MainApplication;
import com.example.software2.helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {
    Stage stage;
    Parent scene;
    @FXML
    private TextField addCustomerAddress;

    @FXML
    private ComboBox<String> addCustomerCountry;

    @FXML
    private TextField addCustomerID;

    @FXML
    private TextField addCustomerName;

    @FXML
    private TextField addCustomerPhoneNumber;

    @FXML
    private TextField addCustomerPostalCode;

    @FXML
    private ComboBox<String> addCustomerProvince;


    /**
     * Upon click of cancel button, navigates to the main-menu.fxml
     */
    @FXML
    void addCustomerCancelButtonClicked(ActionEvent event) throws IOException {
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-menu.fxml")));
            stage.setScene(new Scene(scene));
            stage.show();
    }

    /**
     * On customer save button clicked, it will grab all the information from the form and save it into the SQL
     * database.
     */
    @FXML
    void addCustomerSaveButtonClicked(ActionEvent event) {
        try{
            int id = createId();
            String name = addCustomerName.getText();
            String address = addCustomerAddress.getText();
            String postalCode = addCustomerPostalCode.getText();
            String phoneNumber = addCustomerPhoneNumber.getText();
            String division = addCustomerProvince.getValue();


            JDBC.makeConnection();
            String sql = "INSERT INTO customers (Customer_ID, Customer_Name, address, Postal_Code, Phone, Division_ID)" +
                    " VALUES (?, ?, ?, ?, ?, (SELECT Division_ID FROM first_level_divisions WHERE division = ?))";

            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, postalCode);
            preparedStatement.setString(5, phoneNumber);
            preparedStatement.setString(6, division);

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
     * A createId function.
     * @return a unique integer.
     */
    private int createId() {
        int appointmentID = 0;
        try {
            JDBC.makeConnection();
            String sql = "SELECT MAX(Appointment_ID) AS LargestAppointmentID FROM appointments";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                appointmentID = resultSet.getInt("LargestAppointmentID") + 3;
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

    /**
     * Initializes the add customer controller.
     */

    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> countryNames = getCountryNames();
        addCustomerCountry.setItems(countryNames);
        List<String> allStates = Arrays.asList(
                "Alabama", "Alaska", "Arizona", "Arkansas", "California",
                "Colorado", "Connecticut", "Delaware", "Florida", "Georgia",
                "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
                "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland",
                "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri",
                "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey",
                "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
                "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina",
                "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
                "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
        );

        HashMap<String, List<String>> countrySpecifics = new HashMap<>();
        countrySpecifics.put("Canada", Arrays.asList("Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador", "Nova Scotia", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Nunavut", "Yukon"));
        countrySpecifics.put("United Kingdom", Arrays.asList("England", "Scotland", "Wales", "Northern Ireland"));
        countrySpecifics.put("United States of America", allStates);

        /**
         * Lambda expression used here for readability. Grabbing the new value amd setting it to the selected country.
         */
        addCustomerCountry.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selectedCountry = newValue;
            if (selectedCountry != null) {
                List<String> stateProvinceData = countrySpecifics.get(selectedCountry);
                addCustomerProvince.setItems(FXCollections.observableArrayList(stateProvinceData));
            }
        });

        addCustomerID.setText("Auto Gen - Disabled");
        addCustomerID.setEditable(false);
        addCustomerID.setDisable(true);
    }

    /**
     *  Getting all the country names.
     * @return a list of strings of countries.
     */
    private ObservableList<String> getCountryNames() {
        String[] countries = {
                "Canada",
                "United Kingdom",
                "United States of America",
        };
        return FXCollections.observableArrayList(countries);
    }

}

