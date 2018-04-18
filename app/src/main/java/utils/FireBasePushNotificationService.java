package utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.articles.vacabulary.editorial.gamefever.editorial.CurrentAffairsFeedActivity;
import app.articles.vacabulary.editorial.gamefever.editorial.EditorialFeedActivity;
import app.articles.vacabulary.editorial.gamefever.editorial.R;


/**
 * Created by bunny on 07/07/17.
 */

public class FireBasePushNotificationService extends FirebaseMessagingService {
    String editorialID;

    Intent intent;
    private int contentType=0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        /*if (!PushNotificationManager.getPushNotification(this)) {
            return;
        }*/

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {


            try {
                String messageType = remoteMessage.getData().get("type");
                if (messageType.equalsIgnoreCase("link")) {
                    showLinkNotification(remoteMessage.getData().get("notificationT"), remoteMessage.getData().get("notificationB"),remoteMessage.getData().get("linkToOpen"));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                 contentType = Integer.valueOf(remoteMessage.getData().get("contentType"));


            } catch (Exception e) {
                e.printStackTrace();
            }


            if (contentType!=0) {

                CurrentAffairs currentAffairs = new CurrentAffairs();
                currentAffairs.setId(Integer.valueOf(remoteMessage.getData().get("id")));
                currentAffairs.setTitle(remoteMessage.getData().get("notificationT"));

                intent = new Intent(this, CurrentAffairsFeedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("news", currentAffairs);
                intent.putExtra("isPushNotification", true);
            }else{

                editorialID = remoteMessage.getData().get("editorial");

                intent = new Intent(this, EditorialFeedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("editorialID", editorialID);
                intent.putExtra("isPushNotification", true);

            }

            showNotification(remoteMessage.getData().get("notificationT"), remoteMessage.getData().get("notificationB"));

        }

        // Check if message contains a notification payload.
        /*if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
        }*/
    }

    private void showLinkNotification(String notificationT, String notificationB, String linkToOpen) {

        Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkToOpen));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), linkIntent, PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(notificationT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(notificationB)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int id = (int) System.currentTimeMillis();


        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }

    private void showNotification(String title, String body) {


        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int id = (int) System.currentTimeMillis();


        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }
}
