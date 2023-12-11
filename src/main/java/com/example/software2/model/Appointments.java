package com.example.software2.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Appointments {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private int customerID;
    private int userID;
    private static Map<String, Map<String, Integer>> appointmentsByTypeAndMonth = new HashMap<>();

    /**
     *
     * @param appointmentID id of appt
     * @param title title of appt
     * @param description description of appt
     * @param location location of appt
     * @param contact contact of appt
     * @param type type of appt
     * @param start start date/time of appt
     * @param end end date/time of appt
     * @param customerID customerID of appt
     * @param userID userID of appt
     */
    public Appointments(int appointmentID, String title, String description, String location, String contact, String type, LocalDateTime start, LocalDateTime end, int customerID, int userID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        String monthKey = String.valueOf(start.getMonthValue());
        appointmentsByTypeAndMonth
                .computeIfAbsent(type, k -> new HashMap<>())
                .put(monthKey, appointmentsByTypeAndMonth
                        .computeIfAbsent(type, k -> new HashMap<>())
                        .getOrDefault(monthKey, 0) + 1);
    }

    /**
     *
     * @param type appointment type
     * @param month appointment month
     * @return all appointments based on type and month
     */
    public static int getAppointmentsCountForTypeAndMonth(String type, String month) {
        int count = 0;
        for (Appointments appointment : allAppointments) {
            String appointmentType = appointment.getType();
            int appointmentMonth = appointment.getStart().getMonthValue();

            if (type.equals(appointmentType) && Integer.parseInt(month) == appointmentMonth) {
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @return appt ID
     */
    public int getAppointmentID() {
        return appointmentID;
    }

    /**
     *  set the appt ID
     * @param appointmentID appt ID
     */
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    /**
     * get title
     * @return appt title
     */
    public String getTitle() {
        return title;
    }

    /**
     * set title
     * @param title appt title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * get the appt description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * set the appt description
     * @param description appt description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get the location
     * @return location
     */

    public String getLocation() {
        return location;
    }

    /**
     *  set location
     * @param location appt location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * get the contact
     * @return appt contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * set the contact
     * @param contact appt contact
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * get the type
     * @return appt type
     */
    public String getType() {
        return type;
    }

    /**
     * set the type
     * @param type appt type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * get the appt start date/time
     * @return appt start
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * set the appt start
     * @param start appt start date/time
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * grabbing the appt end date/time
     * @return appt end
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * set the end
     * @param end end appt
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     *
     * @return customer ID
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * sets the customer ID
     * @param customerID customer ID
     */

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * get the user ID
     * @return userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     *  set the user ID
     * @param userID user ID
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * List of customers.
     */
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    /**
     * add the customer
     * @param customer Type customer
     */
    public static void addCustomer(Customer customer) {
        allCustomers.add(customer);
    }

    /**
     * getting all customers
     * @return customers
     */
    public static ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }

    /**
     * List of all appointments.
     */
    private static ObservableList<Appointments> allAppointments = FXCollections.observableArrayList();

    /**
     * Clears all appointments.
     */
    public static void clearAllAppointments() {
        allAppointments.clear();
    }

    /**
     * Clears all customers.
     */
    public static void clearAllCustomers() {
        allCustomers.clear();
    }

    /**
     * add an appointment
     * @param appointment Type appointment
     */
    public static void addAppointment(Appointments appointment) {
        allAppointments.add(appointment);
    }

    /**
     *
     * @return all appointments
     */
    public static ObservableList<Appointments> getAllAppointments() {
        return allAppointments;
    }
}
