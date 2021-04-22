package com.example.parkez;

import java.util.Comparator;

public class Carpark implements Comparator<Carpark> {
    private String carparkNo;
    private String address;
    private long xCoord;
    private long yCoord;
    private String carparkType;
    private String parkingSystemType;
    private String shortTermParking;
    private String freeParking;
    private String nightParking;
    private int carparkDecks;
    private double gantryHeight;
    private String carparkBasement;

    public Carpark(){}

    public Carpark(String carparkNo){
        this.carparkNo = carparkNo;
        this.address = "-";
        this.xCoord = (long)0.0;
        this.yCoord = (long)0.0;
        this.carparkType = null;
        this.parkingSystemType = null;
        this.shortTermParking = null;
        this.freeParking = null;
        this.nightParking = "NO";
        this.carparkDecks = 0;
        this.gantryHeight = 0;
        this.carparkBasement = "No";

    }


    public String getCarparkNo() {
        return carparkNo;
    }

    public void setCarparkNo(String carparkNo) {
        this.carparkNo = carparkNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getXCoord() {
        return xCoord;
    }

    public void setXCoord(long xCoord) {
        this.xCoord = xCoord;
    }

    public long getYCoord() {
        return yCoord;
    }

    public void setYCoord(long yCoord) {
        this.yCoord = yCoord;
    }

    public String getCarparkType() {
        return carparkType;
    }

    public void setCarparkType(String carparkType) {
        this.carparkType = carparkType;
    }

    public String getParkingSystemType() {
        return parkingSystemType;
    }

    public void setParkingSystemType(String parkingSystemType) {
        this.parkingSystemType = parkingSystemType;
    }

    public String getShortTermParking() {
        return shortTermParking;
    }

    public void setShortTermParking(String shortTermParking) {
        this.shortTermParking = shortTermParking;
    }

    public String getFreeParking() {
        return freeParking;
    }

    public void setFreeParking(String freeParking) {
        this.freeParking = freeParking;
    }

    public String getNightParking() {
        return nightParking;
    }

    public void setNightParking(String nightParking) {
        this.nightParking = nightParking;
    }

    public int getCarparkDecks() {
        return carparkDecks;
    }

    public void setCarparkDecks(int carparkDecks) {
        this.carparkDecks = carparkDecks;
    }

    public double getGantryHeight() {
        return gantryHeight;
    }

    public void setGantryHeight(double gantryHeight) {
        this.gantryHeight = gantryHeight;
    }

    public String getCarparkBasement() {
        return carparkBasement;
    }

    public void setCarparkBasement(String carparkBasement) {
        this.carparkBasement = carparkBasement;
    }

    //comparing available lots
    public static Comparator<Carpark> lotsComparator = new Comparator<Carpark>() {
        @Override
        public int compare(Carpark o1, Carpark o2) {
            //compare available lots of cp1 and cp2
            return 1;
        }
    };

    //not in use
    @Override
    public int compare(Carpark o1, Carpark o2) {
        return 0;
    }
}
