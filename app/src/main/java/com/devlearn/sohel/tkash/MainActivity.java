package com.devlearn.sohel.tkash;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.devlearn.sohel.tkash.Models.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    android.support.v7.widget.GridLayout mainGrid;

    TextView txtusername, txtcurrentBalance, txtTotalbalance;
    double currentbalance, totalbalance;

    String username;
    public String user_id;

    private String numberProvider, number_Type, withdrawNumber;
    private int selectedId = -1;
    private int selectedId2 = -1;
    private double withdrawAmount;

    public SpotsDialog waitingDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUserDetails;
    private DatabaseReference mDatabaseTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mDatabaseUserDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mDatabaseUserDetails.keepSynced(true);
        mDatabaseTask = FirebaseDatabase.getInstance().getReference().child("Tasks");

        txtusername = (TextView)findViewById(R.id.username);
        txtcurrentBalance = (TextView)findViewById(R.id.currentBalance);
        txtTotalbalance = (TextView)findViewById(R.id.totalbalance);

        mainGrid = (android.support.v7.widget.GridLayout)findViewById(R.id.mainGrid);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set event for gridview
        setSingleEvent(mainGrid);

        mDatabaseUserDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                username = userDetails.getUserName();
                txtusername.setText(username);
                txtcurrentBalance.setText(userDetails.getCurrentBalance().toString());
                txtTotalbalance.setText(userDetails.getTotalBalance().toString());
                Log.d("username","error: "+username);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                String error = databaseError.getMessage();
                Toast.makeText(MainActivity.this, "Error "+error, Toast.LENGTH_SHORT).show();
                Log.d("username error","error: "+error);
            }
        });

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
                    else if(finalI == 3)
                    {
                        alertDialog();
                    }

                    Toast.makeText(MainActivity.this, "Clicked"+ finalI, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void alertDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Money withdraw");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_withdraw = inflater.inflate(R.layout.layout_withdraw, null);
        final RadioGroup radioProvider = layout_withdraw.findViewById(R.id.radioProvider);
        final RadioGroup radioNumberType = layout_withdraw.findViewById(R.id.radioNumberType);
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

                selectedId2 = radioNumberType.getCheckedRadioButtonId();
                RadioButton numberType = radioNumberType.findViewById(selectedId2);

                if((selectedId != -1) && (selectedId2 != -1) && (!TextUtils.isEmpty(edtWithdrawNumber.getText().toString())) && (!TextUtils.isEmpty(edtWithdrawAmount.getText().toString())))
                {
                    numberProvider = provider.getText().toString();
                    number_Type = numberType.getText().toString();
                    withdrawNumber = edtWithdrawNumber.getText().toString();
                    withdrawAmount = Double.valueOf(edtWithdrawAmount.getText().toString());

                    Toast.makeText(MainActivity.this, "Success!!"+numberProvider+" "+number_Type+" "+withdrawNumber+selectedId+" "+selectedId2+"Amount "+withdrawAmount, Toast.LENGTH_SHORT).show();

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

    private void checkTaskAvailability() {

        //dot waitng process
        waitingDialog = new SpotsDialog(MainActivity.this);
        waitingDialog.show();

        mDatabaseTask.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id))
                {
                    waitingDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, TaskActivity.class);

                }
                else
                {
                    mDatabaseTask.child(user_id).child("task1").child("impressions").setValue(0);
                    mDatabaseTask.child(user_id).child("task1").child("clicks").setValue(0);
                    mDatabaseTask.child(user_id).child("task2").child("impressions").setValue(0);
                    mDatabaseTask.child(user_id).child("task2").child("clicks").setValue(0);
                    Toast.makeText(MainActivity.this, "Success!!", Toast.LENGTH_LONG).show();
                    waitingDialog.dismiss();
                    startActivity(new Intent(MainActivity.this, TaskActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Log.d("taskerror","error "+error);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser CurrentUser = mAuth.getCurrentUser();
//        if(CurrentUser == null)
//        {
//            startActivity(new Intent(MainActivity.this,LoginActivity.class));
//            finish();
//
//        }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
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
        // Handle navigation view item clicks here.
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
