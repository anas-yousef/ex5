package com.example.ex5;

public class LocationInfo {
    private double accuracy;
    private double longitude;
    private double latitude;

    public LocationInfo(double accuracy, double longitude, double latitude) {
        this.accuracy = accuracy;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
