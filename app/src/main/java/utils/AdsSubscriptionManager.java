package utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import java.util.Date;

/**
 * Created by bunny on 25/08/17.
 */

public class AdsSubscriptionManager {

    private final static int DAYS_UNTIL_PROMPT = 5;//Min number of days


    public static int ADSPOSITION_COUNT = 8;

    public static void setSubscriptionTime(Context mContext, int subscriptionDays) {
        SharedPreferences prefs = mContext.getSharedPreferences("adsmanager", 0);


        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter

        editor.putLong("subscriptionTime", System.currentTimeMillis());
        editor.putInt("subscriptionDays", subscriptionDays);


        editor.apply();
    }

    public static void setSubscription(Context mContext, boolean isSubscribed) {
        SharedPreferences prefs = mContext.getSharedPreferences("adsmanager", 0);


        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter

        editor.putBoolean("issubscribed", isSubscribed);


        editor.apply();
    }

    public static boolean checkShowAds(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("adsmanager", 0);

        boolean isSubscribed = prefs.getBoolean("issubscribed", false);
        if (isSubscribed) {
            return false;
        }

        long subscriptionTime = prefs.getLong("subscriptionTime", 0);
        int subscriptionDays = prefs.getInt("subscriptionDays", 3);

        long currentTime = System.currentTimeMillis();

        long subscriptionDuration = currentTime - subscriptionTime;

        //3days subscribe
        if (subscriptionDuration < (86400000l * subscriptionDays) && 0 < subscriptionDuration) {
            return false;
        } else {
            return true;
        }


    }


    public static boolean getSubscription(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("adsmanager", 0);

        boolean isSubscribed = prefs.getBoolean("issubscribed", false);
        return isSubscribed;

    }


    public static boolean checkSubscriptionDialog(Context mContext) {

        try {

            SharedPreferences prefs = mContext.getSharedPreferences("adsmanager", 0);
            if (prefs.getBoolean("dontshowagain", false)) {
                return false;
            }

            SharedPreferences.Editor editor = prefs.edit();


            // Get date of first launch
            Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
            if (date_firstLaunch == 0) {
                date_firstLaunch = System.currentTimeMillis();
                editor.putLong("date_firstlaunch", date_firstLaunch);
            }

            // Wait at least n days before opening

            if (!getSubscription(mContext)) {
                if (System.currentTimeMillis() >= date_firstLaunch +
                        (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                    editor.putLong("date_firstlaunch", System.currentTimeMillis());

                    return true;
                }
            }
            editor.apply();


        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }


    public static void showSubscriptionDialog(final Context mContext, final SharedPreferences.Editor editor) {

        try{

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Subscription");

        builder.setMessage("Love Daily Editorial app ? \nRate us on play store, it motivates us to improve it further.")
                .setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=app.craftystudio.vocabulary.dailyeditorial")));


                        if (editor != null) {
                            editor.putLong("date_firstlaunch", System.currentTimeMillis());
                            editor.commit();

                        }
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (editor != null) {
                            editor.putLong("date_firstlaunch", System.currentTimeMillis());
                            editor.commit();

                        }

                        dialogInterface.dismiss();

                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();

    }catch (Exception e){
        e.printStackTrace();
    }


    }


}
