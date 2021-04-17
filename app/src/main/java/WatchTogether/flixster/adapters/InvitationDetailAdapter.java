package WatchTogether.flixster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.codepath.yutinggan.flixster.R;

import org.parceler.Parcels;

import java.util.List;

import WatchTogether.flixster.DetailActivity;
import WatchTogether.flixster.models.Invitation;
import WatchTogether.flixster.models.Movie;

public class InvitationDetailAdapter extends Adapter<InvitationDetailAdapter.ViewHolder>{
    private static final String TAG = "InvitationDetailAdapter";
    private List<Invitation> invitation_list;
    Context context;


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
        // TODO: get inviteFrom and inviteTo user's icon and attach image to imageView
        //String inviteFromProfileImageURL =
        //String inviteToProfileImageURL =
        //Glide.with(context).load(inviteFromProfileImageURL).into(ivProfileFrom);
        //Glide.with(context).load(inviteToProfileImageURL).into(ivProfileTo);
        holder.tvUserFrom.setText(invitation.getInviteFrom().getName());
        holder.tvUserTo.setText(invitation.getInviteTo().getName());
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
            }
        });
        holder.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitation.decline();
                holder.btnDecline.setEnabled(false);
                holder.btnAccept.setEnabled(true);
                holder.container.setBackgroundResource(R.drawable.bg_red);
            }
        });

    }

    @Override
    public int getItemCount() {
        return invitation_list.size();
    }
}
