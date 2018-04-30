package com.example.group17.medaas.API.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samarth on 4/29/2018.
 */

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String address;
    private String phoneNumber;
    private String emergencyNumber;
    private String userType;
    private String location;
    private String email;
    private String password;

    public User() {
    }

    public User(JSONObject json) throws JSONException {
        try {
            id = json.getLong("id");
        } catch(NullPointerException e) {
            id = 0L;
        }
        try {
            firstName = json.getString("firstName");
        } catch(NullPointerException e) {
            firstName = "";
        }
        try {
            lastName = json.getString("lastName");
        } catch(NullPointerException e) {
            lastName = "";
        }
        try {
            age = json.getInt("age");
        } catch(NullPointerException e) {
            age = 0;
        }
        try {
            address = json.getString("address");
        } catch(NullPointerException e) {
            address = "";
        }
        try {
            phoneNumber = json.getString("phoneNumber");
        } catch(NullPointerException e) {
            phoneNumber = "";
        }
        try {
            emergencyNumber = json.getString("emergencyNumber");
        } catch(NullPointerException e) {
            emergencyNumber = "";
        }
        try {
            userType = json.getString("userType");
        } catch(NullPointerException e) {
            userType = "";
        }
        try {
            location = json.getString("location");
        } catch(NullPointerException e) {
            location = "";
        }
        try {
            email = json.getString("email");
        } catch(NullPointerException e) {
            email = "";
        }
        try {
            password = json.getString("password");
        } catch(NullPointerException e) {
            password = "";
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public void setEmergencyNumber(String emergencyNumber) {
        this.emergencyNumber = emergencyNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
