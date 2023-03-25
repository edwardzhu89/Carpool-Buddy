package com.example.carpoolbuddy;

import java.util.ArrayList;

public class Parent extends User{

    private ArrayList<String> childrenUIDs;

    public Parent(String uid, String name, String email, String userType, double priceMultiplier, ArrayList<String> ownedVehicles) {
        super(uid, name, email, userType, priceMultiplier, ownedVehicles);
    }

    public Parent(String uid, String name, String email, String userType, double priceMultiplier) {
        super(uid, name, email, userType, priceMultiplier);
    }

    public Parent() {

    }

    @Override
    public String toString() {
        return "Parent{" +
                "childrenUIDs=" + childrenUIDs +
                '}';
    }

    public ArrayList<String> getChildrenUIDs() {
        return childrenUIDs;
    }

    public void setChildrenUIDs(ArrayList<String> childrenUIDs) {
        this.childrenUIDs = childrenUIDs;
    }
}
