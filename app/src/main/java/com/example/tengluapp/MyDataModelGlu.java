package com.example.tengluapp;

public class MyDataModelGlu {
    private String date;
    private String hour;
    private String TDG;

    public MyDataModelGlu(String date, String hour, String TDG) {
        this.date = date;
        this.hour = hour;
        this.TDG = TDG;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {
        return hour;
    }

    public String getTDG() {
        return TDG;
    }
}
