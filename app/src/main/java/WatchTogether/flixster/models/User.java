package WatchTogether.flixster.models;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class User {
    private String userId;
    private String name;
    private Bitmap profileImg;

    public User() {}

    public User(String userId, String name, Bitmap profileImg) {
        this.userId = userId;
        this.name = name;
        this.profileImg = profileImg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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

}
