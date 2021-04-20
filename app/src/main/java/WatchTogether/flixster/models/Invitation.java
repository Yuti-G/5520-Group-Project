package WatchTogether.flixster.models;

import WatchTogether.flixster.models.User;

import org.parceler.Parcel;

@Parcel
public class Invitation {
    private int invitationId;
    private String dateTime;
    private String location;
    private Movie movie;
    private User inviteFrom;
    private User inviteTo;
    private String message;
    Boolean accepted;

    //empty constructor needed by the Parceler library
    public Invitation() {}


    public Invitation(int invitationId, String dateTime, String location, Movie movie,
                      User inviteFrom, User inviteTo, String message, boolean accepted) {
        this.invitationId = invitationId;
        this.dateTime = dateTime;
        this.location = location;
        this.movie = movie;
        this.inviteFrom = inviteFrom;
        this.inviteTo = inviteTo;
        this.message = message;
        this.accepted = accepted;
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

    public User getInviteFrom() {
        return inviteFrom;
    }

    public void setInviteFrom(User inviteFrom) {
        this.inviteFrom = inviteFrom;
    }

    public User getInviteTo() {
        return inviteTo;
    }

    public void setInviteTo(User inviteTo) {
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
