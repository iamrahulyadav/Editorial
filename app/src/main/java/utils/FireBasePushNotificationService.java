package utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.articles.vacabulary.editorial.gamefever.editorial.EditorialFeedActivity;
import app.articles.vacabulary.editorial.gamefever.editorial.R;


/**
 * Created by bunny on 07/07/17.
 */

public class FireBasePushNotificationService extends FirebaseMessagingService {
    String editorialID  ;

    Intent intent;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            editorialID = remoteMessage.getData().get("editorial");






            intent = new Intent(this, EditorialFeedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("editorialID", editorialID);
            intent.putExtra("isPushNotification" , true);

            showNotification(remoteMessage.getData().get("notificationT"), remoteMessage.getData().get("notificationB"));

        }

        // Check if message contains a notification payload.
        /*if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
        }*/
    }

    private void showNotification(String title, String body) {



        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle( title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText( body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(title))
                ;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int id = (int) System.currentTimeMillis();


        notificationManager.notify(id /* ID of notification */,notificationBuilder.build() );
    }
}
