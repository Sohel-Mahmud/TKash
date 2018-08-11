package com.devlearn.sohel.tkash.Models;

public class UpdateLink {

    private String url;
    private double version;


    public UpdateLink(){

    }
    public UpdateLink(String url, double version) {
        this.url = url;
        this.version = version;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

}
