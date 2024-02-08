package com.example.convos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    Button button;
    EditText email,password;
    FirebaseAuth auth;
    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth=FirebaseAuth.getInstance();
        button=findViewById(R.id.btnLogin);
        email=findViewById(R.id.etEmail);
        password=findViewById(R.id.etPassword);
        String epattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        signUp=findViewById(R.id.txtSignUp);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email=email.getText().toString();
                String pass=password.getText().toString();

                if((TextUtils.isEmpty(Email))){
                    Toast.makeText(LoginActivity.this,"Enter the Email",Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(pass)){
                    Toast.makeText(LoginActivity.this,"Enter the Password",Toast.LENGTH_SHORT).show();
                }
                else if(!Email.matches(epattern)){
                    email.setError("Give Proper Email Address");
                }
                else if(pass.length()<8){
                    password.setError("Enter Password longer than 8 character");
                }
                else{
                    auth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                try{
                                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }catch (Exception e){
                                    Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });

    }
}