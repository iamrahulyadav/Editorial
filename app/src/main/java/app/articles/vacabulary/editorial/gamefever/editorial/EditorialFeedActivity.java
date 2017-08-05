package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.messaging.FirebaseMessaging;


import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import utils.UrlShortner;

public class EditorialFeedActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {


    EditorialFullInfo currentEditorialFullInfo = new EditorialFullInfo(new EditorialGeneralInfo(), new EditorialExtraInfo());

    private TextToSpeech tts;

    TextView translateText;
    private BottomSheetBehavior mBottomSheetBehavior;
    public String selectedWord = "null";

    CommentsListViewAdapter mCommentAdapter;
    ArrayList<Comment> commentList = new ArrayList<>();

    boolean isPushNotification = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editorial_feed);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.editorialfeed_activity_toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("Feeds");
            // getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        } catch (Exception e) {

        }

        tts = new TextToSpeech(this, this);
        Intent i = getIntent();
        intialiseViewAndFetch(i);

        translateText = (TextView) findViewById(R.id.editorial_feed_cardview_textview);


        View bottomSheet = findViewById(R.id.editorial_activity_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {


                    openBottomSheet(true);

                    Dictionary dictionary = new Dictionary(selectedWord);
                    dictionary.fetchWordMeaning(selectedWord, EditorialFeedActivity.this);

                    TextView tv = (TextView) findViewById(R.id.editorial_bottomsheet_heading_textview);
                    tv.setText(translateText.getText());

                    initializeBottomSheetAd();

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        if (EditorialListWithNavActivity.isShowingAd) {
            initializeAds();
        }

        setThemeinactivity();

        checkRateUsOption();


    }


    private void checkRateUsOption() {
        SharedPreferences prefs = getSharedPreferences("RateUsNum", MODE_PRIVATE);
        int ratenum = prefs.getInt("ratenum", 0);

        if (ratenum < 5) {
            ratenum++;

            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("ratenum", ratenum);
            edit.apply();


        } else {

            boolean rateeus = prefs.getBoolean("rateus", false);
            if (rateeus) {


                return;
            } else {


                //show rate Pop Up


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorialFeedActivity.this);


                // Setting Dialog Title
                alertDialog.setTitle("Rate us");

                // Setting Dialog Message
                alertDialog.setMessage("Do you like this app");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.ic_menu_share);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Toast.makeText(EditorialFeedActivity.this, "ThankYou", Toast.LENGTH_SHORT).show();

                        // Write your code here to invoke YES event
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(EditorialListWithNavActivity.shareLink)));

                        } catch (Exception exception) {

                        }
                        dialog.cancel();
                    }
                });


                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Not much", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event

                        sendSuggestionEmail();

                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();

                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("rateus", true);
                edit.commit();

            }
        }


    }

    private void sendSuggestionEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"acraftystudio@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion for Editorial App");
        intent.putExtra(Intent.EXTRA_TEXT, "Your suggestion here \n");

        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Select Email Sending App"));
    }

    private void intialiseViewAndFetch(Intent i) {
        EditorialGeneralInfo editorialGeneralInfo = new EditorialGeneralInfo();
        editorialGeneralInfo.setEditorialID(i.getExtras().getString("editorialID"));
        editorialGeneralInfo.setEditorialDate(i.getExtras().getString("editorialDate"));
        editorialGeneralInfo.setEditorialHeading(i.getExtras().getString("editorialHeading"));
        editorialGeneralInfo.setEditorialSource(i.getExtras().getString("editorialSource"));
        editorialGeneralInfo.setEditorialSubHeading(i.getExtras().getString("editorialSubheading"));
        editorialGeneralInfo.setEditorialTag(i.getExtras().getString("editorialTag"));

        boolean isBookMarked = i.getBooleanExtra("isBookMarked", false);
        isPushNotification = i.getBooleanExtra("isPushNotification", false);


        if (isBookMarked) {
            DatabaseHandlerBookMark databasehandlerBookmark = new DatabaseHandlerBookMark(this);
            EditorialExtraInfo editorialExtraInfo = databasehandlerBookmark.getBookMarkEditorial(editorialGeneralInfo.getEditorialID());
            currentEditorialFullInfo.setEditorialExtraInfo(editorialExtraInfo);
            init(editorialExtraInfo.getEditorialText());
            findViewById(R.id.editorialfeed_activity_progressbar).setVisibility(View.GONE);


        } else {
            DBHelperFirebase dbHelper = new DBHelperFirebase();
            dbHelper.getEditorialFullInfoByID(editorialGeneralInfo, this);
        }


        if (isPushNotification) {

            new DBHelperFirebase().getEditorialGeneralInfoByID(editorialGeneralInfo.getEditorialID(), new DBHelperFirebase.OnEditorialListener() {
                @Override
                public void onEditorialGeneralInfo(EditorialGeneralInfo editorialGeneralInfo, boolean isSuccessful) {

                    if (isSuccessful) {

                        currentEditorialFullInfo.setEditorialGeneralInfo(editorialGeneralInfo);

                        TextView tv = (TextView) findViewById(R.id.editorial_heading_textview);
                        tv.setText(editorialGeneralInfo.getEditorialHeading());
                        tv = (TextView) findViewById(R.id.editorial_source_textview);
                        tv.setText(editorialGeneralInfo.getEditorialSource());
                        tv = (TextView) findViewById(R.id.editorial_date_textview);
                        tv.setText(editorialGeneralInfo.getEditorialDate());
                        tv = (TextView) findViewById(R.id.editorial_tag_textview);
                        tv.setText(editorialGeneralInfo.getEditorialTag());

                        try {
                            Answers.getInstance().logContentView(new ContentViewEvent()
                                    .putContentId(editorialGeneralInfo.getEditorialID())
                                    .putContentName(editorialGeneralInfo.getEditorialHeading())
                                    .putContentType("By push notification"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(EditorialFeedActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onEditorialExtraInfo(EditorialExtraInfo editorialExtraInfo, boolean isSuccessful) {

                }
            });


        }


        //Fetching comments
        new DBHelperFirebase().fetchComment(editorialGeneralInfo.getEditorialID(), 50, new DBHelperFirebase.OnCommentListener() {
            @Override
            public void onCommentInserted(Comment comment) {

            }

            @Override
            public void onCommentFetched(ArrayList<Comment> commentArrayList) {
                EditorialFeedActivity.this.commentList = commentArrayList;
                initializeCommentList();

            }
        });
        //end fetching comment


        currentEditorialFullInfo.setEditorialGeneralInfo(editorialGeneralInfo);

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();

        }

        TextView tv = (TextView) findViewById(R.id.editorial_heading_textview);
        tv.setText(editorialGeneralInfo.getEditorialHeading());
        tv = (TextView) findViewById(R.id.editorial_source_textview);
        tv.setText(editorialGeneralInfo.getEditorialSource());
        tv = (TextView) findViewById(R.id.editorial_date_textview);
        tv.setText(editorialGeneralInfo.getEditorialDate());
        tv = (TextView) findViewById(R.id.editorial_tag_textview);
        tv.setText(editorialGeneralInfo.getEditorialTag());

        try {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentId(editorialGeneralInfo.getEditorialID())
                    .putContentName(editorialGeneralInfo.getEditorialHeading()));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                //ds.setUnderlineText(false);

               /* if (mWord.contentEquals(selectedWord)){
                    ds.setColor(ds.linkColor);
                }*/
                //ds.setColor(ds.linkColor);
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

        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            onDictionaryClick(translateText);

        }
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
        dictionary.fetchWordMeaning(selectedWord, EditorialFeedActivity.this);


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
            speakOutWord("");
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {


        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            if (isPushNotification) {
                Intent intent = new Intent(EditorialFeedActivity.this, EditorialListWithNavActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {

                super.onBackPressed();

            }
        }

    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);
            tts.setPitch(0.8f);


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

        try {
            tts.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);
        } catch (Exception e) {

        }
    }

    public void onGetEditorialFullInfo(EditorialFullInfo editorialFullInfo) {

        try {

            init(editorialFullInfo.getEditorialExtraInfo().getEditorialText());
            currentEditorialFullInfo.setEditorialExtraInfo(editorialFullInfo.getEditorialExtraInfo());
            findViewById(R.id.editorialfeed_activity_progressbar).setVisibility(View.GONE);
            initializeCommentList();
        } catch (NullPointerException nl) {
            editorialFullInfo.getEditorialExtraInfo().setEditorialText("No editorial found");
            init(editorialFullInfo.getEditorialExtraInfo().getEditorialText());
            currentEditorialFullInfo = editorialFullInfo;
            findViewById(R.id.editorialfeed_activity_progressbar).setVisibility(View.GONE);
            initializeCommentList();
            nl.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show();
        }

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

            case R.id.action_refresh:
                // refresh
                onRefreashClick();
                return true;
            case R.id.action_share:
                // help action
                onShareClick();
                return true;
            case R.id.action_bookmark:
                // refresh
                onBookmark();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onBookmark() {
        DatabaseHandlerBookMark databaseHandlerBookMark = new DatabaseHandlerBookMark(this);
        databaseHandlerBookMark.addToBookMark(currentEditorialFullInfo.getEditorialGeneralInfo(), currentEditorialFullInfo.getEditorialExtraInfo());
        Toast.makeText(this, "Editorial Bookmarked", Toast.LENGTH_SHORT).show();

    }

    private void onVacabularyClick() {
        Intent i = new Intent(this, VacabularyActivity.class);
        startActivity(i);

    }

    private void onShareClick() {


        String appCode = getString(R.string.app_code);
        String appName = getString(R.string.app_name);
        String packageName = this.getPackageName();


        String utmSource = getString(R.string.utm_source);
        String utmCampaign = getString(R.string.utm_campaign);
        String utmMedium = getString(R.string.utm_medium);

        String url = "https://" + appCode + ".app.goo.gl/?link=https://dailyeditorial.com/"
                + currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID()
                + "&apn=" +
                packageName + "&st=" +
                currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading()
                + "&sd=" +
                appName + "&utm_source=" +
                utmSource + "&utm_medium=" +
                utmMedium + "&utm_campaign=" +
                utmCampaign;

        // Toast.makeText(this, "Shared an article " + url, Toast.LENGTH_SHORT).show();

        url = url.replaceAll(" ", "+");
        url = url.replaceAll("\n", "");


        final ProgressDialog pd = new ProgressDialog(EditorialFeedActivity.this);
        pd.setMessage("Creating link ...");
        pd.show();

        new UrlShortner(url, new UrlShortner.UrlShortnerListner() {


            @Override
            public void onCancel(String longUrl) {
                pd.dismiss();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the app and Start reading");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, longUrl + " \nHey read this editorial");
                startActivity(Intent.createChooser(sharingIntent, "Share Editorial via"));



            }

            @Override
            public void onUrlShort(String shortUrl, String longUrl) {
                pd.dismiss();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                //sharingIntent.putExtra(Intent.EXTRA_STREAM, newsMetaInfo.getNewsImageLocalPath());

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the app and Start reading");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shortUrl
                        + "\n"+currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading()
                        +"\n\nRead full editorial at Daily editorial app  ");
                startActivity(Intent.createChooser(sharingIntent, "Share Editorial via"));

            }
        }).execute("");




        Answers.getInstance().logShare(new ShareEvent()
                .putContentName(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading())
                .putContentId(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID()));


    }




    private void onAboutClick() {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    private void onRefreashClick() {
        /*demo for bookmark function */


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

    public void initializeBottomSheetAd(){
        AdView mAdView = (AdView) findViewById(R.id.editorialFeed_bottomSheet_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void readFullArticle(View view) {
        if (tts.isSpeaking()) {
            speakOutWord("");

        } else {
            speakOutWord(currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText());
        }
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

        if (commentList.size() == 0) {
            //commentList = new ArrayList<>();

            if (currentEditorialFullInfo.getEditorialExtraInfo().getComments() == null) {
                Comment comment = new Comment();
                comment.setCommentText("No Comment");
                comment.seteMailID("");
                commentList.add(comment);


            } else {
                for (Comment comment : currentEditorialFullInfo.getEditorialExtraInfo().getComments().values()) {
                    commentList.add(comment);
                }
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

        if (emailString.length() > 5 && commentString.length() > 1) {
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

    public void setThemeinactivity() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("theme_list", "Day");

        TextView mainText = (TextView) findViewById(R.id.editorial_text_textview);

        if (theme.contentEquals("Night")) {

            ScrollView scrollView = (ScrollView) findViewById(R.id.editorialFeed_scrollView);
            try {
                scrollView.setBackgroundColor(getResources().getColor(R.color.nightThemeBackGroundColor));

                mainText.setTextColor(getResources().getColor(R.color.nightThemeTextColor));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        mainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.valueOf(sharedPref.getString("font_size_list", "16")));

    }

    public void showInterstitialAd() {
        //set editorialcount to 0


    }

    public void hideBottomsheet(View view) {
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }

}
