package WatchTogether.flixster.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.yutinggan.flixster.R;
import com.facebook.stetho.common.ArrayListAccumulator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import WatchTogether.flixster.DetailActivity;
import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;
import WatchTogether.flixster.models.User;
import okhttp3.Headers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class UserAdapter extends Adapter<UserAdapter.ViewHolder> implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private String TAG = "SendInvitation";
    private List<User> usersList;
    Context context;
    int day, month, year, hour, minute;
    TextView tvDateTime;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        RelativeLayout container;
        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.iv_user);
            textView = (TextView)view.findViewById(R.id.tv_user_id);
            container = (RelativeLayout)view.findViewById(R.id.user_container);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public UserAdapter(Context context, List<User> usersList)
    {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.item_user_general,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set the text of each item of
        // Recycler view with the list items
        User user = usersList.get(position);
        holder.textView.setText(user.getName());
        // TODO get user icon and attach image to imageView
        //String userProfileImageURL =
        //Glide.with(context).load(userProfileImageURL).into(imageView);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                showInviteDialog(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showInviteDialog(User inviteTo) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_send_invitation, null);
        ImageView ivInviteFrom = v.findViewById(R.id.iv_invitation_from);
        TextView tvInviteFrom = v.findViewById(R.id.tv_user_id_from);
        ImageView ivInviteTo = v.findViewById(R.id.iv_invitation_to);
        TextView tvInviteTo = v.findViewById(R.id.tv_user_id_to);
        tvInviteTo.setText(inviteTo.getName().split("@")[0]);
        ImageView ivPoster = v.findViewById(R.id.iv_invitation_poster);
        TextView tvTitle = v.findViewById(R.id.tv_movie_title);
        TextView tvOverview = v.findViewById(R.id.tvOverview);
        TextView tvLocation = v.findViewById(R.id.text_location);
        TextView tvMessage = v.findViewById(R.id.text_message);
        tvDateTime = v.findViewById(R.id.text_datetime);
        Button btnSelect = v.findViewById(R.id.btn_datetime);
        Movie movie = DetailActivity.getMovie();
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Send a new invitation")
                .setView(v)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: send data to firebase
                        // Add a Toast when succeeds and send notification to inviteTo user
                        User inviteFrom = new User(mAuth.getUid(), mAuth.getCurrentUser().getEmail(), null);
                        Invitation invitation = new Invitation(movie.getMovieId(), tvDateTime.getText().toString(), tvLocation.getText().toString(), movie, inviteFrom, inviteTo, tvMessage.getText().toString(), false);
                        db.collection("users").document(inviteTo.getName())
                                .update("invitations", FieldValue.arrayUnion(invitation))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                });
                        dialog.cancel();
                    }
                })
                .create();

        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        String imageUrl = movie.getPosterPath();
        // then imgageURL = backdrop image, else = poster image
        Glide.with(context).load(imageUrl).into(ivPoster);
        tvOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //2. navigate to a new activity on tap
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("movie", Parcels.wrap(movie));
                context.startActivity(i);
            }
        });

//        String NON_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
//        //List<Movie> movies = new ArrayListAccumulator<>();
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
//                    // TODO: change the movie to the current movie
//                    //movies.addAll(Movie.fromJsonArray(results));
//                    Movie movie = DetailActivity.getMovie();
//
//                    tvTitle.setText(movie.getTitle());
//                    tvOverview.setText(movie.getOverview());
//                    String imageUrl = movie.getPosterPath();
//                    // then imgageURL = backdrop image, else = poster image
//                    Glide.with(context).load(imageUrl).into(ivPoster);
//                    tvOverview.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //2. navigate to a new activity on tap
//                            Intent i = new Intent(context, DetailActivity.class);
//                            i.putExtra("movie", Parcels.wrap(movie));
//                            context.startActivity(i);
//                        }
//                    });
//                } catch (JSONException e) {
//                    Log.e(TAG, "Hit json exception", e);
//                }
//            }
//            @Override
//            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
//                Log.d(TAG, "onFail");
//            }
//        });

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        tvDateTime.setText(dtf.format(now));
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day, month, year;
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, UserAdapter.this, year, month, day);
                datePickerDialog.show();
            }
        });
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month+1;
        this.day = dayOfMonth;
        Calendar c = Calendar.getInstance();
        int hour, minute;
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, UserAdapter.this, hour, minute, DateFormat.is24HourFormat(context));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        tvDateTime.setText(String.format("%d-%d-%d %d:%d", this.year, this.month, this.day, this.hour, this.minute));
    }
}
