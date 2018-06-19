package com.devlearn.sohel.tkash;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.devlearn.sohel.tkash.Models.TaskDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaskDetailsActivity extends AppCompatActivity {

    private String taskNumber, user_id;

    private DatabaseReference mDatabasetask;
    private FirebaseAuth mAuth;

    private TaskDetails task;
    private TextView txtImpression, txtClicks;
    private Button btnProceed;

    private CircularProgressBar progressImpression, progressClicks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskNumber = getIntent().getExtras().getString("task");

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mDatabasetask = FirebaseDatabase.getInstance().getReference().child("Tasks").child(user_id).child(taskNumber);

        progressImpression = (CircularProgressBar)findViewById(R.id.progressBarImpression);
        progressClicks = (CircularProgressBar)findViewById(R.id.progressBarClicks);
        btnProceed = (Button)findViewById(R.id.btn_proceed);
        txtImpression = (TextView)findViewById(R.id.txtImpression);
        txtClicks = (TextView)findViewById(R.id.txtClicks);

        try{
            mDatabasetask.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    task = dataSnapshot.getValue(TaskDetails.class);

                    int impressions = task.getImpressions();
                    int clicks = task.getClicks();

                    progressImpression.setProgress((float)impressions);
                    txtImpression.setText(String.valueOf(impressions)+"/20");

                    if(clicks == 1)
                    {
                        progressClicks.setProgress((float)clicks);
                        txtClicks.setText(String.valueOf(clicks)+"/1");

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
                Intent intent = new Intent(TaskDetailsActivity.this, TaskProcess.class);
                intent.putExtra("task",taskNumber);
                startActivity(intent);
                finish();
            }
        });

    }
}
