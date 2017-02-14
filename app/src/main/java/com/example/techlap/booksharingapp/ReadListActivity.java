package com.example.techlap.booksharingapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ReadListActivity extends AppCompatActivity {
    private RecyclerView booksList;
    private DatabaseReference mdatabase,mdatabaseOrder,mdatabaseUser;
    private FirebaseAuth mAuth;
    private Context ctx;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private Toolbar toolbar;
    private String Commitee = "";
    private Query commiteeMembersQuery, membermail;
    private String id;
    private UserModel user;
    private String uid;
    private FirebaseRecyclerAdapter<Item, itemViewHolder> firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_list);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        setTitle("My Readlist");

        ctx = getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Readlists");
        mdatabase.keepSynced(true);

        //id=mAuth.getCurrentUser().getUid();
        booksList = (RecyclerView) findViewById(R.id.recyclerview);
        booksList.setHasFixedSize(true);
        // to make the list Vertical
        booksList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        id = mAuth.getCurrentUser().getUid();
        DatabaseReference myReadlist = mdatabase.child(mAuth.getCurrentUser().getUid());
        myReadlist.keepSynced(true);
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Item, itemViewHolder>(
                Item.class,//PoJo
                R.layout.readlist_row,//row view
                itemViewHolder.class,//Hoder class
                myReadlist
        ) {
            @Override
            protected void populateViewHolder(itemViewHolder viewHolder, final Item model, final int position) {

                viewHolder.setName(model.getName());
                viewHolder.setCover(getApplicationContext(),model.getCover());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ReadListActivity.this, "To His Profile", Toast.LENGTH_SHORT).show();
                        // TO DO: POP UP Window
                        //TODO:go to BookDiscriptionActivity
                    }
                });
            }
        };
        booksList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class itemViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        public itemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String memberName) {
            TextView titleField = (TextView) mView.findViewById(R.id.itemName);
            titleField.setText(memberName);
        }

        public void setCover(final Context ctx, final String Url){
            final ImageView imageView=(ImageView)mView.findViewById(R.id.itemCover);
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
