package WatchTogether.flixster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import com.codepath.yutinggan.flixster.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.net.URL;

public class MyFirebaseMessageService extends FirebaseMessagingService {
    public static final String TAG = "message";
    private static final String CHANNEL_ID  = "CHANNEL_ID";
    private static final String CHANNEL_NAME  = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION  = "CHANNEL_DESCRIPTION";

//    public MyFirebaseMessageService() {
//        super();
//        Log.d(TAG, "service running");
//    }

//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        Log.d(TAG, "notification received");
//
//
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: " + remoteMessage.getFrom() + "recieved an message! ");
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            showNotification(remoteMessage);
//        }
//    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("message", "Refreshed token from MessageService: " + token);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", token).apply();
    }

    // get new token, if no nre toekn created, return empty
    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

    //set notification
//    private void showNotification(RemoteMessage remoteMessage) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Notification notification;
//        NotificationCompat.Builder builder;
//        NotificationManager notificationManager = getSystemService(NotificationManager.class);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
//        {
//            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
//            // Configure the notification channel
//            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
//            notificationManager.createNotificationChannel(notificationChannel);
//            builder = new NotificationCompat.Builder(this,CHANNEL_ID);
//
//        }
//        else {
//            builder = new NotificationCompat.Builder(this);
//        }
//
//
//        notification = builder.setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent)
//                .build();
//
//
//        notificationManager.notify(0,notification);
//    }

}