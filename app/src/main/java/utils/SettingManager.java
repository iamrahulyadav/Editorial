package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bunny on 16/10/17.
 */

public class SettingManager {

    public static void setMuteVoice(Context mContext , boolean muteVoice) {
        SharedPreferences prefs = mContext.getSharedPreferences("settings", 0);


        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter

        editor.putBoolean("muteVoice", muteVoice);



        editor.apply();
    }

    public static boolean getMuteVoice(Context mContext ) {
        SharedPreferences prefs = mContext.getSharedPreferences("settings", 0);

        return prefs.getBoolean("muteVoice", false);

    }



}
