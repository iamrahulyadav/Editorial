package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;

import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;

import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseState;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import com.crashlytics.android.answers.PurchaseEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.AdListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import io.fabric.sdk.android.Fabric;
import utils.AdsSubscriptionManager;
import utils.ClickListener;
import utils.DatabaseHandlerRead;
import utils.LanguageManager;
import utils.NightModeManager;
import utils.PushNotificationManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;


public class EditorialListWithNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static int sortedListLimit = 0;
    public static ArrayList<Object> editorialListArrayList = new ArrayList<>();
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
    com.facebook.ads.InterstitialAd facebookInterstitial;


    private int editorialcountAdMax = 2;
    boolean isActivityInitialized = false;
    //private InterstitialAd mSubscriptionInterstitialAd;

    //sort variable
    int sortSourceIndex = -1;
    int sortCategoryIndex = -1;
    long sortDateMillis = 1507660200000l;
    boolean sortByDate = false;

    private RewardedVideoAd mAd;
    Spinner spinner;

    BillingProcessor bp;
    final String SUBSCRIPTION_ID = "ad_free_subscription";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if (NightModeManager.getNightMode(getApplicationContext())) {
            setTheme(R.style.FeedActivityThemeDark);
            isNightMode = true;
        }

        initializeRemoteConfig();

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8455191357100024~6634740792");


        if (!isNetworkAvailable()) {

            //Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorialListWithNavActivity.this);


            // Setting Dialog Title
            alertDialog.setTitle("No Internet connection");

            // Setting Dialog Message
            alertDialog.setMessage("App is running in offline mode");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.ic_action_information);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    onBookMark();

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

        initializeInAppBilling();


        if (PushNotificationManager.getPushNotification(this)) {
            FirebaseMessaging.getInstance().subscribeToTopic("subscribed");
        }

        initializeSubscriptionAds();

    }

    private void initializeInAppBilling() {

        try {
            bp = new BillingProcessor(this,
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4lE0nDHaciyEpkXJ2dceN9EPLiuP7hSSOIjzzOF40am/QD4Hwd4CxZg+tco/f7G6GIFW1aJgRiHOCm+crhWUJk854MmWNs3JC1hxe15vH7h0C9s4d6Iw7fTJn4GN5a5tPrQESLd/OFPixFXS7gwePWUCnYl85Uge8tqwPtf2rcotqs3bScxYQQMmCb1fNxXOgB/kULJr9hy9FIzxYdKnSrUMib3rKQTEPKFqyLZgYGOfUwvvclJ7baouZfWemW0nwWKvIxMCsBGdEBI0aCb0on+J8A+pN3f+in5HM8F3eBAHF/MTVkOVoS1EGvIJgjj5exlZJePN+NJI3WtKVFiaPQIDAQAB",
                    new BillingProcessor.IBillingHandler() {
                        @Override
                        public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                            //Toast.makeText(EditorialListWithNavActivity.this, "product purchased - " + productId, Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditorialListWithNavActivity.this);
                            builder.setTitle("Thank you for Subscription");
                            builder.setMessage("We appreciate your contribution by going ads free.\n\nAds will be removed when you open the app next time.");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();

                            AdsSubscriptionManager.setSubscription(EditorialListWithNavActivity.this, true);

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
                            bp.loadOwnedPurchasesFromGoogle();

                            try {
                                TransactionDetails transactionDetails = bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID);

                                if (transactionDetails == null) {
                                    AdsSubscriptionManager.setSubscription(EditorialListWithNavActivity.this, false);
                                    return;
                                }

                                if (transactionDetails.purchaseInfo.purchaseData.purchaseState == PurchaseState.PurchasedSuccessfully) {
                                    AdsSubscriptionManager.setSubscription(EditorialListWithNavActivity.this, true);
                                    Answers.getInstance().logCustom(new CustomEvent("Subscribed user enter"));
                                } else {
                                    AdsSubscriptionManager.setSubscription(EditorialListWithNavActivity.this, false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void getSortCondition() {

        try {
            Intent intent = getIntent();

            sortSourceIndex = intent.getIntExtra("sourceIndex", -1);
            sortCategoryIndex = intent.getIntExtra("categoryIndex", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openDynamicLink() {

        fetchEditorialGeneralList();

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
                            //fetchEditorialGeneralList();
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
                        Log.d("editorial", "getInvitation: no deep link found.");
                        //fetchEditorialGeneralList();
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


        //FirebaseDatabase.getInstance().setLogLevel(Logger.Level.valueOf("DEBUG"));

    }

    @Override
    protected void onResume() {
        if (mAd != null) {
            mAd.resume(this);
        }
        super.onResume();

        if (isSplashScreenVisible && !isRefreshing) {
            fetchEditorialGeneralList();
        }
        if (NightModeManager.getNightMode(getApplicationContext())) {

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

    @Override
    public void onPause() {
        if (mAd != null) {
            mAd.pause(this);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mAd != null) {
            mAd.destroy(this);
        }

        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
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
            //toolbar.setTitle(getString(R.string.app_name));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        try {
            if (AdsSubscriptionManager.getSubscription(this)) {
                View header = navigationView.getHeaderView(0);
                TextView statusTextView = (TextView) header.findViewById(R.id.nav_header_status);
                statusTextView.setText("Subscribed (Ads Free) @ RS. 29/Month");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        navigationView.setNavigationItemSelectedListener(this);


// the content of EditorialListActivity from here
        isSplashScreenVisible = false;


        recyclerView = (RecyclerView) findViewById(R.id.editoriallist_recyclerview);


        mAdapter = new EditorialGeneralInfoAdapter(editorialListArrayList, "day", this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.setAdapter(mAdapter);


        mAdapter.setOnclickListener(new ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > 0) {
                    onRecyclerViewItemClick(position);
                }
            }
        });


        addMoreButton = findViewById(R.id.editoriallist_activity_add_button);
        addMoreButton.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) findViewById(R.id.editoriallist_activity_progressbar);
        progressBar.setVisibility(View.VISIBLE);


        if (AdsSubscriptionManager.checkShowAds(this)) {

            //initializeAds();
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-8455191357100024/2541985598");

            facebookInterstitial = new com.facebook.ads.InterstitialAd(this, "113079036048193_121778881844875");
            initializeInterstitialAds(true);
        }


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.editoriallist_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //sortCategoryIndex = -1;
                //sortSourceIndex = -1;
                fetchEditorialGeneralList();
                swipeRefreshLayout.setRefreshing(true);
            }
        });


        spinner = (Spinner) findViewById(R.id.editoriallist_source_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Spinner", "onItemSelected: " + position);

                onSortBySourceSelected(position);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner", "onItemSelected: ");
            }
        });


    }

    private void onSortBySourceSelected(int position) {
        if (position == 0 && sortSourceIndex == -1) {
            Log.d("Spinner", "no item selected call for setting listener");
        } else {


            if (position == 0) {
                sortSourceIndex = -1;
                sortCategoryIndex = -1;
                sortByDate = false;

                fetchEditorialGeneralList();
            } else {


                if (position == 7) {
                    sortSourceIndex = 8;
                } else if (position == 8) {
                    sortSourceIndex = 9;
                } else if (position == 9) {
                    sortSourceIndex = 10;
                } else {
                    sortSourceIndex = position - 1;
                }

                sortCategoryIndex = -1;
                sortByDate = false;

                //fetchEditorialSourceSortList();
                fetchEditorialGeneralList();
            }


        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }


    public void initializeSplashScreen() {

        setContentView(R.layout.splashlayout);

    }


    private void onRecyclerViewItemClick(int position) {

        if (position < 0) {
            recreate();
            return;
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

        try {
            editorialgenralInfo.setReadStatus(true);
            mAdapter.notifyDataSetChanged();

            new DatabaseHandlerRead(this).addReadNews(editorialgenralInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        } else if (sortByDate) {
            dbHelperFirebase.fetchDateSortEditorialList(sortDateMillis, (sortDateMillis + 86400000), onEditorialListListener);
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

                } else if (sortByDate) {
                    Toast.makeText(this, "No more Editorial available", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 1)).getEditorialID(), onEditorialListListener);

                }
            } else {
                if (sortSourceIndex > -1) {
                    dbHelperFirebase.fetchSourceSortEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 2)).getEditorialID(), sortSourceIndex, onEditorialListListener);

                } else if (sortCategoryIndex > -1) {
                    dbHelperFirebase.fetchCategorySortEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 2)).getEditorialID(), sortCategoryIndex, onEditorialListListener);

                } else if (sortByDate) {
                    Toast.makeText(this, "No more Editorial available", Toast.LENGTH_SHORT).show();

                } else {
                    dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialListArrayList.get(editorialListArrayList.size() - 2)).getEditorialID(), onEditorialListListener);

                }
            }
        }
        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        //sortEditorList(selectedSortWord);

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
            editorialGeneralInfo.rectifySubHeading();
        }

        addReadStatus();

        if (isFirst) {
            prioritizeArrayList(editorialListArrayList);
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

    private void prioritizeArrayList(ArrayList<Object> editorialGeneralInfoArraylist) {

        int replaceNumber = 0;
        for (int i = 0; i < editorialGeneralInfoArraylist.size() - 1; i++) {

            EditorialGeneralInfo editorialGeneralInfo = (EditorialGeneralInfo) editorialGeneralInfoArraylist.get(i);

            if (editorialGeneralInfo.isMustRead() && !editorialGeneralInfo.isReadStatus()) {
                editorialGeneralInfoArraylist.remove(i);
                editorialGeneralInfoArraylist.add(replaceNumber, editorialGeneralInfo);
                replaceNumber++;
            }

        }

    }

    private void addReadStatus() {
        try {
            DatabaseHandlerRead databaseHandlerRead = new DatabaseHandlerRead(this);
            EditorialGeneralInfo editorialGeneralInfo;
            for (Object editorialGeneralInfoObject : editorialListArrayList) {
                if (editorialGeneralInfoObject.getClass() == EditorialGeneralInfo.class) {
                    editorialGeneralInfo = (EditorialGeneralInfo) editorialGeneralInfoObject;
                    editorialGeneralInfo.setReadStatus(databaseHandlerRead.getNewsReadStatus(editorialGeneralInfo.getEditorialID()));
                }
            }

            Log.d("DEBUG", "addReadStatus: " + editorialListArrayList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNativeExpressAds() {

        boolean checkShowAds = AdsSubscriptionManager.checkShowAds(this);


        for (int i = 0; i < (editorialListArrayList.size()); i += 8) {
            if (editorialListArrayList.get(i) != null) {
                if (editorialListArrayList.get(i).getClass() != NativeAd.class) {


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
                            mAdapter.notifyDataSetChanged();
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

                    editorialListArrayList.add(i, nativeAd);

                }
            }
        }


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
    /*    final AdView mAdView = (AdView) findViewById(R.id.editorialList_activity_adView);

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
*/
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

                if (sortCategoryIndex > -1 || sortSourceIndex > -1 || sortByDate) {
                    sortCategoryIndex = -1;
                    sortSourceIndex = -1;
                    sortByDate = false;

                    spinner.setSelection(0);
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
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_editorial_list_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {


            /*case R.id.action_refresh:
                // help action
                onSortByDate();
                return true;*/

            default:
                return super.onOptionsItemSelected(item);


        }
    }

    private void onSortByDate() {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {


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

            case R.id.nav_sort_byDate:
                onSortByDateClick();
                break;

            case R.id.nav_notes:
                onNotesClick();
                break;

            case R.id.nav_pushNotification:
                onPushNotification();
                break;

            case R.id.nav_sign_in:
                onSignInOpenClick();
                break;

            case R.id.nav_pib_app:
                onPIBAppClick();
                //onSortByDateClick();
                break;

            case R.id.nav_aptitude_app:
                onInstallAptitudeClick();
                break;

            case R.id.nav_purchase:
                onPurchaseClick();
                break;
            case R.id.nav_computer:
                onComputerClick();
                break;


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onComputerClick() {
        try {
            String link = "https://play.google.com/store/apps/details?id=app.computer.basic.quiz.craftystudio.computerbasic";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPurchaseClick() {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Go Ads Free");
            builder.setMessage("We are not a money making app. Ads are integrated to help app development and maintenance of apps.\n\nPlease make a small contribution and go ads free");
            builder.setPositiveButton("Go Ads Free", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (bp != null) {
                        bp.subscribe(EditorialListWithNavActivity.this, SUBSCRIPTION_ID);

                        Answers.getInstance().logCustom(new CustomEvent("Subscription Flow").putCustomAttribute("Selection", "yes"));

                    }
                }
            });
            builder.setNegativeButton("Maybe Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Answers.getInstance().logCustom(new CustomEvent("Subscription Flow").putCustomAttribute("Selection", "No"));

                }
            });

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onInstallAptitudeClick() {
        try {
            String link = "https://play.google.com/store/apps/details?id=app.aptitude.quiz.craftystudio.aptitudequiz";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onPIBAppClick() {
        try {
            String link = "https://play.google.com/store/apps/details?id=app.crafty.studio.current.affairs.pib";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (Exception e) {

        }
    }

    private void onSignInOpenClick() {
        Intent intent = new Intent(EditorialListWithNavActivity.this, SignInActivity.class);
        startActivity(intent);
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
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("subscribed");

                } else {
                    PushNotificationManager.setPushNotification(EditorialListWithNavActivity.this, true);
                    FirebaseMessaging.getInstance().subscribeToTopic("subscribed");
                }

            }
        });


        if (!PushNotificationManager.getPushNotification(this)) {
            Log.d("Test", "onPushNotification: ");
        }

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
                sortByDate = false;

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
                sortByDate = false;
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
        //final CharSequence sources[] = new CharSequence[]{"All", "The Hindu", "Financial Express", "Economic Times", "Indian Express", "TOI", "Hindustan Times", "The Telegraph", "NY Times", "Live Mint", "Business Standard", "Other"};
        final CharSequence sources[] = new CharSequence[]{"All", "The Hindu", "Financial Express", "Economic Times", "Indian Express", "TOI", "Hindustan Times", "Live Mint", "The Tribune", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose source");
        builder.setItems(sources, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               /* if (which == 0){
                    sortSourceIndex = -1;
                    sortCategoryIndex = -1;
                    fetchEditorialGeneralList();
                }else{
                    sortSourceIndex = which-1;
                    sortCategoryIndex = -1;
                    fetchEditorialSourceSortList();
                }
*/
                onSortBySourceSelected(which);

                spinner.setSelection(which, true);

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
                sortByDate = false;

                fetchEditorialGeneralList();


            }
        });

        builder.show();
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

                    sortByDate = true;
                    sortDateMillis = date.getTime();

                    fetchEditorialGeneralList();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMinDate(1501525800000l);
        datePickerDialog.show();

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

        String languages[] = new String[]{"Hindi", "Telugu", "Marathi", "Tamil", "Bengali", "Kannada", "Urdu", "Malayalam", "Gujarati", "Punjabi"};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language");
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
                } else if (which == 5) {
                    languageCode = "kn";
                } else if (which == 6) {
                    languageCode = "ur";
                } else if (which == 7) {
                    languageCode = "ml";
                } else if (which == 8) {
                    languageCode = "gu";
                } else if (which == 9) {
                    languageCode = "pa";
                } else {
                    languageCode = "hi";
                }

                LanguageManager.setLanguageCode(EditorialListWithNavActivity.this, languageCode);


            }
        });
        builder.show();

    }

    private void onRemoveAdsClick() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Ads for Free");

        builder.setMessage("Remove all the ads from app for free in just one click for 2 days\n" +
                "Press Remove Ads button \n--> Video Ad will be Shown \n--> Watch the full video ad \n--> Done. All the ads from app will be removed from app for 2 days")
                .setPositiveButton("Remove Ads", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        showSubscriptionAd();
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

    private void showSubscriptionAd() {
     /*   if (mSubscriptionInterstitialAd.isLoaded()) {
            mSubscriptionInterstitialAd.show();
        } else if (mSubscriptionInterstitialAd.isLoading()) {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        } else {
            mSubscriptionInterstitialAd.loadAd(new AdRequest.Builder().build());
        }

*/

        if (mAd.isLoaded()) {
            mAd.show();
        } else {
            Toast.makeText(this, "Ad not loaded yet! Try again later", Toast.LENGTH_SHORT).show();
            initializeSubscriptionAds();
        }

    }

    public void initializeSubscriptionAds() {

        mAd = MobileAds.getRewardedVideoAdInstance(this);
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

                if (AdsSubscriptionManager.checkShowAds(EditorialListWithNavActivity.this)) {

                } else {
                    Toast.makeText(EditorialListWithNavActivity.this, "Thank you for subscribing. \nAll the ads will be removed from next session for 2 days", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

                AdsSubscriptionManager.setSubscriptionTime(EditorialListWithNavActivity.this, rewardItem.getAmount());

                //Toast.makeText(EditorialListWithNavActivity.this, "Thank you for subscribing. \nAll the ads will be removed from next session for "+rewardItem.getAmount()+" days", Toast.LENGTH_LONG).show();

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Subscribed").putCustomAttribute("user subscribed from nav drawer", "nav drawer"));
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

    private void onNightMode() {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //NightModeManager.setNightMode(EditorialListWithNavActivity.this,true);
        NightModeManager.setNightMode(this, true);
        recreate();
    }

    private void onDayMode() {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //NightModeManager.setNightMode(EditorialListWithNavActivity.this,false);
        NightModeManager.setNightMode(this, false);
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
                // Toast.makeText(EditorialListWithNavActivity.this, "Ad failed - " + i, Toast.LENGTH_SHORT).show();
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

    public void initializeInterstitialAds(boolean isFacebook) {
        facebookInterstitial.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }


            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                facebookInterstitial.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.d("TAG", "onError: ");
                initializeInterstitialAds();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
                //interstitialAd.show();
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

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown

        try {
            facebookInterstitial.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showInterstitialAd() {
        //set editorialcount to 0


        if (AdsSubscriptionManager.checkShowAds(this)) {
            if (EDITORIALCOUNTADS >= editorialcountAdMax) {

                if (facebookInterstitial != null) {
                    if (facebookInterstitial.isAdLoaded()) {

                        facebookInterstitial.show();
                        EDITORIALCOUNTADS = 0;
                    } else if (mInterstitialAd != null) {

                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            EDITORIALCOUNTADS = 0;

                        } else {
                            loadInterstitialAd();
                        }

                    }
                }

            } else {
                EDITORIALCOUNTADS++;
            }
        }

    }


}
