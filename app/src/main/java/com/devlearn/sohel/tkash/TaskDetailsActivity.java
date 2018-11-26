package com.devlearn.sohel.tkash;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.devlearn.sohel.tkash.IPClass.IPCheck;
import com.devlearn.sohel.tkash.Models.TaskDetails;
import com.devlearn.sohel.tkash.Models.UserDetails;
import com.devlearn.sohel.tkash.SharedPref.TaskSharedPref;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
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
    private boolean Escaped = false;
    private TaskSharedPref usp;

    private CountDownTimer countDownTimer, countDownTimer2;

    private CircularProgressBar progressImp, progressclks;

    private ValueEventListener valueEventListener;

    private int questions, clks, impLimit;
    private String status = "Running";

    private RewardedVideoAd mRewardedVideoAd;
    private AdView mAdView;
    private AdRequest reqInterstitial;
    private InterstitialAd interstitialAd;
    public UserDetails userDetails;
    ConstraintLayout taskDetailslayout;

    public IPCheck ipCheck;
    public static final int LENGTH = 30;

    long mills = 30000;

    private double currentBalance;

    private long timestamp;
    private long currentTimeStamp;
    private long cutoff;
    private TextView mlog;
    private double estimatedServerTimeMs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskNumber = getIntent().getExtras().getString("task");

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabasetask = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child(taskNumber);
        mDatabaseUserDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mlog = findViewById(R.id.mlog);

        MobileAds.initialize(this,getString(R.string.admobAppId));
//        MobileAds.initialize(this,"ca-app-pub-3940256099942544/5224354917");

        mAdView = findViewById(R.id.adViewInTask);

        usp = new TaskSharedPref(TaskDetailsActivity.this);

        bannerAdRequest();


        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        loadInterstitial();

        loadRewardedVideo();

        Log.d("lifecycle","onCreate");

        progressImp = (CircularProgressBar)findViewById(R.id.progressBarImp);
        progressclks = (CircularProgressBar)findViewById(R.id.progressBarClks);
        btnProceed = (Button)findViewById(R.id.btn_proceed);
        txtImp = (TextView)findViewById(R.id.txtImp);
        txtclks = (TextView)findViewById(R.id.txtClks);
        btnReset = findViewById(R.id.btnReset);
        taskDetailslayout = (ConstraintLayout)findViewById(R.id.taskdetails);
        btnReset.setEnabled(false);
//        btnReset.setEnabled(true);

        try{
            DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
            offsetRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    double offset = snapshot.getValue(Double.class);
                    currentTimeStamp = (long) (System.currentTimeMillis() + offset);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled");
                }
            });


            valueEventListener = mDatabasetask.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    task = new TaskDetails();
                    task = dataSnapshot.getValue(TaskDetails.class);

                    questions = task.getImp();
                    clks = task.getClks();
                    impLimit = task.getLimitImp();
                    timestamp = task.getTimestamp();

                    Log.d("Getlong","timestamp:"+timestamp);

                    //currentTimeStamp = new Date().getTime() + TimeUnit.MILLISECONDS.convert(40, TimeUnit.SECONDS);


                    Log.d("Time","currentTimeStamp:"+currentTimeStamp);
                    Log.d("Time","CurrentTime new date.gettime"+new Date().getTime());
                    Log.d("Time","CurrentTime Server"+(long)estimatedServerTimeMs);



                    progressImp.setProgress((float)questions);
                    progressImp.setMaximum((float)impLimit);
                    txtImp.setText(String.valueOf(questions)+"/"+String.valueOf(impLimit));

                    progressclks.setProgress((float)clks);
                    txtclks.setText(String.valueOf(clks)+"/1");

                    startResetCounting();

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
//                if(ipCheck==null)
//                {
//                    Snackbar.make(taskDetailslayout, "Couldn't Load the verifier ", Snackbar.LENGTH_LONG)
//                            .show();
//                    GetIp();
//                }
                if(questions == impLimit && clks == 1)
                {
                    Toast.makeText(TaskDetailsActivity.this, "Your task is over, go to next task or You can reset task", Toast.LENGTH_LONG).show();
                }
                else {

                    if(interstitialAd.isLoaded())
                    {
                        interstitialAd.show();
                        if(questions<impLimit && !adMissclicked)
                        {
                            int finalImpression = questions+1;
                            mDatabasetask.child("imp").setValue(finalImpression);
                            Toast.makeText(TaskDetailsActivity.this, "counted!!!", Toast.LENGTH_SHORT).show();

                        }

                    }
                    else
                    {
//                        startActivity(new Intent(TaskDetailsActivity.this, TaskProcess.class));
                        Snackbar.make(taskDetailslayout, "Couldn't Load the Data and Verifier", Snackbar.LENGTH_LONG)
                                .show();
                        interstitialAd.loadAd(new AdRequest.Builder().build());

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
                else
                {
//                    Snackbar.make(taskDetailslayout, "Can't load the video, try again!", Snackbar.LENGTH_LONG)
//                            .show();
                    resetTasks();
                }

            }
        });

    }

    private void bannerAdRequest() {
        AdRequest adRequest = new AdRequest.Builder().build();
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
                    Toast.makeText(TaskDetailsActivity.this, "You've done something terrible!!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(TaskDetailsActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

//                    Log.d("adopenadmissclicked","missclicked"+adMissclicked);
                }catch (Exception e)
                {
                    Toast.makeText(TaskDetailsActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void startResetCounting() {
        if(questions == impLimit && clks == 1)
        {
            if(currentTimeStamp<timestamp)
            {
                long leftTime = timestamp-currentTimeStamp;
                int timeleft = (int) leftTime;

                Log.d("Getlong", "timeleft: "+timeleft);

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
            btnReset.setEnabled(false);
            btnReset.setText("Reset");
        }
    }

    private void loadRewardedVideo() {
        if(!mRewardedVideoAd.isLoaded())
        {
            mRewardedVideoAd.loadAd(getString(R.string.RewardVideo),
                    new AdRequest.Builder().build());

        }

    }

    private void resetTasks() {

                Map<String, Object> newValues = new HashMap<>();
                newValues.put("clks",0);
                newValues.put("imp",0);
                newValues.put("limitImp", getRandomNumberImp());
                newValues.put("timestamp",ServerValue.TIMESTAMP);

                mDatabasetask.setValue(newValues);
    }
    private int getRandomNumberImp(){
        Random rand = new Random();
        return (25 + rand.nextInt((30 - 25) + 1));

    }

    private void loadInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.InterstitialAd));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener(){

            @Override
            public void onAdClosed() {

                startActivity(new Intent(TaskDetailsActivity.this, TaskProcess.class));
                interstitialAd.loadAd(new AdRequest.Builder().build());

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                Escaped = true;
                if(questions==impLimit && clks == 0)
                {
                    final int finalClicks = clks+1;

                    countDownTimer2 = new CountDownTimer(17000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        Toast.makeText(TaskDetailsActivity.this, "Wait "+millisUntilFinished/1000+" sec", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFinish() {
                        try{
                        cutoff = new Date().getTime() + TimeUnit.MILLISECONDS.convert(300, TimeUnit.MINUTES);
                        Log.d("Getlong","cutoff "+cutoff);
                        double addBalance = currentBalance+1;
                        mDatabasetask.child("clks").setValue(finalClicks);
                        mDatabasetask.child("timestamp").setValue(cutoff);
                        mDatabaseUserDetails.child("currentBalance").setValue(addBalance);

                        if(taskNumber.equals("task1")){
                            usp.setTaskNumber(2);
                            Toast.makeText(TaskDetailsActivity.this, "taskNumber"+usp.getTaskNumber(), Toast.LENGTH_SHORT).show();
                        }
                        else if(taskNumber.equals("task2"))
                        {
                            usp.setTaskNumber(3);
                            Toast.makeText(TaskDetailsActivity.this, "taskNumber"+usp.getTaskNumber(), Toast.LENGTH_SHORT).show();

                        }
                        else if(taskNumber.equals("task3"))
                        {
                            usp.setTaskNumber(4);
                            Toast.makeText(TaskDetailsActivity.this, "taskNumber"+usp.getTaskNumber(), Toast.LENGTH_SHORT).show();

                        }
                        else if(taskNumber.equals("task4"))
                        {
                            usp.setTaskNumber(5);
                            Toast.makeText(TaskDetailsActivity.this, "taskNumber"+usp.getTaskNumber(), Toast.LENGTH_SHORT).show();

                        }
                        else if(taskNumber.equals("task5"))
                        {
                            usp.setTaskNumber(1);
                            Toast.makeText(TaskDetailsActivity.this, "taskNumber"+usp.getTaskNumber(), Toast.LENGTH_SHORT).show();

                        }

                        Toast.makeText(TaskDetailsActivity.this, "Completed!", Toast.LENGTH_LONG).show();

                        }catch (Exception e)
                        {
                        Toast.makeText(TaskDetailsActivity.this, "Error adding"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Error adding Balance","errrr!"+e.getMessage());

                        }

                        }
                    }.start();

                }
                else
                {
                    try{
                        mDatabaseUserDetails.child("accStatus").setValue("Banned");
                        adMissclicked = true;
                        Toast.makeText(TaskDetailsActivity.this, "You've done something terrible!!", Toast.LENGTH_LONG).show();
                        Intent intent =  new Intent(TaskDetailsActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

//                    Log.d("adopenadmissclicked","missclicked"+adMissclicked);
                    }catch (Exception e)
                    {
                        Toast.makeText(TaskDetailsActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

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
    }


    private void GetIp() {
        ipCheck = new IPCheck();
        RequestQueue queue = Volley.newRequestQueue(this);

        String urlip = "http://checkip.amazonaws.com/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlip, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String url ="http://ip-api.com/json/" +response;
//                    Log.d("actuallink","link: "+url);
                ipCheck.setIp(response);
                getIPDetails(url);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ipCheck.setError1("Loading..");
            }
        });

        queue.add(stringRequest);
    }

    private void getIPDetails(String url) {
        RequestQueue queue2 = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ipCheck.setCountry(response.getString("country"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ipCheck.setError2("Error "+error.getMessage());
            }
        });

        queue2.add(jsonObjectRequest);
    }

    @Override
    protected void onStart() {
        Log.d("lifecycle","onStart");

        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
//            String user_id = user.getUid();
            try{
                mDatabaseUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        userDetails = dataSnapshot.getValue(UserDetails.class);
                        if(userDetails!=null)
                        {
                            currentBalance = userDetails.currentBalance;
                            Log.d("found!","balance"+currentBalance);
                        }


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
        loadRewardedVideo();
        //GetIp();

    }

    @Override
    protected void onDestroy() {
        Log.d("lifecycle","onDestroy");

        if (mAdView != null) {
            mAdView.destroy();
        }
        mDatabasetask.removeEventListener(valueEventListener);
        mRewardedVideoAd.destroy(this);
        super.onDestroy();

    }
    @Override
    public void onPause() {
        Log.d("lifecycle","onPause");

        if (mAdView != null) {
            mAdView.pause();
        }
        mRewardedVideoAd.pause(this);
        super.onPause();

    }

    @Override
    public void onResume() {
        Log.d("lifecycle","onResume");

        if (mAdView != null) {
            mAdView.resume();
        }
        mRewardedVideoAd.resume(this);
        //for viewing ad when activity regains it focus
        loadInterstitial();
        //GetIp();
        super.onResume();

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mlog.append("video loaded\n");
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

        resetTasks();
        Toast.makeText(this, "onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
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
