package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity
{
    private Button loginButton, phoneLoginButton ;
    private EditText loginEmail, loginPassword ;
    private TextView forgetPasswordLink, registerPageLink ;

    private FirebaseAuth auth ;

    private ProgressDialog loadingBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance() ;

        InitializeFields() ;

        registerPageLink.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                SendUserToRegisterActivity();
            }
        }) ;

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AllowUserToLogin() ;
            }
        });
    }

    private void AllowUserToLogin()
    {
        String email = loginEmail.getText().toString() ;
        String password = loginPassword.getText().toString() ;

        if(TextUtils.isEmpty(email))
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show() ;

        else if(TextUtils.isEmpty(password))
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show() ;

        else
        {
            loadingBar.setTitle("Logging in");
            loadingBar.setMessage("Please wait while we log you in ...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show() ;

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {


                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "You are now logged in", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }

                    else
                    {
                        String message = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void InitializeFields()
    {
        loginButton = findViewById(R.id.login_button) ;
        phoneLoginButton = findViewById(R.id.phone_login_button) ;
        loginEmail = findViewById(R.id.login_email) ;
        loginPassword = findViewById(R.id.login_password) ;
        forgetPasswordLink = findViewById(R.id.forgot_password_link) ;
        registerPageLink = findViewById(R.id.register_page_link) ;
        loadingBar = new ProgressDialog(this) ;
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class) ;
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
        startActivity(mainIntent) ;
        finish() ;
    }

    private void SendUserToRegisterActivity()
    {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class) ;
        startActivity(registerIntent) ;
        finish();
    }
}
