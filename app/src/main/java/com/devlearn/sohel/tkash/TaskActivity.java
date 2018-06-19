package com.devlearn.sohel.tkash;

import android.content.Intent;
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
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class TaskActivity extends AppCompatActivity {

    private DatabaseReference mDatabasetask1, mDatabasetask2;
    private FirebaseAuth mAuth;
    public String user_id;

    SpotsDialog waitingDialog;

    public TaskDetails task1, task2;

    private TextView taskone, tasktwo;

    private CardView taskcard1, taskcard2, taskcard3, taskcard4, taskcard5, taskcard6, taskcard7, taskcard8, taskcard9, taskcard10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabasetask1 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task1");
        mDatabasetask1.keepSynced(true);
        mDatabasetask2 = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child("task2");
        mDatabasetask2.keepSynced(true);

        taskcard1 = findViewById(R.id.taskcard1);
        taskcard2 = findViewById(R.id.taskcard2);
        taskcard3 = findViewById(R.id.taskcard3);
        taskcard4 = findViewById(R.id.taskcard4);
        taskcard5 = findViewById(R.id.taskcard5);
        taskcard6 = findViewById(R.id.taskcard6);
        taskcard7 = findViewById(R.id.taskcard7);
        taskcard8 = findViewById(R.id.taskcard8);
        taskcard9 = findViewById(R.id.taskcard9);
        taskcard10 = findViewById(R.id.taskcard10);

        waitingDialog = new SpotsDialog(TaskActivity.this);
        waitingDialog.show();
        try{
            mDatabasetask1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    task1 = dataSnapshot.getValue(TaskDetails.class);
//                    taskone.setText(String.valueOf(task1.getImpressions()));
                    Log.d("impression","i="+task1.getImpressions());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String error = databaseError.getMessage();
                    Log.d("taskone","error "+error);

                }
            });

            mDatabasetask2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    task2 = dataSnapshot.getValue(TaskDetails.class);
//                    tasktwo.setText(task2.getImpressions());
                    Log.d("impression","i="+task2.getImpressions());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String error = databaseError.getMessage();
                    Log.d("tasktwo","error "+error);
                }
            });

            waitingDialog.dismiss();

            taskcard1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int impressions = task1.getImpressions();
                    int clicks = task1.getClicks();
                    if(clicks<1 || impressions<20)
                    {
                        Intent intent = new Intent(TaskActivity.this, TaskDetailsActivity.class);
                        intent.putExtra("task","task1");
                        startActivity(intent);
                        Toast.makeText(TaskActivity.this, "task1", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(TaskActivity.this, "Task 1 is completed, Proceed to next Task", Toast.LENGTH_LONG).show();
                    }

                }
            });

            taskcard2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int impressions = task1.getImpressions();
                    int clicks = task1.getClicks();
                    if(clicks<1 || impressions<20)
                    {
                        Intent intent = new Intent(TaskActivity.this, TaskDetailsActivity.class);
                        intent.putExtra("task","task2");
                        startActivity(intent);
                        Toast.makeText(TaskActivity.this, "task2", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(TaskActivity.this, "Task 2 is completed, Proceed to next Task", Toast.LENGTH_LONG).show();
                    }

                }
            });

            taskcard3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            taskcard4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            taskcard5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }catch (Exception e)
        {
            String error = e.getMessage();
            Log.d("tryerror","error "+error);
            waitingDialog.dismiss();
        }

    }
}
