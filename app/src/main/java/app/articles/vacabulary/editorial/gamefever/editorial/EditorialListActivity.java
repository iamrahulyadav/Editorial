package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;

public class EditorialListActivity extends AppCompatActivity {

    private List<EditorialGeneralInfo> editorialListArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditorialGeneralInfoAdapter mAdapter;

    private ListView mDrawerList;
    private ArrayAdapter<String> mDrawerAdapter;
    View addMoreButton;
    ProgressBar progressBar;
    private boolean isRefreshing = true;
    private  boolean isSplashScreenVisible =true;
    public static boolean isShowingAd =false;
    public static String shareLink ="";
    public static int listLimit =10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeRemoteConfig();

        fetchEditorialGeneralList();

        initializeSplashScreen();


    }

    public void initializeActivity(){

        setContentView(R.layout.activity_editorial_list);
        isSplashScreenVisible=false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.editoriallist_activity_toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


// set the icon
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        recyclerView =(RecyclerView)findViewById(R.id.editoriallist_recyclerview);

        mAdapter = new EditorialGeneralInfoAdapter(editorialListArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //recyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));


        recyclerView.setAdapter(mAdapter);



        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(this, recyclerView ,new RecyclerTouchListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever

                        onRecyclerViewItemClick(position);

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        // mDrawerList = (ListView)findViewById(R.id.editoriallist_activity_drawer_listview);
        // initializeNavDrawer();

        addMoreButton = (View) findViewById(R.id.editoriallist_activity_add_button);
        addMoreButton.setVisibility(View.INVISIBLE);

        progressBar=(ProgressBar)findViewById(R.id.editoriallist_activity_progressbar);
        progressBar.setVisibility(View.VISIBLE);



        if(isShowingAd) {
            initializeAds();
        }



    }


    public void initializeSplashScreen(){

        setContentView(R.layout.splashlayout);

    }




    private void onRecyclerViewItemClick(int position) {
        EditorialGeneralInfo editorialgenralInfo = editorialListArrayList.get(position);
        Intent i = new Intent(this , EditorialFeedActivity.class);
        i.putExtra("editorialID",editorialgenralInfo.getEditorialID());
        i.putExtra("editorialDate",editorialgenralInfo.getEditorialDate());
        i.putExtra("editorialHeading",editorialgenralInfo.getEditorialHeading());
        i.putExtra("editorialSource",editorialgenralInfo.getEditorialSource());
        i.putExtra("editorialSubheading",editorialgenralInfo.getEditorialSubHeading());
        i.putExtra("editorialTag",editorialgenralInfo.getEditorialTag());

        startActivity(i);


    }

    public void fetchEditorialGeneralList(){
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchEditorialList(EditorialListActivity.listLimit ,"" ,this , true);

    }



    private void prepareMovieData() {

        EditorialGeneralInfo editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("This is heading text for demo purpose and testing layout");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);


        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("this is heading text for demo purpose and testing layout");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("economics time");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("this is heading text for demo purpose and testing layout");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("this is heading text for demo purpose and testing layout");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("this is heading text for demo purpose and testing layout");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);


        mAdapter.notifyDataSetChanged();
    }

    public void loadMoreClick(View view) {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchEditorialList(EditorialListActivity.listLimit ,editorialListArrayList.get(editorialListArrayList.size()-1).getEditorialID() ,this ,false);

        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

    }

    public void onFetchEditorialGeneralInfo(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist) {

        if(isSplashScreenVisible) {
            initializeActivity();
        }

        int insertPosition = editorialListArrayList.size();
        editorialGeneralInfoArraylist.remove(editorialGeneralInfoArraylist.size()-1);

        for (EditorialGeneralInfo editorialGeneralInfo : editorialGeneralInfoArraylist){
            editorialListArrayList.add(insertPosition ,editorialGeneralInfo);
        }
        mAdapter.notifyDataSetChanged();



        addMoreButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        isRefreshing =false;

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
        Intent i = new Intent(this ,VacabularyActivity.class);
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

        Intent i = new Intent(this ,AboutActivity.class);
        startActivity(i);

    }

    private void onRefreashClick() {

        editorialListArrayList.clear();
        mAdapter.notifyDataSetChanged();
        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if(!isRefreshing) {
            fetchEditorialGeneralList();
            isRefreshing =true;
        }

    }


    public void initializeAds(){
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8455191357100024~6634740792");
        AdView mAdView = (AdView) findViewById(R.id.editorialList_activity_adView);
        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

    }


    public void initializeRemoteConfig(){
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


         EditorialListActivity.shareLink = mFirebaseRemoteConfig.getString("shareLink");

        try{
            EditorialListActivity.isShowingAd =Boolean.valueOf( mFirebaseRemoteConfig.getString("isShowingAd"));

        }catch(Exception e){
            e.printStackTrace();
            EditorialListActivity.isShowingAd=false;
        }

        try{
            EditorialListActivity.listLimit =Integer.valueOf( mFirebaseRemoteConfig.getString("listLimit"));

        }catch(Exception e){
            e.printStackTrace();
            EditorialListActivity.listLimit=20;
        }

    }


}
