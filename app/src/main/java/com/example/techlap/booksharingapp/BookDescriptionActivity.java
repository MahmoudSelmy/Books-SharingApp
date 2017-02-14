package com.example.techlap.booksharingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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

public class BookDescriptionActivity extends AppCompatActivity {

    private DatabaseReference mdatabase,mproductsDB;
    private FirebaseAuth mAuth;
    //private Toolbar toolbar;
    private RecyclerView booksList;
    private Book book;
    private String Bookid;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private ImageView pic;
    private Query reviewsQuery;
    private ArrayList<String> Ids;
    private TextView bookAuthor;
    private FirebaseRecyclerAdapter<Review,reviewViewHoder> firebaseRecyclerAdapter;
    private DatabaseReference mReviewsdatabase,mBooksdatabase,mreadlistdatabase;
    private Button downloadButton,ReviewButton,addreadlistbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);
        setContentView(R.layout.activity_book_description);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        setTitle("Book");
        mAuth=FirebaseAuth.getInstance();

        mReviewsdatabase = FirebaseDatabase.getInstance().getReference().child("Reviews");
        mBooksdatabase=FirebaseDatabase.getInstance().getReference().child("Books");
        mreadlistdatabase =FirebaseDatabase.getInstance().getReference().child("Readlists");

        mReviewsdatabase.keepSynced(true);

        pic=(ImageView)findViewById(R.id.profile_id);
        Bookid =getIntent().getExtras().getString("Bookid");
        Ids=new ArrayList<String>();

        booksList =(RecyclerView)findViewById(R.id.productsList);
        booksList.setHasFixedSize(true);
        // to make the list Vertical
        booksList.setLayoutManager(new LinearLayoutManager(this));
        bookAuthor =(TextView)findViewById(R.id.bookAuthor);
        downloadButton=(Button)findViewById(R.id.downloadBut);
        ReviewButton=(Button)findViewById(R.id.makeReviewBut);
        addreadlistbutton=(Button)findViewById(R.id.addReadlistBut);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // ToDo: don't use == use equals

        passData(Bookid);
        mBooksdatabase.keepSynced(true);
        reviewsQuery = mBooksdatabase.orderByChild("bookId").equalTo(Bookid);//finally done hohoooo
        reviewsQuery.keepSynced(true);

        prepareRecyler(reviewsQuery);

        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this,R.color.colorPrimary));
        collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(this,R.color.colorPrimary));
        toolbarTextAppernce();

    }
    private void passData(final String BookId) {
        mBooksdatabase.child(BookId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                book =dataSnapshot.getValue(Book.class);
                collapsingToolbarLayout.setTitle(book.getName());
                Picasso.with(getApplicationContext()).load(book.getCover()).networkPolicy(NetworkPolicy.OFFLINE).into(pic, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        // if it not available online we need to download it
                        Picasso.with(getApplicationContext()).load(book.getCover()).into(pic);
                    }
                });

                bookAuthor.setText(book.getAuthor());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BookDescriptionActivity.this,"Error Fetching Your Data",Toast.LENGTH_SHORT).show();
            }
        });
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(book.getPdf()) );
                startActivity( browse );
            }
        });
        ReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(BookDescriptionActivity.this,BookReviewActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("Bookid",BookId);
                i.putExtras(bundle);//Extras not Extra
                startActivity(i);
            }
        });
        addreadlistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item=new Item(book.getName(),BookId,book.getCover());
                DatabaseReference myReadlist=mreadlistdatabase.child(mAuth.getCurrentUser().getUid());
                myReadlist.push().setValue(item);
                Toast.makeText(BookDescriptionActivity.this,"Added To Your Readlist",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }

    private void prepareRecyler(Query query) {
        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Review, reviewViewHoder>(
                Review.class,//PoJo
                R.layout.review_design,//row view
                reviewViewHoder.class,//Hoder class
                mReviewsdatabase
        ) {
            @Override
            protected void populateViewHolder(reviewViewHoder viewHolder, final Review model, final int position) {

                viewHolder.setRate(model.getRate());
                viewHolder.setUserImage(getApplicationContext(),model.getUserImage());
                viewHolder.setUserName(model.getUsername());
                viewHolder.setReview(model.getReview());

            }
        };
        booksList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class reviewViewHoder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView username;
        ImageView imageView;
        public reviewViewHoder(View itemView) {
            super(itemView);
            mView=itemView;
            username=(TextView)mView.findViewById(R.id.userName);
        }
        public void setUserName(String name){
            username.setText(name);
        }
        public void setRate(String name){
            TextView titleField=(TextView)mView.findViewById(R.id.userRate);
            titleField.setText(name);
        }
        public void setReview(String name){
            TextView titleField=(TextView)mView.findViewById(R.id.userReview);
            titleField.setText(name);
        }
        public void setUserImage(final Context ctx, final String Url){
            final ImageView imageView=(ImageView)mView.findViewById(R.id.userImage);
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
