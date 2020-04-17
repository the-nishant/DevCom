package com.example.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity
{

    private Button registerButton ;
    private EditText registerEmail, registerPassword ;
    private TextView loginPageLink ;

    private FirebaseAuth auth ;
    private DatabaseReference rootReference ;

    private ProgressDialog loadingBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance() ;
        rootReference = FirebaseDatabase.getInstance().getReference() ;

        InitializeFields() ;

        loginPageLink.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                SendUserToLoginActivity();
            }
        }) ;

        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Register() ;
            }
        });


    }

    private void Register()
    {
        String email = registerEmail.getText().toString() ;
        String password = registerPassword.getText().toString() ;

        if(TextUtils.isEmpty(email))
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show() ;

        else if(TextUtils.isEmpty(password))
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show() ;

        else
        {
            loadingBar.setTitle("Creating account");
            loadingBar.setMessage("Please wait while we create your account ...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show() ;

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid() ;
                        rootReference.child("Users").child(currentUserId).setValue("") ;

                        SendUserToMainActivity();
                        Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss() ;
                    }

                    else
                    {
                        String message = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(RegisterActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            }) ;
        }
    }

    private void InitializeFields()
    {
        registerButton = findViewById(R.id.register_button) ;
        registerEmail = findViewById(R.id.register_email) ;
        registerPassword = findViewById(R.id.register_password) ;
        loginPageLink = findViewById(R.id.login_page_link) ;
        loadingBar = new ProgressDialog(this) ;
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class) ;
        startActivity(loginIntent) ;
        finish();
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class) ;
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        startActivity(mainIntent) ;
        finish() ;
    }
}
