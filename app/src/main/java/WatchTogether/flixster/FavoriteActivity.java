package WatchTogether.flixster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.yutinggan.flixster.R;
import com.facebook.stetho.common.ArrayListAccumulator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import WatchTogether.flixster.adapters.MovieAdapter;
import WatchTogether.flixster.models.Movie;
import okhttp3.Headers;

import static WatchTogether.flixster.MainActivity.NON_PLAYING_URL;

public class FavoriteActivity extends AppCompatActivity {
    public static final String TAG = "FavoriteActivity";
    List<Movie> favorite_list = new ArrayListAccumulator<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    MovieAdapter movieAdapter;
    static boolean load = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        load = false;
        RecyclerView rvFavoriteMovies = findViewById(R.id.rv_favorite_movie);
        favorite_list.clear();
        // Create the adapter
        movieAdapter = new MovieAdapter(this, favorite_list);

        // Set the adapter on the recycler view
        rvFavoriteMovies.setAdapter(movieAdapter);

        // Set a Layout Manager on the recycler view
        rvFavoriteMovies.setLayoutManager(new LinearLayoutManager(this));

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
                            favorite_list.add(movie);
                            movieAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!load) {
            load = true;
        } else {
            favorite_list.clear();

            DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<Long> favouriteMovieList = (ArrayList) documentSnapshot.get("movies");
                    if (favouriteMovieList.size() == 0) {
                        Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                    for (long f : favouriteMovieList) {
                        db.collection("movies").document(String.valueOf(f)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Movie movie = documentSnapshot.toObject(Movie.class);
                                favorite_list.add(movie);
                                movieAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FavoriteActivity.this, ProfileActivity.class);
        startActivity(intent);
        return;
    }
}