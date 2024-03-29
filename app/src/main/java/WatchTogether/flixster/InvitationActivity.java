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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    InvitationDetailAdapter invitationDetailAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Invitations");
        setContentView(R.layout.activity_invitation);
        Log.d(TAG,"Starting invitation detail");
        RecyclerView rvInvitationDetails = findViewById(R.id.rv_invitations_detail);
        invitationList = new ArrayList<>();

        invitationDetailAdapter = new InvitationDetailAdapter(this, invitationList);
        //invitationDetailAdapter.notifyDataSetChanged();
        rvInvitationDetails.setLayoutManager(new LinearLayoutManager(this));
        rvInvitationDetails.setAdapter(invitationDetailAdapter);

        AddItemsToInvitationList();
    }

    // Function to add items in RecyclerView.
    public void AddItemsToInvitationList()
    {
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
                    boolean accepted = (boolean) invitationMap.get("acceptedStatus");
                    HashMap m = (HashMap) invitationMap.get("movie");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        String redundantPosterUrl = (String) m.get("backdropPath");
                        int actualPosterUrlStartPos = redundantPosterUrl.indexOf("0", 8);
                        String imagePosterUrl = redundantPosterUrl.substring(actualPosterUrlStartPos + 3);
                        String redundantBackdropUrl = (String) m.get("posterPath");
                        int actualBackdropUrlStartPos = redundantBackdropUrl.indexOf("0", 8);
                        String imageBackdropUrl = redundantBackdropUrl.substring(actualBackdropUrlStartPos + 3);
                        jsonObject.put("backdrop_path", imagePosterUrl);
                        jsonObject.put("poster_path", imageBackdropUrl);
                        jsonObject.put("title", m.get("title"));
                        jsonObject.put("overview", m.get("overview"));
                        jsonObject.put("id", m.get("movieId"));
                        jsonObject.put("vote_average", m.get("rating"));
                        Movie movie = new Movie(jsonObject);
                        Invitation invitation = new Invitation((int) invitationId, dateTime, location, movie, inviteFrom, inviteTo, message, accepted);
                        invitationList.add(invitation);
                        invitationDetailAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}