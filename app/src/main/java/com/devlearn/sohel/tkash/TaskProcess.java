package com.devlearn.sohel.tkash;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.devlearn.sohel.tkash.Models.McqDetails;
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

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class TaskProcess extends AppCompatActivity {


    private int ans;
    private int quesNumber;
    public McqDetails mcqDetails;
    private TextView txtQuestion, txtWait;


    GridLayout ansGrid;
    private DatabaseReference mDatabaseQuestions, mDatabaseUserDetails;
    private FirebaseAuth mAuth;

    String user_id;

    public UserDetails userDetails;

    private Button option1,option2,option3;

    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_process);

        ansGrid = findViewById(R.id.optionGrid);

        option1 = findViewById(R.id.btnOption1);
        option2 = findViewById(R.id.btnOption2);
        option3 = findViewById(R.id.btnOption3);
        txtQuestion = findViewById(R.id.txtQuestion);
        txtWait = findViewById(R.id.txtwait);


        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabaseUserDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        setToggleEvent(ansGrid);

        setTimerForGoingBack();

    }

    private void setTimerForGoingBack() {
        countDownTimer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txtWait.setTextColor(getResources().getColor(R.color.errorcolor));
                txtWait.setText("Wait( " + millisUntilFinished / 1000+" sec)");
                onBackPressed();
            }

            @Override
            public void onFinish() {

                if(!userDetails.accStatus.equals("active"))
                {
                    Intent intent =  new Intent(TaskProcess.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    txtWait.setText("Ok");
                    TaskProcess.super.onBackPressed();
                }


            }
        }.start();
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();  commented this to disable the back press
    }

    private void setToggleEvent(final GridLayout ansGrid) {
        for(int i=0; i<ansGrid.getChildCount();i++)
        {
            final Button btnOption = (Button)ansGrid.getChildAt(i);
            final int cursor = i;
            btnOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cursor == 0 && ans == 1)
                    {
                        btnOption.setBackgroundColor(Color.parseColor("#388E3C"));

                    }
                    else if(cursor == 1 && ans == 2)
                    {
                        btnOption.setBackgroundColor(Color.parseColor("#388E3C"));


//                        btnOption.setBackgroundColor(Color.parseColor(String.valueOf(R.color.correct)));

                    }
                    else if(cursor == 2 && ans == 3)
                    {
                        btnOption.setBackgroundColor(Color.parseColor("#388E3C"));


//                        btnOption.setBackgroundColor(Color.parseColor(String.valueOf(R.color.correct)));

                    }
                    else
                    {
                        btnOption.setBackgroundColor(Color.parseColor("#b00020"));

//                        btnOption.setBackgroundColor(Color.parseColor(String.valueOf(R.color.error)));

                    }

                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        Random r = new Random();

        quesNumber = r.nextInt(30)+1;
        String quesNumbers = String.valueOf(quesNumber);
        mDatabaseQuestions = FirebaseDatabase.getInstance().getReference().child("Questions").child(quesNumbers);

        ValueEventListener quesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mcqDetails = dataSnapshot.getValue(McqDetails.class);
//                Log.d("mcqdata","ques"+mcqDetails.question);
//                Log.d("mcqdata","opt1"+mcqDetails.option1);
//                Log.d("mcqdata","opt2"+mcqDetails.option2);
//                Log.d("mcqdata","opt3"+mcqDetails.option3);
//                Log.d("mcqdata","ans"+mcqDetails.ans);

                if(mcqDetails!=null)
                {
                    option1.setText(mcqDetails.option1);
                    option2.setText(mcqDetails.option2);
                    option3.setText(mcqDetails.option3);
                    txtQuestion.setText(mcqDetails.ques);
                    ans = mcqDetails.ans;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseQuestions.addListenerForSingleValueEvent(quesListener);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
//            String user_id = user.getUid();
            try{
                mDatabaseUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        userDetails = dataSnapshot.getValue(UserDetails.class);

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

