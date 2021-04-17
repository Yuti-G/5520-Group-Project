package WatchTogether.flixster;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.yutinggan.flixster.R;

import WatchTogether.flixster.adapters.UserAdapter;
import WatchTogether.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import WatchTogether.flixster.models.User;
import okhttp3.Headers;


public class DetailActivity extends YouTubeBaseActivity {

    public static final String YOUTUBE_API_KEY = "AIzaSyBlHhL9EqgJx0ZFIuzc5vn2yUAe96pZhs8";
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    ImageView btFavorite;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;
    YouTubePlayerView youTubePlayerView;
    RecyclerView rvUserList;
    List<User> userList;
    boolean liked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        btFavorite = findViewById(R.id.ic_favorite);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        ratingBar = findViewById(R.id.ratingBar);
        youTubePlayerView = findViewById(R.id.player);
        rvUserList = findViewById(R.id.rv_liked_users);

        //TODO: get data from database whether this user like this movie or not and set whether
        // this favorite icon is filled or empty
        // boolean liked = user.likedMovie(thismovie)
        liked = false;
        if (liked) btFavorite.setImageResource(R.mipmap.ic_favorite_filled);
        btFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liked) {
                    btFavorite.setImageResource(R.mipmap.ic_favorite);
                    //TODO: user.removeMovieFromFavoriteList(this movie)
                }
                else {
                    btFavorite.setImageResource(R.mipmap.ic_favorite_filled);
                    //TODO: user.addMovieToFavoriteList(this movie)
                }
                liked = !liked;
            }
        });

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float)movie.getRating());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if(results.length() == 0){
                        return;
                    }
                    String youtubeKey = results.getJSONObject(0).getString("key");
                    Log.d("DetailActivity", youtubeKey);
                    initializeYoutube(youtubeKey);

                } catch (JSONException e) {
                    Log.e("DetailActivity", "Failed to parse JSON", e);

                }
            }
          

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {

            }
        });

        // favorite user list
        userList = new ArrayList<>();

        AddItemsToUsersList();
        UserAdapter userAdapter = new UserAdapter(this, userList);
        userAdapter.notifyDataSetChanged();
        rvUserList.setLayoutManager(new LinearLayoutManager(this));
        rvUserList.setAdapter(userAdapter);
    }

    private void initializeYoutube(final String youtubeKey) {
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("DetailActivity", "onInitializationSuccess");
                youTubePlayer.cueVideo(youtubeKey);

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("DetailActivity", "onInitializationFailure");
            }
        });
    }

    private void AddItemsToUsersList() {
        //TODO: get usersList from firebase, maybe need to add one more attribute to Movie class to
        // storage this list
        User u1 = new User(1, "Ann", null, new ArrayList<>());
        User u2 = new User(2, "Bob", null, new ArrayList<>());

        userList.add(u1);
        userList.add(u2);
    }
}