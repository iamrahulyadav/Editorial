package app.articles.vacabulary.editorial.gamefever.editorial;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
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
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.InviteEvent;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.RatingEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Logger;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;


import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TreeMap;

import utils.AdsSubscriptionManager;
import utils.AppRater;
import utils.AuthenticationManager;
import utils.ClickListener;
import utils.CommentAdapter;
import utils.DatabaseHandlerRead;
import utils.Like;
import utils.NightModeManager;
import utils.SettingManager;
import utils.ShortNotesManager;

public class EditorialFeedActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {


    EditorialFullInfo currentEditorialFullInfo = new EditorialFullInfo(new EditorialGeneralInfo(), new EditorialExtraInfo());

    private TextToSpeech tts;

    TextView translateText;
    private BottomSheetBehavior mBottomSheetBehavior;
    public String selectedWord = "null";

    CommentAdapter mCommentAdapter;
    ArrayList<Comment> commentList = new ArrayList<>();

    boolean isPushNotification = false;
    boolean isDynamicLink = false;
    private boolean notesMode = false;
    private InterstitialAd mSubscriptionInterstitialAd;


    private ShortNotesManager shortNotesManager = new ShortNotesManager(new TreeMap<String, String>());
    private boolean saveShortNotes;

    boolean muteVoice = false;

    private RewardedVideoAd mAd;
    WebView webView;

    ArrayList<Object> suggestedEditorialArrayList;
    EditorialGeneralInfoAdapter editorialGeneralInfoAdapter;
    boolean checkShowAds = true;

    int voiceReaderChunk = 0;

    BillingProcessor bp;
    final String SUBSCRIPTION_ID = "ad_free_subscription";
    boolean bpStatus = false;

    TextView descriptionTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (NightModeManager.getNightMode(this)) {
            setTheme(R.style.FeedActivityThemeDark);
        }

        setContentView(R.layout.activity_editorial_feed);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.editorialfeed_activity_toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setSubtitle("Feeds");
            // getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        } catch (Exception e) {

        }

        Intent i = getIntent();
        intialiseViewAndFetch(i);

        translateText = (TextView) findViewById(R.id.editorial_feed_cardview_textview);

        muteVoice = SettingManager.getMuteVoice(EditorialFeedActivity.this);

        View bottomSheet = findViewById(R.id.editorial_activity_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setHideable(false);
        Button button = (Button) findViewById(R.id.editorial_bottomsheet_audio_button);
        if (muteVoice) {
            button.setBackgroundResource(R.drawable.ic_action_audio_off);
        }
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()

        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {


                    // loadWebview(selectedWord);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        MobileAds.initialize(

                getApplicationContext(), "ca-app-pub-8455191357100024~6634740792");
        mAd = MobileAds.getRewardedVideoAdInstance(this);

        checkShowAds = AdsSubscriptionManager.checkShowAds(this);

        if (checkShowAds) {

            initializeTopNativeAds(true);
            //initializeAds();
            initializeNativeAds(true);
            initializeBottomSheetAd(true);
            initializeSubscriptionAds();
            CardView cardView = (CardView) findViewById(R.id.editorialfeed_removeAd_cardView);
            cardView.setVisibility(View.VISIBLE);
        }


        //setThemeinactivity();

        //checkRateUsOption();


        initializeWebView();


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
        try {
            editorialGeneralInfo = (EditorialGeneralInfo) i.getSerializableExtra("editorial");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (editorialGeneralInfo == null) {
            editorialGeneralInfo = new EditorialGeneralInfo();
            try {
                editorialGeneralInfo.setEditorialID(i.getExtras().getString("editorialID"));
                editorialGeneralInfo.setEditorialDate(i.getExtras().getString("editorialDate"));
                editorialGeneralInfo.setEditorialHeading(i.getExtras().getString("editorialHeading"));
                editorialGeneralInfo.setEditorialSource(i.getExtras().getString("editorialSource"));
                editorialGeneralInfo.setEditorialSubHeading(i.getExtras().getString("editorialSubheading"));
                editorialGeneralInfo.setEditorialTag(i.getExtras().getString("editorialTag"));
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }


        boolean isBookMarked = i.getBooleanExtra("isBookMarked", false);
        isPushNotification = i.getBooleanExtra("isPushNotification", false);
        isDynamicLink = i.getBooleanExtra("isDynamicLink", false);


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

            try {
                Answers.getInstance().logInvite(new InviteEvent()
                        .putMethod("push notification")
                        .putCustomAttribute("editorialID", editorialGeneralInfo.getEditorialID())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }


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

        descriptionTextView = (TextView) findViewById(R.id.editorial_text_textview);

        try {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentId(editorialGeneralInfo.getEditorialID())
                    .putContentType(editorialGeneralInfo.getEditorialSource())
                    .putCustomAttribute("Category", editorialGeneralInfo.getEditorialCategory())
                    .putContentName(editorialGeneralInfo.getEditorialHeading()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void init(String textToShow) {

        try {

            String definition = textToShow;
            TextView definitionView = descriptionTextView;
            definitionView.setTextIsSelectable(false);
            definitionView.setMovementMethod(LinkMovementMethod.getInstance());
            definitionView.setText(definition, TextView.BufferType.SPANNABLE);

            setTextSize(definitionView);
            SpannableString spans = (SpannableString) definitionView.getText();
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


        Translation translation = new Translation(mWord);
        translation.fetchTranslation(this);

        translateText.setText(mWord);
        selectedWord = mWord;

        speakOutWord(mWord);
        fetchWordMeaning();

    }

    private void fetchWordMeaning() {
        //openBottomSheet(true);

       /* Dictionary dictionary = new Dictionary(selectedWord);
        dictionary.fetchWordMeaning(selectedWord, EditorialFeedActivity.this);
*/


        loadWebview(selectedWord);
    }

    public void updateTranslateText(Translation translation) {
        if (translation.word.equalsIgnoreCase(translateText.getText().toString().trim())) {
            translateText.setText(translation.word + " = " + translation.wordTranslation);
        }
    }

    public void updateDictionaryText(final Dictionary dictionary) {
        if (selectedWord.equalsIgnoreCase(dictionary.getWord())) {
        /*    TextView tv;
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
                    dictionary.setWord(translateText.getText().toString());
                    databaseHandler.addToDictionary(dictionary);
                    Toast.makeText(EditorialFeedActivity.this, "Word Added To Dictionary", Toast.LENGTH_SHORT).show();

                }
            });*/
        }
    }

    public void onDictionaryClick(View v) {
        //Intent i =new Intent(this ,);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        /*openBottomSheet(true);

        Dictionary dictionary = new Dictionary(selectedWord);
        dictionary.fetchWordMeaning(selectedWord, EditorialFeedActivity.this);
*/

        /*openBottomSheet(true);
        Dictionary dictionary = new Dictionary(selectedWord);
        dictionary.fetchWordMeaning(selectedWord, EditorialFeedActivity.this);
*/

    }

    private void openBottomSheet(boolean b) {

        /*TextView tv;
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_meaning_textview);
        tv.setText("Loading...");
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_partspeech_textview);
        tv.setText("Loading...");
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_synonyms_textview);

        tv.setText("Loading...");*/
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (mAd != null) {
            mAd.destroy(this);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (mAd != null) {
            mAd.resume(this);
        }
        super.onResume();
        tts = new TextToSpeech(this, this);

    }

    @Override
    public void onPause() {
        if (mAd != null) {
            mAd.pause(this);
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tts != null) {
            speakOutWord(".");
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onBackPressed() {


        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {

            if (saveShortNotes) {
                onNotesSaveClick();
            }

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

            Locale locale = new Locale("en", "IN");
            int availability = tts.isLanguageAvailable(locale);
            int result = 0;
            switch (availability) {
                case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE: {
                    result = tts.setLanguage(locale);
                    tts.setPitch(0.9f);
                    tts.setSpeechRate(SettingManager.getVoiceReaderSpeed(this));
                    break;
                }
                case TextToSpeech.LANG_NOT_SUPPORTED:
                case TextToSpeech.LANG_MISSING_DATA:
                case TextToSpeech.LANG_AVAILABLE: {
                    result = tts.setLanguage(Locale.US);
                    tts.setPitch(0.9f);
                    tts.setSpeechRate(SettingManager.getVoiceReaderSpeed(this));
                }
            }


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
            if (!muteVoice) {
                if (Build.VERSION.SDK_INT > 18) {
                }

                tts.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onGetEditorialFullInfo(EditorialFullInfo editorialFullInfo) {

        try {

            currentEditorialFullInfo.setEditorialExtraInfo(editorialFullInfo.getEditorialExtraInfo());
            if (SettingManager.getLiteReaderMode(this)) {
                initializeLiteReaderMode();
            } else {
                init(editorialFullInfo.getEditorialExtraInfo().getEditorialText());
            }
            initializeSourceLink();
            initializeCommentList();

            initializeSuggestedEditorial();
            intializeShareAndLike();

            //calling rate now dialog

            AppRater.app_launched(EditorialFeedActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show();
        }
        findViewById(R.id.editorialfeed_activity_progressbar).setVisibility(View.GONE);

    }

    private void initializeLiteReaderMode() {

        descriptionTextView.setText(currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText());
        descriptionTextView.setTextIsSelectable(true);
        translateText.setText("Long press on Word for Meaning");
        descriptionTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                getSelectedWord(1000);
                return false;
            }
        });

    }

    public void getSelectedWord(long timeDelay) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String string = descriptionTextView.getText().toString();
                if (descriptionTextView.hasSelection()) {
                    selectedWord = string.substring(descriptionTextView.getSelectionStart(), descriptionTextView.getSelectionEnd()).trim();
                }

                onWordTap(selectedWord);

                descriptionTextView.clearFocus();



            }
        }, timeDelay);

    }


    private void initializeSuggestedEditorial() {

        final ArrayList<Object> editorialGeneralInfoArrayList = EditorialListWithNavActivity.editorialListArrayList;

        suggestedEditorialArrayList = new ArrayList<>();

        EditorialGeneralInfo currentEditorial = currentEditorialFullInfo.getEditorialGeneralInfo();

        for (int i = 0; i < editorialGeneralInfoArrayList.size() && suggestedEditorialArrayList.size() < 3; i++) {

            if (editorialGeneralInfoArrayList.get(i).getClass() == EditorialGeneralInfo.class) {

                EditorialGeneralInfo editorialGeneralInfo = (EditorialGeneralInfo) editorialGeneralInfoArrayList.get(i);


                if ((editorialGeneralInfo.isMustRead() ||
                        editorialGeneralInfo.getEditorialCategoryIndex() == currentEditorial.getEditorialCategoryIndex() ||
                        editorialGeneralInfo.getEditorialSourceIndex() == currentEditorial.getEditorialSourceIndex() ||
                        editorialGeneralInfo.getEditorialLike() >= 3) &&
                        !editorialGeneralInfo.getEditorialID().equalsIgnoreCase(currentEditorial.getEditorialID()) &&
                        !editorialGeneralInfo.isReadStatus()
                        ) {
                    suggestedEditorialArrayList.add(editorialGeneralInfo);

                }

            }

        }

        Log.d("DEBUG", "initializeSuggestedEditorial: " + suggestedEditorialArrayList);

        addNativeExpressAds();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.editorialFeed_suggestion_recyclerView);
        editorialGeneralInfoAdapter = new EditorialGeneralInfoAdapter(suggestedEditorialArrayList, "", this);

        editorialGeneralInfoAdapter.setOnclickListener(new ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position < 0) {
                    recreate();
                    return;
                }

                if (position % 8 == 0) {
                    return;
                }

                EditorialGeneralInfo editorialGeneralInfo = (EditorialGeneralInfo) suggestedEditorialArrayList.get(position);

                Intent i = new Intent(EditorialFeedActivity.this, EditorialFeedActivity.class);
                i.putExtra("editorial", editorialGeneralInfo);

                try {
                    editorialGeneralInfo.setReadStatus(true);


                    new DatabaseHandlerRead(EditorialFeedActivity.this).addReadNews(editorialGeneralInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                startActivity(i);
                finish();

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.setAdapter(editorialGeneralInfoAdapter);


    }

    private void addNativeExpressAds() {
        NativeAd nativeAd = new NativeAd(this, "113079036048193_119892505366846");
        nativeAd.setAdListener(new com.facebook.ads.AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "List native").putCustomAttribute("errorType", error.getErrorMessage()).putCustomAttribute("Source", "Facebook"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                editorialGeneralInfoAdapter.notifyDataSetChanged();
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

        // Request an ad
        if (checkShowAds) {
            nativeAd.loadAd();
        }

        suggestedEditorialArrayList.add(0, nativeAd);

    }

    private void intializeShareAndLike() {
        TextView likeTextView = (TextView) findViewById(R.id.editorialfeed_like_textView);
        likeTextView.setText(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialLike() + " Likes");

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.editorialfeed_like_linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLikeClick(v);
                // v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                linearLayout.setSelected(true);
                linearLayout.setEnabled(false);
            }
        });
        final Button button = (Button) findViewById(R.id.editorialFeed_share_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareClick();

            }
        });


    }

    private void initializeSourceLink() {
        TextView textView = (TextView) findViewById(R.id.editorialfeed_sourceLink_textView);
        textView.setText("Read Editorial from - " + currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialSourceLink());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (notesMode) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_notes_mode_action, menu);
        } else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_editorial_feed_actions, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_toggle:

                switchTheme();
                return true;
            case R.id.action_share:
                // help action
                onShareClick();
                return true;
            case R.id.action_bookmark:
                // refresh
                onBookmark();
                return true;

            case R.id.action_tts_reader:
                onTtsReaderClick(item);
                return true;

            case R.id.action_notes:
                onTakeNotesClick();
                return true;

            case R.id.notes_mode_action_add_point:
                onAddPointClick();
                return true;


            case R.id.notes_mode_action_save:
                onNotesSaveClick();
                return true;

            case R.id.notes_mode_action_cancel:
                onNotesCancelClick();
                return true;

            case R.id.action_open_notes:
                onOpenNotesActivity();
                return true;

            case R.id.action_textsize:
                onTextSizeClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onTextSizeClick() {
        final CharSequence sources[] = new CharSequence[]{"Small", "Medium", "Large", "Extra Large"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Text Size");
        builder.setItems(sources, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                TextView definitionView = (TextView) findViewById(R.id.editorial_text_textview);


                int size = 18;

                if (which == 0) {
                    size = 16;
                } else if (which == 1) {
                    size = 18;
                } else if (which == 2) {
                    size = 20;
                } else if (which == 3) {
                    size = 22;
                }

                setTextSize(definitionView, size);

                SettingManager.setTextSize(EditorialFeedActivity.this, size);


            }
        });

        builder.show();
    }

    private void onTtsReaderClick(MenuItem item) {
        try {
            if (tts.isSpeaking()) {
                speakOutWord("");
                item.setTitle("Read Editorial (Voice)");

            } else {
                item.setTitle("Stop Reader");
                if (currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText().length() < 3999) {
                    speakOutWord(currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText());
                } else {
                    voiceReaderChunk = 0;
                    voiceReaderChunkManager();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void voiceReaderChunkManager() {

        if (currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText().length() > (voiceReaderChunk)) {

            String chunk = currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText().substring(voiceReaderChunk, Math.min(voiceReaderChunk + 3999, currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText().length()));

            voiceReaderChunk = voiceReaderChunk + 3999;

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.d("TTS", "onDone: " + utteranceId);
                }

                @Override
                public void onDone(String utteranceId) {

                    Log.d("TTS", "onDone: " + utteranceId);
                    voiceReaderChunkManager();

                }

                @Override
                public void onError(String utteranceId) {
                    Log.d("TTS", "onDone: " + utteranceId);
                }
            });

            try {
                if (Build.VERSION.SDK_INT > 21) {
                    tts.speak(chunk, TextToSpeech.QUEUE_FLUSH, null, "1");
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }

    private void onOpenNotesActivity() {
        Intent intent = new Intent(EditorialFeedActivity.this, NotesActivity.class);
        startActivity(intent);
    }

    private void onNotesCancelClick() {
        Toast.makeText(this, "Cancel notes", Toast.LENGTH_SHORT).show();
        saveShortNotes = false;
        onTakeNotesClick();
    }

    private void onNotesSaveClick() {
        //Toast.makeText(this, "save notes", Toast.LENGTH_SHORT).show();

        String userUID = AuthenticationManager.getUserUID(EditorialFeedActivity.this);
        if (userUID == null) {
            return;
        }

        if (saveShortNotes) {
            saveShortNotes = false;


            final ProgressDialog pd = ProgressDialog.show(EditorialFeedActivity.this, "Saving Notes", "Please wait");
            new DBHelperFirebase().uploadShortNote(userUID, shortNotesManager, new DBHelperFirebase.OnShortNoteListListener() {
                @Override
                public void onShortNoteList(ArrayList<ShortNotesManager> shortNotesManagerArrayList, boolean isSuccessful) {

                }

                @Override
                public void onShortNoteUpload(boolean isSuccessful) {

                    try {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isSuccessful) {
                        //Toast.makeText(EditorialFeedActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();

                        Snackbar snackbar = Snackbar
                                .make(translateText, "Note saved successfully 👍", Snackbar.LENGTH_LONG);
                        snackbar.setAction("View", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onOpenNotesActivity();
                            }
                        });

                        snackbar.show();
                    }
                }
            });


        }

        onTakeNotesClick();

    }

    private void onAddPointClick() {
        // Toast.makeText(this, "add point notes", Toast.LENGTH_SHORT).show();

        TextView definitionView = (TextView) findViewById(R.id.editorialfeed_notesText_textview);
        String selectedString = "";
        if (definitionView.hasSelection()) {
            selectedString = currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText().substring(definitionView.getSelectionStart(), definitionView.getSelectionEnd());

        }


        Toast.makeText(EditorialFeedActivity.this, "Point added to notes", Toast.LENGTH_SHORT).show();

        shortNotesManager.getShortNotePointList().put(definitionView.getSelectionStart() + "-" + definitionView.getSelectionEnd(), selectedString);

        definitionView.clearFocus();
        saveShortNotes = true;

    }

    private void onTakeNotesClick() {

        if (notesMode) {
            TextView notesTextview = (TextView) findViewById(R.id.editorialfeed_notesText_textview);
            notesTextview.setText(null);

            init(currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText());
            notesMode = false;
            //item.setTitle("Take Notes");
            try {
                getSupportActionBar().setSubtitle("Feeds");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ActivityCompat.invalidateOptionsMenu(this);

        } else {
            TextView editorialText = (TextView) findViewById(R.id.editorial_text_textview);
            editorialText.setText(null);

            final TextView definitionView = (TextView) findViewById(R.id.editorialfeed_notesText_textview);
            definitionView.setText(currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText());
            definitionView.setTextIsSelectable(true);
            setTextSize(definitionView);
            notesMode = true;
            // item.setTitle("Exit notes mode");
            Toast.makeText(this, "Entered notes mode", Toast.LENGTH_SHORT).show();

            try {
                getSupportActionBar().setSubtitle("Notes");
            } catch (Exception e) {
                e.printStackTrace();
            }

            shortNotesManager.setNoteArticleID(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID());
            shortNotesManager.setShortNoteHeading(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading());
            shortNotesManager.setNoteArticleSource(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialSource());
            shortNotesManager.setShortNoteEditTimeInMillis(currentEditorialFullInfo.getEditorialGeneralInfo().getTimeInMillis());

            shortNotesManager.setNotesCategory(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialCategory());

            ActivityCompat.invalidateOptionsMenu(this);


        }

    }

    private void switchTheme() {
        if (NightModeManager.getNightMode(this)) {
            NightModeManager.setNightMode(EditorialFeedActivity.this, false);
        } else {
            NightModeManager.setNightMode(EditorialFeedActivity.this, true);
        }
        recreate();
    }

    private void onBookmark() {
        DatabaseHandlerBookMark databaseHandlerBookMark = new DatabaseHandlerBookMark(this);
        databaseHandlerBookMark.addToBookMark(currentEditorialFullInfo.getEditorialGeneralInfo(), currentEditorialFullInfo.getEditorialExtraInfo());


        Snackbar snackbar = Snackbar
                .make(translateText, "Editorial Bookmarked successfully 👍", Snackbar.LENGTH_LONG);
        snackbar.setAction("View", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditorialFeedActivity.this, EditorialListActivity.class);
                startActivity(intent);
            }
        });

        snackbar.show();

        try {
            Answers.getInstance().logCustom(new CustomEvent("Bookmark").putCustomAttribute("Editorial title", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading()));

        } catch (Exception e) {

        }

    }

    private void onShareClick() {

        String appCode = getString(R.string.app_code);
        String appName = getString(R.string.app_name);
        String packageName = this.getPackageName();
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/editorial-8cbf6.appspot.com/o/editorial%20logo%20png.png?alt=media&token=632a8d65-b5cb-4f68-94a0-e65b20890405";


        String utmSource = getString(R.string.utm_source);
        String utmCampaign = getString(R.string.utm_campaign);
        String utmMedium = getString(R.string.utm_medium);

        final ProgressDialog pd = new ProgressDialog(EditorialFeedActivity.this);
        pd.setMessage("Creating link ...");
        pd.show();

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://goo.gl/Ae4Mhw?editorialID=" + currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID()))
                .setDynamicLinkDomain(appCode)
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(packageName)
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading())
                                .setDescription(appName)
                                .setImageUrl(Uri.parse("https://firebasestorage.googleapis.com/v0/b/editorial-8cbf6.appspot.com/o/newlogogpng.png?alt=media&token=feb60ba4-ce3c-42cb-abf9-1c876cb01a63"))
                                .build())
                .setGoogleAnalyticsParameters(
                        new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource(utmSource)
                                .setMedium(utmMedium)
                                .setCampaign(utmCampaign)
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            if (pd.isShowing()) {
                                try {
                                    pd.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            openShareDialog(shortLink);

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditorialFeedActivity.this, "Connection Failed! Try again later", Toast.LENGTH_SHORT).show();
                        try {
                            pd.dismiss();
                        } catch (Exception exception) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void openShareDialog(Uri shortLink) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //sharingIntent.putExtra(Intent.EXTRA_STREAM, newsMetaInfo.getNewsImageLocalPath());

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the app and Start reading");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shortLink
                + "\n" + currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading()
                + "\n\nRead full editorial at Daily editorial app");
        startActivity(Intent.createChooser(sharingIntent, "Share Editorial via"));
        try {
            Answers.getInstance().logCustom(new CustomEvent("Share link created").putCustomAttribute("Content Id", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID())
                    .putCustomAttribute("Shares", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void initializeNativeAds() {
        NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.editorialfeed_native_adView);
        adView.setVisibility(View.VISIBLE);

        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);

        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "Feed native bottom").putCustomAttribute("errorType", i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });


    }

    public void initializeNativeAds(boolean isFacebook) {

        final NativeAd nativeAd = new NativeAd(this, "113079036048193_119919118697518");
        nativeAd.setAdListener(new com.facebook.ads.AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                initializeNativeAds();

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "Feed native bottom").putCustomAttribute("errorType", error.getErrorMessage()).putCustomAttribute("Source", "Facebook"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback

                CardView linearLayout = (CardView) findViewById(R.id.editorialfeed_facebook_cardView);
                linearLayout.setVisibility(View.VISIBLE);
                NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                        .setBackgroundColor(Color.parseColor("#28292e"))
                        .setTitleTextColor(Color.WHITE)
                        .setButtonTextColor(Color.WHITE)
                        .setDescriptionTextColor(Color.WHITE)
                        .setButtonColor(Color.parseColor("#F44336"));

                View adView = NativeAdView.render(EditorialFeedActivity.this, nativeAd, NativeAdView.Type.HEIGHT_400, viewAttributes);

                linearLayout.removeAllViews();
                linearLayout.addView(adView);

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

        // Request an ad
        nativeAd.loadAd();


    }

    public void initializeBottomSheetAd() {
        AdView mAdView = (AdView) findViewById(R.id.editorialFeed_bottomSheet_bannerAdview);
        mAdView.setVisibility(View.VISIBLE);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "Bottom sheet").putCustomAttribute("errorType", i));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void initializeBottomSheetAd(boolean isFacebook) {


        final NativeAd nativeAd = new NativeAd(this, "113079036048193_121732315182865");
        nativeAd.setAdListener(new com.facebook.ads.AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                initializeBottomSheetAd();


                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "Feed native meaning bottom").putCustomAttribute("errorType", error.getErrorMessage()).putCustomAttribute("Source", "Facebook"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.editorialFeed_bottomSheet_adcontainer);
                linearLayout.setVisibility(View.VISIBLE);
                NativeAdViewAttributes viewAttributes;
                if (NightModeManager.getNightMode(EditorialFeedActivity.this)) {

                    viewAttributes = new NativeAdViewAttributes()
                            .setBackgroundColor(Color.parseColor("#28292e"))
                            .setTitleTextColor(Color.WHITE)
                            .setButtonTextColor(Color.WHITE)
                            .setDescriptionTextColor(Color.WHITE)
                            .setButtonColor(Color.parseColor("#F44336"));

                } else {
                    viewAttributes = new NativeAdViewAttributes()
                            .setBackgroundColor(Color.LTGRAY)

                            .setButtonTextColor(Color.WHITE)
                            .setButtonColor(Color.parseColor("#F44336"));

                }

                View adView = NativeAdView.render(EditorialFeedActivity.this, nativeAd, NativeAdView.Type.HEIGHT_100, viewAttributes);


                linearLayout.removeAllViews();
                linearLayout.addView(adView);

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

        // Request an ad
        nativeAd.loadAd();
    }

    private void initializeTopNativeAds() {
        NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.editorialFeed_top_nativeAds);
        adView.setVisibility(View.VISIBLE);

        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "top Native small").putCustomAttribute("errorType", i));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initializeTopNativeAds(boolean isFacebook) {


        final NativeAd nativeAd = new NativeAd(this, "113079036048193_121737141849049");
        nativeAd.setAdListener(new com.facebook.ads.AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                initializeTopNativeAds();


                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "Feed native top").putCustomAttribute("errorType", error.getErrorMessage()).putCustomAttribute("Source", "Facebook"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.editorialFeed_top_adContainer);
                linearLayout.setVisibility(View.VISIBLE);
                NativeAdViewAttributes viewAttributes;
                if (NightModeManager.getNightMode(EditorialFeedActivity.this)) {

                    viewAttributes = new NativeAdViewAttributes()
                            .setBackgroundColor(Color.parseColor("#28292e"))
                            .setTitleTextColor(Color.WHITE)
                            .setButtonTextColor(Color.WHITE)
                            .setDescriptionTextColor(Color.WHITE)
                            .setButtonColor(Color.parseColor("#F44336"));

                } else {
                    /*viewAttributes = new NativeAdViewAttributes()
                            .setBackgroundColor(Color.LTGRAY)

                            .setButtonTextColor(Color.WHITE)
                            .setButtonColor(Color.parseColor("#F44336"));*/

                    viewAttributes = new NativeAdViewAttributes()
                            .setBackgroundColor(Color.LTGRAY)
                            .setButtonTextColor(Color.WHITE)
                            .setButtonColor(Color.parseColor("#F44336"));

                }

                View adView = NativeAdView.render(EditorialFeedActivity.this, nativeAd, NativeAdView.Type.HEIGHT_120, viewAttributes);


                linearLayout.removeAllViews();
                linearLayout.addView(adView);

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

        // Request an ad
        nativeAd.loadAd();
    }

    public void readFullArticle(View view) {
        if (muteVoice) {
            muteVoice = false;
            Toast.makeText(this, "Voice enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Voice disabled", Toast.LENGTH_SHORT).show();
            muteVoice = true;
        }
        SettingManager.setMuteVoice(EditorialFeedActivity.this, muteVoice);
        Button button = (Button) findViewById(R.id.editorial_bottomsheet_audio_button);
        if (muteVoice) {
            button.setBackgroundResource(R.drawable.ic_action_audio_off);
        } else {
            button.setBackgroundResource(R.drawable.ic_action_audio);
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

        TextView textView = (TextView) findViewById(R.id.editorialfeed_comment_textView);
        textView.setText(commentList.size() + " Discussion");

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


        //resizeCommentListView();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.editorialFeed_comments_recyclerView);
        mCommentAdapter = new CommentAdapter(commentList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecorator dividerItemDecorator = new DividerItemDecorator(this, DividerItemDecorator.VERTICAL_LIST);
        recyclerView.addItemDecoration(dividerItemDecorator);


        recyclerView.setAdapter(mCommentAdapter);


    }

    public void insertCommentBtnClick(View view) {

        //EditText editText = (EditText) findViewById(R.id.editorialFeed_commentemail_edittext);
        String emailString = "abcd@gmail.com";
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


            editText2.setText("");
            Toast.makeText(this, "Posting", Toast.LENGTH_SHORT).show();

            commentList.add(commentToPost);
            mCommentAdapter.notifyDataSetChanged();


        } else {
            Toast.makeText(this, "Comment Size is small", Toast.LENGTH_SHORT).show();
        }


    }

    public void hideBottomsheet(View view) {
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }

    public void onLikeClick(View view) {
        Like like = new Like();
        like.setEditorialID(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID());
        like.setEditorialTitle(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading());

        new DBHelperFirebase().uploadLike(like, new DBHelperFirebase.OnLikeListener() {
            @Override
            public void onLikeUpload(boolean isSuccessful) {
                //Toast.makeText(EditorialFeedActivity.this, "Thank you for liking the editorial ", Toast.LENGTH_SHORT).show();

            }
        });

        currentEditorialFullInfo.getEditorialGeneralInfo().setEditorialLike(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialLike() + 1);
        TextView likeTextView = (TextView) findViewById(R.id.editorialfeed_like_textView);
        likeTextView.setText((currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialLike()) + " Likes");

        try {
            Answers.getInstance().logRating(new RatingEvent()
                    .putContentName("Like")
                    .putContentId(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID())
                    .putContentType(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading())
                    .putContentName(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialSource())
                    .putRating(1)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onShareButtonClick(View view) {
        onShareClick();
    }

    public void initializeSubscriptionAds() {
     /*   mSubscriptionInterstitialAd = new InterstitialAd(this);
        mSubscriptionInterstitialAd.setAdUnitId("ca-app-pub-8455191357100024/6262441391");
        //test ad unit
        //mSubscriptionInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mSubscriptionInterstitialAd.loadAd(new AdRequest.Builder().build());

        mSubscriptionInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mSubscriptionInterstitialAd.loadAd(new AdRequest.Builder().build());


                Button button = (Button) EditorialFeedActivity.this.findViewById(R.id.editorialfeed_removeAd_button);
                if (AdsSubscriptionManager.checkShowAds(EditorialFeedActivity.this)) {
                    Toast.makeText(EditorialFeedActivity.this, "You need to click on the ad to get Pro features (with no ads) \n Try again", Toast.LENGTH_LONG).show();
                    button.setText("Try again? Click on the ads");
                } else {
                    Toast.makeText(EditorialFeedActivity.this, "Thank you for subscribing. \nAll the ads will be removed from next session.", Toast.LENGTH_LONG).show();
                    button.setText("Thank you for subscription");
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "Subscription ad feed").putCustomAttribute("Error code", i));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }




            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();

                AdsSubscriptionManager.setSubscriptionTime(EditorialFeedActivity.this);

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Subscription").putCustomAttribute("user subscribed from feed", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
*/

        mAd.loadAd("ca-app-pub-8455191357100024/4421294382", new AdRequest.Builder().build());
        mAd.setImmersiveMode(true);
        mAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

                Button button = (Button) EditorialFeedActivity.this.findViewById(R.id.editorialfeed_removeAd_button);
                if (AdsSubscriptionManager.checkShowAds(EditorialFeedActivity.this)) {
                    button.setText("Try again? Watch full video");
                } else {
                    button.setText("Thank you for subscription");
                    Toast.makeText(EditorialFeedActivity.this, "Thank you for subscribing. \nAll the ads will be removed from next session for 2 days", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

                AdsSubscriptionManager.setSubscriptionTime(EditorialFeedActivity.this, rewardItem.getAmount());


                try {
                    Answers.getInstance().logCustom(new CustomEvent("Subscribed").putCustomAttribute("user subscribed from feed", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading()));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }
        });

    }

    public void onRemoveAdClick(View view) {

       /* if (mAd != null) {
            if (mAd.isLoaded()) {
                mAd.show();
            } else {
                Toast.makeText(this, "Ad not loaded yet! Try again later", Toast.LENGTH_SHORT).show();

                initializeSubscriptionAds();
            }
        }*/

        bpStatus = false;

        try {
            bp = new BillingProcessor(this,
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4lE0nDHaciyEpkXJ2dceN9EPLiuP7hSSOIjzzOF40am/QD4Hwd4CxZg+tco/f7G6GIFW1aJgRiHOCm+crhWUJk854MmWNs3JC1hxe15vH7h0C9s4d6Iw7fTJn4GN5a5tPrQESLd/OFPixFXS7gwePWUCnYl85Uge8tqwPtf2rcotqs3bScxYQQMmCb1fNxXOgB/kULJr9hy9FIzxYdKnSrUMib3rKQTEPKFqyLZgYGOfUwvvclJ7baouZfWemW0nwWKvIxMCsBGdEBI0aCb0on+J8A+pN3f+in5HM8F3eBAHF/MTVkOVoS1EGvIJgjj5exlZJePN+NJI3WtKVFiaPQIDAQAB",
                    new BillingProcessor.IBillingHandler() {
                        @Override
                        public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                            //Toast.makeText(EditorialListWithNavActivity.this, "product purchased - " + productId, Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditorialFeedActivity.this);
                            builder.setTitle("Thank you for Subscription");
                            builder.setMessage("We appreciate your contribution by going ads free.\n\nAds will be removed when you open the app next time.");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();

                            AdsSubscriptionManager.setSubscription(EditorialFeedActivity.this, true);
                            Answers.getInstance().logPurchase(new PurchaseEvent().putItemType("Subscription").putSuccess(true));


                        }

                        @Override
                        public void onPurchaseHistoryRestored() {

                        }

                        @Override
                        public void onBillingError(int errorCode, @Nullable Throwable error) {

                        }

                        @Override
                        public void onBillingInitialized() {

                            if (bpStatus) {
                                if (bp != null) {
                                    bp.subscribe(EditorialFeedActivity.this, SUBSCRIPTION_ID);
                                }
                            } else {
                                bpStatus = true;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Go Ads Free");
            builder.setMessage("We are not a money making app. Ads are integrated to help app development and maintenance of apps.\n\nPlease make a small contribution and go ads free");
            builder.setPositiveButton("Go Ads Free", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (bpStatus) {
                        if (bp != null) {
                            bp.subscribe(EditorialFeedActivity.this, SUBSCRIPTION_ID);
                            Answers.getInstance().logCustom(new CustomEvent("Subscription Flow").putCustomAttribute("Selection", "yes"));

                        }
                    } else {
                        bpStatus = true;
                    }
                }
            });
            builder.setNegativeButton("Maybe Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Answers.getInstance().logCustom(new CustomEvent("Subscription Flow").putCustomAttribute("Selection", "no"));

                }
            });

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void onSourceTextClick(View view) {
        try {
            Intent intent = new Intent(EditorialFeedActivity.this, EditorialListWithNavActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("sourceIndex", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialSourceIndex());
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onCategoryTextClick(View view) {
        try {
            Intent intent = new Intent(EditorialFeedActivity.this, EditorialListWithNavActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("categoryIndex", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialCategoryIndex());
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeWebView() {
        webView = (WebView) findViewById(R.id.editorial_bottomSheet_webview);

        webView.getSettings().setLoadsImagesAutomatically(false);

        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                return shouldOverrideUrlLoading(url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                Uri uri = request.getUrl();
                return shouldOverrideUrlLoading(uri.toString());
            }

            private boolean shouldOverrideUrlLoading(final String url) {
                // Log.i(TAG, "shouldOverrideUrlLoading() URL : " + url);

                // Here put your code
                webView.loadUrl(url);

                return true; // Returning True means that application wants to leave the current WebView and handle the url itself, otherwise return false.
            }
        });

        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAppCachePath(this.getCacheDir().getPath());
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


    }

    public void loadWebview(String mWord) {
        webView.loadUrl("http://www.dictionary.com/browse/" + mWord);
    }

    public void initializeImage() {
  /*      ImageView imageView = (ImageView) findViewById(R.id.editorialfeed_feedImage_imageView);
        currentEditorialFullInfo.getEditorialGeneralInfo().setEditorialImageUrl("http://images.indianexpress.com/2017/09/piyush-goyal.jpg");

        String imageUrl = currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialImageUrl();

        if (imageUrl !=null && !imageUrl.isEmpty() ) {
            Glide.with(this)
                    .load("http://images.indianexpress.com/2017/09/piyush-goyal.jpg")
                    .thumbnail(0.3f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

        }else{
            imageView.setVisibility(View.GONE);
        }
*/
    }

    public void onAddToVocabularyClick(View view) {
        DatabaseHandler databaseHandler = new DatabaseHandler(EditorialFeedActivity.this);
        Dictionary dictionary = new Dictionary();
        dictionary.setWord(translateText.getText().toString());
        //dictionary.setWord(translateText.getText().toString());
        WebView webView = (WebView) findViewById(R.id.editorial_bottomSheet_webview);
        dictionary.setWordMeaning(webView.getUrl());
        databaseHandler.addToDictionary(dictionary);

        Snackbar snackbar = Snackbar
                .make(translateText, "Word saved successfully 👍", Snackbar.LENGTH_LONG);
        snackbar.setAction("View", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditorialFeedActivity.this, VacabularyActivity.class);
                startActivity(intent);
            }
        });

        snackbar.show();
    }

    public void onInstallPIBClick(View view) {
        try {
            String link = "https://play.google.com/store/apps/details?id=app.crafty.studio.current.affairs.pib";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (Exception e) {

        }
    }

    public void onInstallAptitudeClick(View view) {
        try {
            String link = "https://play.google.com/store/apps/details?id=app.aptitude.quiz.craftystudio.aptitudequiz";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (Exception e) {

        }
    }

    public void setTextSize(TextView tv) {

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingManager.getTextSize(this));
    }

    public void setTextSize(TextView tv, int size) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }


    public void onTakeNotesButtonClick(View view) {
        onTakeNotesClick();
    }
}
