package com.example.techlap.booksharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookReviewActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase, mdatabaseUsers;
    private RatingBar ratingBar;
    private EditText mAddReview;
    private Button AddReviewButton;
    private String rate;
    private String user,userImage;
    private String Bookid;
    private Toolbar toolbar;


    //ID , Review , Rating Bar , bookID
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_review);

        toolbar =(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        setTitle("Review");

        mAddReview = (EditText) findViewById(R.id.Add_Review_view);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        AddReviewButton = (Button) findViewById(R.id.Add_Review_button);
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Reviews");
        mdatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        rate = "";
        Bookid =getIntent().getExtras().getString("Bookid");
        AddReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add review method
                AddReviewtext();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                rate = String.valueOf(rating);
            }
        });


    }

    private void AddReviewtext() {
        final String Reviewtext = mAddReview.getText().toString().trim();

        if (TextUtils.isEmpty(Reviewtext)) {
            Toast.makeText(BookReviewActivity.this, "please enter Review Text", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference newUser = mdatabaseUsers.child(mAuth.getCurrentUser().getUid());////mAuth.getCurrentUser().getUid());
            newUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user=dataSnapshot.child("name").getValue(String.class);
                    userImage=dataSnapshot.child("profileImage").getValue(String.class);
                    Review review=new Review(user,userImage,Bookid,Reviewtext,rate);
                    DatabaseReference newReview = mdatabase.push();////mAuth.getCurrentUser().getUid());
                    newReview.setValue(review);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //    public Review(String username, String bookId, String review, String rate) {

            Toast.makeText(BookReviewActivity.this, "Review has been added..", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(BookReviewActivity.this,MainActivity.class));
        }
    }
}

