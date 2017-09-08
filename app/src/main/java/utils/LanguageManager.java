package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bunny on 25/08/17.
 */

public class LanguageManager {

    static String languageCode;

    public static void setLanguageCode(Context mContext ,String languageCode ) {
        SharedPreferences prefs = mContext.getSharedPreferences("languagemanager", 0);


        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter

        editor.putString("languageCode", languageCode);

        LanguageManager.languageCode=languageCode;

        editor.apply();
    }

    public static String getLanguageCode(Context mContext ) {

        try {
            if (languageCode == null) {
                SharedPreferences prefs = mContext.getSharedPreferences("languagemanager", 0);


                languageCode = prefs.getString("languageCode", "hi");

                return languageCode;

            } else {
                return languageCode;
            }
        }catch (Exception e){
            if (languageCode==null) {
                return "hi";
            }else{
                return languageCode;
            }
        }

    }


}
