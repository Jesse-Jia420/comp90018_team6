package comp90018.fitness.ui.moments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import comp90018.fitness.BuildConfig;
import comp90018.fitness.R;

public class AddMomentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "";
    private Button mButtonChooseImage;
    private Button mButtonTakePhoto;
    private Button mButtonSubmit;
    private ProgressBar mProgressBar;
    private EditText mTitle;
    private EditText mContent;
    private String avatarUrl = null;
    private ImageView mUserAvatar;
    private TextView mUserName;
    private double mLatitude;
    private double mLongitude;
    private TextView mPosition;
    private ConstraintLayout mUploadImgList;
    private String mAuthorId;

    private StorageReference mStorageRef;
    private FirebaseFirestore mDatabaseRef;

    private Uri mImageUri;
    private ArrayList<Uri> mImageUriList = new ArrayList<Uri>();
    private ArrayList<ImageView> ivList = new ArrayList<ImageView>();
    private int currentImgIndex = 0;

    private static final int RESULT_LOAD_IMAGE = 1;

    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;

    private ArrayList<String> imgUrlList = new ArrayList<String>();

    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_moment);

        //跳转相机动态权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonTakePhoto = findViewById(R.id.button_choose_image_photo);
        mButtonSubmit = findViewById(R.id.button_submit);
        mProgressBar = findViewById(R.id.progress_bar);

        mButtonSubmit.setEnabled(false);
        mButtonSubmit.setTextColor(0xFFFFFFFF);

        mTitle = findViewById(R.id.edit_title_edit);
        mContent = findViewById(R.id.edit_content_edit);
        mUserAvatar = findViewById(R.id.user_avatar);
        mUserName = findViewById(R.id.user_name);
        mPosition = findViewById(R.id.position_text_view);
        mUploadImgList = findViewById(R.id.upload_img_list);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseFirestore.getInstance();

        getUserInfo();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getUserPosition();

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        mButtonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }

        });


    }

    private void getUserInfo() {
        String USER_ID = "";
        Bundle bundle = getIntent().getExtras();
        if(! bundle.getString("UID").equals("none"))
        {
            //TODO here get the string stored in the string variable and do
            // setText() on userName
            USER_ID = bundle.getString("UID");
            mAuthorId = USER_ID;

            final FirebaseFirestore db= FirebaseFirestore.getInstance();
            AddMomentActivity that;
            that = this;
            DocumentReference docRef = db.collection("users").document(mAuthorId);
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
                                    .load(avatarUrl)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_menu_moments)
                                    .into(mUserAvatar);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }else{
            Toast.makeText(AddMomentActivity.this, "You need to login first!", Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }

    private void submit() {
        Post post = new Post(mTitle.getText().toString().trim(), mContent.getText().toString().trim(), imgUrlList, mUserName.getText().toString().trim(), avatarUrl, mAuthorId, mLatitude, mLongitude);
        mDatabaseRef.collection("post_test").add(post);
        this.finish();
    }


    private void getUserPosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(AddMomentActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if(location != null){
                            mPosition.setText("");
                            mPosition.setText( "Current Position: "+location.getLatitude()+" "+location.getLongitude());
                            mLatitude = location.getLatitude();
                            mLongitude = location.getLongitude();

//                            //translate geo point into meaningful location
//                            Geocoder geocoder = new Geocoder(AddMomentActivity.this);
//                            try {
//                                List<Address> mAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),3);
//                                Address address = mAddresses.get(0);
//                                CharArrayWriter mStringBuilder = null;
//                                mStringBuilder.append(address.getAdminArea()).append(", ").append(address.getLocality()).append(", ").append(address.getCountryName());
//                            }catch (Exception e){
//                            }
                        }
                    }

                });
    }

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private Uri ImageUri;
    public static final int TAKE_PHOTO = 101;
    public static final int TAKE_CAMARA = 100;

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Piscture"), RESULT_LOAD_IMAGE);
    }


    public int verifyPermissions(Activity activity, java.lang.String permission) {
        int Permission = ActivityCompat.checkSelfPermission(activity, permission);
        if (Permission == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "已经同意权限");
            return 1;
        } else {
            Log.i(TAG, "没有同意权限");
            return 0;
        }
    }
    private void openCamera(){
        if (verifyPermissions(AddMomentActivity.this, PERMISSIONS_STORAGE[2]) == 0) {
            Log.i(TAG, "提示是否要授权");
            ActivityCompat.requestPermissions(AddMomentActivity.this, PERMISSIONS_STORAGE, 3);
        } else {
            //已经有权限
            //创建File对象，用于存储拍照后的图片
//        File outputImage = new File(getExternalCacheDir(), "outputImage.jpg");
            File outputImage = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
            if (outputImage.exists()) {
                outputImage.delete();
            } else {
                try {
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //判断SDK版本高低，ImageUri方法不同
            if (Build.VERSION.SDK_INT >= 24) {
                ImageUri = FileProvider.getUriForFile(AddMomentActivity.this, BuildConfig.APPLICATION_ID + ".provider", outputImage);
            } else {
                ImageUri = Uri.fromFile(outputImage);
            }

            //启动相机程序
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == TAKE_PHOTO || requestCode == RESULT_LOAD_IMAGE) && resultCode == RESULT_OK){
//            mImageUriList.clear();
            imgUrlList.clear();
            if(requestCode == TAKE_PHOTO){
                ImageView iv = new ImageView(getApplicationContext());
                iv.setId(currentImgIndex);
                mUploadImgList.addView(iv);
                currentImgIndex++;
                mImageUriList.add(ImageUri);
                ivList.add(iv);
            } else if(requestCode == RESULT_LOAD_IMAGE){
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
                                mButtonSubmit.setEnabled(true);
                                mButtonSubmit.setTextColor(0xFFFFFFFF);
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

                                mButtonSubmit.setEnabled(false);
                                mButtonSubmit.setTextColor(0xFFFFFFFF);

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