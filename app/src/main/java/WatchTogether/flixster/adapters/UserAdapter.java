package WatchTogether.flixster.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.yutinggan.flixster.R;
import com.facebook.stetho.common.ArrayListAccumulator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import WatchTogether.flixster.DetailActivity;
import WatchTogether.flixster.MainActivity;
import WatchTogether.flixster.ProfileActivity;
import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;
import WatchTogether.flixster.models.User;
import okhttp3.Headers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Scanner;

public class UserAdapter extends Adapter<UserAdapter.ViewHolder> implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private static final String SERVER_KEY = "key=AAAAfte_3rA:APA91bF5tFMiJEmO-Ob4p927DABrWcaXir9PMNtiFxZoBSI52iw8QAbWHZYscei0iU3sSVelMjdiKhFLNeBiAst598cYUa2WJNvN3vPyoymgKjLAgik9DtnVfQo5hveTeKA2ahF3HxdW";
    private String TAG = "SendInvitation";
    private List<User> usersList;
    Context context;
    int day, month, year, hour, minute;
    TextView tvDateTime;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    Uri meUri, toUri;

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

        // TODO: get user icon and attach image to imageView
        String  userId = user.getUserId();
        Log.d(TAG, "userId " + userId);
        StorageReference fileRef = firebaseStorage.getReference().child(userId + ".jpeg");
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "Image URI:  " + uri);
                Picasso.get().load(uri).into(holder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Get Image fail ");
            }
        });

        holder.container.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                showInviteDialog(user, fileRef);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showInviteDialog(User inviteTo, StorageReference imgFileRef) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_send_invitation, null);
        // ImageView ivInviteFrom = v.findViewById(R.id.iv_invitation_from);
        // TextView tvInviteFrom = v.findViewById(R.id.tv_user_id_from);
        ImageView ivInviteTo = v.findViewById(R.id.iv_invitation_to);
        // load invite to img
        imgFileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "Image URI:  " + uri);
                Picasso.get().load(uri).into(ivInviteTo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Get invite to img fail ");
            }
        });
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
                .setTitle("new invitation")
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

                        // Add a Toast when succeeds and send notification to inviteTo user
                        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String fromToken = (String) documentSnapshot.get("token");
                                User inviteFrom = new User(mAuth.getUid(), mAuth.getCurrentUser().getEmail(), null, fromToken);
                                Invitation invitation = new Invitation(movie.getMovieId(), tvDateTime.getText().toString(), tvLocation.getText().toString(), movie, inviteFrom, inviteTo, tvMessage.getText().toString(), false);
                                db.collection("users").document(inviteTo.getName())
                                        .update("invitations", FieldValue.arrayUnion(invitation))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");

                                                String fromUsername = inviteFrom.getName();
                                                String toToken = inviteTo.getToken();
                                                //showToast("send notification to :" + toToken);
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        sendMessageToDevice(toToken, fromUsername);
                                                    }
                                                }).start();

                                            }
                                        });
                                db.collection("users").document(inviteFrom.getName())
                                        .update("invitations", FieldValue.arrayUnion(invitation))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                            }
                                        });


                                dialog.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "DocumentSnapshot successfully Fail! file : " + TAG);
                            }
                        });
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




    // Toast when invitation sent
    private void showToast(String toastMessage) {
        Toast.makeText(this.context, toastMessage, Toast.LENGTH_LONG).show();
    }


    private void sendMessageToDevice(String targetToken, String fromUsername) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "You got a New Invitation!");
            jNotification.put("body", fromUsername + "invite you to watch together!");
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
            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);


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
                    // Toast.makeText(MainActivity.this,resp,Toast.LENGTH_LONG).show();
                    // showToast(resp);
                    showToast("invitation send successfully");
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
