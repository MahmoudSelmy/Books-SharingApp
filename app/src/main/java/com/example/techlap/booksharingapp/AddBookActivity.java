package com.example.techlap.booksharingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddBookActivity extends AppCompatActivity {

    private DatabaseReference mdatabase,muserDB;
    private FirebaseAuth mAuth;
    private StorageReference fireStorage;
    private EditText bookname, authorname, bookdescription;
    private Button BrowseforPdf;
    private Button BrowseforCover;
    private Button AddBook;
    private Uri imgUri=null;
    private Uri pdfUri=null;
    final int GALLERY_REQUEST=1;
    final int FILE_REQUEST=2;
    public Uri downloadcoverUrl;
    private String userId;
    private Toolbar toolbar;
    private ProgressDialog mprogress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        toolbar =(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        setTitle("Add Book");
        mprogress=new ProgressDialog(this);

        bookname =(EditText)findViewById(R.id.bookname);
        authorname =(EditText)findViewById(R.id.authorname);
        bookdescription=(EditText)findViewById(R.id.bookdescription);
        BrowseforCover=(Button)findViewById(R.id.BrowseforCover);
        BrowseforPdf=(Button)findViewById(R.id.BrowseforPdf);
        AddBook=(Button)findViewById(R.id.AddBook);
        fireStorage= FirebaseStorage.getInstance().getReference();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Books");
        muserDB= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();



        BrowseforCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
            }
        });

        BrowseforPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent,FILE_REQUEST);
            }
        });


        AddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Adding();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST&&resultCode==RESULT_OK){
            imgUri=data.getData();
        }
        if(requestCode==FILE_REQUEST&&resultCode==RESULT_OK){
            pdfUri=data.getData();
        }
    }

    private void Adding() {
        final String BookName= bookname.getText().toString().trim();
        final String AuthorName= authorname.getText().toString().trim();
        final String BookDescription= bookdescription.getText().toString().trim();

        if(!TextUtils.isEmpty(BookName)&& !TextUtils.isEmpty(AuthorName)&&!TextUtils.isEmpty(BookDescription)&&imgUri!=null){

            StorageReference CoverRef=fireStorage.child("CoverImages").child(imgUri.getLastPathSegment());
            mprogress.setMessage("Singing Up..");
            mprogress.show();

            CoverRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadcoverUrl = taskSnapshot.getDownloadUrl();
                    final String downloadcoverUrlString = downloadcoverUrl.toString();
                    userId = mAuth.getCurrentUser().getUid();

                    StorageReference PdfRef = fireStorage.child("Pdf_files").child(userId).child(pdfUri.getLastPathSegment());
                    PdfRef.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadpdfUrl = taskSnapshot.getDownloadUrl();
                            String downloadpdfUrlString = downloadcoverUrl.toString();


                            final DatabaseReference newBook = mdatabase.push();
                            newBook.child("name").setValue(BookName);
                            newBook.child("author").setValue(AuthorName);
                            newBook.child("desc").setValue(BookDescription);
                            newBook.child("pdf").setValue(downloadpdfUrlString);
                            newBook.child("cover").setValue(downloadcoverUrlString);
                            newBook.child("userId").setValue(userId);
                            //    public Book(, String user,,,,, String userImage,)

                            muserDB.child(userId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    newBook.child("user").setValue(dataSnapshot.getValue(UserModel.class).getName());
                                    newBook.child("userImage").setValue(dataSnapshot.getValue(UserModel.class).getProfileImage());
                                    
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            newBook.child("cover").setValue(downloadcoverUrl.toString());
                            newBook.child("pdf").setValue(downloadpdfUrl.toString());
                            mprogress.dismiss();
                            Toast.makeText(AddBookActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddBookActivity.this, MainActivity.class));
                        }
                    });
                }
            });
        }
    }


}
