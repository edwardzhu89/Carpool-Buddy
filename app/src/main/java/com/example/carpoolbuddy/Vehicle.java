package com.example.carpoolbuddy;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

public class Vehicle implements Parcelable {

    private String owner; //
    private String model; //
    private int capacity; //
    private String vehicleID;
    private ArrayList<String> ridersUIDs;
    private boolean open;
    private String vehicleType;
    private double basePrice;
    private ArrayList<String> ridersNames;

    private ArrayList<String> ridersLong;
    private ArrayList<String> ridersLat;

    private String ownerEmail;

    private String link;

    public Vehicle(String owner) {
        this.owner = owner;
    }

    public Vehicle() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    protected Vehicle(Parcel in) {
        owner = in.readString();
        model = in.readString();
        ownerEmail = in.readString();
        capacity = in.readInt();
        vehicleID = in.readString();
        ridersUIDs = in.createStringArrayList();
        open = in.readByte() != 0;
        vehicleType = in.readString();
        basePrice = in.readDouble();
        ridersNames = in.createStringArrayList();
        link = in.readString();

        ridersLong = in.createStringArrayList();
        ridersLat = in.createStringArrayList();
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    @Override
    public String toString() {
        return "Vehicle{" +
                "owner='" + owner + '\'' +
                ", model='" + model + '\'' +
                ", capacity=" + capacity +
                ", vehicleID='" + vehicleID + '\'' +
                ", ridersUIDs=" + ridersUIDs +
                ", open=" + open +
                ", vehicleType='" + vehicleType + '\'' +
                ", basePrice=" + basePrice +
                ", ridersNames=" + ridersNames +
                ", ownerEmail='" + ownerEmail + '\'' +
                '}';
    }

    public ArrayList<String> getRidersLong() {
        return ridersLong;
    }

    public void setRidersLong(ArrayList<String> ridersLong) {
        this.ridersLong = ridersLong;
    }

    public ArrayList<String> getRidersLat() {
        return ridersLat;
    }

    public void setRidersLat(ArrayList<String> ridersLat) {
        this.ridersLat = ridersLat;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public ArrayList<String> getRidersNames() {
        return ridersNames;
    }

    public void setRidersNames(ArrayList<String> ridersNames) {
        this.ridersNames = ridersNames;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public ArrayList<String> getRidersUIDs() {
        return ridersUIDs;
    }

    public void setRidersUIDs(ArrayList<String> ridersUIDs) {
        this.ridersUIDs = ridersUIDs;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(owner);
        parcel.writeString(model);
        parcel.writeString(ownerEmail);
        parcel.writeInt(capacity);
        parcel.writeString(vehicleID);
        parcel.writeStringList(ridersUIDs);
        parcel.writeByte((byte) (open ? 1 : 0));
        parcel.writeString(vehicleType);
        parcel.writeDouble(basePrice);
        parcel.writeStringList(ridersNames);
        parcel.writeString(link);

        parcel.writeStringList(ridersLong);
        parcel.writeStringList(ridersLat);
    }
}
