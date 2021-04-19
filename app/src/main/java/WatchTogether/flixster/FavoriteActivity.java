package WatchTogether.flixster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    List<Movie> favorite_list;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        RecyclerView rvFavoriteMovies = findViewById(R.id.rv_favorite_movie);
        favorite_list = new ArrayListAccumulator<>();
        // Create the adapter
        MovieAdapter movieAdapter = new MovieAdapter(this, favorite_list);

        // Set the adapter on the recycler view
        rvFavoriteMovies.setAdapter(movieAdapter);

        // Set a Layout Manager on the recycler view
        rvFavoriteMovies.setLayoutManager(new LinearLayoutManager(this));

        //TODO: replace the following code to get favorite_list from database
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

//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get(NON_PLAYING_URL, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int i, Headers headers, JSON json) {
//
//                Log.d(TAG, "onSuccess");
//                JSONObject jsonObject = json.jsonObject;
//                try {
//                    JSONArray results = jsonObject.getJSONArray("results");
//                    Log.i(TAG, "Results: " + results.toString());
//                    favorite_list.addAll(Movie.fromJsonArray(results));
//                    movieAdapter.notifyDataSetChanged();
//                    Log.i(TAG, "Movies: " + favorite_list.size());
//                } catch (JSONException e) {
//                    Log.e(TAG, "Hit json exception", e);
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
//                Log.d(TAG, "onFail");
//            }
//        });

        //Specify what action a specific gesture performs, in this case swiping right or left deletes the entry
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(FavoriteActivity.this, "Remove a movie!", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getLayoutPosition();
                // TODO: updated favorite list in database
                favorite_list.remove(position);
                movieAdapter.notifyItemRemoved(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(rvFavoriteMovies);
    }
}