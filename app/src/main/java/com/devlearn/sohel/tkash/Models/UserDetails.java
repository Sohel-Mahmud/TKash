package com.devlearn.sohel.tkash.Models;

public class UserDetails {
    public String userPhone, userName, accStatus;
    public Double currentBalance;


    public UserDetails(){

    }

    public UserDetails(String userPhone, String userName, String accStatus, Double currentBalance) {
        this.userPhone = userPhone;
        this.userName = userName;
        this.accStatus = accStatus;
        this.currentBalance = currentBalance;
    }


}
