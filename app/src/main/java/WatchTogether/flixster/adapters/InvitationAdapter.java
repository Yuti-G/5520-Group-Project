package WatchTogether.flixster.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.codepath.yutinggan.flixster.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;

public class InvitationAdapter extends Adapter<InvitationAdapter.ViewHolder>{
    private static final String TAG = "InvitationAdapter";
    private List<Invitation> invitation_list;
    Context context;
    View.OnClickListener onClickListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.iv_invitation);
            textView = (TextView)view.findViewById(R.id.tv_display);
            imageView.setOnClickListener(onClickListener);
            textView.setOnClickListener(onClickListener);
        }
    }

    public InvitationAdapter(Context context, List<Invitation> invitation_list, View.OnClickListener onClickListener)
    {
        this.context = context;
        this.invitation_list = invitation_list;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.item_invitation_general,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set the text of each item of
        // Recycler view with the list items
        Invitation invitation = invitation_list.get(position);
        String text;
        if (invitation.getMovie() != null)
            text = "invites you to watch" + invitation.getMovie().getTitle() + " together!";
        else
            text = "invites you to watch xxx together!";
        holder.textView.setText(text);
        // TODO get inviteFrom user's icon and attach image to imageView
        //String inviteFromProfileImageURL =
        //Glide.with(context).load(inviteFromProfileImageURL).into(imageView);

        // get inviteFrom user's icon and attach image to imageView
        String  inviteFromUserId = invitation.getInviteFrom().getUserId();
        Log.d(TAG, "get invited from " + inviteFromUserId);
        StorageReference fileRef = firebaseStorage.getReference().child(inviteFromUserId + ".jpeg");

        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "Image URI:  " + uri);
                //holder.imageView.setImageURI(uri);
                //Glide.with(context).load(uri).into(holder.imageView);
                Picasso.get().load(uri).into(holder.imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Get Image fail ");
            }
        });

    }

    @Override
    public int getItemCount() {
        return invitation_list.size();
    }

}
