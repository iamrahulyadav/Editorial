package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;

import utils.AdsSubscriptionManager;
import utils.AuthenticationManager;
import utils.NightModeManager;
import utils.ShortNotesManager;


public class NotesFeedActivity extends AppCompatActivity {
    ShortNotesManager shortNotesManager;
    String shortNotesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NightModeManager.getNightMode(this)){
            setTheme(R.style.FeedActivityThemeDark);
        }

        setContentView(R.layout.activity_notes_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            Answers.getInstance().logCustom(new CustomEvent("Notes activity feed open").putCustomAttribute("Editorial heading",shortNotesManager.getShortNoteHeading()));
        }catch (Exception e){
            e.printStackTrace();
        }

        initializeActivity();


        //initializeInterstitialAd();

    }


    private void initializeActivity() {
        Intent intent = getIntent();

        shortNotesManager = (ShortNotesManager) intent.getSerializableExtra("shortNotes");
        shortNotesText = intent.getStringExtra("shortNotesText");
        if (shortNotesManager != null) {

            initializeView();

        } else {

        }

        if (AdsSubscriptionManager.checkShowAds(this)) {
           initializeNativeAds(true);
        }


    }

    private void initializeNativeAds() {
        NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.notesFeed_top_nativeAds);
        adView.setVisibility(View.VISIBLE);

        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "notes feed native").putCustomAttribute("errorType", i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void initializeNativeAds(boolean isFacebook) {
       final NativeAd nativeAd = new NativeAd(this, "113079036048193_120131468676283");


        nativeAd.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                initializeNativeAds();
            }

            @Override
            public void onAdLoaded(Ad ad) {

                NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                        .setAutoplay(true);

                View adView = NativeAdView.render(NotesFeedActivity.this, nativeAd, NativeAdView.Type.HEIGHT_400,viewAttributes);
                // Find the Ad Container
                CardView adContainer = (CardView) findViewById(R.id.notesFeed_adContainer_linearLayout);

                // Add the ad view to your activity layout
                adContainer.addView(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        // Initiate a request to load an ad.
        nativeAd.loadAd();



    }

    private void initializeView() {
        TextView textView = (TextView) findViewById(R.id.notesFeed_heading_textview);
        textView.setText(shortNotesManager.getShortNoteHeading());

        textView = (TextView) findViewById(R.id.notesFeed_date_textview);
        textView.setText(shortNotesManager.getNoteArticleDate());

        textView = (TextView) findViewById(R.id.notesFeed_source_textview);
        textView.setText(shortNotesManager.getNoteArticleSource());

        textView = (TextView) findViewById(R.id.notesFeed_text_textview);


        String notesString = "";
        for (String tempstr : shortNotesManager.getShortNotePointList().values()) {
            tempstr = "• " + tempstr + "\n\n";
            notesString = notesString.concat(tempstr);
        }

        textView.setText(notesString);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_notes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {


            case R.id.notes_action_delete:
                // help action
                onDeleteClicked();
                return true;

            case R.id.notes_action_read_editorial:
                onReadEditorialClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }

    private void onReadEditorialClick() {
        Intent intent = new Intent(this, EditorialFeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("editorialID", shortNotesManager.getNoteArticleID());
        intent.putExtra("isPushNotification", true);


        startActivity(intent);

    }

    private void onDeleteClicked() {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this Notes")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteNotes();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void deleteNotes() {
        final ProgressDialog pd = ProgressDialog.show(this, "Deleting", "Please wait");

        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.deleteShortNotes(AuthenticationManager.getUserUID(this), shortNotesManager.getNoteArticleID(), new DBHelperFirebase.OnShortNoteListListener() {
            @Override
            public void onShortNoteList(ArrayList<ShortNotesManager> shortNotesManagerArrayList, boolean isSuccessful) {

            }

            @Override
            public void onShortNoteUpload(boolean isSuccessful) {

                pd.dismiss();
            }
        });
    }

     /* private void initializeInterstitialAd() {
        final InterstitialAd interstitialAd = new InterstitialAd(this, "113079036048193_118000352222728");
        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(NotesFeedActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();

    }
*/

}
