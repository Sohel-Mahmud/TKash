package com.devlearn.sohel.tkash;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.devlearn.sohel.tkash.Models.TaskDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TaskDetailsActivity extends AppCompatActivity {

    private String taskNumber, user_id;

    private DatabaseReference mDatabasetask;
    private FirebaseAuth mAuth;

    private TaskDetails task;
    private TextView txtImpression, txtClicks;
    private Button btnProceed;
    private Button btnReset;

    private CountDownTimer countDownTimer;

    private CircularProgressBar progressImpression, progressClicks;

    private int impressions, clicks;
    private String status = "Running";
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskNumber = getIntent().getExtras().getString("task");

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabasetask = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child(taskNumber);

        MobileAds.initialize(this,String.valueOf(R.string.admobAppId));
        progressImpression = (CircularProgressBar)findViewById(R.id.progressBarImpression);
        progressClicks = (CircularProgressBar)findViewById(R.id.progressBarClicks);
        btnProceed = (Button)findViewById(R.id.btn_proceed);
        txtImpression = (TextView)findViewById(R.id.txtImpression);
        txtClicks = (TextView)findViewById(R.id.txtClicks);
        btnReset = findViewById(R.id.btnReset);
        btnReset.setEnabled(false);

        mAdView = findViewById(R.id.adViewInTask);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        try{
            mDatabasetask.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    task = dataSnapshot.getValue(TaskDetails.class);

                    impressions = task.getImpressions();
                    clicks = task.getClicks();
                    long timestamp = task.getTimestamp();

                    Log.d("timestamp","t:"+timestamp);

                    long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES);

                    Log.d("cutoff","t:"+cutoff);


                    progressImpression.setProgress((float)impressions);
                    txtImpression.setText(String.valueOf(impressions)+"/20");

                    progressClicks.setProgress((float)clicks);
                    txtClicks.setText(String.valueOf(clicks)+"/1");

                    if(timestamp>cutoff)
                    {
                        long leftTime = timestamp-cutoff;
                        int timeleft = (int) leftTime;

                        Log.d("timeleft", "onDataChange: "+timeleft);

                        countDownTimer = new CountDownTimer(timeleft,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                                String text = String.format(Locale.getDefault(), "Time Remaining %02d min: %02d sec",
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                                btnReset.setText(text);
                                btnReset.setEnabled(false);
                    }

                    @Override
                    public void onFinish() {
                        btnReset.setEnabled(true);
                        btnReset.setText("Reset");

                    }
                }.start();
                    }
                    else
                    {
                        Toast.makeText(TaskDetailsActivity.this, "Time to reset", Toast.LENGTH_SHORT).show();
                        btnReset.setEnabled(true);
                        btnReset.setText("Reset");
                    }
//                    app:foregroundStrokeColor="#0488d1"


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e)
        {
            String error = e.getMessage();
            Toast.makeText(this, "error "+error, Toast.LENGTH_LONG).show();
        }

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(impressions == 24 && clicks == 1)
                {
                    Toast.makeText(TaskDetailsActivity.this, "Your task is over, go to next task or You can reset task", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(TaskDetailsActivity.this, TaskProcess.class);
                    intent.putExtra("task",taskNumber);
                    intent.putExtra("impressions",impressions);
                    intent.putExtra("clicks",clicks);
                    startActivity(intent);
//                    finish();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> newValues = new HashMap<>();

                newValues.put("clicks",0);
                newValues.put("impressions",0);
                newValues.put("status","Running");
                newValues.put("timestamp",ServerValue.TIMESTAMP);

                mDatabasetask.setValue(newValues);

//                startActivity(new Intent(TaskDetailsActivity.this,TaskActivity.class));
//                finish();

            }
        });

    }
}
