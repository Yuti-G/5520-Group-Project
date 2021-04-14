package WatchTogether.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.yutinggan.flixster.FavoriteActivity;
import com.codepath.yutinggan.flixster.InvitationActivity;
import com.codepath.yutinggan.flixster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import WatchTogether.flixster.adapters.FavoriteAdapter;
import WatchTogether.flixster.adapters.InvitationAdapter;
import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;
import okhttp3.Headers;

import androidx.recyclerview.widget.LinearLayoutManager;

public class ProfileActivity extends AppCompatActivity {
    RecyclerView rvInvitations;
    List<Invitation> invitationList;
    InvitationAdapter invitationAdapter;

    RecyclerView rvFavorite;
    List<Movie> favoriteList;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    FavoriteAdapter favoriteAdapter;
    LinearLayoutManager HorizontalLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_acticity);

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
}