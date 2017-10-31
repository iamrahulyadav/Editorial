package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;

import utils.AdsSubscriptionManager;
import utils.AuthenticationManager;
import utils.NightModeManager;
import utils.ShortNotesAdapter;
import utils.ShortNotesManager;


public class NotesActivity extends AppCompatActivity {
    private ArrayList<Object> shortNotesArrayList = new ArrayList<>();

    private RecyclerView recyclerView;
    private ShortNotesAdapter mAdapter;
    private boolean isMoreNotesAvailable=true;
    private boolean isLodingMoreNotes=true;

    private DBHelperFirebase.OnShortNoteListListener onShortNoteListListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NightModeManager.getNightMode(this)){
            setTheme(R.style.FeedActivityThemeDark);
        }

        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        recyclerView = (RecyclerView) findViewById(R.id.notes_list_recyclerView);
        mAdapter = new ShortNotesAdapter(shortNotesArrayList, this);
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
                        onRecyclerViewItemLongClick(position);
                    }
                })
        );


        //loading more
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                                              @Override
                                              public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                  super.onScrolled(recyclerView, dx, dy);
                                                  if (!recyclerView.canScrollVertically(1)) {

                                                      if (isMoreNotesAvailable) {
                                                          if (!isLodingMoreNotes) {

                                                              loadMoreNotes();
                                                          } else {

                                                          }
                                                      }
                                                  }
                                              }
                                          }

        );


        onShortNoteListListener= new DBHelperFirebase.OnShortNoteListListener() {
            @Override
            public void onShortNoteList(ArrayList<ShortNotesManager> shortNotesManagerArrayList, boolean isSuccessful) {

                isLodingMoreNotes=false;
                if (isSuccessful) {
                    //initialize list

                    if (shortNotesManagerArrayList.size() >19){
                        isMoreNotesAvailable =true;
                    }else{
                        isMoreNotesAvailable=false;
                    }


                    for (Object shortNotes :shortNotesManagerArrayList){
                        shortNotesArrayList.add(shortNotes);
                    }


                    addNativeExpressAds();
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onShortNoteUpload(boolean isSuccessful) {

            }
        };


        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchShortNotesList(AuthenticationManager.getUserUID(this), 20, onShortNoteListListener);


        try {
            Answers.getInstance().logCustom(new CustomEvent("Notes activity open").putCustomAttribute("User id", AuthenticationManager.getUserEmail(this)));
        }catch (Exception e){
            e.printStackTrace();
        }




    }

    private void loadMoreNotes() {
        isLodingMoreNotes =true;


        if (shortNotesArrayList.get(shortNotesArrayList.size()-1).getClass()==ShortNotesManager.class){

            new DBHelperFirebase().fetchShortNotesList(AuthenticationManager.getUserUID(this),20
            ,((ShortNotesManager)shortNotesArrayList.get(shortNotesArrayList.size()-1)).getNoteArticleID() ,
                    onShortNoteListListener);

        }else{

            new DBHelperFirebase().fetchShortNotesList(AuthenticationManager.getUserUID(this),20
                    ,((ShortNotesManager)shortNotesArrayList.get(shortNotesArrayList.size()-2)).getNoteArticleID() ,
                    onShortNoteListListener);
        }


    }

    private void onRecyclerViewItemLongClick(final int position) {
        if (position % 8 == 0) {
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this Notes")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteNotes(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();

    }

    private void deleteNotes(final int position) {
        final ProgressDialog pd = ProgressDialog.show(this,"Deleting","Please wait");

        DBHelperFirebase dbHelperFirebase =new DBHelperFirebase();
        dbHelperFirebase.deleteShortNotes(AuthenticationManager.getUserUID(this),((ShortNotesManager)shortNotesArrayList.get(position)).getNoteArticleID(), new DBHelperFirebase.OnShortNoteListListener() {
            @Override
            public void onShortNoteList(ArrayList<ShortNotesManager> shortNotesManagerArrayList, boolean isSuccessful) {

            }

            @Override
            public void onShortNoteUpload(boolean isSuccessful) {
                shortNotesArrayList.remove(position);
                mAdapter.notifyDataSetChanged();
                pd.dismiss();

                Toast.makeText(NotesActivity.this, "Notes Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_notes_tutorial_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {


            case R.id.notes_action_tutorial:
                onTutorialClick();
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }

    private void onTutorialClick() {
        Answers.getInstance().logCustom(new CustomEvent("On tutorial open").putCustomAttribute("User id", AuthenticationManager.getUserEmail(this)));

        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("https://dailyeditorial.quora.com/How-to-make-Editorials-Notes-in-Daily-Editorial"));
        startActivity(intent);
    }

    private void onRecyclerViewItemClick(int position) {
        if (position % 8 == 0) {
            return;
        }



        Intent intent =new Intent(NotesActivity.this ,NotesFeedActivity.class);
        intent.putExtra("shortNotes",((ShortNotesManager)shortNotesArrayList.get(position)));


        startActivity(intent);

    }

    private void initializeRecyclerView() {

    }

    private void addNativeExpressAds() {

        boolean checkShowAds = AdsSubscriptionManager.checkShowAds(this);

        //main function where ads is merged in editorial list as an object

        for (int i = 0; i < (shortNotesArrayList.size()); i += 8) {
            if (shortNotesArrayList.get(i).getClass() != NativeExpressAdView.class) {
                final NativeExpressAdView adView = new NativeExpressAdView(this);

                adView.setAdUnitId("ca-app-pub-8455191357100024/8254824112");
                adView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

                adView.setAdSize(new AdSize(320, 132));
                if (checkShowAds) {
                    adView.loadAd(new AdRequest.Builder().build());
                }
                shortNotesArrayList.add(i, adView);

            }
        }


    }
/*

    private void addNativeExpressAds() {

        boolean checkShowAds = AdsSubscriptionManager.checkShowAds(this);


        for (int i = 0; i < (shortNotesArrayList.size()); i += 8) {
            if (shortNotesArrayList.get(i) != null) {
                if (shortNotesArrayList.get(i).getClass() == ShortNotesManager.class) {


                    NativeAd nativeAd = new NativeAd(this, "113079036048193_119873465368750");
                    nativeAd.setAdListener(new com.facebook.ads.AdListener() {

                        @Override
                        public void onError(Ad ad, AdError error) {
                            // Ad error callback
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

                    shortNotesArrayList.add(i, nativeAd);

                }
            }
        }


    }
*/




}
