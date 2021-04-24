package WatchTogether.flixster.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.content.Context;

import com.bumptech.glide.Glide;

import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.LayoutInflater;
import java.util.List;

import com.codepath.yutinggan.flixster.R;

import WatchTogether.flixster.models.Movie;

public class FavoriteAdapter extends Adapter<FavoriteAdapter.ViewHolder>{
    private List<Movie> favorite_list;
    Context context;
    View.OnClickListener onClickListener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.iv_poster);
            imageView.setOnClickListener(onClickListener);
        }
    }

    public FavoriteAdapter(Context context, List<Movie> favorite_list, View.OnClickListener onClickListener)
    {
        this.context = context;
        this.favorite_list = favorite_list;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.item_favorite_general,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set the text of each item of
        // Recycler view with the list items
        Movie movie = favorite_list.get(position);
        // if phone is in landscape
        String imageUrl = movie.getPosterPath();
        Glide.with(context).load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return favorite_list.size();
    }



}
