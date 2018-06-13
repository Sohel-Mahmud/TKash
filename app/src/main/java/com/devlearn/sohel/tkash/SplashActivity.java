package com.devlearn.sohel.tkash;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;
    FirebaseAuth mAuth;
    private static int SplashTime = 4000;

    //ctrl+o
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashThme);
        //before setcontentview
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Ubuntu.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.spin_kit);
        ThreeBounce threeBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(threeBounce);

        progressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser CurrentUser = mAuth.getCurrentUser();
                if(CurrentUser == null)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    finish();

                }
                else if(CurrentUser!=null)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);

                }

            }
        },SplashTime);


    }
}
