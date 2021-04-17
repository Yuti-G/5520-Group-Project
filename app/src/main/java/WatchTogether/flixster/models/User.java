package WatchTogether.flixster.models;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class User {
    private int userId;
    private String name;
    private Bitmap profileImg;
    private List<Movie> favoriteList;

    public User() {}

    public User(int userId, String name, Bitmap profileImg, List<Movie> favoriteList) {
        this.userId = userId;
        this.name = name;
        this.profileImg = profileImg;
        this.favoriteList = favoriteList;
    }

    //TODO: Add codes to update database for all setters and add/remove

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(Bitmap profileImg) {
        this.profileImg = profileImg;
    }

    public List<Movie> getFavoriteList() {
        return favoriteList;
    }

    public void addMovieToFavoriteList(Movie movie) {
        this.favoriteList.add(movie);
    }

    public void removeMovieFromFavoriteList(Movie movie) {
        this.favoriteList.remove(movie);
    }

    public boolean likedMovie(Movie movie) {
        return this.favoriteList.contains(movie);
    }
}
