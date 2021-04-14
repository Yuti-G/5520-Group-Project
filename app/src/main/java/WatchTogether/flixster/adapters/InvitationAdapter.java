package WatchTogether.flixster.adapters;

import android.content.Context;
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

import java.util.List;

import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;

public class InvitationAdapter extends Adapter<InvitationAdapter.ViewHolder>{
    private List<Invitation> invitation_list;
    Context context;
    ImageView imageView;
    TextView textView;
    View.OnClickListener onClickListener;


    public class ViewHolder extends RecyclerView.ViewHolder {
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
            text = "invites you to watch" + invitation.getMovie().getTitle() + "together!";
        else
            text = "invites you to watch xxx together!";
        textView.setText(text);
        // TODO get inviteFrom user's icon and attach image to imageView
        //String inviteFromProfileImageURL =
        //Glide.with(context).load(inviteFromProfileImageURL).into(imageView);
    }

    @Override
    public int getItemCount() {
        return invitation_list.size();
    }

}
