package com.example.techlap.booksharingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailF,nameF,passwordF;
    private Button regbtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private ProgressDialog mprogress;
    private DatabaseReference mdatabase;

    // 3 -if user clicks "Sign Up" > take data from fields & check it  > logIn with the given Account > goto the SetupActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth=FirebaseAuth.getInstance();
        mAuthListner=new FirebaseAuth.AuthStateListener() {
            //triggered after Authacation
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(mAuth.getCurrentUser()!=null){
                    //startActivity(new Intent(RegisterActivity.this,SetUpActivity.class));

                    //startActivity(new Intent(MainActivity.this,GoogleSignInActivity.class));
                    Intent intent =new Intent(SignUpActivity.this,SetUpActivity.class);
                    //enable back
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        mprogress=new ProgressDialog(this);

        emailF=(EditText)findViewById(R.id.emailField);
        nameF=(EditText)findViewById(R.id.etName);
        passwordF=(EditText)findViewById(R.id.passwordField);
        regbtn=(Button)findViewById(R.id.regBtn);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);
    }
    private void signUp() {
        final String name=nameF.getText().toString().trim();
        final String email=emailF.getText().toString().trim();
        String password=passwordF.getText().toString().trim();

        //TODO: add more conditions here
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){

            mprogress.setMessage("Singing Up..");
            mprogress.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        String userId=mAuth.getCurrentUser().getUid();
                        DatabaseReference newUser=mdatabase.child(userId);
                        //public UserModel(String profileimage, String name, String email, String commitee, String birthDate, String position)
                        UserModel user=new UserModel("",name,email,"","","","");//the last personal info
                        newUser.setValue(user);
                        mprogress.dismiss();
                    }

                }
            });

        }
    }
}
