package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bunny on 12/09/17.
 */

public class PushNotificationManager {
    public static void setPushNotification(Context mContext ,boolean pushNotification) {
        SharedPreferences prefs = mContext.getSharedPreferences("push_manager", 0);


        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter

        editor.putBoolean("pushNotification", pushNotification);


        editor.apply();
    }

    public static boolean getPushNotification(Context mContext ) {
        SharedPreferences prefs = mContext.getSharedPreferences("push_manager", 0);



       return prefs.getBoolean("pushNotification", true) ;

    }







}
