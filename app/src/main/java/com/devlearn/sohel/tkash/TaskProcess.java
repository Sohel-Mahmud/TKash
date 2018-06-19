package com.devlearn.sohel.tkash;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class TaskProcess extends AppCompatActivity {

    RingProgressBar ringProgress_bar;

    int progress = 0;

    private String taskNumber;

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

        ringProgress_bar.setOnProgressListener(new RingProgressBar.OnProgressListener() {
            @Override
            public void progressToComplete() {
                Intent intent = new Intent(TaskProcess.this, TaskDetailsActivity.class);
                intent.putExtra("task",taskNumber);
                startActivity(intent);
                Toast.makeText(TaskProcess.this, "task1", Toast.LENGTH_SHORT).show();
                finish();
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
}
