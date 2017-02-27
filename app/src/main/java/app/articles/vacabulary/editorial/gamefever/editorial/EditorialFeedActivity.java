package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crash.FirebaseCrash;

import java.text.BreakIterator;
import java.util.Locale;

public class EditorialFeedActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    String editorialText = "";

    private TextToSpeech tts;

    TextView translateText;
    private BottomSheetBehavior mBottomSheetBehavior;
    public String selectedWord = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editorial_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.editorialfeed_activity_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Feeds");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        tts = new TextToSpeech(this, this);
        Intent i = getIntent();
        intialiseViewAndFetch(i);

        translateText = (TextView) findViewById(R.id.editorial_feed_cardview_textview);

        init(editorialText);

        View bottomSheet = findViewById(R.id.editorial_activity_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        if (EditorialListActivity.isShowingAd) {
            initializeAds();
        }


    }

    private void intialiseViewAndFetch(Intent i) {
        EditorialGeneralInfo editorialGeneralInfo = new EditorialGeneralInfo();
        editorialGeneralInfo.setEditorialID(i.getExtras().getString("editorialID"));
        editorialGeneralInfo.setEditorialDate(i.getExtras().getString("editorialDate"));
        editorialGeneralInfo.setEditorialHeading(i.getExtras().getString("editorialHeading"));
        editorialGeneralInfo.setEditorialSource(i.getExtras().getString("editorialSource"));
        editorialGeneralInfo.setEditorialSubHeading(i.getExtras().getString("editorialSubheading"));
        editorialGeneralInfo.setEditorialTag(i.getExtras().getString("editorialTag"));

        DBHelperFirebase dbHelper = new DBHelperFirebase();
        dbHelper.getEditorialFullInfoByID(editorialGeneralInfo, this);

        TextView tv = (TextView) findViewById(R.id.editorial_heading_textview);
        tv.setText(editorialGeneralInfo.getEditorialHeading());
        tv = (TextView) findViewById(R.id.editorial_source_textview);
        tv.setText(editorialGeneralInfo.getEditorialSource());
        tv = (TextView) findViewById(R.id.editorial_date_textview);
        tv.setText(editorialGeneralInfo.getEditorialDate());
        tv = (TextView) findViewById(R.id.editorial_tag_textview);
        tv.setText(editorialGeneralInfo.getEditorialTag());


    }

    private void init(String textToShow) {

        try {
            FirebaseCrash.log("Showing spannable text");
            String definition = textToShow;
            TextView definitionView = (TextView) findViewById(R.id.editorial_text_textview);
            definitionView.setMovementMethod(LinkMovementMethod.getInstance());
            definitionView.setText(definition, TextView.BufferType.SPANNABLE);
            Spannable spans = (Spannable) definitionView.getText();
            BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
            iterator.setText(definition);
            int start = iterator.first();
            for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                    .next()) {
                String possibleWord = definition.substring(start, end);
                if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
                    ClickableSpan clickSpan = getClickableSpan(possibleWord);
                    spans.setSpan(clickSpan, start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }catch(Exception e){
            TextView definitionView = (TextView) findViewById(R.id.editorial_text_textview);
            definitionView.setText(textToShow);

        }

    }

    public void fetchEditorial(String id) {
        //call to fetch editorial object from firebase db
    }

    public void updateEditorialFetch(String editorial) {

        TextView tv;
        tv = (TextView) findViewById(R.id.editorial_heading_textview);
//initialize heading
        tv = (TextView) findViewById(R.id.editorial_date_textview);
//initialize date
        tv = (TextView) findViewById(R.id.editorial_source_textview);
//initialize source
        tv = (TextView) findViewById(R.id.editorial_text_textview);
//initialize text
        init(editorialText);


    }

    private ClickableSpan getClickableSpan(final String word) {
        return new ClickableSpan() {
            final String mWord;

            {
                mWord = word;
            }

            @Override
            public void onClick(View widget) {
                Log.d("tapped on:", mWord);

                onWordTap(mWord);
            }

            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(ds.linkColor);
                //super.updateDrawState(ds);
            }
        };
    }

    private void onWordTap(String mWord) {

        speakOutWord(mWord);
        Translation translation = new Translation(mWord);
        translation.fetchTranslation(this);

        translateText.setText(mWord);
        selectedWord = mWord;
    }

    public void updateTranslateText(Translation translation) {
        if (translation.word.equalsIgnoreCase(translateText.getText().toString().trim())) {
            translateText.setText(translation.word + " = " + translation.wordTranslation);
        }
    }

    public void updateDictionaryText(final Dictionary dictionary) {
        TextView tv;
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_meaning_textview);
        tv.setText(dictionary.getWordMeaning());
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_partspeech_textview);
        tv.setText(dictionary.getWordPartOfSpeech());
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_synonyms_textview);
        String synonymstring = "";
        for (int i = 0; i < dictionary.getWordsynonym().length; i++) {
            synonymstring = synonymstring + dictionary.getWordsynonym()[i] + " , ";
        }
        tv.setText(synonymstring);

        Button bt = (Button) findViewById(R.id.editorial_bottomSheet_add_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler databaseHandler = new DatabaseHandler(EditorialFeedActivity.this);
                databaseHandler.addToDictionary(dictionary);
                Toast.makeText(EditorialFeedActivity.this, "Word Added To Dictionary", Toast.LENGTH_SHORT).show();


            }
        });
    }

    public void onDictionaryClick(View v) {
        //Intent i =new Intent(this ,);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        openBottomSheet(true);
        Dictionary dictionary = new Dictionary(selectedWord);
        dictionary.fetchWordMeaning(selectedWord, this);

        TextView tv = (TextView) findViewById(R.id.editorial_bottomsheet_heading_textview);
        tv.setText(translateText.getText());


    }

    private void openBottomSheet(boolean b) {

        TextView tv;
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_meaning_textview);
        tv.setText("Loading...");
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_partspeech_textview);
        tv.setText("Loading...");
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_synonyms_textview);

        tv.setText("Loading...");
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // btnSpeak.setEnabled(true);
                speakOutWord("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOutWord(String speakWord) {


        tts.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onGetEditorialFullInfo(EditorialFullInfo editorialFullInfo) {

        init(editorialFullInfo.getEditorialExtraInfo().getEditorialText());
        editorialText =editorialFullInfo.getEditorialExtraInfo().getEditorialText();
        findViewById(R.id.editorialfeed_activity_progressbar).setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_editorial_list_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_about:
                // search action
                onAboutClick();
                return true;

            case R.id.action_refresh:
                // refresh
                onRefreashClick();
                return true;
            case R.id.action_share:
                // help action
                onShareClick();
                return true;
            case R.id.action_vacabulary:
                // help action
                onVacabularyClick();
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
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, EditorialListActivity.shareLink);
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
/*

    public void addToDictionary(View view) {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        Dictionary dictionary = new Dictionary(selectedWord);

        TextView tv;
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_meaning_textview);
        dictionary.setWordMeaning(tv.getText().toString());
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_partspeech_textview);
        dictionary.setWordPartOfSpeech(tv.getText().toString());

        tv = (TextView) findViewById(R.id.editorial_bottomsheet_synonyms_textview);
        dictionary.setWordsynonym(new String[]{tv.getText().toString()});


        databaseHandler.addToDictionary(dictionary);
    }
*/

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void initializeAds() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8455191357100024~6634740792");
        AdView mAdView = (AdView) findViewById(R.id.editorialfeed_activity_adView);
        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

    }

    public void readFullArticle(View view) {
        speakOutWord(editorialText);
    }
}
