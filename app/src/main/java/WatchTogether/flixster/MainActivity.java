package WatchTogether.flixster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.yutinggan.flixster.R;

import WatchTogether.flixster.adapters.MovieAdapter;
import WatchTogether.flixster.models.Movie;
import com.facebook.stetho.common.ArrayListAccumulator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {
    private static final String SERVER_KEY = "key=AAAAfte_3rA:APA91bF5tFMiJEmO-Ob4p927DABrWcaXir9PMNtiFxZoBSI52iw8QAbWHZYscei0iU3sSVelMjdiKhFLNeBiAst598cYUa2WJNvN3vPyoymgKjLAgik9DtnVfQo5hveTeKA2ahF3HxdW";
    private static final String CLIENT_TOKEN = "cCqUtuMLTXuHg5xSg7CJhp:APA91bFE6QrDPLE6e9_RGMD78LlD7cxg28EsbyVDkb3d4LPTuQfxo4ZHRhWzNXIif6vpufRcERee3ZONyGQYgImSn0_bJb9RoPJvvkJnVjy2PS5n7cOxM5UMdyZOShXjir3NhUeAb-q5";

    public static final String NON_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private String token;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rvMovies = findViewById(R.id.rvMovies);
        movies = new ArrayListAccumulator<>();

        mAuth = FirebaseAuth.getInstance();

        Map<String, Object> userDoc = new HashMap<>();
        userDoc.put("uid", mAuth.getUid());
        userDoc.put("movies", new ArrayList<>());
        userDoc.put("invitations", new ArrayList<>());
        setToken();
        userDoc.put("token", token);
        DocumentReference userRef = db.collection("users").document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()));
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // update token field every time open app.
                        userRef.update("token", token);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        userRef.set(userDoc)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // Create the adapter
        MovieAdapter movieAdapter = new MovieAdapter(this, movies);

        // Set the adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);

        // Set a Layout Manager on the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(NON_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {

                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies: " + movies.size());

                    for (Movie m: movies) {
                        String redundantPosterUrl = m.getPosterPath();
                        int actualPosterUrlStartPos = redundantPosterUrl.indexOf("0", 8);
                        String imagePosterUrl = redundantPosterUrl.substring(actualPosterUrlStartPos + 3);
                        String redundantBackdropUrl = m.getPosterPath();
                        int actualBackdropUrlStartPos = redundantBackdropUrl.indexOf("0", 8);
                        String imageBackdropUrl = redundantBackdropUrl.substring(actualBackdropUrlStartPos + 3);
                        db.collection("movies").document(String.valueOf(m.getMovieId()))
                                .set(m)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                        db.collection("movies").document(String.valueOf(m.getMovieId()))
                                .update("backdropPath", imageBackdropUrl, "posterPath", imagePosterUrl)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);

                }

            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFail");
            }
        });

//        String email = getIntent().getExtras().getString("email");
//        Toast.makeText(MainActivity.this, "Signed in with " + email,
//                Toast.LENGTH_SHORT).show();

        findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                Log.v(TAG, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Successfully Signed Out",
                        Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.profile_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });



    }

    private void setToken() {
        String newToken = MyFirebaseMessageService.getToken(getApplicationContext());
        Log.e("message", newToken);
        if (!newToken.equals("empty")) {
            token = newToken;
            Log.d(TAG, "Token created :" + token);
            return;
        }

        DocumentReference userRef = db.collection("users").document(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()));
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String oldToken = (String)document.get("token");
                    // if token changed, update token of this user
                    if (! oldToken.equals(newToken) ){
                        token = newToken;
                    }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        Log.d(TAG, "No Change to the Token :" + token);
    }

    public void sendMessageToDevice(View type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDevice(CLIENT_TOKEN);
            }
        }).start();
    }

    private void sendMessageToDevice(String targetToken) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "Stick It To 'Em");
            jNotification.put("body", "Sticker Sent From xxx");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            // If sending to a single client
            jPayload.put("to", targetToken);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */

            // If sending to a single client
            jPayload.put("to", "cCqUtuMLTXuHg5xSg7CJhp:APA91bFE6QrDPLE6e9_RGMD78LlD7cxg28EsbyVDkb3d4LPTuQfxo4ZHRhWzNXIif6vpufRcERee3ZONyGQYgImSn0_bJb9RoPJvvkJnVjy2PS5n7cOxM5UMdyZOShXjir3NhUeAb-q5"); // CLIENT_REGISTRATION_TOKEN);


            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data",jdata);


            /***
             * The Payload object is now populated.
             * Send it to Firebase to send the message to the appropriate recipient.
             */
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: " + resp);
                    Toast.makeText(MainActivity.this,resp,Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function
     * @param is
     * @return
     */
    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

}