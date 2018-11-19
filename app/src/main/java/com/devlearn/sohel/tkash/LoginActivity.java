package com.devlearn.sohel.tkash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devlearn.sohel.tkash.Models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {
    private EditText edtPhone;
    private EditText edtName;
    private TextView txtMessege;
    private RelativeLayout rootlayout;
    private ProgressBar progressBar;
    private Button btnLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;

    public String phoneNumber;
    public String userName;
    private String mVerificationId;
    public SpotsDialog waitingDialog;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

//    ctrl+o
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
        setContentView(R.layout.activity_login);
        

        edtPhone = (EditText)findViewById(R.id.edtphone);
        edtName = findViewById(R.id.edtName);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        txtMessege = (TextView)findViewById(R.id.txtMessege);
        rootlayout = (RelativeLayout)findViewById(R.id.rootlayout);

        mAuth = FirebaseAuth.getInstance();


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                String error = e.getMessage();
                Snackbar.make(rootlayout, "Error "+e, Snackbar.LENGTH_LONG)
                        .show();
                Log.d("Error fire",error);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

//                progressBar.setVisibility(View.INVISIBLE);

                // ...
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                phoneNumber = edtPhone.getText().toString();
                userName = edtName.getText().toString().trim();

                if(TextUtils.isEmpty(phoneNumber))
                {
                    edtPhone.setError("Incorrect phone number format");
                    requestFocus(edtPhone);
                }
                else if(TextUtils.isEmpty(userName))
                {
                    edtName.setError("Please insert your name");
                    requestFocus(edtName);
                }
                else
                {
                    phoneNumber = edtPhone.getText().toString();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            mCallbacks
                    );

                    LoginUisngPhone();

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser != null)
        {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();

        }
    }

    private void LoginUisngPhone() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Verification");
//        dialog.setMessage("Please provide verification Code");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_verification = inflater.inflate(R.layout.layout_verification,null);

        //OTP code enter field
        final EditText edtVerificationCode = layout_verification.findViewById(R.id.edtVerificationCode);
        final TextView txtCountdown = layout_verification.findViewById(R.id.txtCountdown);
        final ProgressBar progressBar = layout_verification.findViewById(R.id.progressbar);

        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtCountdown.setText("Please provide Verificatin Code sec remaining: " + millisUntilFinished / 1000);
                txtMessege.setTextColor(getResources().getColor(R.color.errorcolor));
                txtMessege.setText("Please wait for "+ millisUntilFinished / 1000+" sec to try Again");
                btnLogin.setEnabled(false);
                edtName.setEnabled(false);
                edtPhone.setEnabled(false);

            }

            public void onFinish() {
                txtCountdown.setText("Please Try Again!");
                txtMessege.setText("A verification Code will be sent when you click send verification");
                btnLogin.setEnabled(true);
                edtName.setEnabled(true);
                edtPhone.setEnabled(true);
            }
        }.start();

        dialog.setView(layout_verification);

        //removed OTP Code enter feature
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
                //disable sign in button while processing
//                btnLogin.setEnabled(false);

                //validate the data
                if (TextUtils.isEmpty(edtVerificationCode.getText().toString())) {
//                    edtVerificationCode.setError("Enter verification Code");
//                    requestFocus(layout_verification.findViewById(R.id.edtVerificationCode));
                    Toast.makeText(LoginActivity.this, "Insert Code", Toast.LENGTH_LONG).show();

                }
                else
                {
                    String verificationCode = edtVerificationCode.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    //dot waitng process
//                    waitingDialog = new SpotsDialog(LoginActivity.this);
//                    waitingDialog.show();

                    signInWithPhoneAuthCredential(credential);
//                    waitingDialog.dismiss();
                }



            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                            //dot waitng process
                            Toast.makeText(LoginActivity.this, "Success! checking user exists or not", Toast.LENGTH_SHORT).show();
                            checkUserExists();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                String error = task.getException().getMessage();
                                Snackbar.make(rootlayout, "Invalid " + error, Snackbar.LENGTH_LONG)
                                        .show();
                                Log.d("Error fire2",error);

                            }
                        }
                    }
                });
    }

    private void checkUserExists() {
        if(mAuth.getCurrentUser()!=null)
        {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

            mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(user_id))
                    {
                        Toast.makeText(LoginActivity.this, "Welcome!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
//                        Intent intent = new Intent(LoginActivity.this,AccountSetupActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                        UserDetails userDetails = new UserDetails(phoneNumber,userName,"active",0.0);

                        mDatabaseUsers.child(user_id).setValue(userDetails);
//                        mDatabaseUsers.child(user_id).child("userName").setValue(userName);
//                        mDatabaseUsers.child(user_id).child("userPhone").setValue(phoneNumber);
//                        mDatabaseUsers.child(user_id).child("currentBalance").setValue(0.0);
//                        mDatabaseUsers.child(user_id).child("accStatus").setValue("active");
                        Toast.makeText(LoginActivity.this, "Success!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {



                }
            });

        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
