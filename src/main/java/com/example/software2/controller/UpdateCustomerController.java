package com.example.software2.controller;

import com.example.software2.MainApplication;
import com.example.software2.helper.JDBC;
import com.example.software2.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UpdateCustomerController implements Initializable {
    Stage stage;
    Parent scene;
    @FXML
    private TextField updateCustomerAddress;

    @FXML
    private ComboBox<String> updateCustomerCountry;

    @FXML
    private TextField updateCustomerID;

    @FXML
    private TextField updateCustomerName;

    @FXML
    private TextField updateCustomerPhoneNumber;

    @FXML
    private TextField updateCustomerPostalCode;

    @FXML
    private ComboBox<String> updateCustomerProvince;


    /**
     * Return to the main-menu.fxml.
     */
    @FXML
    void updateCustomerCancelButtonClicked(ActionEvent event) throws IOException {
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-menu.fxml")));
            stage.setScene(new Scene(scene));
            stage.show();
    }

    /**
     * Update and send the updated customer into the sql database.
     */
    @FXML
    void updateCustomerSaveButtonClicked(ActionEvent event) {
        try{
            int id = Integer.parseInt(updateCustomerID.getText());
            String name = updateCustomerName.getText();
            String address = updateCustomerAddress.getText();
            String postalCode = updateCustomerPostalCode.getText();
            String phoneNumber = updateCustomerPhoneNumber.getText();
            String customerID = updateCustomerID.getText();
            String division = updateCustomerProvince.getValue();

            JDBC.makeConnection();
            String sql = "UPDATE customers SET Customer_ID = ?, Customer_Name = ? , address = ?, Postal_Code = ?, Phone = ?, Division_ID = (SELECT Division_ID FROM first_level_divisions WHERE division = ?) WHERE Customer_ID = ?";

            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, postalCode);
            preparedStatement.setString(5, phoneNumber);
            preparedStatement.setString(6, division);
            preparedStatement.setString(7, customerID);

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
     * A list of states.
     */
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

    /**
     * UK list of provinces.
     */
    List<String> UK = Arrays.asList(
            "England", "Scotland", "Wales", "Northern Ireland"
    );

    /**
     * Canada list of provinces.
     */
    List<String> Canada = Arrays.asList(
            "Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador", "Nova", "Scotia",
            "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan"
    );


    /**
     * Grabbing the customer and setting the form fields with the customer information that was sent in.
     * @param customer type Customer
     */
    public void sendCustomer(Customer customer) {
        updateCustomerID.setEditable(false);
        updateCustomerID.setDisable(true);
        updateCustomerID.setText(Integer.toString(customer.getId()));
        updateCustomerName.setText(customer.getName());
        updateCustomerPhoneNumber.setText(customer.getPhoneNumber());
        updateCustomerPostalCode.setText(customer.getPostalCode());
        updateCustomerAddress.setText(customer.getAddress());
        updateCustomerProvince.setValue(customer.getProvinceState());

        if (allStates.contains(customer.getProvinceState())) {
            updateCustomerCountry.setValue("United States of America");
        } else if (Canada.contains(customer.getProvinceState())) {
            updateCustomerCountry.setValue("Canada");
        } else if (UK.contains(customer.getProvinceState())) {
            updateCustomerCountry.setValue("United Kingdom");
        }
    }

    /**
     * Initializing the controller.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> countryNames = getCountryNames();
        updateCustomerCountry.setItems(countryNames);

        HashMap<String, List<String>> countrySpecifics = new HashMap<>();
        countrySpecifics.put("Canada", Canada);
        countrySpecifics.put("United Kingdom", UK);
        countrySpecifics.put("United States of America", allStates);

        updateCustomerCountry.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selectedCountry = newValue;
            if (selectedCountry != null) {
                List<String> stateProvinceData = countrySpecifics.get(selectedCountry);
                updateCustomerProvince.setItems(FXCollections.observableArrayList(stateProvinceData));
            }
        });

        updateCustomerID.setText("Auto Gen - Disabled");
        updateCustomerID.setEditable(false);
        updateCustomerID.setDisable(true);
    }

    /**
     * function to get all country names.
     * @return all the country names
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

