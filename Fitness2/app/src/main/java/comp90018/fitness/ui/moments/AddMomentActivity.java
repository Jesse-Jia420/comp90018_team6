package comp90018.fitness.ui.moments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.ArrayList;

import comp90018.fitness.R;
import comp90018.fitness.ui.moments.placeholder.PlaceholderContent;

public class AddMomentActivity extends AppCompatActivity {

    private static final String USER_ID = "ASDASXCIWEWQNADS";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mButtonChooseImage;
//    private Button mButtonUpload;
    private Button mButtonSubmit;
    private ProgressBar mProgressBar;
    private EditText mTitle;
    private EditText mContent;
    private String avatarUrl = null;
    private ImageView mUserAvatar;
    private TextView mUserName;
//    private double mLatitude;
//    private double mLongitude;
//    private TextView mPosition;
    private ConstraintLayout mUploadImgList;
    private String mAuthorId;

    private StorageReference mStorageRef;
    private FirebaseFirestore mDatabaseRef;

    private Uri mImageUri;
    private ArrayList<Uri> mImageUriList = new ArrayList<Uri>();
    private ArrayList<ImageView> ivList = new ArrayList<ImageView>();
    private int currentImgIndex=0;

    private static final int RESULT_LOAD_IMAGE = 1;

    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;

    private ArrayList<String> imgUrlList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_moment);

        mButtonChooseImage = findViewById(R.id.button_choose_image);
//        mButtonUpload = findViewById(R.id.button_upload);
        mButtonSubmit = findViewById(R.id.button_submit);
        mProgressBar = findViewById(R.id.progress_bar);

        mTitle = findViewById(R.id.edit_title_edit);
        mContent = findViewById(R.id.edit_content_edit);
        mUserAvatar = findViewById(R.id.user_avatar);
        mUserName = findViewById(R.id.user_name);
//        mPosition = findViewById(R.id.position_text_view);
        mUploadImgList = findViewById(R.id.upload_img_list);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseFirestore.getInstance();

        getUserInfo(USER_ID);

        mButtonChooseImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openFileChooser();
            }
        });
//        mButtonUpload.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                uploadFile();
//            }
//        });
        mButtonSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                submit();
            }

        });


    }

    private void submit() {
        Post post = new Post(mTitle.getText().toString().trim(), mContent.getText().toString().trim(), imgUrlList, mUserName.getText().toString().trim(), avatarUrl, mAuthorId);
        mDatabaseRef.collection("post_test").add(post);
        this.finish();
    }
    private void getUserInfo(final String id){
        mAuthorId = id;

        final FirebaseFirestore db= FirebaseFirestore.getInstance();
        AddMomentActivity that;
        that = this;
        DocumentReference docRef = db.collection("user_test").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            private static final String TAG = "";

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            avatarUrl = document.get("avatarUrl").toString();
//                            //show user info
                            mUserName.setText(document.get("name").toString());
                            Glide.with(that)
                                    .load(avatarUrl).placeholder(R.drawable.ic_menu_moments)
                                    .into(mUserAvatar);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }




    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        startActivityForResult(Intent.createChooser(intent, "Select Piscture"), RESULT_LOAD_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            if(data.getClipData() != null){
                Toast.makeText(AddMomentActivity.this, "Selected Multiple Images", Toast.LENGTH_SHORT).show();
                int totalItemsSelected = data.getClipData().getItemCount();
                for (int i = 0; i < totalItemsSelected; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    ImageView iv = new ImageView(getApplicationContext());
                    iv.setId(currentImgIndex);
                    mUploadImgList.addView(iv);
                    currentImgIndex++;
                    mImageUriList.add(fileUri);
                    ivList.add(iv);
                }
            }
            else if (data.getData() != null){
                Toast.makeText(AddMomentActivity.this, "Selected Single Image", Toast.LENGTH_SHORT).show();
                Uri fileUri = data.getData();
                ImageView iv = new ImageView(getApplicationContext());
                iv.setId(currentImgIndex);
                mUploadImgList.addView(iv);
                currentImgIndex++;
                mImageUriList.add(fileUri);
                ivList.add(iv);
            }


            for(int i=0; i<mImageUriList.size(); i++){
                Glide.with(this)
                        .load(mImageUriList.get(i))
                        .override(300, 300)
                        .centerCrop()
                        .into(ivList.get(i));
            }


            //adjust constraint layout
            constraintLayout = (ConstraintLayout) findViewById(R.id.upload_img_list);
            constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(ivList.get(0).getId(), ConstraintSet.LEFT,constraintLayout.getId(), ConstraintSet.LEFT, 0);
            constraintSet.connect(ivList.get(0).getId(), ConstraintSet.TOP,constraintLayout.getId(), ConstraintSet.TOP, 0);
            for (int i = 1; i < mImageUriList.size(); i++) {
                constraintSet.connect(ivList.get(i).getId(), ConstraintSet.LEFT, ivList.get(i-1).getId(), ConstraintSet.RIGHT, 5);
                constraintSet.connect(ivList.get(i).getId(), ConstraintSet.TOP, ivList.get(i-1).getId(), ConstraintSet.TOP, 0);
                constraintSet.connect(ivList.get(i).getId(), ConstraintSet.BOTTOM, ivList.get(i-1).getId(), ConstraintSet.BOTTOM, 0);
                constraintSet.applyTo(constraintLayout);
            }
            uploadFile();

        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if(mImageUriList.size() != 0){
            for(Uri e: mImageUriList){
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(e));
                fileReference.putFile(e)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mProgressBar.setProgress(0);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgressBar.setProgress(0);
                                    }
                                }, 5000);
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadUrl = uri;
                                        imgUrlList.add(downloadUrl.toString());
                                    }
                                });
                                Toast.makeText(AddMomentActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddMomentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mProgressBar.setProgress((int) progress);
                            }
                        });
            }

        }else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}