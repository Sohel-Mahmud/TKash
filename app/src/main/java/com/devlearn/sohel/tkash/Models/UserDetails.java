package com.devlearn.sohel.tkash.Models;

public class UserDetails {
    public String userPhone, userName, accStatus, referStatus;
    public Double currentBalance;


    public UserDetails(){

    }

    public UserDetails(String userPhone, String userName, String accStatus, String referStatus, Double currentBalance) {
        this.userPhone = userPhone;
        this.userName = userName;
        this.accStatus = accStatus;
        this.referStatus = referStatus;
        this.currentBalance = currentBalance;
    }
    public UserDetails(Double currentBalance, String referStatus){
        this.currentBalance = currentBalance;
        this.referStatus = referStatus;
    }


}
