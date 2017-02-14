package com.example.techlap.booksharingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class LogInActivity extends AppCompatActivity {

    private EditText mEmailText;
    private  EditText mPasswordText;
    private Button mLoginButton;
    private TextView mSignUpText;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mProgressDialog=new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            //go to books

            finish();
            //go to books

            // startActivity(new Intent(this,BooksActivity.class));
            // TODO:startActivity(new Intent(this,BooksActivity.class));
        }

        mEmailText=(EditText)findViewById(R.id.email_edit_text);
        mPasswordText=(EditText)findViewById(R.id.password_edit_text);
        mLoginButton=(Button)findViewById(R.id.signin_button);
        mSignUpText=(TextView)findViewById(R.id.signup_text_view);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();

            }
        });
        mSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(LoginActivity.this,"move to register",Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
                //TODO:startActivity(new Intent(getApplicationContext(),RegisterationActivity.class));


            }
        });
    }
    private void userLogin(){
        String email=mEmailText.getText().toString().trim();
        String password =mPasswordText.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(LogInActivity.this,"please enter email",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(LogInActivity.this,"please enter password",Toast.LENGTH_SHORT).show();
            return;
        }
        else if( TextUtils.isEmpty(email)&&TextUtils.isEmpty(password)  ){
            Toast.makeText(LogInActivity.this,"please enter your login information",Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog.setMessage("Login..");
        mProgressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){

                    @Override
                    public void  onComplete(@NonNull Task<AuthResult> task){

                        mProgressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(LogInActivity.this,"login Sucessful",Toast.LENGTH_SHORT).show();
                            //go to books
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }
                        else{
                            // Toast.makeText(LoginActivity.this,"login Failed",Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }


                });
    }
}
