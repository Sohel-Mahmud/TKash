package com.devlearn.sohel.tkash.Models;

public class WithdrawListDetails {

    private String numberProvider;
    private String withdrawNumber;
    private String withdrawStatus;
    private Double withdrawAmount;

    public WithdrawListDetails(){

    }

    public WithdrawListDetails(String numberProvider, String withdrawNumber, String withdrawStatus, Double withdrawAmount) {
        this.numberProvider = numberProvider;
        this.withdrawNumber = withdrawNumber;
        this.withdrawStatus = withdrawStatus;
        this.withdrawAmount = withdrawAmount;
    }
    public String getWithdrawStatus() {
        return withdrawStatus;
    }

    public void setWithdrawStatus(String withdrawStatus) {
        this.withdrawStatus = withdrawStatus;
    }

    public String getNumberProvider() {
        return numberProvider;
    }

    public void setNumberProvider(String numberProvider) {
        this.numberProvider = numberProvider;
    }


    public String getWithdrawNumber() {
        return withdrawNumber;
    }

    public void setWithdrawNumber(String withdrawNumber) {
        this.withdrawNumber = withdrawNumber;
    }

    public Double getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(Double withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }
}
