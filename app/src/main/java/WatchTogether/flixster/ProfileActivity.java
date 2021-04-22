package WatchTogether.flixster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.yutinggan.flixster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import WatchTogether.flixster.adapters.FavoriteAdapter;
import WatchTogether.flixster.adapters.InvitationAdapter;
import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;
import WatchTogether.flixster.models.User;
import okhttp3.Headers;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    public static final int REQUEST_IMAGE = 100;
    RecyclerView rvInvitations;
    List<Invitation> invitationList;
    InvitationAdapter invitationAdapter;

    RecyclerView rvFavorite;
    List<Movie> favoriteList;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    FavoriteAdapter favoriteAdapter;
    LinearLayoutManager HorizontalLayout;

    ImageView imgProfile;

    TextView userID, followers, following;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);

        //set up basic info
        // Access a Cloud Firestore instance from your Activity
        userID = (TextView) findViewById(R.id.profile_id);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userID.setText(email.split("@")[0]);
        followers = (TextView) findViewById(R.id.followers_num);
        //followers.setText();
        following = (TextView) findViewById(R.id.following_num);
        //following.setText();

        imgProfile = (ImageView) findViewById(R.id.iv_profile);
        loadProfileDefault();
        loadProfileImg();
        ImagePickerActivity.clearCache(this);

        findViewById(R.id.ic_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileImageClick();
            }
        });
        findViewById(R.id.iv_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileImageClick();
            }
        });

        invitationList = new ArrayList<>();
        rvInvitations = (RecyclerView) findViewById(R.id.rv_invitations_general);
        rvInvitations.setLayoutManager(new LinearLayoutManager(this));
        invitationAdapter = new InvitationAdapter(this, invitationList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InvitationActivity.class);
                startActivity(intent);
            }
        });
        rvInvitations.setAdapter(invitationAdapter);
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<HashMap> invitationMapList = (ArrayList) documentSnapshot.get("invitations");
                for (int i = 0; i < invitationMapList.size(); i++) {
                    HashMap invitationMap = invitationMapList.get(i);
                    long invitationId = (long) invitationMap.get("invitationId");
                    String dateTime = (String) invitationMap.get("dateTime");
                    String location = (String) invitationMap.get("location");
                    HashMap userFrom = (HashMap) invitationMap.get("inviteFrom");
                    User inviteFrom = new User((String) userFrom.get("userId"), (String) userFrom.get("name"), null, (String) userFrom.get("token"));
                    HashMap userTo = (HashMap) invitationMap.get("inviteTo");
                    User inviteTo = new User((String) userTo.get("userId"), (String) userTo.get("name"), null, (String) userFrom.get("token"));
                    String message = (String) invitationMap.get("message");
                    HashMap m = (HashMap) invitationMap.get("movie");
                    boolean accepted = (boolean) invitationMap.get("acceptedStatus");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("backdrop_path", m.get("backdropPath"));
                        jsonObject.put("poster_path", m.get("posterPath"));
                        jsonObject.put("title", m.get("title"));
                        jsonObject.put("overview", m.get("overview"));
                        jsonObject.put("id", m.get("movieId"));
                        jsonObject.put("vote_average", m.get("rating"));
                        Movie movie = new Movie(jsonObject);
                        Invitation invitation = new Invitation((int) invitationId, dateTime, location, movie, inviteFrom, inviteTo, message, accepted);
                        invitationList.add(invitation);
                        invitationAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        rvFavorite = (RecyclerView) findViewById(R.id.rv_favorite_general);
        AddItemsToFavoriteList();
        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvFavorite.setLayoutManager(RecyclerViewLayoutManager);
        favoriteAdapter = new FavoriteAdapter(this, favoriteList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FavoriteActivity.class);
                startActivity(intent);
            }
        });
        HorizontalLayout = new LinearLayoutManager(ProfileActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        rvFavorite.setLayoutManager(HorizontalLayout);
        rvFavorite.setAdapter(favoriteAdapter);
    }

    // Function to add items in RecyclerView.
    public void AddItemsToFavoriteList()
    {
        // Adding items to ArrayList
        favoriteList = new ArrayList<>();

        DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<Long> favouriteMovieList = (ArrayList) documentSnapshot.get("movies");
                for (long f: favouriteMovieList) {
                    db.collection("movies").document(String.valueOf(f)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Movie movie = documentSnapshot.toObject(Movie.class);
                            favoriteList.add(movie);
                            favoriteAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        Glide.with(this)
                .load(url)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void loadProfileDefault() {
        Glide.with(this).load(R.drawable.baseline_account_circle_black_48)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, R.color.profile_default_tint));
    }

    public void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:244
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // upload this bitmap to firebase
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    uploadImgToFirebase(bitmap);

                    // loading profile image from local cache
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadProfileImgFromFirebase() {
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userId = document.get("uid").toString();
                        StorageReference fileRef = storageReference.child(userId + ".jpeg");
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(ProfileActivity.this, "Image download success! ", Toast.LENGTH_LONG).show();
                                Picasso.get().load(uri).into(imgProfile);
                                imgProfile.setColorFilter(ContextCompat.getColor(ProfileActivity.this, android.R.color.transparent));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, "Image download fail! ", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else  {

                }
            }
        });


    }


    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        return;
    }


    private void loadProfileImg() {
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userId = document.get("uid").toString();
                        StorageReference fileRef = storageReference.child(userId + ".jpeg");
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(ProfileActivity.this, "Image download success! ", Toast.LENGTH_LONG).show();
                                Picasso.get().load(uri).into(imgProfile);
                                imgProfile.setColorFilter(ContextCompat.getColor(ProfileActivity.this, android.R.color.transparent));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, "Image download fail! ", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else  {

                }
            }
        });
    }

//    private void loadProfileImgFromFirebase() {
//        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        String userId = document.get("uid").toString();
//                        StorageReference fileRef = storageReference.child(userId + ".jpeg");
//                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Toast.makeText(ProfileActivity.this, "Image download success! ", Toast.LENGTH_LONG).show();
//                                Picasso.get().load(uri).into(imgProfile);
//                                imgProfile.setColorFilter(ContextCompat.getColor(ProfileActivity.this, android.R.color.transparent));
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(ProfileActivity.this, "Image download fail! ", Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                } else  {
//
//                }
//            }
//        });
//
//
//    }

    // upload the pic to firebase storage
    private void uploadImgToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ProfileActivity.this.getContentResolver(), bitmap, "Title", null);
        Uri imgUri = Uri.parse(path);

        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data userId: " + document.get("uid").toString());
                        String userId = document.get("uid").toString();
                        StorageReference fileRef = storageReference.child(userId + ".jpeg");
                        fileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(ProfileActivity.this, "Image upload success! ", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, "Image upload Fail! ", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}