package com.devlearn.sohel.tkash;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.devlearn.sohel.tkash.Models.UserDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class TaskProcess extends AppCompatActivity {

    RingProgressBar ringProgress_bar;

    int progress = 0;

    private InterstitialAd mInterstitialAd;

    private String taskNumber;
    private int impressions, clicks;

    private boolean adShowed = false;
    private boolean adClicked = false;
    private boolean adMissclicked = false;
    private boolean adEscaped = false;

    private DatabaseReference mDatabasetask,mDatabaseUserDetails;
    private FirebaseAuth mAuth;

    private String user_id;

    private double currentBalance;

    Handler myHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0)
            {
                if(progress<100)
                {
                    progress++;
                    ringProgress_bar.setProgress(progress);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_process);

        ringProgress_bar = (RingProgressBar)findViewById(R.id.progress_bar);

        taskNumber = getIntent().getExtras().getString("task");
        impressions = getIntent().getIntExtra("impressions",0);
        clicks = getIntent().getIntExtra("clicks",1);

        Log.d("Values","m"+taskNumber+impressions+clicks);


        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        mDatabasetask = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child(taskNumber);
        mDatabaseUserDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);


        MobileAds.initialize(this, String.valueOf(R.string.admobAppId));

        mInterstitialAd = new InterstitialAd(TaskProcess.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
                                          public void onAdLoaded() {
                                              // Code to be executed when an ad finishes loading.
                                              if (mInterstitialAd.isLoaded()) {
                                                  mInterstitialAd.show();

                                                  adShowed = true;
                                              }
                                          }


            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                if(impressions == 20)
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
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                //its distrubing
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());


            }
        });
        ringProgress_bar.setOnProgressListener(new RingProgressBar.OnProgressListener() {
            @Override
            public void progressToComplete() {

                if(adShowed && impressions<20 && !adMissclicked)
                {
                    int finalImpression = impressions+1;
                    mDatabasetask.child("impressions").setValue(finalImpression);
//                    Intent intent = new Intent(TaskProcess.this, TaskDetailsActivity.class);
//                    intent.putExtra("task",taskNumber);
//                    startActivity(intent);
                    Toast.makeText(TaskProcess.this, "counted!!!", Toast.LENGTH_SHORT).show();
//                    finish();

                }
                else if(adShowed && adMissclicked)
                {
                    Toast.makeText(TaskProcess.this, "You've Clicked before 20 impressions, this will not be counted and you are banned", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(TaskProcess.this, TaskDetailsActivity.class);
//                    intent.putExtra("task",taskNumber);
                    Log.d("adSavemissclicked","missclickedIntent"+adMissclicked);
//                    startActivity(intent);
//                    finish();
                }
                else if(adShowed && impressions==20 && adClicked)
                {
                    int finalClicks = clicks+1;

                    try{
                        double addBalance = currentBalance+0.75;
                        mDatabasetask.child("clicks").setValue(finalClicks);
                        mDatabaseUserDetails.child("currentBalance").setValue(addBalance);

                    }catch (Exception e)
                    {
                        Toast.makeText(TaskProcess.this, "Error adding Balance"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Error adding Balance","errrr!"+e.getMessage());

                    }
//                    Intent intent = new Intent(TaskProcess.this, TaskDetailsActivity.class);
//                    intent.putExtra("task",taskNumber);
                    Log.d("adperfectclicked","clicked"+adClicked);
//                    startActivity(intent);
                    Toast.makeText(TaskProcess.this, "Click Counted", Toast.LENGTH_LONG).show();
//                    finish();
                }
                else
                {
//                    Intent intent = new Intent(TaskProcess.this, TaskDetailsActivity.class);
//                    intent.putExtra("task",taskNumber);
//                    startActivity(intent);
                    Toast.makeText(TaskProcess.this, "Impression not Added :D", Toast.LENGTH_LONG).show();
//                    finish();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<100; i++)
                {
                    try{
                        Thread.sleep(100);
                        myHandler.sendEmptyMessage(0);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


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
}
