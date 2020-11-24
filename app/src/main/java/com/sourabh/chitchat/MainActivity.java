package com.sourabh.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText edtUserName, edtEmail, edtPassword;
    private Button btnSubmit;
    private TextView txtLoginInfo;

    private boolean isSigningUp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUserName=findViewById(R.id.edtUserName);
        edtEmail=findViewById(R.id.edtEmail);
        edtPassword=findViewById(R.id.edtPassword);

        btnSubmit=findViewById(R.id.btnSubmit);
        txtLoginInfo=findViewById(R.id.txtLoginInfo);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this,FriendsActivity.class));
            finish();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()){
                    if(isSigningUp && edtUserName.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Invalid Input!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(isSigningUp){
                    handleSignUp();
                }
                else {
                    handleLogin();
                }
            }
        });

        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSigningUp) {
                    isSigningUp = false;
                    edtUserName.setVisibility(View.GONE);
                    btnSubmit.setText("Login");
                    txtLoginInfo.setText("Don't have an account? Sign Up.");
                }else {
                    isSigningUp=true;
                    edtUserName.setVisibility(View.VISIBLE);
                    btnSubmit.setText("SignUp");
                    txtLoginInfo.setText("Already have an account? Login here.");
                }
            }
        });
    }
    private void handleSignUp(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtUserName.getText().toString(),edtEmail.getText().toString(),""));
                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));
                    Toast.makeText(MainActivity.this, "Account Created Successfully!!!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void handleLogin(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this,FriendsActivity.class));
                    Toast.makeText(MainActivity.this, "Logged in Successfully!!!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}