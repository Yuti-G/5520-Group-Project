package WatchTogether.flixster.models;

import java.time.LocalDateTime;

import org.parceler.Parcel;

@Parcel
public class Invitation {
    private int invitationId;
    private String dateTime;
    private String location;
    private Movie movie;
    private String inviteFrom;
    private String inviteTo;
    private String message;
    Boolean accepted;

    //empty constructor needed by the Parceler library
    public Invitation() {}


    public Invitation(int invitationId, String dateTime, String location, Movie movie,
                      String inviteFrom, String inviteTo, String message) {
        this.invitationId = invitationId;
        this.dateTime = dateTime;
        this.location = location;
        this.movie = movie;
        this.inviteFrom = inviteFrom;
        this.inviteTo = inviteTo;
        this.message = message;
    }

    //TODO: Add codes to update database for all setters and accept/decline()

    public int getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(int invitationId) {
        this.invitationId = invitationId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String getInviteFrom() {
        return inviteFrom;
    }

    public void setInviteFrom(String inviteFrom) {
        this.inviteFrom = inviteFrom;
    }

    public String getInviteTo() {
        return inviteTo;
    }

    public void setInviteTo(String inviteTo) {
        this.inviteTo = inviteTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getAcceptedStatus() {
        return this.accepted;
    }

    public void accept() {
        this.accepted = true;
    }

    public void decline() {
        this.accepted = false;
    }
}
