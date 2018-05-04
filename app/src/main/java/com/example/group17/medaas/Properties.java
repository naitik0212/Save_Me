package com.example.group17.medaas;

import android.os.Environment;
import android.util.Log;

import com.example.group17.medaas.API.model.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class Properties {
    public static final String ip = "35.190.86.40";
    public static final String port = "";
    public static String credFile = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"/SaveMe/credentials.txt";
    public static String credDir = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"/SaveMe";
    public static String activeSessionFile = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"/SaveMe/active_session.txt";
    public static User user = null;
    public static ClientDoctorSession clientDoctorSession = null;
    public static final int TOKEN_ID_NULL = -1;
    public static final int ETA_NULL = -1;
    public static int tokenId = TOKEN_ID_NULL;

    public static class RegistrationParameters {
        public String firstName = "";
        public String lastName = "";
        public String age = "";
        public String address = "";
        public String phoneNumber = "";
        public String emergencyNumber = "";
        public String email = "";
        public String password="";
        public String userType="";




        RegistrationParameters(String firstName,String lastName,String age,String address,String phoneNumber,String emergencyNumber,String email,String password,String userType) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.address = address;
            this.phoneNumber = phoneNumber;
            this.emergencyNumber = emergencyNumber;
            this.email =email;
            this.password = password;
            this.userType =userType;

        }

        public File saveParameters() {
            // create parameter string to write
            String fcontent = "First Name: " + firstName + "\nLast Name: "+ lastName + "\nAge: "+ age + "\nAddress: "+ address + "\nContact: "+phoneNumber+"\nEmergency contact=" + emergencyNumber + "\nEmail Id: " + email + "\npasswprd: " + password + "\nProfession: "+ userType;
            Log.i("", "saveParameters: hi");


            // save parameters to a file locally
            File directory = new File(credDir);
            File file = new File(Properties.credFile);
            if (!directory.exists()) {
                Log.i("", "saveParameters: directory created" + directory.exists());

                directory.mkdirs();
                Log.i("", "saveParameters: directory created" + directory.exists());
            }

            // If file does not exists, then create it
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fcontent);
            bw.close();
            Log.d("Success", "Success");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public static void saveToFile(String body, String fileDir, String filePath) {
        // create parameter string to write
        String fcontent = body;
        Log.d("", "saveToFile: " + body);

        // save parameters to a file locally
        File directory = new File(fileDir);
        File file = new File(filePath);
        if (!directory.exists()) {
            Log.i("", "saveParameters: directory created" + directory.exists());

            directory.mkdirs();
            Log.i("", "saveParameters: directory created" + directory.exists());
        }

        // If file does not exist, then create it
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fcontent);
            bw.close();
            Log.d("Success", "Success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void retriveSessionFromFile() {
        if (Properties.clientDoctorSession != null) return;

        File file = new File(Properties.activeSessionFile);

        if (!file.exists()) {
            Properties.clientDoctorSession = null;
            return;
        }

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            Gson gson = new Gson();
            Properties.clientDoctorSession = gson.fromJson(text.toString(), ClientDoctorSession.class);
        } catch (IOException e) {
            Log.d("", "checkCredentials: " + e.getMessage());
            Properties.clientDoctorSession = null;
        }
    }

    public static void retriveUserFromFile() {
        if (Properties.user != null) return;

        File file = new File(Properties.credFile);

        if (!file.exists()) {
            Properties.user = null;
            return;
        }

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            Gson gson = new Gson();
            Properties.user = gson.fromJson(text.toString(), User.class);
        } catch (IOException e) {
            Log.d("", "checkCredentials: " + e.getMessage());
        }
    }
}
