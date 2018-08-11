package com.devlearn.sohel.tkash.Models;

public class UserDetails {
    private String userPhone, userName;
    private Double currentBalance, totalBalance;


    public UserDetails(){

    }

    public UserDetails(String userPhone, String userName, Double currentBalance, double totalBalance) {
        this.userPhone = userPhone;
        this.userName = userName;
        this.currentBalance = currentBalance;
        this.totalBalance = totalBalance;
    }



    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }
    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
