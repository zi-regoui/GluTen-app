package com.example.tengluapp;

public class MyDataModel {
    private String date;
    private String hour;
    private String SYS;
    private String DIA;

    public MyDataModel(String date, String hour, String SYS, String DIA) {
        this.date = date;
        this.hour = hour;
        this.SYS = SYS;
        this.DIA = DIA;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {
        return hour;
    }

    public String getSYS() {
        return SYS;
    }

    public String getDIA() {
        return DIA;
    }
}
