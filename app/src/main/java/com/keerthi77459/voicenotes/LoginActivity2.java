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

public class LoginActivity2 extends AppCompatActivity {

    EditText loginMail,loginPassWord;
    ProgressBar loadingBar1;
    Button loginAccount;
    TextView createAccountActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        loginMail = findViewById(R.id.loginEmail);
        loginPassWord = findViewById(R.id.loginPassword);
        loadingBar1 = findViewById(R.id.loadingBar1);
        loginAccount = findViewById(R.id.loginAccount);
        createAccountActivityButton = findViewById(R.id.createAccountActivityButton);

        loginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginNewAccount();
            }
        });

        createAccountActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity2.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginNewAccount() {
        String email = loginMail.getText().toString();
        String password = loginPassWord.getText().toString();

        boolean validate = checkData(email,password);

        if(validate){
            changeProgress(true);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(LoginActivity2.this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            changeProgress(false);
                            if(task.isSuccessful()){
                                if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                    Intent intent = new Intent(LoginActivity2.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else{
                                    Toast.makeText(LoginActivity2.this,"Verify your Mail",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity2.this,"Failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    private boolean checkData(String email,String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginMail.setError("Proper Mail Needed");
            return false;
        }
        if((password.length() <6)){
            loginPassWord.setError("Password Greater than 6 Character");
            return false;
        }
        return true;
    }

    private void changeProgress(boolean progress){
        if(progress){
            loadingBar1.setVisibility(View.VISIBLE);
            loginAccount.setVisibility(View.GONE);
        } else {
            loadingBar1.setVisibility(View.GONE);
            loginAccount.setVisibility(View.VISIBLE);
        }
    }
}