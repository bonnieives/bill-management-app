package com.example.bill_management_app;

public class DateModel {

    private int day;
    private int month;
    private int year;

    public DateModel() {
        this.day = 1;
        this.month = 1;
        this.year = 1000;
    }

    public DateModel(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
