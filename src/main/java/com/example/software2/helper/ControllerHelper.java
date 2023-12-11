package com.example.software2.helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControllerHelper {

    /**
     * A formatted date time to UTC.
     * @param datePicker date picker
     * @param timeComboBox time combo box
     * @return formatted date time
     */
    public static String formatDateTime(DatePicker datePicker, ComboBox<String> timeComboBox) {
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timeComboBox.getValue();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(selectedTime, timeFormatter);
        LocalDateTime dateTime = LocalDateTime.of(selectedDate, time);
        ZoneId localZoneId = ZoneId.systemDefault();
        ZonedDateTime localZDT = ZonedDateTime.of(dateTime, localZoneId);
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZonedDateTime utcZDT = ZonedDateTime.ofInstant(localZDT.toInstant(), utcZoneId);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = utcZDT.format(dateTimeFormatter);
        return formattedDateTime;
    }

    /**
     * A function that converts to local time.
     * @param formattedDateTime formattedDateTime paramater
     * @return converts the formattedDateTime to local time.
     */

    public static String convertToLocalTime(String formattedDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime utcDateTime = LocalDateTime.parse(formattedDateTime, dateTimeFormatter);
        ZonedDateTime utcZDT = utcDateTime.atZone(ZoneId.of("UTC"));
        ZoneId localZoneId = ZoneId.systemDefault();
        ZonedDateTime localZDT = utcZDT.withZoneSameInstant(localZoneId);
        String localFormattedDateTime = localZDT.format(dateTimeFormatter);
        return localFormattedDateTime;
    }

    /**
     * Function that finds the contact ID.
     * @param name name of contact person
     * @return returns the contact ID.
     */
    public static Integer findContactID(String name) {
        Integer contactID = null;
        try {
            JDBC.makeConnection();
            String sql = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";

            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                contactID = resultSet.getInt("Contact_ID");
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
        return contactID;
    }

    /**
     * Gets all the contact names.
     * @return contact names.
     */
    public static ObservableList<String> getContactNames() {
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
     * Gets all the user Ids.
     * @return user IDs.
     */
    public static ObservableList<String> getUserIDs() {
        List<String> userIDs;
        try {
            JDBC.makeConnection();
            String sql = "SELECT User_ID FROM users order by user_ID asc;";

            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            userIDs = new ArrayList<>();
            while (resultSet.next()) {
                String userId = resultSet.getString("User_ID");
                userIDs.add(userId);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
        return FXCollections.observableArrayList(userIDs);
    }

    /**
     * gets all the customer IDs.
     * @return customer IDs
     */
    public static ObservableList<String> getCustomerIDs() {
        List<String> customerIDs;
        try {
            JDBC.makeConnection();
            String sql = "SELECT Customer_ID FROM customers order by Customer_ID asc";

            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            customerIDs = new ArrayList<>();
            while (resultSet.next()) {
                String customerID = resultSet.getString("Customer_ID");
                customerIDs.add(customerID);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
        return FXCollections.observableArrayList(customerIDs);
    }

    /**
     * Returns the time.
     * @return the time
     */
    public static ObservableList<String> getTimes() {
        ObservableList<String> times = FXCollections.observableArrayList();
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                String time = String.format("%02d:%02d", hour, minute);
                times.add(time);
            }
        }
        return times;
    }

    /**
     * Returns whether the business hour is true or false
     * @param startTime the start time of the appt
     * @param endTime the end time of the appt
     * @return boolean
     */

    public static boolean businessHours(LocalTime startTime, LocalTime endTime) {
        LocalTime businessStart = LocalTime.of(8, 0);
        LocalTime businessEnd = LocalTime.of(22, 0);
        ZoneId localZoneId = ZoneId.systemDefault();

        ZonedDateTime startDateTime = ZonedDateTime.of(LocalDate.now(), startTime, localZoneId);
        ZonedDateTime endDateTime = ZonedDateTime.of(LocalDate.now(), endTime, localZoneId);

        ZonedDateTime etStartDateTime = startDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime etEndDateTime = endDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));

        return !etStartDateTime.toLocalTime().isBefore(businessStart) && !etEndDateTime.toLocalTime().isAfter(businessEnd);
    }

    /**
     * Returns true or false on whether there are overlapping appts.
     * @param customerID customer ID
     * @param start start time/date
     * @param end end time/date
     * @return boolean on whether there are overlapping appts
     */
    public static boolean overlappingAppointments(String customerID, String start, String end) throws SQLException {
        try {
            JDBC.makeConnection();
            String sql = "SELECT * FROM appointments WHERE Customer_ID = ? AND NOT (End <= ? OR Start >= ?)";
            PreparedStatement preparedStatement = JDBC.connection.prepareStatement(sql);
            preparedStatement.setString(1, customerID);
            preparedStatement.setString(2, start);
            preparedStatement.setString(3, end);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBC.closeConnection();
        }
    }

    /**
     * Will log whether the login is success or failed.
     * @param username username
     * @param success boolean
     */
    public static void loginActivity(String username, boolean success) {
        Path logFilePath = Path.of("login_activity.txt");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        String status = success ? "Success" : "Failure";
        String logEntry = timestamp + " - Username: " + username + ", Status: " + status + "\n";

        try {
            Files.write(logFilePath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your application's needs
        }
    }
}
