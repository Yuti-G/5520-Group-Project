package WatchTogether.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.yutinggan.flixster.R;

import java.util.ArrayList;
import java.util.List;

import WatchTogether.flixster.adapters.InvitationAdapter;
import WatchTogether.flixster.adapters.InvitationDetailAdapter;
import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;

public class InvitationActivity extends AppCompatActivity {
    private static final String TAG = "InvitationActivity";
    List<Invitation> invitation_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        Log.d(TAG,"Starting invitation detail");
        RecyclerView rvInvitationDetails = findViewById(R.id.rv_invitations_detail);
        invitation_list = new ArrayList<>();

        AddItemsToInvitationList();
        InvitationDetailAdapter invitationDetailAdapter = new InvitationDetailAdapter(this, invitation_list);
        invitationDetailAdapter.notifyDataSetChanged();
        rvInvitationDetails.setLayoutManager(new LinearLayoutManager(this));
        rvInvitationDetails.setAdapter(invitationDetailAdapter);
    }

    // Function to add items in RecyclerView.
    public void AddItemsToInvitationList()
    {
        // Adding items to ArrayList
        invitation_list = new ArrayList<>();

        // TODO: change the followings to fetch user's invitations list data
        Invitation i1 = new Invitation(1, "2021-04-14 05:24", "online", new Movie(),
                "Ann", "Bob", "Let's watch together");
        i1.accept();
        invitation_list.add(i1);
        invitation_list.add(new Invitation(2, "2021-04-16 05:24", "online", new Movie(),
                "An", "Bo", "Let's watch together"));
        invitation_list.add(new Invitation());
        invitation_list.add(new Invitation());
    }
}