package c.com.ecomerceuser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class UploadImage extends AppCompatActivity {

    Button next;
    Button upload;
    ImageView uploadImage;
    private Uri filePath;
    FirebaseAuth mAuth;
    Uri mresultUri=null;

    EditText name,phone;

    private static final int PICK_IMAGE_REQUEST = 234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        next = (Button) findViewById(R.id.next);
        name    = (EditText) findViewById(R.id.name);
        phone   = (EditText) findViewById(R.id.phone);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().length()>0) {
                    uploadFile();
                }
            }
        });

        //upload  = (Button)findViewById(R.id.upload_button);
        uploadImage = (ImageView)findViewById(R.id.upload_image);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


            }
        });

        /*

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {

                //   Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //   uploadImage.setImageBitmap(bitmap);

                CropImage.activity(filePath)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)

        {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                mresultUri= result.getUri();
                uploadImage.setImageURI(mresultUri);
            }
        }

    }


    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            mAuth   = FirebaseAuth.getInstance();
            final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Customers");
            final String userid = mAuth.getCurrentUser().getUid();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
            StorageReference riversRef = storageRef.child(mresultUri.getLastPathSegment());
            riversRef.putFile(mresultUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            if(mAuth!=null)
                            {
                                DatabaseReference currentuserdb = firebaseDatabase.child(userid);
                                // HashMap hashMap = new HashMap();
                                final Uri  downloadUrl = taskSnapshot.getDownloadUrl();

                                String imgurl="images/"+filePath.getLastPathSegment();
                                // hashMap.put("imgurl",""+imgurl);
                                currentuserdb.child("imgurl").setValue(downloadUrl.toString());
                                currentuserdb.child("name").setValue(name.getText().toString());
                                currentuserdb.child("phone").setValue(phone.getText().toString());

                                Intent intent = new Intent(UploadImage.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast

            mAuth   = FirebaseAuth.getInstance();
            final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Customers");
            final String userid = mAuth.getCurrentUser().getUid();
            DatabaseReference currentuserdb = firebaseDatabase.child(userid);
            currentuserdb.child("name").setValue(name.getText().toString());
            currentuserdb.child("phone").setValue(phone.getText().toString());
            Intent intent = new Intent(UploadImage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


        }
    }


}
