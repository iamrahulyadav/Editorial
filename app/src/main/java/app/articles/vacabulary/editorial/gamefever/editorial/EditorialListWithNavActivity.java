package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;

public class EditorialListWithNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static int sortedListLimit = 0;
    private List<EditorialGeneralInfo> editorialListArrayList = new ArrayList<>();
    private List<EditorialGeneralInfo> editorialListSortedArrayList = new ArrayList<>();

    private RecyclerView recyclerView;
    private EditorialGeneralInfoAdapter mAdapter;


    View addMoreButton;
    ProgressBar progressBar;
    private boolean isRefreshing = true;
    private boolean isSplashScreenVisible = true;
    public static boolean isShowingAd = false;
    public static String shareLink = "https://play.google.com/store/apps/details?id=app.articles.vacabulary.editorial.gamefever.editorial";
    public static String visitUsLink = "https://appforyou.wixsite.com/android";


    public static int listLimit = 10;
    public static int EDITORIALCOUNTADS = 0;

    public String selectedSortWord = "";
    private String activityCurrentTheme = "Day";

    SwipeRefreshLayout swipeRefreshLayout;

    InterstitialAd mInterstitialAd;
    private  int editorialcountAdMax =2;
    GoogleApiClient mGoogleApiClient;
    boolean isActivityInitialized=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initializeRemoteConfig();


        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorialListWithNavActivity.this);


            // Setting Dialog Title
            alertDialog.setTitle("No Internet connection");

            // Setting Dialog Message
            alertDialog.setMessage("Do you want to open Bookmarks for offline reading");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.ic_action_information);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    onBookMark();

                    dialog.cancel();
                }
            });


            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("Not much", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event

                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();


        }

        initializeSplashScreen();


// Build GoogleApiClient with AppInvite API for receiving deep links
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(EditorialListWithNavActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(AppInvite.API)
                .build();

        // Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
        // would automatically launch the deep link if one is found.
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    // Extract deep link from Intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    //  Toast.makeText(EditorialListWithNavActivity.this, "link is"+deepLink, Toast.LENGTH_SHORT).show();

                                    int lastIndex = deepLink.indexOf("?", 27);
                                    String editorialID = deepLink.substring(27, lastIndex);
                                    // Toast.makeText(EditorialListWithNavActivity.this, "id  "+editorialID, Toast.LENGTH_SHORT).show();

                                    fetchEditorialByID(editorialID);
                                    // Handle the deep link. For example, open the linked
                                    // content, or apply promotional credit to the user's
                                    // account.

                                    // ...
                                } else {
                                    Log.d("editorial", "getInvitation: no deep link found.");
                                    fetchEditorialGeneralList();
                                    isActivityInitialized = true;

                                }
                            }
                        });


        FirebaseMessaging.getInstance().subscribeToTopic("subscribed");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8455191357100024/2541985598");
        loadInterstitialAd();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (isSplashScreenVisible) {
            fetchEditorialGeneralList();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

/*        if (isActivityInitialized && mGoogleApiClient != null) {

            boolean autoLaunchDeepLink = false;
            AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                    .setResultCallback(
                            new ResultCallback<AppInviteInvitationResult>() {
                                @Override
                                public void onResult(@NonNull AppInviteInvitationResult result) {
                                    if (result.getStatus().isSuccess()) {
                                        // Extract deep link from Intent
                                        Intent intent = result.getInvitationIntent();
                                        String deepLink = AppInviteReferral.getDeepLink(intent);
                                        //  Toast.makeText(EditorialListWithNavActivity.this, "link is"+deepLink, Toast.LENGTH_SHORT).show();

                                        int lastIndex = deepLink.indexOf("?", 27);
                                        String editorialID = deepLink.substring(27, lastIndex);
                                        // Toast.makeText(EditorialListWithNavActivity.this, "id  "+editorialID, Toast.LENGTH_SHORT).show();

                                        fetchEditorialByID(editorialID);
                                        // Handle the deep link. For example, open the linked
                                        // content, or apply promotional credit to the user's
                                        // account.

                                        // ...
                                    } else {


                                    }
                                }
                            });

        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isSplashScreenVisible) {
            fetchEditorialGeneralList();
        }

    }

    private void fetchEditorialByID(String editorialID) {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.getEditorialExtraInfoByID(editorialID, this);

    }

    public void initializeActivity() {

// the content to show and initialize navigation drawer

        setContentView(R.layout.activity_editorial_list_with_nav);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
           // getSupportActionBar().setIcon(R.mipmap.ic_launcher);
            getSupportActionBar().setTitle(getString(R.string.app_name));
        } catch (Exception e) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


// the content of EditorialListActivity from here
        isSplashScreenVisible = false;


        recyclerView = (RecyclerView) findViewById(R.id.editoriallist_recyclerview);


        mAdapter = new EditorialGeneralInfoAdapter(editorialListSortedArrayList, getActivityTheme());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever

                        onRecyclerViewItemClick(position);
                        showInterstitialAd();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(EditorialListWithNavActivity.this, "loading more items ", Toast.LENGTH_SHORT).show();
                    loadMoreClick();
                }
            }
        });*/


        addMoreButton = (View) findViewById(R.id.editoriallist_activity_add_button);
        addMoreButton.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) findViewById(R.id.editoriallist_activity_progressbar);
        progressBar.setVisibility(View.VISIBLE);


        if (isShowingAd) {
            initializeAds();
        }


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.editoriallist_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchEditorialGeneralList();
                swipeRefreshLayout.setRefreshing(true);
            }
        });


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!isSplashScreenVisible) {

            if (!activityCurrentTheme.contentEquals(getActivityTheme())) {
                changeActivityTheme(getActivityTheme());
            }
        }
    }

    private String getActivityTheme() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


        String theme = sharedPref.getString("theme_list", "Day");
        this.activityCurrentTheme = theme;
        return theme;
    }

    private void changeActivityTheme(String day) {
        mAdapter = new EditorialGeneralInfoAdapter(editorialListSortedArrayList, day);
        recyclerView.setAdapter(mAdapter);
    }


    public void initializeSplashScreen() {

        setContentView(R.layout.splashlayout);

    }


    private void onRecyclerViewItemClick(int position) {
        EditorialGeneralInfo editorialgenralInfo = editorialListArrayList.get(position);
        Intent i = new Intent(this, EditorialFeedActivity.class);
        i.putExtra("editorialID", editorialgenralInfo.getEditorialID());
        i.putExtra("editorialDate", editorialgenralInfo.getEditorialDate());
        i.putExtra("editorialHeading", editorialgenralInfo.getEditorialHeading());
        i.putExtra("editorialSource", editorialgenralInfo.getEditorialSource());
        i.putExtra("editorialSubheading", editorialgenralInfo.getEditorialSubHeading());
        i.putExtra("editorialTag", editorialgenralInfo.getEditorialTag());
        i.putExtra("isBookMarked", false);


        startActivity(i);


    }

    private void onSharedLinkOpen(EditorialGeneralInfo editorialgenralInfo) {

        Intent i = new Intent(this, EditorialFeedActivity.class);
        i.putExtra("editorialID", editorialgenralInfo.getEditorialID());
        i.putExtra("editorialDate", editorialgenralInfo.getEditorialDate());
        i.putExtra("editorialHeading", editorialgenralInfo.getEditorialHeading());
        i.putExtra("editorialSource", editorialgenralInfo.getEditorialSource());
        i.putExtra("editorialSubheading", editorialgenralInfo.getEditorialSubHeading());
        i.putExtra("editorialTag", editorialgenralInfo.getEditorialTag());
        i.putExtra("isBookMarked", false);


        startActivity(i);


    }


    public void fetchEditorialGeneralList() {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, "", this, true);

    }


    public void loadMoreClick(View view) {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, editorialListArrayList.get
                (editorialListArrayList.size() - 1).getEditorialID(), this, false);

        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        sortEditorList(selectedSortWord);

    }

    public void loadMoreClick() {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, editorialListArrayList.get(editorialListArrayList.size() - 1).getEditorialID(), this, false);

        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        sortEditorList(selectedSortWord);

    }

    public void onFetchEditorialGeneralInfo(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist, boolean isFirst) {

        if (isSplashScreenVisible) {
            initializeActivity();
        }


        if (!isFirst) {
            editorialGeneralInfoArraylist.remove(editorialGeneralInfoArraylist.size() - 1);
        } else {
            editorialListArrayList.clear();
        }

        int insertPosition = editorialListArrayList.size();
        for (EditorialGeneralInfo editorialGeneralInfo : editorialGeneralInfoArraylist) {
            editorialListArrayList.add(insertPosition, editorialGeneralInfo);
        }
        mAdapter.notifyDataSetChanged();


        addMoreButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        isRefreshing = false;

        sortEditorList(selectedSortWord);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    private void sortEditorList(String selectedSortWord) {


        if (!selectedSortWord.equals("")) {


            editorialListSortedArrayList.clear();
            for (EditorialGeneralInfo editorialGeneralInfo : editorialListArrayList) {

                if (editorialGeneralInfo.getEditorialSource().equals(selectedSortWord)) {
                    editorialListSortedArrayList.add(editorialGeneralInfo);


                }
            }
        } else {
            editorialListSortedArrayList.clear();
            for (EditorialGeneralInfo editorialGeneralInfo : editorialListArrayList) {
                editorialListSortedArrayList.add(editorialGeneralInfo);
            }

        }
        mAdapter.notifyDataSetChanged();

        setToolBarSubTitle(selectedSortWord);

        if (editorialListSortedArrayList.size() < EditorialListWithNavActivity.sortedListLimit) {
            loadMoreClick();

        }

    }


    public void initializeAds() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8455191357100024~6634740792");
        final AdView mAdView = (AdView) findViewById(R.id.editorialList_activity_adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                FirebaseCrash.log(" Editorial list Ad failed to load - " + i);


            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
                FirebaseCrash.log(" Editorial list Ad loaded " );
            }
        });

    }


    public void initializeRemoteConfig() {
        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();


        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);


        mFirebaseRemoteConfig.fetch()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(EditorialListActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();

                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            // Toast.makeText(EditorialListActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                        //displayWelcomeMessage();
                    }
                });

/*got an static share link to use so remote config for share link is not required*/


        try {
            EditorialListWithNavActivity.shareLink = mFirebaseRemoteConfig.getString("shareLink");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            EditorialListWithNavActivity.isShowingAd = Boolean.valueOf(mFirebaseRemoteConfig.getString("isShowingAd"));

        } catch (Exception e) {
            e.printStackTrace();
            EditorialListWithNavActivity.isShowingAd = false;
            FirebaseCrash.log("Value of isShowingad isWrong");
        }

        try {

            EditorialListWithNavActivity.listLimit = Integer.valueOf(mFirebaseRemoteConfig.getString("listLimit"));

        } catch (Exception e) {
            e.printStackTrace();
            EditorialListWithNavActivity.listLimit = 20;
            FirebaseCrash.log("Value of listLimit isWrong");
        }


        try {

            EditorialListWithNavActivity.sortedListLimit = Integer.valueOf(mFirebaseRemoteConfig.getString("sortedListLimit"));

        } catch (Exception e) {
            e.printStackTrace();
            EditorialListWithNavActivity.sortedListLimit = 2;
            FirebaseCrash.log("Value of sortedlistlimit isWrong");
        }

        try {

            editorialcountAdMax = Integer.valueOf(mFirebaseRemoteConfig.getString("editorialCountAdMax"));

        } catch (Exception e) {
            e.printStackTrace();

            editorialcountAdMax = 2;
            FirebaseCrash.log("Value of editorialcountadmax isWrong");
        }

        try {
            EditorialListWithNavActivity.visitUsLink = mFirebaseRemoteConfig.getString("visitAppForYou");

        } catch (Exception e) {
            e.printStackTrace();

        }


    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onBackPressed() {
        if (!isSplashScreenVisible) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.activity_editorial_list_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_refresh:
                // refresh
                onRefreashClick();
                return true;
            case R.id.action_share:
                // help action
                onShareClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_all:
                onAllClick();
                break;

            case R.id.nav_the_hindu:
                onTheHinduClick();
                break;

            case R.id.nav_financial_express:
                onfinancialExpClick();
                break;

            case R.id.nav_economic_times:
                onEconomicTimesClick();
                break;


            case R.id.nav_vacabulary:
                onVacabularyClick();
                break;

            case R.id.nav_share:
                onShareClick();
                break;




             /* case R.id.nav_tutorial:
                onTutorialClick();
                break;
            */
            case R.id.nav_setting:
                onSettingClick();
                break;


            case R.id.nav_rate_us:
                onRateUs();
                break;


            case R.id.nav_bookmark:
                onBookMark();
                break;


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onBookMark() {
        Intent intent = new Intent(this, EditorialListActivity.class);
        startActivity(intent);
    }

    private void onCurrentAffairs() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://currentaffairsonly.com/")));
        } catch (Exception e) {

        }
    }

    private void onRateUs() {
        try {
            String link = "https://play.google.com/store/apps/details?id=" + this.getPackageName();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (Exception e) {

        }
    }

    private void onTheHinduNoteClick() {

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://currentaffairsonly.com/the-hindu-notes/")));
        } catch (Exception e) {

        }
    }

    private void onVacabularyClick() {
        Intent i = new Intent(this, VacabularyActivity.class);
        startActivity(i);

    }

    private void onShareClick() {




        /*Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the app and Start reading");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, EditorialListWithNavActivity.shareLink);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
*/

        int applicationNameId = this.getApplicationInfo().labelRes;
        final String appPackageName = this.getPackageName();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, getString(applicationNameId));
        String text = "Install this  Daily Editorial and improve you vocabulary: ";
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        i.putExtra(Intent.EXTRA_TEXT, text + "\n " + link);
        startActivity(Intent.createChooser(i, "Share App :"));


    }

    private void onSettingClick() {

        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);

    }

    private void onAboutClick() {

        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);

    }

    private void onRefreashClick() {

        editorialListArrayList.clear();
        mAdapter.notifyDataSetChanged();
        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if (!isRefreshing) {
            fetchEditorialGeneralList();
            isRefreshing = true;
        }

    }


    private void onEconomicTimesClick() {
        selectedSortWord = "The Economic Times";
        sortEditorList(selectedSortWord);


    }

    private void onfinancialExpClick() {
        selectedSortWord = "The Financial Express";
        sortEditorList(selectedSortWord);

    }

    private void onTheHinduClick() {
        selectedSortWord = "The Hindu";
        sortEditorList(selectedSortWord);
    }

    private void onAllClick() {
        selectedSortWord = "";
        sortEditorList(selectedSortWord);
    }


    private void onTutorialClick() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);

    }

    public void setToolBarSubTitle(String subTitle) {
        try {
            getSupportActionBar().setSubtitle(subTitle);
        } catch (Exception e) {

        }
    }


    public void getEditorialExtraInfoByIDListner(EditorialGeneralInfo editorialGeneralInfo) {
        onSharedLinkOpen(editorialGeneralInfo);
    }


    private void loadInterstitialAd() {

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                //Toast.makeText(EditorialListWithNavActivity.this, "Ad failed - " + i, Toast.LENGTH_SHORT).show();
                FirebaseCrash.log("Ad failed to load - " + i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                FirebaseCrash.log("Ad opened in editorial count " + EDITORIALCOUNTADS);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                //Toast.makeText(EditorialListWithNavActivity.this, "Ad loaded", Toast.LENGTH_SHORT).show();
                FirebaseCrash.log("Ad loaded in editorial count " + EDITORIALCOUNTADS);
            }
        });

    }

    public void showInterstitialAd() {
        //set editorialcount to 0


        if (EDITORIALCOUNTADS > editorialcountAdMax) {

            if (mInterstitialAd != null) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    EDITORIALCOUNTADS = 0;
                    loadInterstitialAd();
                } else {
                    loadInterstitialAd();
                }
            } else {
                loadInterstitialAd();
            }
        } else {
            EDITORIALCOUNTADS++;
        }

    }

}
