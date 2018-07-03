package com.devlearn.sohel.tkash;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devlearn.sohel.tkash.Models.WithdrawListDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class WithdrawHistoryActivity extends AppCompatActivity {

    private RecyclerView mHistoryList;

    private DatabaseReference mDatabaseWithdraw;
    private FirebaseAuth mAuth;

    private String user_id;

    Query databaseReference;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseRecyclerAdapter <WithdrawListDetails, HistoryViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_history);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

//        mDatabaseWithdraw = FirebaseDatabase.getInstance().getReference().child("Withdraws").child(user_id);
//        mDatabaseWithdraw.keepSynced(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Withdraws").child(user_id);
        databaseReference.keepSynced(true);

        mHistoryList = (RecyclerView) findViewById(R.id.history_list);
        mHistoryList.setHasFixedSize(true);
        mHistoryList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<WithdrawListDetails> options = new FirebaseRecyclerOptions.Builder<WithdrawListDetails>()
                .setQuery(databaseReference, WithdrawListDetails.class)
                .setLifecycleOwner(this)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<WithdrawListDetails, HistoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HistoryViewHolder holder, int position, @NonNull WithdrawListDetails model) {
                holder.setPhoneNumber(model.getWithdrawNumber());
                holder.setNumberProvider(model.getNumberProvider());
                holder.setWithdrawAmount(model.getWithdrawAmount());
                holder.setWithdrawStatus(model.getWithdrawStatus());
            }

            @NonNull
            @Override
            public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row,parent,false);
                return new HistoryViewHolder(view);
            }
        };
        mHistoryList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    //static class remember
    public static class HistoryViewHolder extends RecyclerView.ViewHolder{

        TextView txtPhoneNumber, txtNumberProvider, txtWithdrawAmount, txtWithdrawStatus;

        View mView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setPhoneNumber(String withdrawNumber)
        {
            txtPhoneNumber = mView.findViewById(R.id.txtPhoneNumber);
            txtPhoneNumber.setText(withdrawNumber);
        }
        public void setNumberProvider(String numberProvider)
        {
            txtNumberProvider = mView.findViewById(R.id.txtNumberProvider);
            txtNumberProvider.setText(numberProvider);
        }

        public void setWithdrawAmount(Double withdrawAmount)
        {
            txtWithdrawAmount = mView.findViewById(R.id.txtWithdrawAmount);
            txtWithdrawAmount.setText(String.valueOf(withdrawAmount));
        }

        public void setWithdrawStatus(String withdrawStatus)
        {
            txtWithdrawStatus = mView.findViewById(R.id.txtWithdrawStatus);
            txtWithdrawStatus.setText("Status: "+withdrawStatus);
        }
    }
}
