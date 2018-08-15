package com.devlearn.sohel.tkash.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;

public class TaskSharedPref {
    private int taskNumber;
    private Context context;
    private SharedPreferences sp;

    public TaskSharedPref(Context context){
        this.context=context;
        sp=context.getSharedPreferences("completePref",Context.MODE_PRIVATE);
    }

    public void setTaskNumber(int taskNumber){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("taskNumber",taskNumber);
        editor.apply();
    }

    public int getTaskNumber()
    {
        return sp.getInt("taskNumber",1);
    }

    public void clearAll(){
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}
