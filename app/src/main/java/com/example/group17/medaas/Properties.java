package com.example.group17.medaas;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Properties {
    public static final String ip = "35.190.86.40";
    public static final String port = "";
    public static String credFile = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"/SaveMe/credentials.txt";
    public static String credDir = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"/SaveMe";

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
}
