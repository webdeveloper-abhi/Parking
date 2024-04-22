package com.example.parking.RecycleciewStructures;

public class ParkingHistoryStructure {
    String parkingstationname;
    String date;
    String amount;


    public ParkingHistoryStructure() {

    }

    public ParkingHistoryStructure(String parkingstationname, String date, String amount) {
        this.parkingstationname = parkingstationname;
        this.date = date;
        this.amount = amount;
    }

    public String getParkingstationname() {
        return parkingstationname;
    }

    public void setParkingstationname(String parkingstation) {
        this.parkingstationname = parkingstation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
