package com.devlearn.sohel.tkash.Models;

public class TaskDetails {
    private int imp, clks;

    private long timestamp;

    private int limitImp;


    public TaskDetails(){

    }

    public TaskDetails(int imp, int clks, long timestamp, int limitImp) {
        this.imp = imp;
        this.clks = clks;
        this.timestamp = timestamp;
        this.limitImp = limitImp;
    }

    public int getImp() {
        return imp;
    }

    public void setImp(int imp) {
        this.imp = imp;
    }

    public int getClks() {
        return clks;
    }

    public void setClks(int clks) {
        this.clks = clks;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLimitImp() {
        return limitImp;
    }

    public void setLimitImp(int limitImp) {
        this.limitImp = limitImp;
    }
}
