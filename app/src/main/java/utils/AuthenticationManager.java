package utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.articles.vacabulary.editorial.gamefever.editorial.SignInActivity;

/**
 * Created by bunny on 15/09/17.
 */

public class AuthenticationManager {

    Context mContext;

    private FirebaseAuth mAuth;


    public AuthenticationManager(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();

    }

    public static String getUserUID( Context mContext) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            showSignUpDialogue(mContext);
        }


        return null;
    }

    private static void showSignUpDialogue(final Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false)
                .setTitle("Sign in")
                .setMessage("Sign in to create notes")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(mContext, SignInActivity.class);
                        mContext.startActivity(intent);
                    }
                });

        // Create the AlertDialog object and return it
        builder.setCancelable(false);
        builder.create();
        builder.show();

    }


}
