package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;

public class EditorialListActivity extends AppCompatActivity {

    private List<EditorialGeneralInfo> editorialListArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditorialGeneralInfoAdapter mAdapter;

   // private ListView mDrawerList;
   // private ArrayAdapter<String> mDrawerAdapter;
    View addMoreButton;
    ProgressBar progressBar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeActivity();


        fetchEditorialGeneralList();





    }


    public void initializeActivity(){

        setContentView(R.layout.activity_editorial_list);



        Toolbar toolbar = (Toolbar) findViewById(R.id.editoriallist_activity_toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


// set the icon
        try {
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
            getSupportActionBar().setSubtitle("BookMarks");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception e){
            e.printStackTrace();
        }

        recyclerView =(RecyclerView)findViewById(R.id.editoriallist_recyclerview);

        mAdapter = new EditorialGeneralInfoAdapter(editorialListArrayList ,getActivityTheme() ,this);
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







    }


    public void initializeSplashScreen(){

        setContentView(R.layout.splashlayout);

    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String getActivityTheme() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("theme_list", "Day");
        return theme;
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
        i.putExtra("isBookMarked",true);

        startActivity(i);


    }

    public void fetchEditorialGeneralList(){
        DatabaseHandlerBookMark databaseHandlerBookMark = new DatabaseHandlerBookMark(this);
        editorialListArrayList= databaseHandlerBookMark.getAllBookMarkEditorial();



        mAdapter = new EditorialGeneralInfoAdapter(editorialListArrayList,getActivityTheme() ,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //recyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));

        progressBar.setVisibility(View.GONE);

        recyclerView.setAdapter(mAdapter);

    }




/*

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
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.activity_editorial_list_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {


            case R.id.action_share:
                // help action
                onShareClick();
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
        //sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, EditorialListWithNavActivity.shareLink);
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
       /* if(!isRefreshing) {
            fetchEditorialGeneralList();
            isRefreshing =true;
        }*/

    }


    public void initializeAds(){
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8455191357100024~6634740792");
        AdView mAdView = (AdView) findViewById(R.id.editorialList_activity_adView);
        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

    }





        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }



}
