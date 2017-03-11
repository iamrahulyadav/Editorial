package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crash.FirebaseCrash;

import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditorialFeedActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {


    EditorialFullInfo currentEditorialFullInfo = new EditorialFullInfo(new EditorialGeneralInfo(), new EditorialExtraInfo());

    private TextToSpeech tts;

    TextView translateText;
    private BottomSheetBehavior mBottomSheetBehavior;
    public String selectedWord = "null";

    CommentsListViewAdapter mCommentAdapter;

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


        View bottomSheet = findViewById(R.id.editorial_activity_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        if ( EditorialListActivity.isShowingAd ) {
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

        currentEditorialFullInfo.setEditorialGeneralInfo(editorialGeneralInfo);

        if ( !isNetworkAvailable() ) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
            Snackbar.make(translateText, "No Network", Snackbar.LENGTH_LONG)
                    .setAction("No action", null).show();
        }

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
            for ( int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                    .next() ) {
                String possibleWord = definition.substring(start, end);
                if ( Character.isLetterOrDigit(possibleWord.charAt(0)) ) {
                    ClickableSpan clickSpan = getClickableSpan(possibleWord);
                    spans.setSpan(clickSpan, start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        } catch (Exception e) {
            TextView definitionView = (TextView) findViewById(R.id.editorial_text_textview);
            definitionView.setText(textToShow);

        }

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
        if ( translation.word.equalsIgnoreCase(translateText.getText().toString().trim()) ) {
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
        for ( int i = 0; i < dictionary.getWordsynonym().length; i++ ) {
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
        if ( tts != null ) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if ( status == TextToSpeech.SUCCESS ) {

            int result = tts.setLanguage(Locale.US);

            if ( result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED ) {
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
        currentEditorialFullInfo = editorialFullInfo;
        findViewById(R.id.editorialfeed_activity_progressbar).setVisibility(View.GONE);
        initializeCommentList();


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
        speakOutWord(currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText());
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();


    }

    public void initializeCommentList() {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.editorialFeed_commentsystem_linearLayout);
        linearLayout.setVisibility(View.VISIBLE);

        ListView commentListView = (ListView) findViewById(R.id.editorialFeed_comments_listView);

        ArrayList<Comment> commentList = new ArrayList<>();

        if ( currentEditorialFullInfo.getEditorialExtraInfo().getComments() == null ) {
            Comment comment = new Comment();
            comment.setCommentText("No Comment");
            comment.seteMailID("");
            commentList.add(comment);


        } else {
            for ( Comment comment : currentEditorialFullInfo.getEditorialExtraInfo().getComments().values() ) {
                commentList.add(comment);
            }
        }

         mCommentAdapter = new CommentsListViewAdapter(this, commentList);
        commentListView.setAdapter(mCommentAdapter);

        resizeCommentListView();



    }

    private void resizeCommentListView() {
        ListView commentListView = (ListView) findViewById(R.id.editorialFeed_comments_listView);

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());

        switch (mCommentAdapter.getCount()) {

            case 1:
                height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getResources()
                                .getDisplayMetrics());
                break;

            case 2:
                height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                        getResources()
                                .getDisplayMetrics());
                break;
            case 3:
                height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150,
                        getResources()
                                .getDisplayMetrics());
                break;


        }

        ViewGroup.LayoutParams layoutParams = commentListView.getLayoutParams();

        layoutParams.height = height;

        commentListView.setLayoutParams(layoutParams);

    }


    public void insertCommentBtnClick(View view) {

        EditText editText = (EditText) findViewById(R.id.editorialFeed_commentemail_edittext);
        String emailString = editText.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.editorialFeed_commenttext_edittext);
        String commentString = editText2.getText().toString();

        if ( emailString.length() > 5 && commentString.length() > 1 ) {
            DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
            Comment commentToPost = new Comment();
            commentToPost.setCommentText(commentString);
            commentToPost.seteMailID(emailString);

            commentToPost.setCommentDate(SimpleDateFormat.getDateInstance().format(Calendar.getInstance().getTime()));


            dbHelperFirebase.insertComment(currentEditorialFullInfo.getEditorialGeneralInfo()
                    .getEditorialID(), commentToPost);

            editText.setText("");
            editText2.setText("");
            Toast.makeText(this, "Posting", Toast.LENGTH_SHORT).show();

            mCommentAdapter.add(commentToPost);
            mCommentAdapter.notifyDataSetChanged();
            resizeCommentListView();


        } else {
            Toast.makeText(this, "Comment Size is small", Toast.LENGTH_SHORT).show();
        }


    }
}
