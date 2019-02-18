package com.devlearn.sohel.tkash;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devlearn.sohel.tkash.Models.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class ReferUser extends AppCompatActivity {

    private TextView currentUsersRefCode;

    private TextView copyRefCode, getReward, referelCodelabel;
    private EditText edtTextEnterCode;

    private Button sendInvitation;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUserDetails;
    private DatabaseReference mDatabaseRefererUserDetails;
    private DatabaseReference mDatabaseRefererAmountIncrease;

    private String userId;
    private UserDetails userDetails;
    private Double currentBalance;

    private RelativeLayout enterReferCodeLayout;
    private String referStatus;

    private SpotsDialog waitingDialog;
    private UserDetails referersUserDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_user);

        currentUsersRefCode = findViewById(R.id.iviteCode);
        copyRefCode = findViewById(R.id.copyCode);

        getReward = findViewById(R.id.applyCode);
        edtTextEnterCode = findViewById(R.id.edttextSetInviteCode);
        referelCodelabel = findViewById(R.id.txtReferelCodelabel);

        sendInvitation = findViewById(R.id.btnInvite);
        enterReferCodeLayout = findViewById(R.id.enterReferCodelayout);
        enterReferCodeLayout.setVisibility(View.INVISIBLE);
        referelCodelabel.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mDatabaseUserDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mDatabaseUserDetails.keepSynced(true);

        copyRefCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("CopiedText",userId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ReferUser.this, "Invitaition Code is Copied to the clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        getReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingDialog = new SpotsDialog(ReferUser.this);
                waitingDialog.show();

                if(TextUtils.isEmpty(edtTextEnterCode.getText().toString())){
                    waitingDialog.dismiss();
                    Toast.makeText(ReferUser.this, "Please Enter Referrel Code", Toast.LENGTH_SHORT).show();
                }else if(edtTextEnterCode.getText().toString().equals(userId)){
                    waitingDialog.dismiss();
                    Toast.makeText(ReferUser.this, "Are u Fucking Mad!! Put correct Invitation Code", Toast.LENGTH_SHORT).show();
                }else{
                    final String referersUId = edtTextEnterCode.getText().toString().trim();
                    mDatabaseRefererUserDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(referersUId);

                    mDatabaseRefererUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                referersUserDetails = new UserDetails();
                                referersUserDetails = dataSnapshot.getValue(UserDetails.class);
                                if(referersUserDetails !=null){
                                    mDatabaseUserDetails.child("currentBalance").setValue(currentBalance+1);
                                    mDatabaseUserDetails.child("referStatus").setValue("used");
                                    Log.d("Refer", ""+ referersUserDetails.currentBalance+ referersUserDetails.referStatus);
                                    increaseReferersAmount(referersUserDetails,referersUId);

                                }else {
                                    waitingDialog.dismiss();
                                    Toast.makeText(ReferUser.this, "Wrong Refer Code try again", Toast.LENGTH_SHORT).show();
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            waitingDialog.dismiss();
                            Toast.makeText(ReferUser.this, "Error "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        sendInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://drive.google.com/open?id=1nYC7NYs1DQ5rEiMAwpk_Su6GfA_RRa_4";
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my Invitation code:\n\n"+currentUsersRefCode.getText().toString().trim()+"\n\n Use this to get" +
                        " Bonus.\n TKash App Download Link:\n"+url);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

    }

    private void increaseReferersAmount(UserDetails referersUserDetails, String referersUId) {
        final double balance = referersUserDetails.currentBalance;
        mDatabaseRefererAmountIncrease = FirebaseDatabase.getInstance().getReference().child("Users").child(referersUId);

        mDatabaseRefererAmountIncrease.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDatabaseRefererAmountIncrease.child("currentBalance").setValue(balance+1);
                enterReferCodeLayout.setVisibility(View.INVISIBLE);
                referelCodelabel.setVisibility(View.INVISIBLE);
                waitingDialog.dismiss();
                Toast.makeText(ReferUser.this, "Success!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ReferUser.this, "Error "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        try{
            mDatabaseUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userDetails = dataSnapshot.getValue(UserDetails.class);

                    if(userDetails!=null)
                    {

                        currentBalance = userDetails.currentBalance;
                        referStatus = userDetails.referStatus;
                        currentUsersRefCode.setText(userId);
                        if(referStatus.equals("unused")){
                            referelCodelabel.setVisibility(View.VISIBLE);
                            enterReferCodeLayout.setVisibility(View.VISIBLE);
                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    String error = databaseError.getMessage();
                    Toast.makeText(ReferUser.this, "userdetailserror "+error, Toast.LENGTH_SHORT).show();
                    Log.d("username error","error: "+error);
                }
            });
        }catch (Exception e)
        {
            Log.d("username error","error: "+e.getMessage());

        }
    }
}
