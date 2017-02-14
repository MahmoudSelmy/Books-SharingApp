package com.example.techlap.booksharingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mdatabase,mproductsDB;
    private FirebaseAuth mAuth;
    //private Toolbar toolbar;
    private RecyclerView booksList;
    private UserModel user;
    private String Uid;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private ImageView pic;
    private Query productsQuery;
    private ArrayList<String> Ids;
    private TextView acPhone,acAddress,acEmail,acBirth;
    private FirebaseRecyclerAdapter<Book,bookViewHoder> firebaseRecyclerAdapter;
    private DatabaseReference mbooksdatabase,mdatabaseCart,mdatabaseUser, mreadlistdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        setTitle("Profile");
        mAuth=FirebaseAuth.getInstance();

        mbooksdatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mdatabaseUser= FirebaseDatabase.getInstance().getReference().child("Users");
        mreadlistdatabase =FirebaseDatabase.getInstance().getReference().child("Readlists");



        pic=(ImageView)findViewById(R.id.profile_id);
        Uid=getIntent().getExtras().getString("Uid");
        Ids=new ArrayList<String>();

        booksList =(RecyclerView)findViewById(R.id.productsList);
        booksList.setHasFixedSize(true);
        // to make the list Vertical
        booksList.setLayoutManager(new LinearLayoutManager(this));
        acPhone=(TextView)findViewById(R.id.accountPhone);
        acAddress=(TextView)findViewById(R.id.accountAddress);
        acEmail=(TextView)findViewById(R.id.accountEmail);
        acBirth=(TextView)findViewById(R.id.accountBirthDate);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // ToDo: don't use == use equals

        passData(Uid);
        productsQuery = mbooksdatabase.orderByChild("userId").equalTo(Uid);//finally done hohoooo
        productsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child:dataSnapshot.getChildren()){
                    String id=child.getKey();
                    Ids.add(id);
                    String name=child.child("name").getValue(String.class);
                    Log.d("CHECK22",id+name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        prepareRecyler(productsQuery);

        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this,R.color.colorPrimary));
        collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(this,R.color.colorPrimary));
        toolbarTextAppernce();

    }
    private void passData(String Uid) {
        mdatabaseUser.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(UserModel.class);
                collapsingToolbarLayout.setTitle(user.getName());
                Picasso.with(getApplicationContext()).load(user.getProfileImage()).networkPolicy(NetworkPolicy.OFFLINE).into(pic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        // if it not available online we need to download it
                        Picasso.with(getApplicationContext()).load(user.getProfileImage()).into(pic);
                    }
                });
                acPhone.setText(user.getPhone());
                acAddress.setText(user.getAddress());
                acEmail.setText(user.getEmail());
                acBirth.setText(user.getBirthday());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,"Error Fetching Your Data",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    private void prepareRecyler(Query query) {
        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Book, bookViewHoder>(
                Book.class,//PoJo
                R.layout.book_design,//row view
                bookViewHoder.class,//Hoder class
                productsQuery
        ) {
            @Override
            protected void populateViewHolder(bookViewHoder viewHolder, final Book model, final int position) {

                viewHolder.setBookName(model.getName());
                viewHolder.setProfilePic(getApplicationContext(),model.getUserImage());
                viewHolder.setCover(getApplicationContext(),model.getCover());
                viewHolder.setOwnerName(model.getUser());
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(ProfileActivity.this,BookDescriptionActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("Bookid",firebaseRecyclerAdapter.getRef(position).getKey());
                        i.putExtras(bundle);//Extras not Extra
                        startActivity(i);
                    }
                });
            }
        };
        booksList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class bookViewHoder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView username;
        ImageView imageView;
        public bookViewHoder(View itemView) {
            super(itemView);
            mView=itemView;
            username=(TextView)mView.findViewById(R.id.bookOwnerName);
        }
        public void setOwnerName(String name){
            username.setText(name);
        }
        public void setBookName(String name){
            TextView titleField=(TextView)mView.findViewById(R.id.bookName);
            titleField.setText(name);
        }

        public void setCover(final Context ctx, final String Url){
            imageView=(ImageView)mView.findViewById(R.id.bookCover);
            //Picasso.with(ctx).load(Url).into(imageView);

            //check if it available offline or not
            Picasso.with(ctx).load(Url).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError() {
                    // if it not available online we need to download it
                    Picasso.with(ctx).load(Url).into(imageView);
                }
            });
        }
        public void setProfilePic(final Context ctx, final String Url){
            final ImageView imageView=(ImageView)mView.findViewById(R.id.ownerImage);
            //Picasso.with(ctx).load(Url).into(imageView);

            //check if it available offline or not
            Picasso.with(ctx).load(Url).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError() {
                    // if it not available online we need to download it
                    Picasso.with(ctx).load(Url).into(imageView);
                }
            });
        }

    }

}
