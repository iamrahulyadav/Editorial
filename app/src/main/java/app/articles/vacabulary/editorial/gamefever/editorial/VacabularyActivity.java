package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import java.util.ArrayList;

public class VacabularyActivity extends AppCompatActivity {

    ArrayList<Dictionary> dictionaryArrayList;
    ArrayList<String> arrayListword = new ArrayList<String>();
    ArrayAdapter<String> mAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.FeedActivityThemeDark);

        }

        setContentView(R.layout.activity_vacabulary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.vacabulary_activity_toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("vocabulary");
            //getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView listView = (ListView) findViewById(R.id.vacabulary_activity_listview);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListword);

        listView.setAdapter(mAdapter);
        fetchDictionaryword();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                openBottomSheet(dictionaryArrayList.get(position));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                onRecyclerViewItemLongClick(position);
                return true;
            }
        });

        View bottomSheet = findViewById(R.id.vacabulary_activity_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        setActivityTheme();
         final InterstitialAd interstitialAd;
        interstitialAd = new InterstitialAd(this, "113079036048193_118000352222728");

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
                Toast.makeText(VacabularyActivity.this, "Error: " + adError.getErrorMessage(),
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
        interstitialAd.loadAd();

    }

    private void onRecyclerViewItemLongClick(final int position) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this Bookmark")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteNotes(position);
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

    private void deleteNotes(final int position) {

        boolean result= new DatabaseHandler(this).deleteDictionaryWord(dictionaryArrayList.get(position).getWord());

        if (result){
            arrayListword.remove(position);
            mAdapter.notifyDataSetChanged();
        }

    }



    private void setActivityTheme() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("theme_list", "Day");

    }

    private void fetchDictionaryword() {
        DatabaseHandler databasehandler = new DatabaseHandler(this);
        dictionaryArrayList = databasehandler.getAllDictionaryWord();
        for (Dictionary dictionary : dictionaryArrayList) {
            arrayListword.add(dictionary.getWord());
        }

        mAdapter.notifyDataSetChanged();

    }

    public void openBottomSheet(Dictionary dictionary) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        TextView tv;
        tv = (TextView) findViewById(R.id.vacabulary_bottomsheet_heading_textview);
        tv.setText(dictionary.getWord());
        tv = (TextView) findViewById(R.id.vacabulary_bottomsheet_meaning_textview);
        tv.setText(dictionary.getWordMeaning());
        tv = (TextView) findViewById(R.id.vacabulary_bottomsheet_partspeech_textview);
        tv.setText(dictionary.getWordPartOfSpeech());
        tv = (TextView) findViewById(R.id.vacabulary_bottomsheet_synonyms_textview);
        String synonymstring = "";
        for (int i = 0; i < dictionary.getWordsynonym().length; i++) {
            synonymstring = synonymstring + dictionary.getWordsynonym()[i] + " , ";
        }
        tv.setText(synonymstring);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.activity_editorial_list_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {


            case R.id.action_share:
                // help action
                onShareClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onVacabularyClick() {
        Intent i = new Intent(this, VacabularyActivity.class);
        startActivity(i);

    }

    private void onShareClick() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the app and Start reading");
        //sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, EditorialListWithNavActivity.shareLink);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void onSettingClick() {
    }

    private void onAboutClick() {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    private void onRefreashClick() {


    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
