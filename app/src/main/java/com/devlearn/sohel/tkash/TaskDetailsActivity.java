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
import com.devlearn.sohel.tkash.Models.UserDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class TaskDetailsActivity extends AppCompatActivity implements RewardedVideoAdListener{

    private String taskNumber, user_id;

    private DatabaseReference mDatabasetask, mDatabaseUserDetails;
    private FirebaseAuth mAuth;

    private TaskDetails task;
    private TextView txtImp, txtclks;
    private Button btnProceed;
    private Button btnReset;

    private boolean adShowed = false;
    private boolean adClicked = false;
    private boolean adMissclicked = false;
    private boolean adEscaped = false;

    private CountDownTimer countDownTimer;

    private CircularProgressBar progressImp, progressclks;

    private ValueEventListener valueEventListener;

    private int questions, clks;
    private String status = "Running";

    private RewardedVideoAd mRewardedVideoAd;
    private AdView mAdView;
    private AdRequest reqInterstitial;
    private InterstitialAd interstitialAd;

    private double currentBalance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskNumber = getIntent().getExtras().getString("task");

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabasetask = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child(taskNumber);
        mDatabaseUserDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        MobileAds.initialize(this,String.valueOf(R.string.admobTestId));
//        MobileAds.initialize(this,"ca-app-pub-3940256099942544/5224354917");

        mAdView = findViewById(R.id.adViewInTask);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("2C750EBF11C8D60CC8D31D18C832AFEB").build();
        mAdView.loadAd(adRequest);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        
        loadRewardedVideo();

        progressImp = (CircularProgressBar)findViewById(R.id.progressBarImp);
        progressclks = (CircularProgressBar)findViewById(R.id.progressBarClks);
        btnProceed = (Button)findViewById(R.id.btn_proceed);
        txtImp = (TextView)findViewById(R.id.txtImp);
        txtclks = (TextView)findViewById(R.id.txtClks);
        btnReset = findViewById(R.id.btnReset);
//        btnReset.setEnabled(true);

        try{
            valueEventListener = mDatabasetask.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    task = dataSnapshot.getValue(TaskDetails.class);

                    questions = task.getimp();
                    clks = task.getclks();
                    long timestamp = task.getTimestamp();

                    Log.d("timestamp","t:"+timestamp);

                    long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(90, TimeUnit.MINUTES);

                    Log.d("cutoff","t:"+cutoff);


                    progressImp.setProgress((float)questions);
                    txtImp.setText(String.valueOf(questions)+"/24");

                    progressclks.setProgress((float)clks);
                    txtclks.setText(String.valueOf(clks)+"/1");

                    if(questions == 24 && clks == 1)
                    {
                        if(timestamp>cutoff)
                        {
                            long leftTime = timestamp-cutoff;
                            int timeleft = (int) leftTime;

                            Log.d("timeleft", "onDataChange: "+timeleft);

                            countDownTimer = new CountDownTimer(timeleft,1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    String text = String.format(Locale.getDefault(), "Time Remaining %02d hour: %02d min: %02d sec",
                                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished)%60,TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
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
                    }
                    else
                    {
                        btnReset.setEnabled(true);
                        btnReset.setText("Reset");
                    }

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

        loadInterstitial();



        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questions == 24 && clks == 1)
                {
                    Toast.makeText(TaskDetailsActivity.this, "Your task is over, go to next task or You can reset task", Toast.LENGTH_LONG).show();
                }
                else {

                    if(interstitialAd.isLoaded())
                    {
                        interstitialAd.show();
                        adShowed = true;
                        if(adShowed && questions<24 && !adMissclicked)
                        {
                            int finalImpression = questions+1;
                            mDatabasetask.child("imp").setValue(finalImpression);
//                    Intent intent = new Intent(TaskProcess.this, TaskDetailsActivity.class);
//                    intent.putExtra("task",taskNumber);
//                    startActivity(intent);
                            Toast.makeText(TaskDetailsActivity.this, "counted!!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(TaskDetailsActivity.this,TaskProcess.class));
//                    finish();

                        }
                        else if(adShowed && adMissclicked)
                        {
                            Toast.makeText(TaskDetailsActivity.this, "You've Clicked before 20 impressions, this will not be counted and you are banned", Toast.LENGTH_LONG).show();
                            Log.d("adSavemissclicked","missclickedIntent"+adMissclicked);
                        }
                        else if(adShowed && questions==24 && adClicked)
                        {
                            int finalClicks = clks+1;

                            try{
                                double addBalance = currentBalance+0.75;
                                mDatabasetask.child("clks").setValue(finalClicks);
                                mDatabasetask.child("timestamp").setValue(ServerValue.TIMESTAMP);
                                mDatabaseUserDetails.child("currentBalance").setValue(addBalance);

                            }catch (Exception e)
                            {
                                Toast.makeText(TaskDetailsActivity.this, "Error adding Balance"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("Error adding Balance","errrr!"+e.getMessage());

                            }
//                    Intent intent = new Intent(TaskProcess.this, TaskDetailsActivity.class);
//                    intent.putExtra("task",taskNumber);
                            Log.d("adperfectclicked","clicked"+adClicked);
//                    startActivity(intent);
                            Toast.makeText(TaskDetailsActivity.this, "Click Counted", Toast.LENGTH_LONG).show();
//                    finish();
                        }
                        else
                        {
//                    Intent intent = new Intent(TaskProcess.this, TaskDetailsActivity.class);
//                    intent.putExtra("task",taskNumber);
//                    startActivity(intent);
                            Toast.makeText(TaskDetailsActivity.this, "Impression not Added :D", Toast.LENGTH_LONG).show();
//                    finish();
                        }

                    }
                    else
                    {
                        startActivity(new Intent(TaskDetailsActivity.this, TaskProcess.class));
                    }
//                    finish();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mRewardedVideoAd.isLoaded())
                {
                    mRewardedVideoAd.show();
                }

            }
        });

    }

    private void loadRewardedVideo() {
        if(!mRewardedVideoAd.isLoaded())
        {
            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                    new AdRequest.Builder().build());
        }

    }

    private void resetTasks() {
        //                Map<String, Object> newValues = new HashMap<>();
//
//                newValues.put("clks",0);
//                newValues.put("imp",0);
//                newValues.put("status","Running");
//                newValues.put("timestamp",ServerValue.TIMESTAMP);
//
//                mDatabasetask.setValue(newValues);
    }

    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(String.valueOf(R.string.InterstitialTest));
        reqInterstitial = new AdRequest.Builder().addTestDevice("2C750EBF11C8D60CC8D31D18C832AFEB").build();

        interstitialAd.setAdListener(new AdListener(){

            @Override
            public void onAdClosed() {
                startActivity(new Intent(TaskDetailsActivity.this, TaskProcess.class));
                interstitialAd.loadAd(new AdRequest.Builder().addTestDevice("2C750EBF11C8D60CC8D31D18C832AFEB").build());

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                if(questions == 24)
                {
                    adClicked = true;
                    Log.d("adopenadclicked","clicked"+adClicked);

                }
                else
                {
                    adMissclicked = true;
                    Log.d("adopenadmissclicked","missclicked"+adMissclicked);
                }
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdImpression() {
            }
        });
        interstitialAd.loadAd(reqInterstitial);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
//            String user_id = user.getUid();
            try{
                mDatabaseUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                        currentBalance = userDetails.getCurrentBalance();
                        Log.d("found!","balance"+currentBalance);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }catch (Exception e)
            {
                Toast.makeText(this, "Error getting userDetails"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Problem with Login", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }
        mDatabasetask.removeEventListener(valueEventListener);
        mRewardedVideoAd.destroy(this);
    }
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        mRewardedVideoAd.resume(this);
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideo();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        //resetTasks();
        Toast.makeText(this, "Your reward!!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
