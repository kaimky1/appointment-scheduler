package com.example.software2.controller;

import com.example.software2.MainApplication;
import com.example.software2.helper.ControllerHelper;
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
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    Stage stage;
    Parent scene;
    @FXML
    private Label currentLocaleLabel;

    @FXML
    private TextField passwordText;

    @FXML
    private TextField userNameText;

    @FXML
    private Button loginButton;

    @FXML
    private Text loginLabel;
    private Locale currentLocale;
    private ResourceBundle rb;

    /**
     * When the button is clicked, we retrieve the UN and PW from the database and ensure it is correct.
     * @param event
     * @throws IOException
     */
    @FXML
    void onLoginButtonClicked(ActionEvent event) throws IOException {
        String inputtedUserName = userNameText.getText();
        String inputtedPassword = passwordText.getText();

        try {
            JDBC.makeConnection();
            String sql = "SELECT User_Name FROM users WHERE User_Name = '" + inputtedUserName + "' AND Password = '" + inputtedPassword + "' ";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ControllerHelper.loginActivity(inputtedUserName, true);
                System.out.println("User exists");
                stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-menu.fxml")));
                stage.setScene(new Scene(scene));
                stage.show();
            } else {
                ControllerHelper.loginActivity(inputtedUserName, false);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                if (currentLocale.getLanguage().equals("fr")) {
                    alert.setContentText("Nom d'utilisateur et/ou mot de passe incorrect");
                } else {
                    alert.setContentText("Incorrect username and/or password");
                }
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ControllerHelper.loginActivity(inputtedUserName, false);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("User does not exist");
            alert.showAndWait();
        } finally {
            JDBC.closeConnection();
        }
    }

    /**
     * Initializes the login controller and sets the language from the locale.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentLocale = Locale.getDefault();

        if (currentLocale.getLanguage().equals("fr")) {
            rb = ResourceBundle.getBundle("com.example.software2.Nat", currentLocale);
            loginLabel.setText(rb.getString("login"));
            loginButton.setText(rb.getString("login"));
            userNameText.setPromptText(rb.getString("username"));
            passwordText.setPromptText(rb.getString("password"));
        }
        currentLocaleLabel.setText(String.valueOf(ZoneId.systemDefault()));
    }
}