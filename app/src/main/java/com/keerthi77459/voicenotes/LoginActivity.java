package com.keerthi77459.voicenotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText userEmail,userPassword,userConfirmPassword;
    ProgressBar loadingBar;
    Button createAccount;
    TextView loginActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        userConfirmPassword = findViewById(R.id.userConfirmPassword);
        createAccount = findViewById(R.id.createAccount);
        loginActivityButton = findViewById(R.id.loginActivityButton);
        loadingBar = findViewById(R.id.loadingBar);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });

        loginActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,LoginActivity2.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void createNewAccount() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String confirmPassword = userConfirmPassword.getText().toString();

        Boolean validate = checkData(email,password,confirmPassword);
        if(validate){
            changeProgress(true);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(LoginActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                changeProgress(false);
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this,"Created",Toast.LENGTH_SHORT).show();
                                    firebaseAuth.getCurrentUser().sendEmailVerification();
                                    firebaseAuth.signOut();
                                } else {
                                    Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                }
                            }
                    });
        }
    }

    private boolean checkData(String email,String password,String conPassword){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            userEmail.setError("Proper Mail Needed");
            return false;
        }
        if((password.length() <6)){
            userPassword.setError("Password Must Greater than 6 Character");
            return false;
        }
        if(conPassword == password) {
            userConfirmPassword.setError("Password doesn't matches");
            return false;
        }
        return true;
    }

    private void changeProgress(boolean progress){
        if(progress){
            loadingBar.setVisibility(View.VISIBLE);
            createAccount.setVisibility(View.GONE);
        } else {
            loadingBar.setVisibility(View.GONE);
            createAccount.setVisibility(View.VISIBLE);
        }
    }
}













