package com.example.techlap.booksharingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class SetUpActivity extends AppCompatActivity implements View.OnClickListener{
    // private FirebaseAuth.AuthStateListener mAuthListner;
    // private ProgressDialog mProgress;
    private DatabaseReference mdatabase;
    private Uri imgUri;
    // private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private ImageView imgView ;
    private Button btnDone ;
    private Button btnAddImg ;
    private EditText phoneView ;
    private EditText addressView ;
    private Spinner spinner1, spinner2, spinner3;
    public static final int GALLERY_REQUEST = 1;
    private String Uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);


        imgView = (ImageView) findViewById(R.id.imgView);
        btnDone = (Button) findViewById(R.id.btnDone);
        btnAddImg = (Button) findViewById(R.id.btnAddImg);
        phoneView = (EditText) findViewById(R.id.phoneView);
        addressView = (EditText) findViewById(R.id.addressView);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);

        assert btnDone != null;
        btnDone.setOnClickListener(this);

        assert btnAddImg != null;
        btnAddImg.setOnClickListener(this);
        mAuth=FirebaseAuth.getInstance();
        Uid=mAuth.getCurrentUser().getUid();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        addItemsOnSpinner();
    }

    public void addItemsOnSpinner() {
        //months
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        list.add("January");
        list.add("February");
        list.add("March");
        list.add("April");
        list.add("May");
        list.add("June");
        list.add("July");
        list.add("August");
        list.add("September");
        list.add("October");
        list.add("November");
        list.add("December");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);

        //days
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        List<Integer> list1 = new ArrayList<Integer>();
        for(Integer i = 1 ; i< 32 ; i++){
            list1.add(i);
        }
        ArrayAdapter<Integer> dataAdapter1 = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_item, list1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter1);


        //years
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        List <Integer> list2= new ArrayList<Integer>();

        for(Integer i = 1950 ; i< 2016 ; i++){
            list2.add(i);
        }

        ArrayAdapter<Integer> dataAdapter2 = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, list2);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(dataAdapter2);
    }

    void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== GALLERY_REQUEST && resultCode==RESULT_OK){
            imgUri = data.getData();
            imgView.setImageURI(imgUri);
            btnAddImg.setHint("");
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btnAddImg:
                pickImage();
                break;

            case R.id.btnDone:
                final String phone = phoneView.getText().toString();
                final String address = addressView.getText().toString();
                final String birthday = String.valueOf(spinner1.getSelectedItem()) + "-" + String.valueOf(spinner2.getSelectedItem()) + "-" + String.valueOf(spinner3.getSelectedItem());
                if(imgUri != null && !phone.isEmpty() && !address.isEmpty())
                {
                    StorageReference ImgRef = FirebaseStorage.getInstance().getReference().child("UsersImages").child(Uid);
                    ImgRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            DatabaseReference newUser = mdatabase.child(Uid);////mAuth.getCurrentUser().getUid());
                            newUser.child("profileImage").setValue(downloadUrl.toString());//downloadUrl.toString());
                            newUser.child("phone").setValue(phone);
                            newUser.child("address").setValue(address);
                            newUser.child("birthday").setValue(birthday);
                            Toast.makeText(SetUpActivity.this,"Have fun .. " , Toast.LENGTH_SHORT).show();
                            startActivity(new Intent (SetUpActivity.this,MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SetUpActivity.this,"Error!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
            default:
                break;
        }
    }
}
