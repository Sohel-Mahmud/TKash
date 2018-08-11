package com.devlearn.sohel.tkash.Models;

public class TaskDetails {
    private int imp, clks;

    private long timestamp;

    private String status;


    public TaskDetails(){

    }

    public TaskDetails(int imp, int clks, long timestamp, String status) {
        this.imp = imp;
        this.clks = clks;
        this.timestamp = timestamp;
        this.status = status;
    }

    public int getimp() {
        return imp;
    }

    public void setimp(int imp) {
        this.imp = imp;
    }

    public int getclks() {
        return clks;
    }

    public void setclks(int clks) {
        this.clks = clks;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
