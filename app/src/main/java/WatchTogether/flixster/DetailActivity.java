package WatchTogether.flixster;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.yutinggan.flixster.R;

import WatchTogether.flixster.adapters.UserAdapter;
import WatchTogether.flixster.models.Movie;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import WatchTogether.flixster.models.User;
import okhttp3.Headers;


public class DetailActivity extends YouTubeBaseActivity {

    public static final String TAG = "DetailActivity";
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

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static Movie movie;

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

        movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
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

        DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                liked = ((ArrayList) documentSnapshot.get("movies")).contains(Long.valueOf(movie.getMovieId()));
                if (liked) {
                    btFavorite.setImageResource(R.mipmap.ic_favorite_filled);
                } else {
                    btFavorite.setImageResource(R.mipmap.ic_favorite);
                }
            }
        });

        btFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liked) {
                    btFavorite.setImageResource(R.mipmap.ic_favorite);
                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            userRef.update("movies", FieldValue.arrayRemove(movie.getMovieId()));
                        }
                    });
                }
                else {
                    btFavorite.setImageResource(R.mipmap.ic_favorite_filled);
                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            userRef.update("movies", FieldValue.arrayUnion(movie.getMovieId()));
                        }
                    });
                }
                liked = !liked;
            }
        });

        // favorite user list
        userList = new ArrayList<>();
        UserAdapter userAdapter = new UserAdapter(this, userList);
        rvUserList.setLayoutManager(new LinearLayoutManager(this));
        rvUserList.setAdapter(userAdapter);

        CollectionReference usersRef = db.collection("users");
        Query q = usersRef.whereArrayContainsAny("movies", Arrays.asList(movie.getMovieId()));
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        String email = document.getId();
                        if (!email.equals(mAuth.getCurrentUser().getEmail())) {
                            String uid = (String) document.getData().get("uid");
                            userList.add(new User(uid, email, null));
                            userAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
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

    public static Movie getMovie() {
        return movie;
    }
}