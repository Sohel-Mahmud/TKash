package com.devlearn.sohel.tkash;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.devlearn.sohel.tkash.Models.TaskDetails;
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

    public TaskDetails task1, task2, task3, task4, task5;

    private TextView txtResetTask;

    private ValueEventListener taskLishtenr1,taskLishtenr2, taskLishtenr3,taskLishtenr4,taskLishtenr5;

    private TextView txtTaskstatus1,txtTaskstatus2,txtTaskstatus3,txtTaskstatus4,txtTaskstatus5;

    CountDownTimer countDownTimer;

    private CardView taskcard1, taskcard2, taskcard3, taskcard4, taskcard5, taskcard6, taskcard7, taskcard8, taskcard9, taskcard10, resetTaskCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabasetask1 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task1");
        mDatabasetask2 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task2");
        mDatabasetask3 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task3");
        mDatabasetask4 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task4");
        mDatabasetask5 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task5");
        taskcard1 = findViewById(R.id.taskcard1);
        taskcard2 = findViewById(R.id.taskcard2);
        taskcard3 = findViewById(R.id.taskcard3);
        taskcard4 = findViewById(R.id.taskcard4);
        taskcard5 = findViewById(R.id.taskcard5);

        txtTaskstatus1 = findViewById(R.id.txttaskstatus1);
        txtTaskstatus2 = findViewById(R.id.txttaskstatus2);
        txtTaskstatus3 = findViewById(R.id.txttaskstatus3);
        txtTaskstatus4 = findViewById(R.id.txttaskstatus4);
        txtTaskstatus5 = findViewById(R.id.txttaskstatus5);

        resetTaskCard = findViewById(R.id.resetTaskCard);

        txtResetTask = findViewById(R.id.txtResetTask);

        taskcard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    LoadTask1();

                }catch (NullPointerException e)
                {
                    Toast.makeText(TaskActivity.this, "error!"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });

        taskcard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    LoadTask2();

                }catch (NullPointerException e)
                {
                    Toast.makeText(TaskActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        taskcard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    LoadTask3();

                }catch (NullPointerException e)
                {
                    Toast.makeText(TaskActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        taskcard4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    LoadTask4();

                }catch (NullPointerException e)
                {
                    Toast.makeText(TaskActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        taskcard5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    LoadTask5();

                }catch (NullPointerException e)
                {
                    Toast.makeText(TaskActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

//        resetTaskCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDatabaseDelete = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id);
//
//                long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES);
//
////                long test = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
////                System.out.println(test);
////                System.out.println(cutoff);
//
//                Query deleteData = mDatabaseDelete.orderByChild("timestamp").endAt(cutoff);
//
//                deleteData.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for(DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
//                            itemSnapshot.getRef().removeValue();
//                        }
//                        Intent intent = new Intent(TaskActivity.this,MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        String error = databaseError.getMessage();
//                        Toast.makeText(TaskActivity.this, "ErrorDeleting "+error, Toast.LENGTH_SHORT).show();
//                        Log.d("errordeleting","eror "+error);
//                    }
//                });
//            }
//        });



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(taskLishtenr1!=null && taskLishtenr2!=null)
        {
            mDatabasetask1.removeEventListener(taskLishtenr1);
            mDatabasetask2.removeEventListener(taskLishtenr2);
        }

    }

    private void LoadTask1() {
        taskLishtenr1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                task1 = dataSnapshot.getValue(TaskDetails.class);
//                    taskone.setText(String.valueOf(task1.getImpressions()));
//                    int imp = Integer.valueOf(task1.getImpressions());
//                    Log.d("impression","i="+imp);

                int impressions = task1.getImpressions();
                int clicks = task1.getClicks();
                long timestamp = task1.getTimestamp();
                    Intent intent = new Intent(TaskActivity.this, TaskDetailsActivity.class);
                    intent.putExtra("task","task1");
                    startActivity(intent);
                    Toast.makeText(TaskActivity.this, "task1"+clicks+impressions+timestamp, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Log.d("taskone", "error " + error);

            }


        };
        mDatabasetask1.addListenerForSingleValueEvent(taskLishtenr1);

    }
    private void LoadTask2() {
        taskLishtenr2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                task2 = dataSnapshot.getValue(TaskDetails.class);
//                    taskone.setText(String.valueOf(task1.getImpressions()));
//                    int imp = Integer.valueOf(task1.getImpressions());
//                    Log.d("impression","i="+imp);
                int impressions = task2.getImpressions();
                int clicks = task2.getClicks();
                long timestamp = task2.getTimestamp();

                    Intent intent = new Intent(TaskActivity.this, TaskDetailsActivity.class);
                    intent.putExtra("task","task2");
                    startActivity(intent);
                    Toast.makeText(TaskActivity.this, "task2"+clicks+impressions+timestamp, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Log.d("tasktwo", "error " + error);

            }


        };
        mDatabasetask2.addListenerForSingleValueEvent(taskLishtenr2);

    }
    private void LoadTask3() {
        taskLishtenr3 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                task3 = dataSnapshot.getValue(TaskDetails.class);
//                    taskone.setText(String.valueOf(task1.getImpressions()));
//                    int imp = Integer.valueOf(task1.getImpressions());
//                    Log.d("impression","i="+imp);
                int impressions = task3.getImpressions();
                int clicks = task3.getClicks();
                long timestamp = task3.getTimestamp();

                Intent intent = new Intent(TaskActivity.this, TaskDetailsActivity.class);
                intent.putExtra("task","task3");
                startActivity(intent);
                Toast.makeText(TaskActivity.this, "task3"+clicks+impressions+timestamp, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Log.d("taskthree", "error " + error);

            }


        };
        mDatabasetask3.addListenerForSingleValueEvent(taskLishtenr3);
    }

    private void LoadTask4() {
        taskLishtenr4 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                task4 = dataSnapshot.getValue(TaskDetails.class);
//                    taskone.setText(String.valueOf(task1.getImpressions()));
//                    int imp = Integer.valueOf(task1.getImpressions());
//                    Log.d("impression","i="+imp);
                int impressions = task4.getImpressions();
                int clicks = task4.getClicks();
                long timestamp = task4.getTimestamp();

                Intent intent = new Intent(TaskActivity.this, TaskDetailsActivity.class);
                intent.putExtra("task","task4");
                startActivity(intent);
                Toast.makeText(TaskActivity.this, "task4"+clicks+impressions+timestamp, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Log.d("taskthree", "error " + error);

            }


        };
        mDatabasetask4.addListenerForSingleValueEvent(taskLishtenr4);
    }

    private void LoadTask5() {
        taskLishtenr5 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                task5 = dataSnapshot.getValue(TaskDetails.class);
//                    taskone.setText(String.valueOf(task1.getImpressions()));
//                    int imp = Integer.valueOf(task1.getImpressions());
//                    Log.d("impression","i="+imp);
                int impressions = task5.getImpressions();
                int clicks = task5.getClicks();
                long timestamp = task5.getTimestamp();

                Intent intent = new Intent(TaskActivity.this, TaskDetailsActivity.class);
                intent.putExtra("task","task5");
                startActivity(intent);
                Toast.makeText(TaskActivity.this, "task5"+clicks+impressions+timestamp, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Log.d("taskthree", "error " + error);

            }


        };
        mDatabasetask5.addListenerForSingleValueEvent(taskLishtenr5);
    }

//    private void countDownTimeForTask() {
//
//        try{
//            long timestamp = task1.getTimestamp();
//
//            long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(2, TimeUnit.MINUTES);
//
//            if(timestamp>cutoff)
//            {
//                long leftTime = timestamp-cutoff;
//                int timeleft = (int) leftTime;
//
//                countDownTimer = new CountDownTimer(timeleft,1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        String text = String.format(Locale.getDefault(), "Time Remaining %02d min: %02d sec",
//                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
//                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
//                        txtResetTask.setText(text);
//                        resetTaskCard.setEnabled(false);
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        resetTaskCard.setEnabled(true);
//
//                    }
//                }.start();
//
//            }
//        }catch (Exception e)
//        {
//            Toast.makeText(this, "Error Countdown"+e.getMessage(), Toast.LENGTH_SHORT).show();
//            Log.d("countdown","error"+e.getMessage());
//        }
//
//
//    }
}
