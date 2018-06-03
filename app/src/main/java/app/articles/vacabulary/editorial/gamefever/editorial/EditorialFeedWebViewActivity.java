package app.articles.vacabulary.editorial.gamefever.editorial;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

import utils.AuthenticationManager;
import utils.NightModeManager;
import utils.SettingManager;
import utils.ShortNotesManager;

import static com.android.volley.VolleyLog.TAG;


public class EditorialFeedWebViewActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    EditorialGeneralInfo editorialGeneralInfo;
    WebView webView;
    WebView meaningWebView;

    TextView translateText;
    private BottomSheetBehavior mBottomSheetBehavior;
    public String selectedWord = "null";

    private TextToSpeech textToSpeech;
    private boolean muteVoice;
    private boolean notesMode;

    private ShortNotesManager shortNotesManager = new ShortNotesManager(new TreeMap<String, String>());
    private boolean saveShortNotes;
    private boolean isPushNotification;


    Elements ttsVoiceReaderElements;
    private int voiceReaderChunkIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NightModeManager.getNightMode(this)) {
            setTheme(R.style.ActivityTheme_Primary_Base_Dark);
        }
        setContentView(R.layout.activity_editorial_feed_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        webView = findViewById(R.id.editorialfeed_webView);

        translateText = (TextView) findViewById(R.id.editorialfeed_bottomSheet_translate_textview);


        try {
            editorialGeneralInfo = (EditorialGeneralInfo) getIntent().getSerializableExtra("editorial");
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeMeaningWebView();
        initializeWebView();

        initializeActivity();
        initializeBottomSheet();

        muteVoice = SettingManager.getMuteVoice(this);
        textToSpeech = new TextToSpeech(this, this);


        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            Button button = (Button) findViewById(R.id.editorial_bottomsheet_audio_button);
            if (muteVoice) {
                button.setBackgroundResource(R.drawable.ic_action_audio_off);
            }

            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentId(editorialGeneralInfo.getEditorialID())
                    .putContentType(editorialGeneralInfo.getEditorialSource())
                    .putCustomAttribute("Category", editorialGeneralInfo.getEditorialCategory())
                    .putContentName(editorialGeneralInfo.getEditorialHeading()).putCustomAttribute("Mode", "Webview"));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (notesMode) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_notes_mode_action, menu);
        } else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_editorial_feed_webview_actions, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {


            case R.id.action_bookmark:
                // refresh
                onBookmark();
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

            case R.id.action_share:
                onShareClick();
                return true;

            case R.id.action_tts_reader:
                onVoiceReaderClick(item);
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
                onNotesSaveClick();
            }

            if (isPushNotification) {
                Intent intent = new Intent(EditorialFeedWebViewActivity.this, EditorialListWithNavActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {

                super.onBackPressed();

            }


        }

    }

    private void onOpenNotesActivity() {
        Intent intent = new Intent(EditorialFeedWebViewActivity.this, NotesActivity.class);
        startActivity(intent);
    }

    private void onNotesCancelClick() {
        Toast.makeText(this, "Cancel notes", Toast.LENGTH_SHORT).show();
        saveShortNotes = false;
        onTakeNotesClick();
    }

    private void onNotesSaveClick() {
        //Toast.makeText(this, "save notes", Toast.LENGTH_SHORT).show();

        String userUID = AuthenticationManager.getUserUID(EditorialFeedWebViewActivity.this);
        if (userUID == null) {
            return;
        }

        if (saveShortNotes) {
            saveShortNotes = false;


            final ProgressDialog pd = ProgressDialog.show(EditorialFeedWebViewActivity.this, "Saving Notes", "Please wait");
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

        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setJavaScriptEnabled(true);

            webView.evaluateJavascript("(function(){  if(window.getSelection) { return window.getSelection().toString(); }\n" +
                            "        else if(document.getSelection) { return document.getSelection().toString(); }\n" +
                            "        else {var selection = document.selection && document.selection.createRange();\n" +
                            "              if(selection.text) { return selection.text.toString(); }\n" +
                            "                return 'word';}})()",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                            try {
                                value = value.substring(value.indexOf("\"") + 1, value.lastIndexOf("\""));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            shortNotesManager.getShortNotePointList().put("point_" + shortNotesManager.getShortNotePointList().size(), value);

                            webView.clearFocus();
                            saveShortNotes = true;
                            webView.getSettings().setJavaScriptEnabled(false);

                            Toast.makeText(EditorialFeedWebViewActivity.this, "Point added to notes", Toast.LENGTH_SHORT).show();


                        }
                    });
        }


    }

    private void onTakeNotesClick() {

        if (notesMode) {


            notesMode = false;
            //item.setTitle("Take Notes");
            try {
                getSupportActionBar().setSubtitle("Feeds");
            } catch (Exception e) {
                e.printStackTrace();
            }

            webView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    getSelectedWord(500);
                    return false;
                }
            });

            ActivityCompat.invalidateOptionsMenu(this);

        } else {


            notesMode = true;


            webView.setOnLongClickListener(null);

            Toast.makeText(this, "Entered notes mode", Toast.LENGTH_SHORT).show();

            try {
                getSupportActionBar().setSubtitle("Notes");
            } catch (Exception e) {
                e.printStackTrace();
            }

            shortNotesManager.setNoteArticleID(editorialGeneralInfo.getEditorialID());
            shortNotesManager.setShortNoteHeading(editorialGeneralInfo.getEditorialHeading());
            shortNotesManager.setNoteArticleSource(editorialGeneralInfo.getEditorialSource());
            shortNotesManager.setShortNoteEditTimeInMillis(editorialGeneralInfo.getTimeInMillis());

            shortNotesManager.setNotesCategory(editorialGeneralInfo.getEditorialCategory());
            shortNotesManager.setArticleLink(editorialGeneralInfo.getEditorialSourceLink());
            shortNotesManager.setSourceIndex(editorialGeneralInfo.getEditorialSourceIndex());


            ActivityCompat.invalidateOptionsMenu(this);

            try {
                Answers.getInstance().logCustom(new CustomEvent("Making Notes").putCustomAttribute("Mode", "webview"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    private void onBookmark() {
        DatabaseHandlerBookMark databaseHandlerBookMark = new DatabaseHandlerBookMark(this);
        databaseHandlerBookMark.addToBookMark(editorialGeneralInfo, new EditorialExtraInfo());


        Snackbar snackbar = Snackbar
                .make(translateText, "Editorial Bookmarked successfully 👍", Snackbar.LENGTH_LONG);
        snackbar.setAction("View", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditorialFeedWebViewActivity.this, EditorialListActivity.class);
                startActivity(intent);
            }
        });

        snackbar.show();

        try {
            Answers.getInstance().logCustom(new CustomEvent("Bookmark").putCustomAttribute("Mode", "webview").putCustomAttribute("Editorial title", editorialGeneralInfo.getEditorialHeading()));

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

        final ProgressDialog pd = new ProgressDialog(EditorialFeedWebViewActivity.this);
        pd.setMessage("Creating link ...");
        pd.show();

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://goo.gl/Ae4Mhw?editorialID=" + editorialGeneralInfo.getEditorialID()))
                .setDynamicLinkDomain(appCode)
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(packageName)
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(editorialGeneralInfo.getEditorialHeading())
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
                        Toast.makeText(EditorialFeedWebViewActivity.this, "Connection Failed! Try again later", Toast.LENGTH_SHORT).show();
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
                + "\n" + editorialGeneralInfo.getEditorialHeading()
                + "\n\nRead full editorial at Daily editorial app");
        startActivity(Intent.createChooser(sharingIntent, "Share Editorial via"));
        try {
            Answers.getInstance().logCustom(new CustomEvent("Share link created").putCustomAttribute("Content Id", editorialGeneralInfo.getEditorialID())
                    .putCustomAttribute("Mode", "webview").putCustomAttribute("Shares", editorialGeneralInfo.getEditorialHeading()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void initializeWebView() {


        webView.getSettings().setLoadsImagesAutomatically(true);




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

        if (NightModeManager.getNightMode(this)) {
            webView.setBackgroundColor(Color.parseColor("#5a666b"));
        }


    }

    public void initializeMeaningWebView() {
        meaningWebView = (WebView) findViewById(R.id.editorialfeed_bottomSheet_webview);

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

    }


    public void initializeActivity() {


        webView.loadUrl(editorialGeneralInfo.getEditorialSourceLink());


        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getSelectedWord(500);
                return false;
            }
        });

    }


    public void getSelectedWord(long timeDelay) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                callJavaScript();

            }
        }, timeDelay);

    }

    private void callJavaScript() {
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setJavaScriptEnabled(true);

            webView.evaluateJavascript("(function(){  if(window.getSelection) { return window.getSelection().toString(); }\n" +
                            "        else if(document.getSelection) { return document.getSelection().toString(); }\n" +
                            "        else {var selection = document.selection && document.selection.createRange();\n" +
                            "              if(selection.text) { return selection.text.toString(); }\n" +
                            "                return 'word';}})()",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            value = value.replaceAll("\"", "");
                            selectedWord = value.trim();
                            webView.getSettings().setJavaScriptEnabled(false);
                            onWordTap(selectedWord);
                        }
                    });
        }
    }

    private void onWordTap(String mWord) {

        webView.clearFocus();

        selectedWord = mWord;


        translateText.setText(mWord);


        //speakOutWord(mWord);
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
        //openBottomSheet(true);

       /* Dictionary dictionary = new Dictionary(selectedWord);
        dictionary.fetchWordMeaning(selectedWord, EditorialFeedActivity.this);
*/


        loadWebview(selectedWord);
    }

    public void loadWebview(String mWord) {
        meaningWebView.loadUrl("http://www.dictionary.com/browse/" + selectedWord);
    }

    public void readFullArticle(View view) {

        if (muteVoice) {
            muteVoice = false;
            Toast.makeText(this, "Voice enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Voice disabled", Toast.LENGTH_SHORT).show();
            muteVoice = true;
        }
        SettingManager.setMuteVoice(EditorialFeedWebViewActivity.this, muteVoice);
        Button button = (Button) findViewById(R.id.editorial_bottomsheet_audio_button);
        if (muteVoice) {
            button.setBackgroundResource(R.drawable.ic_action_audio_off);
        } else {
            button.setBackgroundResource(R.drawable.ic_action_audio);
        }

    }


    public void onDictionaryClick(View view) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void onVoiceReaderClick(MenuItem item) {

        try {
            if (textToSpeech.isSpeaking()) {
                speakOutWord("");
                item.setTitle("Read Editorial (Voice)");

            } else {
                Toast.makeText(this, "Preparing Voice Reader", Toast.LENGTH_SHORT).show();
                item.setTitle("Stop Reader");
                voiceReaderChunkIndex = 0;
                parseWebpage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void parseWebpage() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {

                    Connection connect = Jsoup.connect(editorialGeneralInfo.getEditorialSourceLink().toString());

                    Document document = connect.get();

                    Elements elements = document.select("p");


                    for (int i = elements.size() - 1; i > 0; i--) {

                        Element element = elements.get(i);
                        if (element.toString().length() < 150) {
                            elements.remove(i);
                        }


                    }

                    String voiceToReader = elements.text();

                    Log.d("TTS Reader", "run: " + voiceToReader);

                    ttsVoiceReaderElements = elements;


                } catch (Exception e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        startVoiceReader();


                    }
                });


            }
        }).start();

    }

    private void startVoiceReader() {

        try {
            if (voiceReaderChunkIndex < ttsVoiceReaderElements.size()) {
                String chunk = ttsVoiceReaderElements.get(voiceReaderChunkIndex).text();

                voiceReaderChunkIndex++;


                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String s) {

                    }

                    @Override
                    public void onDone(String s) {
                        startVoiceReader();

                    }

                    @Override
                    public void onError(String s) {

                    }
                });


                if (Build.VERSION.SDK_INT > 21) {
                    textToSpeech.speak(chunk, TextToSpeech.QUEUE_FLUSH, null, "1");
                }

            }
        } catch (Exception e) {

            e.printStackTrace();
        }


    }


    public void onAddToVocabularyClick(View view) {

        DatabaseHandler databaseHandler = new DatabaseHandler(EditorialFeedWebViewActivity.this);
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
                Intent intent = new Intent(EditorialFeedWebViewActivity.this, VacabularyActivity.class);
                startActivity(intent);
            }
        });

        snackbar.show();

    }

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
                    textToSpeech.setSpeechRate(SettingManager.getVoiceReaderSpeed(this));
                    break;
                }
                case TextToSpeech.LANG_NOT_SUPPORTED:
                case TextToSpeech.LANG_MISSING_DATA:
                case TextToSpeech.LANG_AVAILABLE: {
                    result = textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setPitch(0.9f);
                    textToSpeech.setSpeechRate(SettingManager.getVoiceReaderSpeed(this));
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

                textToSpeech.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onShareButtonClick(View view) {
        onShareClick();
    }
}
