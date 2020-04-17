package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisitProfileActivity extends AppCompatActivity
{
    private String userID ;

    private CircleImageView visitprofileImage ;
    private TextView visitusername, visitstatus ;
    private Button sendRequestButton ;

    private DatabaseReference usersReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_profile);

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users") ;

        userID = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("UserID")).toString() ;

        InitializeFields() ;

        RetrieveUserInfo() ;

    }

    private void RetrieveUserInfo()
    {
        usersReference.child(userID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String username = Objects.requireNonNull(dataSnapshot.child("Username").getValue()).toString() ;
                    String status = Objects.requireNonNull(dataSnapshot.child("Status").getValue()).toString() ;

                    if(dataSnapshot.hasChild("ProfileImage"))
                    {
                        String profileImage = Objects.requireNonNull(dataSnapshot.child("ProfileImage").getValue()).toString() ;
                        Picasso.get().load()
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        }) ;
    }

    private void InitializeFields()
    {
        visitprofileImage = findViewById(R.id.visit_profile_image) ;
        visitusername = findViewById(R.id.visit_profile_username) ;
        visitstatus = findViewById(R.id.visit_profile_status) ;
        sendRequestButton = findViewById(R.id.send_request_button) ;
    }
}
