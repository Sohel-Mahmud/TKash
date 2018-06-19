package com.devlearn.sohel.tkash.Models;

public class TaskDetails {
    int impressions, clicks;

    public TaskDetails(){

    }

    public TaskDetails(int impressions, int clicks) {
        this.impressions = impressions;
        this.clicks = clicks;
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
}
