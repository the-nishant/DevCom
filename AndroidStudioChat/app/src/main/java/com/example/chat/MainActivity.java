package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    private Toolbar toolbar ;
    private ViewPager viewPager ;
    private TabLayout tabLayout ;
    private TabsAccessorAdapter tabsAccessorAdapter ;

    private FirebaseUser currentUser ;
    private FirebaseAuth auth ;
    private DatabaseReference rootReference ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance() ;
        currentUser = auth.getCurrentUser() ;
        rootReference = FirebaseDatabase.getInstance().getReference() ;

        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Chat");

        viewPager = findViewById(R.id.main_tabs_pager) ;
        tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager(),0) ;
        viewPager.setAdapter(tabsAccessorAdapter) ;

        tabLayout = findViewById(R.id.main_tabs) ;
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(currentUser==null)
            SendUserToLoginActivity() ;

        else
            VerifyUserExistence() ;
    }

    private void VerifyUserExistence()
    {
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid() ;
        rootReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!(dataSnapshot.hasChild("Username")))
                {
                    SendUserToProfileActivity();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        }) ;

    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class) ;
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        startActivity(loginIntent) ;
        finish() ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.main_find_people_option:
                SendUserToFindPeopleActivity() ;
                return true ;

            case R.id.main_create_group_option:
                RequestNewGroup() ;
                return true ;

            case R.id.main_profile_option:
                SendUserToProfileActivity();
                return true ;

            case R.id.main_logout_option:
                auth.signOut();
                SendUserToLoginActivity();
                return true;

            default:
                return  super.onOptionsItemSelected(item);
        }
    }

    private void SendUserToFindPeopleActivity()
    {
        Intent findPeopleIntent = new Intent(MainActivity.this, FindPeopleActivity.class) ;
        startActivity(findPeopleIntent) ;
    }

    private void RequestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog) ;
        builder.setTitle("Enter name of the group") ;

        final EditText groupNameField = new EditText(MainActivity.this) ;
        groupNameField.setHint("Groupname");
        builder.setView(groupNameField) ;

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String groupname = groupNameField.getText().toString() ;

                if(TextUtils.isEmpty(groupname))
                    Toast.makeText(MainActivity.this, "Groupname cannot be empty", Toast.LENGTH_SHORT).show();

                else
                {
                    CreateNewGroup(groupname) ;
                }

            }
        }) ;

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        }) ;

        builder.show() ;
    }

    private void CreateNewGroup(String groupname)
    {
        rootReference.child("Groups").child(groupname).setValue("").addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                    Toast.makeText(MainActivity.this, "Group created successfully", Toast.LENGTH_SHORT).show();
            }
        }) ;
    }

    private void SendUserToProfileActivity()
    {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class) ;
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        startActivity(profileIntent) ;
    }
}
