package app.articles.vacabulary.editorial.gamefever.editorial;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.InviteEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import utils.AdsSubscriptionManager;
import utils.CommentAdapter;
import utils.CurrentAffairs;
import utils.CurrentAffairsAdapter;
import utils.JsonParser;
import utils.NightModeManager;
import utils.SettingManager;
import utils.ShortNotesManager;
import utils.VolleyManager;

import static android.content.ContentValues.TAG;


public class CurrentAffairsFeedActivity extends AppCompatActivity {

    CurrentAffairs currentAffairs;

    TextView headingTextView, dateTextView, sourceTextView, contentTextView, notesTextView, sourceLinkTextView, articleTypeTextView, tagTextView;

    TextView translateText;
    private BottomSheetBehavior mBottomSheetBehavior;
    public String selectedWord = "null";
    WebView meaningWebView;


    private boolean muteVoice;
    private boolean notesMode;

    private ShortNotesManager shortNotesManager = new ShortNotesManager(new TreeMap<String, String>());
    private boolean saveShortNotes;
    private boolean isPushNotification;


    private TextToSpeech textToSpeech;
    int voiceReaderChunk = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (NightModeManager.getNightMode(this)) {
            setTheme(R.style.FeedActivityThemeDark);
        }

        setContentView(R.layout.activity_current_affairs_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        currentAffairs = (CurrentAffairs) getIntent().getSerializableExtra("news");
        isPushNotification = getIntent().getBooleanExtra("isPushNotification", false);


        if (currentAffairs == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        } else if (currentAffairs.getContent() == null) {

            currentAffairs.setContent("");
            fetchArticle();

        }

        headingTextView = findViewById(R.id.currentAffairsFeed_heading_textview);
        dateTextView = findViewById(R.id.currentAffairsFeed_date_textview);
        sourceTextView = findViewById(R.id.currentAffairsFeed_source_textview);
        contentTextView = findViewById(R.id.currentAffairsFeed_content_textview);
        notesTextView = findViewById(R.id.currentAffairsFeed_notesText_textview);
        sourceLinkTextView = findViewById(R.id.currentAffairsFeed_sourceLink_textView);
        articleTypeTextView = findViewById(R.id.currentAffairsFeed_articleType_textview);
        tagTextView = findViewById(R.id.currentAffairsFeed_tag_textview);

        muteVoice = SettingManager.getMuteVoice(this);


        initializeTTS();

        initializeBottomSheet();
        initializeMeaningWebView();

        initializeActivity();


        if (AdsSubscriptionManager.checkShowAds(this)) {
            initializeBottomSheetAd(true);
            initializeNativeAds(true);
            initializeTopNativeAds(true);
        }


        if (isPushNotification) {
            try {
                Answers.getInstance().logInvite(new InviteEvent()
                        .putMethod("push notification")
                        .putCustomAttribute("editorialID", String.valueOf(currentAffairs.getId()))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {

            if (currentAffairs.getArticleType() != null) {
                getSupportActionBar().setTitle(currentAffairs.getArticleType());
            }

            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentId(String.valueOf(currentAffairs.getId()))
                    .putContentType(currentAffairs.getArticleType())
                    .putCustomAttribute("Category", currentAffairs.getCategory())
                    .putContentName(currentAffairs.getTitle())
                    .putCustomAttribute("Mode", "Current Affairs")
            );


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeActivity() {


        headingTextView.setText(currentAffairs.getTitle());
        dateTextView.setText(currentAffairs.getDate());
        sourceTextView.setText(currentAffairs.getCategory());

        currentAffairs.resolveContent();
        contentTextView.setText(currentAffairs.getContent());
        init(currentAffairs.getContent());

        sourceLinkTextView.setText("Website Link : " + currentAffairs.getLink());

        articleTypeTextView.setText(currentAffairs.getArticleType());

        tagTextView.setText(currentAffairs.getTag());

    }

    private void fetchArticle() {

        String url = "http://aspirantworld.in/wp-json/wp/v2/posts/" + currentAffairs.getId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        currentAffairs = new JsonParser().parseCurrentAffairs(response);
                        initializeActivity();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG, "onErrorResponse: " + error);

                    }
                });


        jsonObjectRequest.setShouldCache(true);

        VolleyManager.getInstance().addToRequestQueue(jsonObjectRequest, "Group request");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_current_affairs_feed_actions, menu);

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
                onShareClick();
                return true;
            case R.id.action_bookmark:

                onBookmark();
                return true;

            case R.id.action_tts_reader:
                onTtsReaderClick(item);
                return true;

            case R.id.action_notes:
                //onTakeNotesClick();
                return true;

            case R.id.notes_mode_action_add_point:
                //onAddPointClick();
                return true;


            case R.id.notes_mode_action_save:
                //onNotesSaveClick();
                return true;

            case R.id.notes_mode_action_cancel:
                //onNotesCancelClick();
                return true;

            case R.id.action_open_notes:
                //onOpenNotesActivity();
                return true;

            case R.id.action_textsize:
                onTextSizeClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {


        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {

            if (saveShortNotes) {
                //onNotesSaveClick();
            }

            if (isPushNotification) {
                Intent intent = new Intent(CurrentAffairsFeedActivity.this, EditorialListWithNavActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {

                super.onBackPressed();

            }


        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (textToSpeech != null) {
            speakOutWord(".");
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void init(String content) {

        try {

            //content = Html.fromHtml(content).toString();

            contentTextView.setTextIsSelectable(false);
            contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
            //contentTextView.setText(Html.fromHtml(content), TextView.BufferType.SPANNABLE);
            contentTextView.setText(content, TextView.BufferType.SPANNABLE);


            setTextSize(contentTextView);


            SpannableString spans = (SpannableString) contentTextView.getText();


            BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
            iterator.setText(content);
            int start = iterator.first();
            for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                    .next()) {
                String possibleWord = content.substring(start, end);
                if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
                    ClickableSpan clickSpan = getClickableSpan(possibleWord);
                    spans.setSpan(clickSpan, start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

        } catch (Exception e) {
            contentTextView.setText(content);
            e.printStackTrace();

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
                //ds.setUnderlineText(true);

                if (mWord.contentEquals(selectedWord)) {
                    ds.setUnderlineText(true);

                }
                //ds.setColor(ds.linkColor);
                //super.updateDrawState(ds);
            }
        };
    }

    public void onMuteClick(View view) {

        if (muteVoice) {
            muteVoice = false;
            Toast.makeText(this, "Voice enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Voice disabled", Toast.LENGTH_SHORT).show();
            muteVoice = true;
        }
        SettingManager.setMuteVoice(CurrentAffairsFeedActivity.this, muteVoice);
        Button button = (Button) findViewById(R.id.editorial_bottomsheet_audio_button);
        if (muteVoice) {
            button.setBackgroundResource(R.drawable.ic_action_audio_off);
        } else {
            button.setBackgroundResource(R.drawable.ic_action_audio);
        }

    }

    private void initializeBottomSheet() {

        View bottomSheet = findViewById(R.id.editorial_activity_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setHideable(false);
        Button button = (Button) findViewById(R.id.editorial_bottomsheet_audio_button);

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


        translateText = findViewById(R.id.editorial_feed_cardview_textview);

    }

    public void initializeMeaningWebView() {
        meaningWebView = findViewById(R.id.editorial_bottomSheet_webview);

        meaningWebView.getSettings().setLoadsImagesAutomatically(false);

        meaningWebView.setWebViewClient(new WebViewClient() {
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
                meaningWebView.loadUrl(url);

                return true; // Returning True means that application wants to leave the current WebView and handle the url itself, otherwise return false.
            }
        });

        meaningWebView.getSettings().setAppCacheEnabled(true);
        meaningWebView.getSettings().setAppCachePath(this.getCacheDir().getPath());
        meaningWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


    }

    public void onDictionaryClick(View view) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    private void onWordTap(String mWord) {


        selectedWord = mWord;


        translateText.setText(mWord);


        fetchWordMeaning();
        fetchTranslation();
        speakOutWord(selectedWord);

    }

    private void fetchTranslation() {
        Translation translation = new Translation(selectedWord);

        translation.fetchTranslation(new Translation.TranslateListener() {
            @Override
            public void onTranslate(Translation translation) {

                if (translation.word.equalsIgnoreCase(translateText.getText().toString().trim())) {
                    translateText.setText(translation.word + " = " + translation.wordTranslation);
                }

            }
        },this);
    }

    private void fetchWordMeaning() {


        loadWebview(selectedWord);
    }

    public void onAddToVocabularyClick(View view) {

        DatabaseHandler databaseHandler = new DatabaseHandler(CurrentAffairsFeedActivity.this);
        Dictionary dictionary = new Dictionary();
        dictionary.setWord(translateText.getText().toString());
        //dictionary.setWord(translateText.getText().toString());

        dictionary.setWordMeaning(meaningWebView.getUrl());
        databaseHandler.addToDictionary(dictionary);

        Snackbar snackbar = Snackbar
                .make(translateText, "Word saved successfully 👍", Snackbar.LENGTH_LONG);
        snackbar.setAction("View", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentAffairsFeedActivity.this, VacabularyActivity.class);
                startActivity(intent);
            }
        });

        snackbar.show();

    }

    public void loadWebview(String mWord) {
        meaningWebView.loadUrl("http://www.dictionary.com/browse/" + selectedWord);
    }

    public void setTextSize(TextView tv) {

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingManager.getTextSize(this));
    }

    public void setTextSize(TextView tv, int size) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    private void initializeTTS() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    Locale locale = new Locale("en", "IN");
                    int availability = textToSpeech.isLanguageAvailable(locale);
                    int result = 0;
                    switch (availability) {
                        case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                        case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE: {
                            result = textToSpeech.setLanguage(locale);
                            textToSpeech.setPitch(0.9f);
                            textToSpeech.setSpeechRate(SettingManager.getVoiceReaderSpeed(CurrentAffairsFeedActivity.this));
                            break;
                        }
                        case TextToSpeech.LANG_NOT_SUPPORTED:
                        case TextToSpeech.LANG_MISSING_DATA:
                        case TextToSpeech.LANG_AVAILABLE: {
                            result = textToSpeech.setLanguage(Locale.US);
                            textToSpeech.setPitch(0.9f);
                            textToSpeech.setSpeechRate(SettingManager.getVoiceReaderSpeed(CurrentAffairsFeedActivity.this));
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
        });

    }

    private void speakOutWord(String speakWord) {

        try {
            if (!muteVoice) {
                if (Build.VERSION.SDK_INT > 18) {
                }

                textToSpeech.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onTtsReaderClick(MenuItem item) {
        try {
            if (textToSpeech.isSpeaking()) {
                speakOutWord("");
                item.setTitle("Read Editorial (Voice)");

            } else {
                item.setTitle("Stop Reader");
                if (currentAffairs.getContent().length() < 3999) {
                    speakOutWord(currentAffairs.getContent());
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

        if (currentAffairs.getContent().length() > (voiceReaderChunk)) {

            String chunk = currentAffairs.getContent().substring(voiceReaderChunk, Math.min(voiceReaderChunk + 3999, currentAffairs.getContent().length()));


            voiceReaderChunk = voiceReaderChunk + 3999;

            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
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
                    textToSpeech.speak(chunk, TextToSpeech.QUEUE_FLUSH, null, "1");
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }

    private void onBookmark() {
        DatabaseHandlerBookMark databaseHandlerBookMark = new DatabaseHandlerBookMark(this);

        EditorialGeneralInfo editorialGeneralInfo = new EditorialGeneralInfo();
        editorialGeneralInfo.setEditorialHeading(currentAffairs.getTitle());
        editorialGeneralInfo.setEditorialID(String.valueOf(currentAffairs.getId()));
        editorialGeneralInfo.setEditorialSubHeading(currentAffairs.getSubHeading());
        editorialGeneralInfo.setEditorialSource(currentAffairs.getCategory() + "CA");
        editorialGeneralInfo.setEditorialDate(currentAffairs.getDate());
        editorialGeneralInfo.setEditorialTag("CA Notes");


        EditorialExtraInfo editorialExtraInfo = new EditorialExtraInfo();
        editorialExtraInfo.setEditorialId(String.valueOf(currentAffairs.getId()));
        editorialExtraInfo.setEditorialText(currentAffairs.getContent());


        databaseHandlerBookMark.addToBookMark(editorialGeneralInfo, editorialExtraInfo);


        Snackbar snackbar = Snackbar
                .make(translateText, "Editorial Bookmarked successfully 👍", Snackbar.LENGTH_LONG);
        snackbar.setAction("View", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentAffairsFeedActivity.this, EditorialListActivity.class);
                startActivity(intent);
            }
        });

        snackbar.show();

        try {
            Answers.getInstance().logCustom(new CustomEvent("Bookmark").putCustomAttribute("Editorial title", editorialGeneralInfo.getEditorialHeading()));

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

        final ProgressDialog pd = new ProgressDialog(CurrentAffairsFeedActivity.this);
        pd.setMessage("Creating link ...");
        pd.show();

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(currentAffairs.getLink() + "?articleID=" + currentAffairs.getId() + "&contentType=1&editorialID=-L5x80sSEktbLws6a-Qc"))
                .setDynamicLinkDomain(appCode)
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(packageName)
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(currentAffairs.getTitle())
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
                        openShareDialog(Uri.parse(currentAffairs.getLink()));
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
                + "\n" + currentAffairs.getTitle()
                + "\n\nRead full article in Daily editorial app");
        startActivity(Intent.createChooser(sharingIntent, "Share Article via"));
        try {
            Answers.getInstance().logCustom(new CustomEvent("Share link created").putCustomAttribute("Content Id", currentAffairs.getId())
                    .putCustomAttribute("Shares", currentAffairs.getTitle()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void switchTheme() {
        if (NightModeManager.getNightMode(this)) {
            NightModeManager.setNightMode(CurrentAffairsFeedActivity.this, false);
        } else {
            NightModeManager.setNightMode(CurrentAffairsFeedActivity.this, true);
        }
        recreate();
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

                setTextSize(contentTextView, size);

                SettingManager.setTextSize(CurrentAffairsFeedActivity.this, size);


            }
        });

        builder.show();
    }

    public void initializeNativeAds() {


        try {
            final AdView adView = new AdView(this);
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adView.setAdUnitId("ca-app-pub-8455191357100024/8580640678");


            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);

            adView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);


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

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                    CardView cardView = findViewById(R.id.currentAffairsFeed_admob_cardView);
                    cardView.setVisibility(View.VISIBLE);

                    cardView.removeAllViews();
                    cardView.addView(adView);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


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
                            .putCustomAttribute("Placement", "Feed native bottom CA").putCustomAttribute("errorType", error.getErrorMessage()).putCustomAttribute("Source", "Facebook"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback

                CardView linearLayout = (CardView) findViewById(R.id.currentAffairsFeed_facebook_cardView);
                linearLayout.setVisibility(View.VISIBLE);
                NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                        .setBackgroundColor(Color.parseColor("#28292e"))
                        .setTitleTextColor(Color.WHITE)
                        .setButtonTextColor(Color.WHITE)
                        .setDescriptionTextColor(Color.WHITE)
                        .setButtonColor(Color.parseColor("#F44336"));

                View adView = NativeAdView.render(CurrentAffairsFeedActivity.this, nativeAd, NativeAdView.Type.HEIGHT_400, viewAttributes);

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
                if (NightModeManager.getNightMode(CurrentAffairsFeedActivity.this)) {

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

                View adView = NativeAdView.render(CurrentAffairsFeedActivity.this, nativeAd, NativeAdView.Type.HEIGHT_100, viewAttributes);


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

        try {
            final AdView adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId("ca-app-pub-8455191357100024/5236952455");


            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);

            adView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);


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

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                    CardView cardView = findViewById(R.id.editorialfeed_top_admob_cardView);
                    cardView.setVisibility(View.VISIBLE);

                    cardView.removeAllViews();
                    cardView.addView(adView);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initializeTopNativeAds(boolean isFacebook) {


        final NativeAd nativeAd = new NativeAd(this, "113079036048193_121737141849049");
        nativeAd.setAdListener(new com.facebook.ads.AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                initializeTopNativeAds();


                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load").putCustomAttribute("Placement", "Feed native top").putCustomAttribute("errorType", error.getErrorMessage()).putCustomAttribute("Source", "Facebook"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.currentAffairsFeed_top_adContainer);
                linearLayout.setVisibility(View.VISIBLE);
                NativeAdViewAttributes viewAttributes;
                if (NightModeManager.getNightMode(CurrentAffairsFeedActivity.this)) {

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

                View adView = NativeAdView.render(CurrentAffairsFeedActivity.this, nativeAd, NativeAdView.Type.HEIGHT_120, viewAttributes);


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

    public void onShareClick(View view) {
        onShareClick();
    }


    /*public void fetchComments() {


        String url = "http://aspirantworld.in/wp-json/wp/v2/comments?post=" + currentAffairs.getId();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TAG, "onResponse: " + response);


                        commentList = new JsonParser().parseCommentList(response);

                        initializeCommentList();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG, "onErrorResponse: " + error);

                    }
                });


        jsonArrayRequest.setShouldCache(true);

        VolleyManager.getInstance().addToRequestQueue(jsonArrayRequest, "Group request");


    }

    private void initializeCommentList() {

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

        EditText editText2 = findViewById(R.id.editorialFeed_commenttext_edittext);
        final String commentString = editText2.getText().toString();

        String url = "http://aspirantworld.in/wp-json/wp/v2/comments";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "onResponse: " + error);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("content", commentString);
                params.put("post", String.valueOf(currentAffairs.getId()));
                params.put("author", String.valueOf(3));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };


        VolleyManager.getInstance().addToRequestQueue(stringRequest, "post comment");


    }
    */
}
