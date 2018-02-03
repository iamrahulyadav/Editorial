package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SignUpEvent;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import utils.LanguageManager;
import utils.NightModeManager;
import utils.PushNotificationManager;
import utils.SettingManager;


public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;

    GoogleApiClient mGoogleApiClient;

    TextView textSizeTextView, languageTextView, readerSpeedTextView, liteReaderModeTextView;
    Switch notificationSwitch, nightModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NightModeManager.getNightMode(getApplicationContext())) {
            setTheme(R.style.FeedActivityThemeDark);
        }
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            try {
                TextView textView = (TextView) findViewById(R.id.signIn_notification_textView);
                textView.setText("Signed in as - " + currentUser.getEmail());

                CardView cardView = (CardView) findViewById(R.id.signIn_signOut_cardView);
                cardView.setVisibility(View.VISIBLE);

                cardView = (CardView) findViewById(R.id.signIn_cardView);
                cardView.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        textSizeTextView = (TextView) findViewById(R.id.setting_textSize_textView);
        languageTextView = (TextView) findViewById(R.id.setting_language_textView);
        readerSpeedTextView = (TextView) findViewById(R.id.setting_voiceReader_textView);
        notificationSwitch = (Switch) findViewById(R.id.setting_notification_switch);
        nightModeSwitch = (Switch) findViewById(R.id.setting_nightMode_switch);
        liteReaderModeTextView= (TextView)findViewById(R.id.setting_liteReaderMode_textView);


        initializeActivity();

    }

    private void initializeActivity() {

        /*Text size*/
        int textsize = SettingManager.getTextSize(this);
        if (textsize == 16) {
            textSizeTextView.setText("Small");
        } else if (textsize == 18) {
            textSizeTextView.setText("Medium");
        } else if (textsize == 20) {
            textSizeTextView.setText("Large");
        } else if (textsize == 22) {
            textSizeTextView.setText("Extra Large");
        }


        /*Language */
        String languageCode = LanguageManager.getLanguageCode(SignInActivity.this);
        if (languageCode.equalsIgnoreCase("hi")) {
            languageTextView.setText("Hindi");
        } else if (languageCode.equalsIgnoreCase("te")) {
            languageTextView.setText("Telugu");

        } else if (languageCode.equalsIgnoreCase("mr")) {
            languageTextView.setText("Marathi");

        } else if (languageCode.equalsIgnoreCase("ta")) {
            languageTextView.setText("Tamil");

        } else if (languageCode.equalsIgnoreCase("bn")) {
            languageTextView.setText("Bengali");

        } else if (languageCode.equalsIgnoreCase("kn")) {
            languageTextView.setText("Kannada");

        } else if (languageCode.equalsIgnoreCase("ur")) {
            languageTextView.setText("Urdu");

        } else if (languageCode.equalsIgnoreCase("ml")) {
            languageTextView.setText("Malayalam");

        } else if (languageCode.equalsIgnoreCase("gu")) {
            languageTextView.setText("Gujarati");

        } else if (languageCode.equalsIgnoreCase("pa")) {
            languageTextView.setText("Punjabi");

        } else {
            languageTextView.setText("Hindi");

        }


        /*Notification*/
        if (PushNotificationManager.getPushNotification(this)) {
            notificationSwitch.setChecked(true);
        } else {
            notificationSwitch.setChecked(false);
        }

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    PushNotificationManager.setPushNotification(SignInActivity.this, false);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("subscribed");
                    Toast.makeText(SignInActivity.this, "Notification - " + isChecked, Toast.LENGTH_SHORT).show();

                } else {
                    PushNotificationManager.setPushNotification(SignInActivity.this, true);
                    FirebaseMessaging.getInstance().subscribeToTopic("subscribed");
                    Toast.makeText(SignInActivity.this, "Notification - " + isChecked, Toast.LENGTH_SHORT).show();

                }
            }
        });


        nightModeSwitch.setChecked(NightModeManager.getNightMode(this));
        nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    NightModeManager.setNightMode(SignInActivity.this, true);
                } else {
                    NightModeManager.setNightMode(SignInActivity.this, false);
                }
                recreate();
            }
        });

        /*Voice Reader*/
        float speed= SettingManager.getVoiceReaderSpeed(this);
        if (speed ==0.85f){
            readerSpeedTextView.setText("Slow");
        }else if(speed == 1f){
            readerSpeedTextView.setText("Default");
        }else {
            readerSpeedTextView.setText("Fast");
        }

    }

    public void onSignInClick(View view) {


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Sign in", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sign in", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            try {
                                Toast.makeText(SignInActivity.this, "Signed in as " + user.getEmail(), Toast.LENGTH_SHORT).show();

                                Answers.getInstance().logSignUp(new SignUpEvent().putSuccess(true).putCustomAttribute("Email id", user.getEmail()));

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Sign in", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Answers.getInstance().logSignUp(new SignUpEvent().putSuccess(false));

                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.


    }

    public void onSignOutClick(View view) {
        if (mAuth != null) {
            mAuth.signOut();


            CardView cardView = (CardView) findViewById(R.id.signIn_signOut_cardView);
            cardView.setVisibility(View.GONE);

            cardView = (CardView) findViewById(R.id.signIn_cardView);
            cardView.setVisibility(View.VISIBLE);
        }
    }

    public void onTextSizeClick(View view) {

        final CharSequence sources[] = new CharSequence[]{"Small", "Medium", "Large", "Extra Large"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Text Size");
        builder.setItems(sources, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                int size = 18;

                if (which == 0) {
                    size = 16;
                    textSizeTextView.setText("Small");
                } else if (which == 1) {
                    size = 18;
                    textSizeTextView.setText("Medium");
                } else if (which == 2) {
                    size = 20;
                    textSizeTextView.setText("Large");
                } else if (which == 3) {
                    size = 22;
                    textSizeTextView.setText("Extra Large");
                }


                SettingManager.setTextSize(SignInActivity.this, size);

            }
        });

        builder.show();

    }

    public void onLanguageClick(View view) {


        String languages[] = new String[]{"Hindi", "Telugu", "Marathi", "Tamil", "Bengali", "Kannada", "Urdu", "Malayalam", "Gujarati", "Punjabi"};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language");
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]

                String languageCode = "";
                if (which == 0) {
                    languageCode = "hi";
                    languageTextView.setText("Hindi");
                } else if (which == 1) {
                    languageCode = "te";
                    languageTextView.setText("Telugu");

                } else if (which == 2) {
                    languageCode = "mr";
                    languageTextView.setText("Marathi");

                } else if (which == 3) {
                    languageCode = "ta";
                    languageTextView.setText("Tamil");

                } else if (which == 4) {
                    languageCode = "bn";
                    languageTextView.setText("Bengali");

                } else if (which == 5) {
                    languageCode = "kn";
                    languageTextView.setText("Kannada");

                } else if (which == 6) {
                    languageCode = "ur";
                    languageTextView.setText("Urdu");

                } else if (which == 7) {
                    languageCode = "ml";
                    languageTextView.setText("Malayalam");

                } else if (which == 8) {
                    languageCode = "gu";
                    languageTextView.setText("Gujarati");

                } else if (which == 9) {
                    languageCode = "pa";
                    languageTextView.setText("Punjabi");

                } else {
                    languageCode = "hi";
                    languageTextView.setText("Hindi");

                }

                LanguageManager.setLanguageCode(SignInActivity.this, languageCode);


            }
        });
        builder.show();

    }


    public void onVoiceReaderClick(View view) {

        final CharSequence sources[] = new CharSequence[]{"Slow", "Default", "Fast"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Voice Reader Speed");
        builder.setItems(sources, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                float speed = 18;

                if (which == 0) {
                    speed = 0.85f;
                    textSizeTextView.setText("Slow");
                } else if (which == 1) {
                    speed = 1f;
                    textSizeTextView.setText("Default");
                } else if (which == 2) {
                    speed = 1.2f;
                    textSizeTextView.setText("Fast");
                }

                SettingManager.setVoiceReaderSpeed(SignInActivity.this, speed);

            }
        });

        builder.show();

    }

    public void onLiteReaderMode(View view) {

        final CharSequence sources[] = new CharSequence[]{"Default", "Lite"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Reader Mode");
        builder.setItems(sources, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                boolean isLiteMode=false;

                if (which == 0) {

                    liteReaderModeTextView.setText("Default");
                    isLiteMode=false;
                } else if (which == 1) {

                    liteReaderModeTextView.setText("Lite (Better Performance))");
                    isLiteMode=true;
                }

                SettingManager.setLiteReaderMode(SignInActivity.this, isLiteMode);

            }
        });

        builder.show();
    }
}
