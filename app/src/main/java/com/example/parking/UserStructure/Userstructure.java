package com.example.parking.UserStructure;

public class Userstructure {

    String UserId;
    String username, password, email, phone, paymentInfo,ProfileDescription,VehicleNumber;

    // Constructors, getters, and setters...

    @Override
    public String toString() {
        return "User ID: " + UserId + "\n" +
                "Username: " + username + "\n" +
                "Vehicle Number"+VehicleNumber +
                "Email: " + email + "\n" +
                "Phone: " + phone + "\n" +
                "Payment Info: " + paymentInfo +"\n"+
                "Password: "+password +"\n"+
                "Profile Description: "+ProfileDescription;

    }
}
