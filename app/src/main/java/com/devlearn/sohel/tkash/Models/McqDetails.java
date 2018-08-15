package com.devlearn.sohel.tkash.Models;

import java.util.Map;

public class McqDetails {
    public String ques, option1, option2, option3;
    public int ans;

    public McqDetails(){

    }

    public McqDetails(String ques, String option1, String option2, String option3, int ans) {
        this.ques = ques;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.ans = ans;
    }
}
