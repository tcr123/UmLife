package com.example.umlife;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.model.UploadEvent;
import com.example.model.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CreateOrEditEventActivity extends AppCompatActivity {

    //local image
    private ImageView eventImage;
    private static final int PICK_IMAGE_REQUEST = 1;

    // need to install imageUrl;
    private Uri mImageUri;

    //Storage database
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseFirestore mFirebaseRef;


    //editText from XML to receive string type data
    EditText eventName;
    EditText eventDate;
    EditText eventVenue;
    EditText openRegistration;
    EditText endRegistration;
    EditText eventDetail;
    EditText organiserEmail;

    //button
    Button publish;

    //UserInfo
    UserInfo userInfo = new UserInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_edit_event);

        //Get userInfo package
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            System.out.println(e);
        }
        //Storage database References
        mStorageRef = FirebaseStorage.getInstance().getReference("events");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("events");
        mFirebaseRef = FirebaseFirestore.getInstance();


        //Image
        eventImage = findViewById(R.id.eventImage);

        //Button
        publish = findViewById(R.id.button);

        eventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //What happen after "publish" was clicked
                //Toast.makeText(CreateOrEditEventActivity.this, mImageUri.toString(),Toast.LENGTH_LONG).show();
                //Text Retrieval
//                String eventName_ = eventName.getText().toString();
//                String openRegistration_ = openRegistration.getText().toString();
//                String endRegistration_ = endRegistration.getText().toString();
//                String eventDetail_ = eventDetail.getText().toString();
//                String organiserEmail_ = organiserEmail.getText().toString();

                //mStorageRef = FirebaseStorage.getInstance().getReference("Events");
                UploadEvent();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(eventImage);
            //Testing
            Toast.makeText(CreateOrEditEventActivity.this, mImageUri.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void UploadEvent(){
        if(mImageUri != null){
            //Testing to upload with .toString()
            String wantedUri = System.currentTimeMillis() + "." + getFileExtension(mImageUri);
            StorageReference fileReference = mStorageRef.child(wantedUri);

            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    eventName = findViewById(R.id.eventName);
                    eventDate = findViewById(R.id.eventDate);
                    eventVenue = findViewById(R.id.eventVenue);
                    eventDetail = findViewById(R.id.eventDetail);
                    openRegistration = findViewById(R.id.openRegistration);
                    endRegistration = findViewById(R.id.endRegistration);
                    organiserEmail = findViewById(R.id.organiserEmail);


                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UploadEvent uploadEvent = new UploadEvent(uri.toString(), eventName.getText().toString(),
                                    openRegistration.getText().toString(), endRegistration.getText().toString(), eventDetail.getText().toString(),
                                    organiserEmail.getText().toString(), userInfo.getUuid(), eventDate.getText().toString(), eventVenue.getText().toString());

                            //Firebase storing by upload file to "events" collection
                            mFirebaseRef.collection("events").add(uploadEvent).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //What to do after putting file to Firebase
                                    Toast.makeText(CreateOrEditEventActivity.this, "Upload successfully", Toast.LENGTH_LONG).show();
                                    DirectUser(CreateOrEditEventActivity.this, HomePageActivity.class);
                                }
                            });
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    Log.i("UploadImage", "Image uploaded: " + progress);
                }
            });
        }else{
            Toast.makeText(this, "Upload Failed!",Toast.LENGTH_LONG).show();
        }
    }

    private void DirectUser(android.content.Context currentPage, Class<?> nextPage) {
        Intent intent = new Intent(currentPage, nextPage);
        intent.putExtra("userInfo", userInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}