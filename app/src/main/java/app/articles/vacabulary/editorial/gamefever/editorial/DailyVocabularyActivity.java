package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import utils.AdsSubscriptionManager;
import utils.NightModeManager;
import utils.Vocabulary;
import utils.ZoomOutPageTransformer;

public class DailyVocabularyActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    ArrayList<Vocabulary> mVocabularyList = new ArrayList<>();

    boolean isLoading = false;

    ProgressDialog pDialog;
    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NightModeManager.getNightMode(this)) {
            setTheme(R.style.FeedActivityThemeDark);
        }
        setContentView(R.layout.activity_daily_vocabulary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        showLoadingDialog("Loading..");


        mPager = (ViewPager) findViewById(R.id.dailyVocabulary_viewpager);
        initializeViewPager();


        mPagerAdapter.notifyDataSetChanged();


        new DBHelperFirebase().fetchDailyVocabularyList(10, new DBHelperFirebase.VocabularyListener() {
            @Override
            public void onVocabularyList(ArrayList<Vocabulary> vocabularyArrayList, boolean isSuccesful) {

                if (isSuccesful) {
                    for (Vocabulary vocab : vocabularyArrayList) {
                        mVocabularyList.add(vocab);
                    }

                    try {
                        if (AdsSubscriptionManager.checkShowAds(DailyVocabularyActivity.this)) {
                            addNativeAds();
                        }
                    } catch (Exception e) {

                    }

                    mPagerAdapter.notifyDataSetChanged();

                    hideLoadingDialog();
                    try {
                        Answers.getInstance().logCustom(new CustomEvent("Daily Vocabulary open").putCustomAttribute("first word", mVocabularyList.get(0).getmWord()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DailyVocabularyActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

        try {
            tts = new TextToSpeech(this, this);
            Answers.getInstance().logCustom(new CustomEvent("Daily Vocabulary open"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void addNativeAds() {
        for (int i = 0; i < mVocabularyList.size(); i = i + 3) {


            NativeAd nativeAd = new NativeAd(this, "113079036048193_145598676129562");
            nativeAd.setAdListener(new AdListener() {

                @Override
                public void onError(Ad ad, AdError adError) {

                    try {
                        Answers.getInstance().logCustom(new CustomEvent("Ad failed to load").putCustomAttribute("Placement", "vocabulary card").putCustomAttribute("errorType", adError.getErrorMessage()).putCustomAttribute("Source", "Facebook"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    mPagerAdapter.notifyDataSetChanged();

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

            mVocabularyList.get(i).setNativeAd(nativeAd);
            mVocabularyList.get(i).setContentType(1);

        }
    }

    public void showLoadingDialog(String message) {
        pDialog.setMessage(message);
        pDialog.show();
    }

    public void hideLoadingDialog() {
        try {
            pDialog.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeViewPager() {

// Instantiate a ViewPager and a PagerAdapter.

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        //change to zoom
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                speakOutWord(mVocabularyList.get(position).getmWord());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void loadMoreVocabulary() {

        if (isLoading) {

        } else {

            isLoading = true;

            new DBHelperFirebase().fetchDailyVocabularyList(5, mVocabularyList.get(mVocabularyList.size() - 1).getTimeInMillis(), new DBHelperFirebase.VocabularyListener() {
                @Override
                public void onVocabularyList(ArrayList<Vocabulary> vocabularyArrayList, boolean isSuccesful) {
                    isLoading = false;
                    if (isSuccesful) {

                        for (Vocabulary vocab : vocabularyArrayList) {
                            mVocabularyList.add(vocab);
                        }
                        mPagerAdapter.notifyDataSetChanged();

                    } else {

                    }
                }
            });

        }

    }

    private void onSortByDateClick() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Toast.makeText(EditorialListWithNavActivity.this, "Date selected +" + dayOfMonth + " - " + month, Toast.LENGTH_SHORT).show();

                String str_date = dayOfMonth + "-" + (month + 1) + "-" + year;
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date date = (Date) formatter.parse(str_date);

                    long sortDateMillis = date.getTime();

                    fetchVocabularyByDate(sortDateMillis);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMinDate(1517928906692l);
        datePickerDialog.show();

    }

    private void fetchVocabularyByDate(long sortDateMillis) {

        showLoadingDialog("Loading...");

        new DBHelperFirebase().fetchDailyVocabularyList(sortDateMillis, (sortDateMillis + 86400000), new DBHelperFirebase.VocabularyListener() {
            @Override
            public void onVocabularyList(ArrayList<Vocabulary> vocabularyArrayList, boolean isSuccesful) {

                if (isSuccesful) {

                    mVocabularyList.clear();

                    mVocabularyList.addAll(vocabularyArrayList);

                    mPagerAdapter.notifyDataSetChanged();
                    hideLoadingDialog();

                } else {

                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.acitivity_vocabulary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {


            case R.id.vocabulary_action_date:
                onSortByDateClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);


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
                    break;
                }
                case TextToSpeech.LANG_NOT_SUPPORTED:
                case TextToSpeech.LANG_MISSING_DATA:
                case TextToSpeech.LANG_AVAILABLE: {
                    result = tts.setLanguage(Locale.US);
                    tts.setPitch(0.9f);
                    tts.setSpeechRate(0.9f);
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

            tts.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (mVocabularyList.size() - position < 3) {
                loadMoreVocabulary();
            }

            return DailyVocabularyFragment.newInstance(mVocabularyList.get(position));
        }

        @Override
        public int getCount() {
            return mVocabularyList.size();
        }

    }


}
