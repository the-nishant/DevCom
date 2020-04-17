package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public class GroupChatActivity extends AppCompatActivity
{
    private Toolbar toolbar ;
    private ImageButton sendMessage ;
    private EditText newMessage ;
    private ScrollView scrollView ;
    private TextView messages ;

    private FirebaseAuth auth ;
    private DatabaseReference usersReference, currentGroupReference, messageKeyReference ;

    private String currentGroupname, currentUserID, currentUsername, currentDate, currentTime  ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupname = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("Groupname")).toString() ;

        auth = FirebaseAuth.getInstance() ;
        currentUserID = Objects.requireNonNull(auth.getCurrentUser()).getUid() ;
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users") ;
        currentGroupReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupname) ;

        InitializeFields() ;

        GetUserInfo() ;

        sendMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SaveMessageToDatabase() ;

                newMessage.setText(null);

                scrollView.fullScroll(ScrollView.FOCUS_DOWN) ;
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        currentGroupReference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                    DisplayMessages(dataSnapshot) ;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                    DisplayMessages(dataSnapshot) ;
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        }) ;
    }

    private void DisplayMessages(DataSnapshot dataSnapshot)
    {
        Iterator iterator = dataSnapshot.getChildren().iterator() ;

        while(iterator.hasNext())
        {
            String chatDate = Objects.requireNonNull(((DataSnapshot) iterator.next()).getValue()).toString() ;
            String chatMessage = Objects.requireNonNull(((DataSnapshot) iterator.next()).getValue()).toString() ;
            String chatTime = Objects.requireNonNull(((DataSnapshot) iterator.next()).getValue()).toString() ;
            String chatUsername = Objects.requireNonNull(((DataSnapshot) iterator.next()).getValue()).toString() ;

            messages.append(chatUsername + ":\n" + chatMessage + "\n" + chatTime + "  "  + chatDate+ "\n\n\n");

            scrollView.fullScroll(ScrollView.FOCUS_DOWN) ;
        }
    }

    private void SaveMessageToDatabase()
    {
        String message = newMessage.getText().toString() ;

        if(TextUtils.isEmpty(message))
            Toast.makeText(this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
        else
        {
            Calendar calendar = Calendar.getInstance() ;
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy") ;
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a") ;
            currentDate = currentDateFormat.format(calendar.getTime()) ;
            currentTime = currentTimeFormat.format(calendar.getTime()) ;

            String messageKey = currentGroupReference.push().getKey() ;
            assert messageKey != null;
            messageKeyReference = currentGroupReference.child(messageKey) ;

            HashMap<String, Object> messageInfoMap = new HashMap<>() ;
            messageInfoMap.put("Username", currentUsername) ;
            messageInfoMap.put("Message", message) ;
            messageInfoMap.put("Date", currentDate) ;
            messageInfoMap.put("Time", currentTime) ;

            messageKeyReference.updateChildren(messageInfoMap) ;


        }

    }

    private void GetUserInfo()
    {
        usersReference.child(currentUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                    currentUsername = Objects.requireNonNull(dataSnapshot.child("Username").getValue()).toString() ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        }) ;
    }

    private void InitializeFields()
    {
        toolbar = findViewById(R.id.group_chat_bar) ;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(currentGroupname);

        sendMessage = findViewById(R.id.group_chat_send_message) ;
        newMessage = findViewById(R.id.group_chat_new_message) ;
        messages = findViewById(R.id.group_chat_messages) ;
        scrollView = findViewById(R.id.scroll_view) ;
    }
}
