package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    private Button updateButton ;
    private EditText username, status ;
    private CircleImageView profileImage ;

    private String currentUserID;
    private FirebaseAuth auth ;
    private DatabaseReference rootReference ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance() ;
        currentUserID = Objects.requireNonNull(auth.getCurrentUser()).getUid() ;
        rootReference = FirebaseDatabase.getInstance().getReference() ;

        InitializeFields() ;

        updateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UpdateSettings() ;
            }
        });

        RetrieveUserInformation() ;
    }

    private void RetrieveUserInformation()
    {
        rootReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild("Username"))
                {
                    String oldUsername = Objects.requireNonNull(dataSnapshot.child("Username").getValue()).toString() ;
                    String oldStatus = Objects.requireNonNull(dataSnapshot.child("Status").getValue()).toString() ;

                    username.setText(oldUsername);
                    status.setText(oldStatus);

                    String oldProfileImage ;
                    if(dataSnapshot.hasChild("ProfileImage"))
                    {
                        oldProfileImage = Objects.requireNonNull(dataSnapshot.child("ProfileImage").getValue()).toString();
                    }
                }
                else
                    Toast.makeText(ProfileActivity.this, "Please create your Profile", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        }) ;
    }

    private void UpdateSettings()
    {
        String newUsername = username.getText().toString() ;
        String newStatus = status.getText().toString() ;

        if(TextUtils.isEmpty(newUsername))
            Toast.makeText(ProfileActivity.this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();

        else if(TextUtils.isEmpty(newStatus))
            Toast.makeText(ProfileActivity.this, "Status cannot be empty!", Toast.LENGTH_SHORT).show();

        else
        {
            HashMap<String, String> profileMap = new HashMap<>() ;
            profileMap.put("Username",newUsername) ;
            profileMap.put("Status",newStatus) ;

            rootReference.child("Users").child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String message = Objects.requireNonNull(task.getException()).toString() ;
                        Toast.makeText(ProfileActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            }) ;

        }
    }

    private void InitializeFields()
    {
        updateButton = findViewById(R.id.update_button) ;
        username = findViewById(R.id.username) ;
        status = findViewById(R.id.status) ;
        profileImage = findViewById(R.id.profile_image) ;
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class) ;
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        startActivity(mainIntent) ;
        finish();
    }
}
