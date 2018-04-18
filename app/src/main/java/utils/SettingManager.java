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

    public static void setTextSize(Context mContext , int size) {
        SharedPreferences prefs = mContext.getSharedPreferences("settings", 0);


        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter

        editor.putInt("textSize", size);



        editor.apply();
    }

    public static int getTextSize(Context mContext ) {
        SharedPreferences prefs = mContext.getSharedPreferences("settings", 0);

        return prefs.getInt("textSize", 18);

    }


    public static void setVoiceReaderSpeed(Context mContext , float speed) {
        SharedPreferences prefs = mContext.getSharedPreferences("settings", 0);


        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter

        editor.putFloat("voiceReaderSpeed", speed);



        editor.apply();
    }

    public static float getVoiceReaderSpeed(Context mContext ) {
        SharedPreferences prefs = mContext.getSharedPreferences("settings", 0);

        return prefs.getFloat("voiceReaderSpeed", 1);

    }

    public static void setLiteReaderMode(Context mContext , boolean liteReaderMode) {
        SharedPreferences prefs = mContext.getSharedPreferences("settings", 0);


        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        editor.putBoolean("liteMode", liteReaderMode);

        editor.apply();
    }

    public static boolean getLiteReaderMode(Context mContext ) {
        SharedPreferences prefs = mContext.getSharedPreferences("settings", 0);

        return prefs.getBoolean("liteMode", false);

    }


    public static void setTabPosition(Context mContext , int position) {
        SharedPreferences prefs = mContext.getSharedPreferences("settings", 0);


        SharedPreferences.Editor editor = prefs.edit();


        editor.putInt("tabposition", position);



        editor.apply();
    }

    public static int getTabPosition(Context mContext ) {
        SharedPreferences prefs = mContext.getSharedPreferences("settings", 0);

        return prefs.getInt("tabposition", 1);

    }



}
