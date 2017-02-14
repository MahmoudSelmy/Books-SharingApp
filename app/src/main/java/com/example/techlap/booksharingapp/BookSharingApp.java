package com.example.techlap.booksharingapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by tech lap on 18/12/2016.
 */
public class BookSharingApp  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //firebase only store stringd in your cashe
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // so we need the offline cappabilities of picaso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        //indecate where he get the image from cash,online
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        //initialize and create the image loader logic


    }
}
