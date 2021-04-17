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
import com.codepath.yutinggan.flixster.R;
import com.facebook.stetho.common.ArrayListAccumulator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import WatchTogether.flixster.adapters.InvitationAdapter;
import WatchTogether.flixster.adapters.InvitationDetailAdapter;
import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;
import WatchTogether.flixster.models.User;
import okhttp3.Headers;

public class InvitationActivity extends AppCompatActivity {
    private static final String TAG = "InvitationActivity";
    List<Invitation> invitationList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        Log.d(TAG,"Starting invitation detail");
        RecyclerView rvInvitationDetails = findViewById(R.id.rv_invitations_detail);
        invitationList = new ArrayList<>();

        InvitationDetailAdapter invitationDetailAdapter = new InvitationDetailAdapter(this, invitationList);
        invitationDetailAdapter.notifyDataSetChanged();
        rvInvitationDetails.setLayoutManager(new LinearLayoutManager(this));
        rvInvitationDetails.setAdapter(invitationDetailAdapter);

        //AddItemsToInvitationList();
        String NON_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
        List<Movie> movies = new ArrayListAccumulator<>();
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
                    Invitation i1 = new Invitation(1, "2021-04-14 05:24", "online", movies.get(0),
                            new User(1, "Ann", null, new ArrayList<>()),
                            new User(2, "Bob", null, new ArrayList<>()),
                            "Let's watch together");
                    i1.accept();
                    invitationList.add(i1);
                    invitationList.add(new Invitation(2, "2021-04-16 05:24", "online", movies.get(1),
                            new User(1, "Ann", null, new ArrayList<>()),
                            new User(2, "Bob", null, new ArrayList<>()),
                            "Let's watch together"));
                    invitationList.add(new Invitation(3, "2021-04-16 05:24", "online", movies.get(2),
                            new User(1, "Ann", null, new ArrayList<>()),
                            new User(2, "Bob", null, new ArrayList<>()),
                            "Let's watch together"));
                    invitationList.add(new Invitation(4, "2021-04-16 05:24", "online", movies.get(3),
                            new User(1, "Ann", null, new ArrayList<>()),
                            new User(2, "Bob", null, new ArrayList<>()),
                            "Let's watch together"));
                    invitationDetailAdapter.notifyDataSetChanged();
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

        // TODO: change the followings to fetch user's invitations list data

    }
}