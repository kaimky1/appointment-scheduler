package com.example.software2.model;

public class Customer {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;

    private String provinceState;


    /**
     *
     * @return province state
     */
    public String getProvinceState() {
        return provinceState;
    }

    /**
     *
     * @return customer id
     */

    public int getId() {
        return id;
    }

    /**
     *
     * @param id id that gets set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return customer name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name name that gets set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return customer address
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address address that gets set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * set the postal code
     * @param postalCode customer postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     *
     * @return customer phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     *
     * @param phoneNumber setting the phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     *
     * @param provinceState set the province state for the customer
     */
    public void setProvinceState(String provinceState) {
        this.provinceState = provinceState;
    }

    /**
     *
     * @param id customer ID
     * @param name customer name
     * @param address customer address
     * @param postalCode customer postal code
     * @param phoneNumber customer phone number
     * @param provinceState customer province state
     */
    public Customer(int id, String name, String address, String postalCode, String phoneNumber, String provinceState) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.provinceState = provinceState;
    }
}
