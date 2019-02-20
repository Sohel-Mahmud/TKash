package com.devlearn.sohel.tkash;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.devlearn.sohel.tkash.Models.TaskDetails;
import com.devlearn.sohel.tkash.Models.UserDetails;
import com.devlearn.sohel.tkash.SharedPref.TaskSharedPref;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class TaskActivity extends AppCompatActivity {

    private DatabaseReference mDatabasetask1, mDatabasetask2, mDatabasetask3, mDatabasetask4, mDatabasetask5, mDatabaseDelete;
    private FirebaseAuth mAuth;
    public String user_id;

    SpotsDialog waitingDialog;
    ScrollView taskactivity;

    public TaskDetails task1, task2, task3, task4, task5;

    private TextView txtResetTask;

    private TaskSharedPref usp;

    private ValueEventListener taskLishtenr1,taskLishtenr2, taskLishtenr3,taskLishtenr4,taskLishtenr5;

    private TextView txtTaskstatus1,txtTaskstatus2,txtTaskstatus3,txtTaskstatus4,txtTaskstatus5;

    CountDownTimer countDownTimer;

    private CardView taskcard1, taskcard2, taskcard3, taskcard4, taskcard5, taskcard6, taskcard7, taskcard8, taskcard9, taskcard10,ipcard,resetTaskCard;
    private AdView mAdView;

    public UserDetails userDetails;
    private DatabaseReference mDatabaseUserDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mDatabaseUserDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mDatabasetask1 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task1");
        mDatabasetask2 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task2");
//        mDatabasetask4 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task4");
//        mDatabasetask5 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task5");
        taskcard1 = findViewById(R.id.taskcard1);
        taskcard2 = findViewById(R.id.taskcard2);
        taskcard1.setEnabled(false);
        taskcard1.setBackgroundColor(Color.parseColor("#b00020"));
        taskcard2.setEnabled(false);
        taskcard2.setBackgroundColor(Color.parseColor("#b00020"));
//        taskcard4.setEnabled(false);
//        taskcard4.setBackgroundColor(Color.parseColor("#b00020"));
//        taskcard5.setEnabled(false);
//        taskcard5.setBackgroundColor(Color.parseColor("#b00020"));

        usp = new TaskSharedPref(TaskActivity.this);


        txtTaskstatus1 = findViewById(R.id.txttaskstatus1);
        txtTaskstatus2 = findViewById(R.id.txttaskstatus2);
//        txtTaskstatus4 = findViewById(R.id.txttaskstatus4);
//        txtTaskstatus5 = findViewById(R.id.txttaskstatus5);


        MobileAds.initialize(this, getString(R.string.admobTestId));
        mAdView = findViewById(R.id.adView);

        bannerAdRequest();

        taskcard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(task1!=null)
                {
                    Intent intent = new Intent(TaskActivity.this, TaskDetailsActivity.class);
                    intent.putExtra("task","task1");
                    startActivity(intent);
                }



            }
        });

        taskcard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(task2!=null)
                {
                    Intent intent = new Intent(TaskActivity.this, TaskDetailsActivity.class);
                    intent.putExtra("task","task2");
                    startActivity(intent);
                }

            }
        });

    }
    private void bannerAdRequest() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                try{
                    mDatabaseUserDetails.child("accStatus").setValue("Banned");
                    Toast.makeText(TaskActivity.this, "You've done something terrible!!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TaskActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }catch (Exception e)
                {
                    Toast.makeText(TaskActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });

    }
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        taskLishtenr1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                task1 = dataSnapshot.getValue(TaskDetails.class);
//                    taskone.setText(String.valueOf(task1.getImpressions()));
//                    int imp = Integer.valueOf(task1.getImpressions());
//                    Log.d("impression","i="+imp);

//                int impressions = task1.getimp();
//                int clicks = task1.getclks();
//                long timestamp = task1.getTimestamp();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Log.d("taskone", "error " + error);

            }


        };
        mDatabasetask1.addListenerForSingleValueEvent(taskLishtenr1);

        taskLishtenr2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                task2 = dataSnapshot.getValue(TaskDetails.class);
//                    taskone.setText(String.valueOf(task1.getImpressions()));
//                    int imp = Integer.valueOf(task1.getImpressions());
//                    Log.d("impression","i="+imp);
//                int impressions = task2.getimp();
//                int clicks = task2.getclks();
//                long timestamp = task2.getTimestamp();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Log.d("tasktwo", "error " + error);

            }


        };
        mDatabasetask2.addListenerForSingleValueEvent(taskLishtenr2);

        try{
            mDatabaseUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userDetails = dataSnapshot.getValue(UserDetails.class);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    String error = databaseError.getMessage();
                    Toast.makeText(TaskActivity.this, "userdetailserror "+error, Toast.LENGTH_SHORT).show();
                    Log.d("username error","error: "+error);
                }
            });
        }catch (Exception e)
        {
            Log.d("username error","error: "+e.getMessage());

        }

        Toast.makeText(TaskActivity.this, "taskNumber "+usp.getTaskNumber(), Toast.LENGTH_SHORT).show();

                    if(usp.getTaskNumber() == 1)
                    {
                        taskcard1.setEnabled(true);
                        taskcard1.setBackgroundColor(Color.parseColor("#388E3C"));
                        taskcard2.setEnabled(false);
                        taskcard2.setBackgroundColor(Color.parseColor("#b00020"));

                    }
                    else if(usp.getTaskNumber() == 2)
                    {

                        taskcard1.setEnabled(false);
                        taskcard1.setBackgroundColor(Color.parseColor("#b00020"));
                        taskcard2.setEnabled(true);
                        taskcard2.setBackgroundColor(Color.parseColor("#388E3C"));

                    }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(taskLishtenr1!=null && taskLishtenr2!=null && mAdView!=null)
        {
            mAdView.destroy();
            mDatabasetask1.removeEventListener(taskLishtenr1);
            mDatabasetask2.removeEventListener(taskLishtenr2);
        }

    }


}
