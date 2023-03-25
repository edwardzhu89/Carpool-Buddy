package com.example.carpoolbuddy;

public class Van extends Vehicle{

    private int range;

    public Van(String owner, int range) {
        super(owner);
        this.range = range;
    }

    public Van(int range) {
        this.range = range;
    }

    public Van() {
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return "Van{" +
                "range=" + range +
                '}';
    }
}
