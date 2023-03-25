package com.example.carpoolbuddy;

import java.util.ArrayList;

public class Alumni extends User {

    private String graduateYear;

    public Alumni(String uid, String name, String email, String userType, double priceMultiplier, ArrayList<String> ownedVehicles, String graduateYear) {
        super(uid, name, email, userType, priceMultiplier, ownedVehicles);
        this.graduateYear = graduateYear;
    }

    public Alumni(String uid, String name, String email, String userType, double priceMultiplier, String graduateYear) {
        super(uid, name, email, userType, priceMultiplier);
        this.graduateYear = graduateYear;
    }

    public Alumni(String graduateYear) {
        this.graduateYear = graduateYear;
    }

    public Alumni() {
    }

    @Override
    public String toString() {
        return "Alumni{" +
                "graduateYear='" + graduateYear + '\'' +
                '}';
    }

    public String getGraduateYear() {
        return graduateYear;
    }

    public void setGraduateYear(String graduateYear) {
        this.graduateYear = graduateYear;
    }
}
