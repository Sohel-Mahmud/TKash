package com.devlearn.sohel.tkash;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DialogTitle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.GridLayout;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.devlearn.sohel.tkash.Models.UpdateLink;
import com.devlearn.sohel.tkash.Models.UserDetails;
import com.devlearn.sohel.tkash.Models.WithdrawListDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    android.support.v7.widget.GridLayout mainGrid;

    TextView txtusername, txtcurrentBalance, txtTotalbalance, txtuserNumber;
    private double currentBalance, totalBalance;

    private TextView marque;

    private String username, usernumber;
    public String user_id;

    private String numberProvider,withdrawStatus, withdrawNumber;
    private int selectedId = -1;
    private int selectedId2 = -1;
    private double withdrawAmount;

    public SpotsDialog waitingDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUserDetails;
    private DatabaseReference mDatabaseTask;
    private DatabaseReference mDatabaseWithdraw;
    private DatabaseReference mDatabaseWelcome;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, String.valueOf(R.string.admobTestId));


        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mDatabaseUserDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mDatabaseUserDetails.keepSynced(true);
        mDatabaseTask = FirebaseDatabase.getInstance().getReference().child("Tasks");
        mDatabaseWithdraw = FirebaseDatabase.getInstance().getReference().child("Withdraws").child(user_id);
        mDatabaseWelcome = FirebaseDatabase.getInstance().getReference().child("WelcomeText").child("text");

        txtusername = (TextView)findViewById(R.id.username);
        txtuserNumber = findViewById(R.id.userNumber);
        txtcurrentBalance = (TextView)findViewById(R.id.currentBalance);
        txtTotalbalance = (TextView)findViewById(R.id.totalbalance);
        marque = findViewById(R.id.bannerMarque);
        marque.setSelected(true);

        mainGrid = (android.support.v7.widget.GridLayout)findViewById(R.id.mainGrid);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("2C750EBF11C8D60CC8D31D18C832AFEB")
                .build();
        mAdView.loadAd(adRequest);
        if(adRequest.isTestDevice(this)){
            Toast.makeText(this, "Its a test device", Toast.LENGTH_SHORT).show();
            Log.d("test device", "onCreate: test device running ");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set event for gridview
        setSingleEvent(mainGrid);



    }

    private void setSingleEvent(GridLayout mainGrid) {

        //loop all child item for Main Grid
        for(int i=0; i<mainGrid.getChildCount(); i++)
        {
            //all child items are cardview so cast object to cardview
            CardView cardView = (CardView)mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //here set activity for those i;
                    if(finalI == 1)
                    {
                        checkTaskAvailability();
                        startActivity(new Intent(MainActivity.this, TaskActivity.class));

                    }
                    else if(finalI == 2)
                    {
                        startActivity(new Intent(MainActivity.this, WithdrawHistoryActivity.class));
                    }
                    else if(finalI == 3)
                    {
                        alertDialog();
                    }
                    else if(finalI == 0)
                    {
                        infoDetailsAlertDialog();
                    }

                    Toast.makeText(MainActivity.this, "Clicked"+ finalI, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void infoDetailsAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Informaions");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_information = inflater.inflate(R.layout.layout_information,null);
        dialog.setView(layout_information);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void alertDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Money withdraw");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_withdraw = inflater.inflate(R.layout.layout_withdraw, null);
        final RadioGroup radioProvider = layout_withdraw.findViewById(R.id.radioProvider);
        final EditText edtWithdrawNumber = layout_withdraw.findViewById(R.id.edtWithdrawNumber);
        final EditText edtWithdrawAmount = layout_withdraw.findViewById(R.id.edtWithdrawAmount);
//        final RadioButton radioBkash = layout_withdraw.findViewById(R.id.bkash);
//        final RadioButton radioRocket = layout_withdraw.findViewById(R.id.rocket);
//        final RadioButton radioAgent = layout_withdraw.findViewById(R.id.radioAgent);
//        final RadioButton radioPersonal = layout_withdraw.findViewById(R.id.radioPersonal);

        dialog.setView(layout_withdraw);

        dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedId = radioProvider.getCheckedRadioButtonId();
                RadioButton provider = radioProvider.findViewById(selectedId);


                if((selectedId != -1) && (!TextUtils.isEmpty(edtWithdrawNumber.getText().toString())) && (!TextUtils.isEmpty(edtWithdrawAmount.getText().toString())))
                {
                    numberProvider = provider.getText().toString();
                    withdrawNumber = edtWithdrawNumber.getText().toString();
                    withdrawAmount = Double.valueOf(edtWithdrawAmount.getText().toString());

                    Toast.makeText(MainActivity.this, "Success!!"+numberProvider+" "+withdrawNumber+selectedId+" "+selectedId2+"Amount "+withdrawAmount, Toast.LENGTH_SHORT).show();
                    postWithdrawRequest(numberProvider, withdrawNumber, withdrawAmount);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Please Provide all the information", Toast.LENGTH_SHORT).show();

                }

            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void postWithdrawRequest(String numberProvider, String withdrawNumber, double withdrawAmount) {

        if((numberProvider.equals("Mobile Recharge")) && (currentBalance>= withdrawAmount) && (withdrawAmount >=50))
        {
            Toast.makeText(this, "Correct withdraw", Toast.LENGTH_SHORT).show();
            waitingDialog = new SpotsDialog(MainActivity.this);
            waitingDialog.show();

            withdrawStatus = "Pending";

            WithdrawListDetails withdraw = new WithdrawListDetails(numberProvider, withdrawNumber, withdrawStatus, withdrawAmount);

            double newCurrentBalace = currentBalance - withdrawAmount;
            mDatabaseUserDetails.child("currentBalance").setValue(newCurrentBalace);

            final DatabaseReference newPost = mDatabaseWithdraw.push();

            newPost.setValue(withdraw).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    waitingDialog.dismiss();
                    startActivity(new Intent(MainActivity.this, WithdrawHistoryActivity.class));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    waitingDialog.dismiss();
                    String error = e.getMessage();
                    Toast.makeText(MainActivity.this, "Error! "+error, Toast.LENGTH_SHORT).show();
                }
            });

        }

        else if((numberProvider.equals("bKash") || numberProvider.equals("Rocket")) && (currentBalance>= withdrawAmount) && (withdrawAmount >=105))
        {
            Toast.makeText(this, "Correct withdraw", Toast.LENGTH_SHORT).show();
            waitingDialog = new SpotsDialog(MainActivity.this);
            waitingDialog.show();

            withdrawStatus = "Pending";

            WithdrawListDetails withdraw = new WithdrawListDetails(numberProvider, withdrawNumber, withdrawStatus, withdrawAmount);

            double newCurrentBalace = currentBalance - withdrawAmount;
            mDatabaseUserDetails.child("currentBalance").setValue(newCurrentBalace);

            final DatabaseReference newPost = mDatabaseWithdraw.push();

            newPost.setValue(withdraw).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                waitingDialog.dismiss();
                startActivity(new Intent(MainActivity.this, WithdrawHistoryActivity.class));

            }
            }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        String error = e.getMessage();
                        Toast.makeText(MainActivity.this, "Error! "+error, Toast.LENGTH_SHORT).show();
                    }
                });

        }
        else
        {
//            waitingDialog.dismiss();
//            startActivity(new Intent(MainActivity.this, WithdrawHistoryActivity.class));
//            finish();

//            waitingDialog.dismiss();
//            String error  = task.getException().getMessage();
//            Toast.makeText(MainActivity.this, "Error! "+error, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Your can Recharge 50+tk and bkash, rocket 200+tk!", Toast.LENGTH_LONG).show();
        }
    }

    private void checkTaskAvailability() {

        //dot waitng process
        waitingDialog = new SpotsDialog(MainActivity.this);
        waitingDialog.show();

        ValueEventListener mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id))
                {
                    waitingDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, TaskActivity.class);

                }
                else
                {
                    Map<String, Object> task1 = new HashMap<>();
                    Map<String, Object> task2 = new HashMap<>();
                    Map<String, Object> task3 = new HashMap<>();
                    Map<String, Object> task4 = new HashMap<>();
                    Map<String, Object> task5 = new HashMap<>();
                    task1.put("imp",0);
                    task1.put("clks",0);
                    task1.put("timestamp", ServerValue.TIMESTAMP);
                    task1.put("status", "Running");

                    task2.put("imp",0);
                    task2.put("clks",0);
                    task2.put("timestamp", ServerValue.TIMESTAMP);
                    task2.put("status", "Running");


                    task3.put("imp",0);
                    task3.put("clks",0);
                    task3.put("timestamp", ServerValue.TIMESTAMP);
                    task3.put("status", "Running");


                    task4.put("imp",0);
                    task4.put("clks",0);
                    task4.put("timestamp", ServerValue.TIMESTAMP);
                    task4.put("status", "Running");


                    task5.put("imp",0);
                    task5.put("clks",0);
                    task5.put("timestamp", ServerValue.TIMESTAMP);
                    task5.put("status", "Running");

                    mDatabaseTask.child(user_id).child("task1").setValue(task1);
                    mDatabaseTask.child(user_id).child("task2").setValue(task2);
                    mDatabaseTask.child(user_id).child("task3").setValue(task3);
                    mDatabaseTask.child(user_id).child("task4").setValue(task4);
                    mDatabaseTask.child(user_id).child("task5").setValue(task5);
                    waitingDialog.dismiss();


//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(MainActivity.this, "Success!!!", Toast.LENGTH_LONG).show();
//                            waitingDialog.dismiss();
//                            startActivity(new Intent(MainActivity.this, TaskActivity.class));
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                            waitingDialog.dismiss();
//                            String error  = e.getMessage();
//                            Log.d("taskloaderror","eror"+error);
//                            Toast.makeText(MainActivity.this, "Failed loading tasks! "+error, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Log.d("taskerror","error "+error);
            }
        };
        mDatabaseTask.addListenerForSingleValueEvent(mValueEventListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        try{

            mDatabaseWelcome.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String emote = String.valueOf(Character.toChars(0x1F609));
                    String text = (String) dataSnapshot.getValue();
                    marque.setText(text+" "+emote);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e)
        {

        }
        try{
            mDatabaseUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                    username = userDetails.getUserName();
                    usernumber = userDetails.getUserPhone();
                    currentBalance = userDetails.getCurrentBalance();
                    txtusername.setText(username);
                    txtuserNumber.setText(usernumber);
                    txtcurrentBalance.setText(userDetails.getCurrentBalance().toString());
                    txtTotalbalance.setText(userDetails.getTotalBalance().toString());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    String error = databaseError.getMessage();
                    Toast.makeText(MainActivity.this, "userdetailserror "+error, Toast.LENGTH_SHORT).show();
                    Log.d("username error","error: "+error);
                }
            });
        }catch (Exception e)
        {
            Log.d("username error","error: "+e.getMessage());

        }
        try{
            DatabaseReference updateCheck = FirebaseDatabase.getInstance().getReference().child("UpdateFile");
            updateCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UpdateLink updateLink = dataSnapshot.getValue(UpdateLink.class);
                    double version = updateLink.getVersion();
                    String url = updateLink.getUrl();

                    if(version != 0.5)
                    {
                        AlertForUpdate(url);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e)
        {
            Toast.makeText(this, "UpdateError"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void AlertForUpdate(final String url) {

        AlertDialog.Builder updateDialog = new AlertDialog.Builder(this);
        updateDialog.setTitle("Update Available");
        updateDialog.setCancelable(false);
        updateDialog.setMessage("Good news! there is an update for your app, please update to proceed!!")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                });
        updateDialog.show();

    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clks here. The action bar will
        // automatically handle clks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
