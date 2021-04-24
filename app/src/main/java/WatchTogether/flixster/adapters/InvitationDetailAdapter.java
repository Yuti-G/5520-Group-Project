package WatchTogether.flixster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.codepath.yutinggan.flixster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import WatchTogether.flixster.DetailActivity;
import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;
import WatchTogether.flixster.models.User;

public class InvitationDetailAdapter extends Adapter<InvitationDetailAdapter.ViewHolder>{
    private static final String SERVER_KEY = "key=AAAAfte_3rA:APA91bF5tFMiJEmO-Ob4p927DABrWcaXir9PMNtiFxZoBSI52iw8QAbWHZYscei0iU3sSVelMjdiKhFLNeBiAst598cYUa2WJNvN3vPyoymgKjLAgik9DtnVfQo5hveTeKA2ahF3HxdW";
    private static final String TAG = "InvitationDetailAdapter";
    private List<Invitation> invitation_list;
    Context context;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileFrom, ivProfileTo, ivPoster;
        TextView tvUserFrom, tvUserTo, tvTitle, tvOverview, tvDateTime, tvLocation, tvMessage;
        Button btnAccept, btnDecline;
        View container;
        public ViewHolder(View view) {
            super(view);
            ivProfileFrom = (ImageView)view.findViewById(R.id.iv_invitation_from);
            ivProfileTo = (ImageView)view.findViewById(R.id.iv_invitation_to);
            tvUserFrom = (TextView)view.findViewById(R.id.tv_user_id_from);
            tvUserTo = (TextView)view.findViewById(R.id.tv_user_id_to);
            tvTitle = (TextView)view.findViewById(R.id.tv_movie_title);
            tvOverview = (TextView)view.findViewById(R.id.tv_overview);
            ivPoster = (ImageView)view.findViewById(R.id.iv_invitation_poster);
            tvDateTime = (TextView)view.findViewById(R.id.text_datetime);
            tvLocation = (TextView)view.findViewById(R.id.text_location);
            tvMessage = (TextView)view.findViewById(R.id.text_message);
            btnAccept = (Button)view.findViewById(R.id.btn_accept);
            btnDecline = (Button)view.findViewById(R.id.btn_decline);
            container = view.findViewById(R.id.invitation_detail_container);
        }

        public void bind(Movie movie) {
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
        }
    }

    public InvitationDetailAdapter(Context context, List<Invitation> invitation_list)
    {
        this.context = context;
        this.invitation_list = invitation_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.item_invitation_detail,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set the text of each item of
        // Recycler view with the list items
        Invitation invitation = invitation_list.get(position);

        //get inviteFrom and inviteTo user's icon and attach image to imageView
        String  inviteToUserId = invitation.getInviteTo().getUserId();
        String  inviteFromUserId = invitation.getInviteFrom().getUserId();
        firebaseStorage.getReference().child(inviteFromUserId + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "Image URI:  " + uri);
                //holder.imageView.setImageURI(uri);
                //Glide.with(context).load(uri).into(holder.imageView);
                Picasso.get().load(uri).into(holder.ivProfileFrom);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Get Image fail ");
            }
        });

        firebaseStorage.getReference().child(inviteToUserId + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "Image URI:  " + uri);
                Picasso.get().load(uri).into(holder.ivProfileTo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Get Image fail ");
            }
        });

        holder.tvUserFrom.setText(invitation.getInviteFrom().getName().split("@")[0]);
        holder.tvUserTo.setText(invitation.getInviteTo().getName().split("@")[0]);
        if (invitation.getMovie() != null)
            holder.bind(invitation.getMovie());
        holder.tvDateTime.setText(invitation.getDateTime());
        holder.tvLocation.setText(invitation.getLocation());
        holder.tvMessage.setText(invitation.getMessage());
        if (invitation.getAcceptedStatus() != null) {
            if (invitation.getAcceptedStatus()) {
                holder.btnAccept.setEnabled(false);
                holder.container.setBackgroundResource(R.drawable.bg_green);
            } else {
                holder.btnDecline.setEnabled(false);
                holder.container.setBackgroundResource(R.drawable.bg_red);
            }
        }
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitation.accept();
                holder.btnAccept.setEnabled(false);
                holder.btnDecline.setEnabled(true);
                holder.container.setBackgroundResource(R.drawable.bg_green);

                db.collection("users").document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<HashMap> invitationMapList = (ArrayList) documentSnapshot.get("invitations");
                        for (int i = 0; i < invitationMapList.size(); i++) {
                            HashMap invitationMap = invitationMapList.get(i);
                            long invitationId = (long) invitationMap.get("invitationId");
                            String dateTime = (String) invitationMap.get("dateTime");
                            if (invitationId == invitation.getInvitationId() && dateTime.equals(invitation.getDateTime())) {
                                invitationMapList.get(i).put("acceptedStatus", true);
                            }
                        }
                        db.collection("users").document(mAuth.getCurrentUser().getEmail())
                                .update("invitations", invitationMapList)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        String toUsername = invitation.getInviteTo().getName();
                                        String fromToken = invitation.getInviteFrom().getToken();
                                        //showToast("send notification to :" + toToken);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                sendMessageToDevice(fromToken, toUsername, "accepted");
                                            }
                                        }).start();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }
                });
            }
        });
        holder.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitation.decline();
                holder.btnDecline.setEnabled(false);
                holder.btnAccept.setEnabled(true);
                holder.container.setBackgroundResource(R.drawable.bg_red);

                db.collection("users").document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<HashMap> invitationMapList = (ArrayList) documentSnapshot.get("invitations");
                        for (int i = 0; i < invitationMapList.size(); i++) {
                            HashMap invitationMap = invitationMapList.get(i);
                            long invitationId = (long) invitationMap.get("invitationId");
                            String dateTime = (String) invitationMap.get("dateTime");
                            if (invitationId == invitation.getInvitationId() && dateTime.equals(invitation.getDateTime())) {
                                invitationMapList.get(i).put("acceptedStatus", false);
                            }
                        }
                        db.collection("users").document(mAuth.getCurrentUser().getEmail())
                                .update("invitations", invitationMapList)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        String toUsername = invitation.getInviteTo().getName();
                                        String fromToken = invitation.getInviteFrom().getToken();
                                        //showToast("send notification to :" + toToken);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                sendMessageToDevice(fromToken, toUsername, "declined");
                                            }
                                        }).start();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }
                });
            }
        });

    }

    private void sendMessageToDevice(String targetToken, String toUsername, String status) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "You got a New Update!");
            jNotification.put("body", toUsername + " " + status + " your invitation!");
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

    // Toast when invitation sent
    private void showToast(String toastMessage) {
        Toast.makeText(this.context, toastMessage, Toast.LENGTH_LONG).show();
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

    @Override
    public int getItemCount() {
        return invitation_list.size();
    }
}
