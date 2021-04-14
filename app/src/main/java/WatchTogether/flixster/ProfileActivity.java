package WatchTogether.flixster;

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

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.yutinggan.flixster.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import WatchTogether.flixster.adapters.FavoriteAdapter;
import WatchTogether.flixster.adapters.InvitationAdapter;
import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;
import butterknife.OnClick;
import okhttp3.Headers;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_acticity);

        imgProfile = (ImageView) findViewById(R.id.iv_profile);
        loadProfileDefault();
        ImagePickerActivity.clearCache(this);
        findViewById(R.id.ic_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click!!!!!!");
                onProfileImageClick();
            }
        });
        findViewById(R.id.iv_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "click!!!!!!");
                onProfileImageClick();
            }
        });

        rvInvitations = (RecyclerView) findViewById(R.id.rv_invitations_general);
        AddItemsToInvitationList();
        invitationAdapter = new InvitationAdapter(this, invitationList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InvitationActivity.class);
                startActivity(intent);
            }
        });
        invitationAdapter.notifyDataSetChanged();
        rvInvitations.setLayoutManager(new LinearLayoutManager(this));
        rvInvitations.setAdapter(invitationAdapter);

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

        // TODO: change the followings to fetch user's favorite movie list data
        String NON_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
        String TAG = "ProfileActivity";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(NON_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {

                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    favoriteList.addAll(Movie.fromJsonArray(results));
                    favoriteAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFail");
            }
        });
    }

    // Function to add items in RecyclerView.
    public void AddItemsToInvitationList()
    {
        // Adding items to ArrayList
        invitationList = new ArrayList<>();

        // TODO: change the followings to fetch user's invitations list data
        invitationList.add(new Invitation());
        invitationList.add(new Invitation());
        invitationList.add(new Invitation());
        invitationList.add(new Invitation());
    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        Glide.with(this).load(url)
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
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
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
                    // TODO: update this bitmap to firebase
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    // loading profile image from local cache
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
}