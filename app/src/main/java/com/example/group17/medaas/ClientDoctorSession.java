package com.example.group17.medaas;

import com.example.group17.medaas.API.model.User;

/**
 * Created by Samarth on 5/1/2018.
 */

public class ClientDoctorSession {
    private User clientUser = null; // client requesting
    private User doctorUser = null; // doctor attending
    private User[] doctorsList = null; // list of doctors on received on client's request
    private int ETA = Properties.ETA_NULL; // estimated arrival time

    private String status = null;   // state transitions reflect here

    public static String STATUS_READY = "ready";
    public static String STATUS_CLIENT_REQUESTED = "client requests save me";
    public static String STATUS_CLIENT_REQUESTED_OUTGOING = "doctor requests save me";
    public static String STATUS_DOCTOR_RESPONDED = "doctor responded (incoming)";
    public static String STATUS_DOCTOR_RESPONDED_OUTGOING = "doctor responded (outgoint)";
    public static String STATUS_CLIENT_CANCELLED = "client cancelled";
    public static String STATUS_DOCTOR_CANCELLED = "doctor cancelled";
    public static String STATUS_COMPLETED = "completed";

    public ClientDoctorSession() {
        this.status = STATUS_READY;
    }

    public String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        this.status = status;
    }

    public User getClientUser() {
        return clientUser;
    }

    public synchronized void setClientUser(User clientUser) {
        this.clientUser = clientUser;
    }

    public User getDoctorUser() {
        return doctorUser;
    }

    public synchronized void setDoctorUser(User doctorUser) {
        this.doctorUser = doctorUser;
    }

    public User[] getDoctorsList() {
        return doctorsList;
    }

    public synchronized void setDoctorsList(User[] doctorsList) {
        this.doctorsList = doctorsList;
    }

    public int getETA() {
        return ETA;
    }

    public synchronized void setETA(int ETA) {
        this.ETA = ETA;
    }
}
