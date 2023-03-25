package com.example.carpoolbuddy;

public class Car extends Vehicle{

    private int range;

    public Car(String owner, int range) {
        super(owner);
        this.range = range;
    }

    public Car(int range) {
        this.range = range;
    }

    public Car() {
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "Car{" +
                "range=" + range +
                '}';
    }
}
