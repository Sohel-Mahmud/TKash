package com.devlearn.sohel.tkash.Models;

public class TaskDetails {
    int impressions, clicks;

    long timestamp;

    String status;


    public TaskDetails(){

    }

    public TaskDetails(int impressions, int clicks, long timestamp, String status) {
        this.impressions = impressions;
        this.clicks = clicks;
        this.timestamp = timestamp;
        this.status = status;
    }

    public int getImpressions() {
        return impressions;
    }

    public void setImpressions(int impressions) {
        this.impressions = impressions;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
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
