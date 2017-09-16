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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.InviteEvent;
import com.crashlytics.android.answers.PurchaseEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import io.fabric.sdk.android.Fabric;
import utils.AdsSubscriptionManager;
import utils.LanguageManager;
import utils.PushNotificationManager;

import java.util.ArrayList;
import java.util.List;

public class EditorialListWithNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static int sortedListLimit = 0;
    private ArrayList<Object> editorialListArrayList = new ArrayList<>();
    private ArrayList<Object> editorialListSortedArrayList = new ArrayList<>();

    private RecyclerView recyclerView;
    private EditorialGeneralInfoAdapter mAdapter;


    View addMoreButton;
    ProgressBar progressBar;
    private boolean isRefreshing = true;
    private boolean isSplashScreenVisible = true;


    public static int listLimit = 20;
    public static int EDITORIALCOUNTADS = 0;

    public String selectedSortWord = "";
    private boolean isNightMode = false;

    SwipeRefreshLayout swipeRefreshLayout;

    InterstitialAd mInterstitialAd;
    private int editorialcountAdMax = 2;
    boolean isActivityInitialized = false;
    private InterstitialAd mSubscriptionInterstitialAd;

    //sort variable
    int sortSourceIndex = -1;
    int sortCategoryIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.FeedActivityThemeDark);
            isNightMode = true;
        }
        initializeRemoteConfig();

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8455191357100024~6634740792");


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

        //see intentdata for sort condition if any
        getSortCondition();

        openDynamicLink();


        FirebaseMessaging.getInstance().subscribeToTopic("subscribed");


        initializeSubscriptionAds();

    }

    private void getSortCondition() {

        Intent intent = getIntent();

        sortSourceIndex = intent.getIntExtra("sourceIndex", -1);
        sortCategoryIndex = intent.getIntExtra("categoryIndex", -1);


    }

    private void openDynamicLink() {


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d("DeepLink", "onSuccess: " + deepLink);

                            String editorialID = deepLink.getQueryParameter("editorialID");
                            // Toast.makeText(EditorialListWithNavActivity.this, "newsArticle id " + editorialID, Toast.LENGTH_SHORT).show();


                            if (editorialID == null) {
                                String deepLinkstring = deepLink.toString();

                                //  Toast.makeText(EditorialListWithNavActivity.this, "link is"+deepLink, Toast.LENGTH_SHORT).show();

                                int lastIndex = deepLinkstring.indexOf("?", 27);
                                editorialID = deepLinkstring.substring(27, lastIndex);
                                // Toast.makeText(EditorialListWithNavActivity.this, "id  "+editorialID, Toast.LENGTH_SHORT).show();


                                // Handle the deep link. For example, open the linked
                                // content, or apply promotional credit to the user's
                                // account.

                                // ...


                            }


                            fetchEditorialByID(editorialID);

                            try {
                                Answers.getInstance().logCustom(new CustomEvent("User via Dynamic link")
                                        .putCustomAttribute("editorial", editorialID)
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            Log.d("editorial", "getInvitation: no deep link found.");
                            fetchEditorialGeneralList();
                            isActivityInitialized = true;

                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DeepLink", "getDynamicLink:onFailure", e);
                        Log.d("editorial", "getInvitation: no deep link found.");
                        fetchEditorialGeneralList();
                        isActivityInitialized = true;

                    }
                });


    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isSplashScreenVisible && !isRefreshing) {
            fetchEditorialGeneralList();
        }
        if (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES) {

            if (!isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
            }

        } else {

            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
            }
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
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


// the content of EditorialListActivity from here
        isSplashScreenVisible = false;


        recyclerView = (RecyclerView) findViewById(R.id.editoriallist_recyclerview);


       /* String actvityTheme =getActivityTheme();
        if (actvityTheme.contentEquals("Night")){
            View view = findViewById(R.id.activity_editorial_list);
            view.setBackgroundColor(ContextCompat.getColor(this,R.color.editorialList_background_night));

        }
*/
        mAdapter = new EditorialGeneralInfoAdapter(editorialListArrayList, "day", this);
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


        if (AdsSubscriptionManager.checkShowAds(this)) {

            //initializeAds();
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-8455191357100024/2541985598");
            initializeInterstitialAds();
        }


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.editoriallist_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sortCategoryIndex = -1;
                sortSourceIndex = -1;
                fetchEditorialGeneralList();
                swipeRefreshLayout.setRefreshing(true);
            }
        });


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        /*if (!isSplashScreenVisible) {

            if (!activityCurrentTheme.contentEquals(getActivityTheme())) {
                changeActivityTheme(getActivityTheme());
            }
        }*/
    }

   /* private String getActivityTheme() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


        String theme = sharedPref.getString("theme_list", "Day" );
        this.activityCurrentTheme = theme;
        return theme;
    }*/


    public void initializeSplashScreen() {

        setContentView(R.layout.splashlayout);

    }


    private void onRecyclerViewItemClick(int position) {

        if (position < 0) {
            recreate();
        }

        if (position % 8 == 0) {
            return;
        }


        EditorialGeneralInfo editorialgenralInfo = (EditorialGeneralInfo) editorialListArrayList.get(position);
        Intent i = new Intent(this, EditorialFeedActivity.class);
        i.putExtra("editorialID", editorialgenralInfo.getEditorialID());
        i.putExtra("editorialDate", editorialgenralInfo.getEditorialDate());
        i.putExtra("editorialHeading", editorialgenralInfo.getEditorialHeading());
        i.putExtra("editorialSource", editorialgenralInfo.getEditorialSource());
        i.putExtra("editorialSubheading", editorialgenralInfo.getEditorialSubHeading());
        i.putExtra("editorialTag", editorialgenralInfo.getEditorialTag());
        i.putExtra("isBookMarked", false);
        i.putExtra("editorial", editorialgenralInfo);


        startActivity(i);

        showInterstitialAd();

    }

    private void onSharedLinkOpen(EditorialGeneralInfo editorialgenralInfo) {

        if (editorialgenralInfo == null) {
            recreate();
            return;
        }

        Intent i = new Intent(this, EditorialFeedActivity.class);
        i.putExtra("editorialID", editorialgenralInfo.getEditorialID());
        i.putExtra("editorialDate", editorialgenralInfo.getEditorialDate());
        i.putExtra("editorialHeading", editorialgenralInfo.getEditorialHeading());
        i.putExtra("editorialSource", editorialgenralInfo.getEditorialSource());
        i.putExtra("editorialSubheading", editorialgenralInfo.getEditorialSubHeading());
        i.putExtra("editorialTag", editorialgenralInfo.getEditorialTag());
        i.putExtra("isBookMarked", false);
        i.putExtra("editorial", editorialgenralInfo);
        i.putExtra("isDynamicLink", true);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);


    }


    public void fetchEditorialGeneralList() {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();

        DBHelperFirebase.OnEditorialListListener onEditorialListListener = new DBHelperFirebase.OnEditorialListListener() {
            @Override
            public void onEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {
                onFetchEditorialGeneralInfo(editorialGeneralInfoArrayList, true);
            }

            @Override
            public void onMoreEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {

            }
        };

        isRefreshing = true;

        if (sortSourceIndex > -1) {
            dbHelperFirebase.fetchSourceSortEditorialList(EditorialListWithNavActivity.listLimit, sortSourceIndex, onEditorialListListener);

        } else if (sortCategoryIndex > -1) {
            dbHelperFirebase.fetchCategorySortEditorialList(EditorialListWithNavActivity.listLimit, sortCategoryIndex, onEditorialListListener);

        } else {
            dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, onEditorialListListener);

        }

    }


    public void loadMoreClick(View view) {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        //dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, editorialListArrayList.get(editorialListArrayList.size() - 1).getEditorialID(), this, false);

        DBHelperFirebase.OnEditorialListListener onEditorialListListener = new DBHelperFirebase.OnEditorialListListener() {
            @Override
            public void onEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {

            }

            @Override
            public void onMoreEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {

                onFetchEditorialGeneralInfo(editorialGeneralInfoArrayList, false);
            }
        };

        if (editorialListArrayList.size() > 0) {
            if (editorialListArrayList.get(editorialListArrayList.size() - 1).getClass() == EditorialGeneralInfo.class) {
                if (sortSourceIndex > -1) {
                    dbHelperFirebase.fetchSourceSortEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 1)).getEditorialID(), sortSourceIndex, onEditorialListListener);

                } else if (sortCategoryIndex > -1) {
                    dbHelperFirebase.fetchCategorySortEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 1)).getEditorialID(), sortCategoryIndex, onEditorialListListener);

                } else {
                    dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 1)).getEditorialID(), onEditorialListListener);

                }
            } else {
                if (sortSourceIndex > -1) {
                    dbHelperFirebase.fetchSourceSortEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 2)).getEditorialID(), sortSourceIndex, onEditorialListListener);

                } else if (sortCategoryIndex > -1) {
                    dbHelperFirebase.fetchCategorySortEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 2)).getEditorialID(), sortCategoryIndex, onEditorialListListener);

                } else {
                    dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 2)).getEditorialID(), onEditorialListListener);

                }
            }
        }
        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        sortEditorList(selectedSortWord);

    }

    public void loadMoreClick() {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 1)).getEditorialID(), this, false);

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

        isRefreshing = false;

        addNativeExpressAds();

        if (isFirst) {
            recyclerView.smoothScrollToPosition(1);
        }

        mAdapter.notifyDataSetChanged();


        addMoreButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        isRefreshing = false;

        //sortEditorList(selectedSortWord);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    private void addNativeExpressAds() {

        boolean checkShowAds = AdsSubscriptionManager.checkShowAds(this);

        //main function where ads is merged in editorial list as an object

        for (int i = 0; i < (editorialListArrayList.size()); i += 8) {
            if (editorialListArrayList.get(i).getClass() != NativeExpressAdView.class) {
                final NativeExpressAdView adView = new NativeExpressAdView(this);

                adView.setAdUnitId("ca-app-pub-8455191357100024/8254824112");
                adView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

                adView.setAdSize(new AdSize(320, 132));
                if (checkShowAds) {
                    adView.loadAd(new AdRequest.Builder().build());
                }
                editorialListArrayList.add(i, adView);

            }
        }



     /*   recyclerView.post(new Runnable() {
            @Override
            public void run() {
                final float density = EditorialListWithNavActivity.this.getResources().getDisplayMetrics().density;

                AdSize adSize = new AdSize(
                        ((int) (recyclerView.getWidth() / density)) - 20,
                        120
                );

                for (int i = 0; i < editorialListArrayList.size(); i += 8) {
                    NativeExpressAdView nativeExpressAdView = (NativeExpressAdView) editorialListArrayList.get(i);
                    nativeExpressAdView.setAdSize(adSize);
                    nativeExpressAdView.loadAd(new AdRequest.Builder().build());
                }
            }
        });*/


    }

    private void sortEditorList(String selectedSortWord) {


        if (!selectedSortWord.equals("")) {


            editorialListSortedArrayList.clear();
            for (Object editorialGeneralInfo : editorialListArrayList) {

                if (((EditorialGeneralInfo) editorialGeneralInfo).getEditorialSource().equals(selectedSortWord)) {
                    editorialListSortedArrayList.add(editorialGeneralInfo);


                }
            }
        } else {
            editorialListSortedArrayList.clear();
            for (Object editorialGeneralInfo : editorialListArrayList) {
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
        // MobileAds.initialize(getApplicationContext(), "ca-app-pub-8455191357100024~6634740792");
        final AdView mAdView = (AdView) findViewById(R.id.editorialList_activity_adView);

        mAdView.setVisibility(View.GONE);

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);


                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "List banner").putCustomAttribute("errorType", i));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
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

            EditorialListWithNavActivity.listLimit = Integer.valueOf(mFirebaseRemoteConfig.getString("listLimit"));

        } catch (Exception e) {
            e.printStackTrace();
            EditorialListWithNavActivity.listLimit = 20;
        }


        try {

            EditorialListWithNavActivity.sortedListLimit = Integer.valueOf(mFirebaseRemoteConfig.getString("sortedListLimit"));

        } catch (Exception e) {
            e.printStackTrace();
            EditorialListWithNavActivity.sortedListLimit = 2;
        }

        try {

            editorialcountAdMax = Integer.valueOf(mFirebaseRemoteConfig.getString("editorialCountAdMax"));

        } catch (Exception e) {
            e.printStackTrace();

            editorialcountAdMax = 2;
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

                if (sortCategoryIndex > -1 || sortSourceIndex > -1) {
                    sortCategoryIndex = -1;
                    sortSourceIndex = -1;

                    fetchEditorialGeneralList();
                    try {
                        swipeRefreshLayout.setRefreshing(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    super.onBackPressed();
                }


            }
        } else {
            super.onBackPressed();
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
            /*case R.id.nav_all:
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
*/

            case R.id.nav_vacabulary:
                onVacabularyClick();
                break;

            case R.id.nav_share:
                onShareClick();
                break;


            case R.id.nav_rate_us:
                onRateUs();
                break;


            case R.id.nav_bookmark:
                onBookMark();
                break;

            case R.id.nav_day_mode:
                onDayMode();
                break;

            case R.id.nav_night_mode:
                onNightMode();
                break;
            case R.id.nav_removeads:
                onRemoveAdsClick();
                break;

            case R.id.nav_language:
                onLanguageClick();
                break;

            case R.id.nav_suggestion:
                onSuggestionClick();
                break;

            case R.id.nav_sort_bysource:
                onSortBySourceClick();
                break;

            case R.id.nav_sort_bycategory:
                onSortByCategory();
                break;

            case R.id.nav_notes:
                onNotesClick();
                break;

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onNotesClick() {
        Intent intent = new Intent(EditorialListWithNavActivity.this, NotesActivity.class);
        startActivity(intent);
    }

    private void onPushNotification() {
        String languages[] = new String[]{"On", "Off"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Push Notification ?");
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]

                if (which == 1) {
                    PushNotificationManager.setPushNotification(EditorialListWithNavActivity.this, false);
                } else {
                    PushNotificationManager.setPushNotification(EditorialListWithNavActivity.this, true);
                }

            }
        });
        builder.show();
    }

    private void onSortByCategory() {
        final CharSequence category[] = new CharSequence[]{"Agriculture", "Business", "Economy", "Education", "Finance", "Forign Affair", "Health", "History", "India", "International", "Interview", "Judicial", "Policy", "Politics", "Sci-Tech", "Sports", "Other", "Environment", "Social"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose source");
        builder.setItems(category, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sortCategoryIndex = which;
                sortSourceIndex = -1;
                fetchEditorialCategorySortList();
                setToolBarSubTitle(category[which].toString());

                try {
                    Answers.getInstance().logCustom(new CustomEvent("search Category").putCustomAttribute("Category name", category[which].toString()));
                } catch (Exception e) {

                }

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sortCategoryIndex = -1;
                sortSourceIndex = -1;
                fetchEditorialGeneralList();

            }
        });

        builder.show();
    }

    private void fetchEditorialCategorySortList() {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchCategorySortEditorialList(EditorialListWithNavActivity.listLimit, sortCategoryIndex, new DBHelperFirebase.OnEditorialListListener() {
            @Override
            public void onEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {
                onFetchEditorialGeneralInfo(editorialGeneralInfoArrayList, true);
            }

            @Override
            public void onMoreEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {

            }
        });

        try {
            swipeRefreshLayout.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSortBySourceClick() {
        final CharSequence sources[] = new CharSequence[]{"The Hindu", "Financial Express", "Economic Times", "Indian Express", "TOI", "Hindustan Times", "The Telegraph", "NY Times", "Live Mint", "Business Standard", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose source");
        builder.setItems(sources, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sortSourceIndex = which;
                sortCategoryIndex = -1;
                fetchEditorialSourceSortList();
                setToolBarSubTitle(sources[which].toString());

                try {
                    Answers.getInstance().logCustom(new CustomEvent("search Source").putCustomAttribute("Category name", sources[which].toString()));
                } catch (Exception e) {

                }

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortSourceIndex = -1;
                sortCategoryIndex = -1;
                fetchEditorialGeneralList();
            }
        });

        builder.show();
    }

    private void fetchEditorialSourceSortList() {

        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchSourceSortEditorialList(EditorialListWithNavActivity.listLimit, sortSourceIndex, new DBHelperFirebase.OnEditorialListListener() {
            @Override
            public void onEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {
                onFetchEditorialGeneralInfo(editorialGeneralInfoArrayList, true);
            }

            @Override
            public void onMoreEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {

            }
        });

        try {
            swipeRefreshLayout.setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onLanguageClick() {

        String languages[] = new String[]{"Hindi", "Telugu", "Marathi", "Tamil", "Bengali"};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose source");
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]

                String languageCode = "";
                if (which == 0) {
                    languageCode = "hi";
                } else if (which == 1) {
                    languageCode = "te";
                } else if (which == 2) {
                    languageCode = "mr";
                } else if (which == 3) {
                    languageCode = "ta";
                } else if (which == 4) {
                    languageCode = "bn";
                }

                LanguageManager.setLanguageCode(EditorialListWithNavActivity.this, languageCode);


            }
        });
        builder.show();

    }

    private void onRemoveAdsClick() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Ads for Free");

        builder.setMessage("Remove all the ads from app for free in just one click for 3 days\n" +
                "Press Remove Ads button \n--> Ad will be displayed \n--> Click on the Ad shown \n--> Done. All the ads from app will be removed from app for 3 days")
                .setPositiveButton("Remove Ads", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (mSubscriptionInterstitialAd.isLoaded()) {
                            mSubscriptionInterstitialAd.show();
                        } else if (mSubscriptionInterstitialAd.isLoading()) {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        } else {
                            mSubscriptionInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }


                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });

        // Create the AlertDialog object and return it
        builder.create();
        builder.show();


    }

    public void initializeSubscriptionAds() {
        mSubscriptionInterstitialAd = new InterstitialAd(this);
        mSubscriptionInterstitialAd.setAdUnitId("ca-app-pub-8455191357100024/6262441391");
        mSubscriptionInterstitialAd.loadAd(new AdRequest.Builder().build());

        mSubscriptionInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mSubscriptionInterstitialAd.loadAd(new AdRequest.Builder().build());

                if (AdsSubscriptionManager.checkShowAds(EditorialListWithNavActivity.this)) {
                    Toast.makeText(EditorialListWithNavActivity.this, "You need to click on the ad to get Pro features (with no ads) \n Try again", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(EditorialListWithNavActivity.this, "Thank you for subscribing. \nAll the ads will be removed from next session.", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "Subscription ad").putCustomAttribute("Error code", i));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Toast.makeText(EditorialListWithNavActivity.this, "Now Click on the ads to get pro features", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Toast.makeText(EditorialListWithNavActivity.this, "Ad clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();

                AdsSubscriptionManager.setSubscriptionTime(EditorialListWithNavActivity.this);

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Subscription").putCustomAttribute("user subscribed", "1"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void onNightMode() {
        AppCompatDelegate
                .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        recreate();
    }

    private void onDayMode() {
        AppCompatDelegate
                .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        recreate();
    }

    private void onBookMark() {
        Intent intent = new Intent(this, EditorialListActivity.class);
        startActivity(intent);
    }


    private void onRateUs() {
        try {
            String link = "https://play.google.com/store/apps/details?id=" + this.getPackageName();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (Exception e) {

        }
    }


    private void onVacabularyClick() {
        Intent i = new Intent(this, VacabularyActivity.class);
        startActivity(i);

    }

    private void onShareClick() {


        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String link = "https://play.google.com/store/apps/details?id=" + this.getPackageName();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the app and Start reading");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));


    }


    private void onSuggestionClick() {

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"acraftystudio@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion for Editorial App");
        intent.putExtra(Intent.EXTRA_TEXT, "Your suggestion here \n");

        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Select Email App"));

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
        selectedSortWord = "Economic Times";
        sortEditorList(selectedSortWord);


    }

    private void onfinancialExpClick() {
        selectedSortWord = "Financial Express";
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

    }


    public void initializeInterstitialAds() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadInterstitialAd();

            }


            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                //Toast.makeText(EditorialListWithNavActivity.this, "Ad failed - " + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                //Toast.makeText(EditorialListWithNavActivity.this, "Ad loaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showInterstitialAd() {
        //set editorialcount to 0

        if (AdsSubscriptionManager.checkShowAds(this)) {
            if (EDITORIALCOUNTADS > editorialcountAdMax) {

                if (mInterstitialAd != null) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                        EDITORIALCOUNTADS = 0;

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

}
