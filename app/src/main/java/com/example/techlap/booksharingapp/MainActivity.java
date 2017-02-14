package com.example.techlap.booksharingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView booksList;
    private DatabaseReference mbooksdatabase,mdatabaseCart,mdatabaseUser, mreadlistdatabase;
    private FirebaseAuth mAuth;
    private Context ctx;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private Toolbar toolbar;
    private String uid;
    private UserModel user;
    private FirebaseRecyclerAdapter<Book,bookViewHoder> firebaseRecyclerAdapter;

    // get all products
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar =(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        setTitle("Bookish");

        mAuth=FirebaseAuth.getInstance();
        mAuthListner=new FirebaseAuth.AuthStateListener() {
            //triggered after Authacation
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(mAuth.getCurrentUser()==null){
                    //startActivity(new Intent(MainActivity.this,GoogleSignInActivity.class));
                    Intent intent =new Intent(MainActivity.this,LogInActivity.class);
                    Log.d("CHECK22","go To log");
                    //enable back
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        ctx=getApplicationContext();
        mbooksdatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mdatabaseUser= FirebaseDatabase.getInstance().getReference().child("Users");
        mreadlistdatabase =FirebaseDatabase.getInstance().getReference().child("Readlists");

        mbooksdatabase.keepSynced(true);
        mreadlistdatabase.keepSynced(true);
        mdatabaseUser.keepSynced(true);

        booksList =(RecyclerView)findViewById(R.id.productsList);
        booksList.setHasFixedSize(true);
        // to make the list Vertical
        booksList.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);

        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Book, bookViewHoder>(
                Book.class,//PoJo
                R.layout.book_design,//row view
                bookViewHoder.class,//Hoder class
                mbooksdatabase
        ) {
            @Override
            protected void populateViewHolder(bookViewHoder viewHolder, final Book model, final int position) {

                viewHolder.setBookName(model.getName());
                viewHolder.setProfilePic(getApplicationContext(), model.getUserImage());
                viewHolder.setCover(getApplicationContext(), model.getCover());
                viewHolder.setOwnerName(model.getUser());
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(MainActivity.this,BookDescriptionActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("Bookid",firebaseRecyclerAdapter.getRef(position).getKey());
                        i.putExtras(bundle);//Extras not Extra
                        startActivity(i);
                    }
                });
                viewHolder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(MainActivity.this,ProfileActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("Uid",model.getUserId());
                        i.putExtras(bundle);//Extras not Extra
                        startActivity(i);
                    }
                });


            }
        };
        booksList.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_addBook:
                Intent intent=new Intent(MainActivity.this,AddBookActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_signUp:
                mAuth.signOut();
                intent=new Intent(MainActivity.this,LogInActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_profile :
                Intent i=new Intent(MainActivity.this,ProfileActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("Uid",mAuth.getCurrentUser().getUid());
                i.putExtras(bundle);//Extras not Extra
                startActivity(i);
                return true;
            case R.id.action_readlist:
                Intent intent1=new Intent(MainActivity.this,ReadListActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
