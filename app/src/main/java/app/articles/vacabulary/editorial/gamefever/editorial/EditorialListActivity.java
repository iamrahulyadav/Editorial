package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.ActionBar;
import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
        //actionBar.setIcon(R.drawable.common_google_signin_btn_icon_dark);

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
                        Toast.makeText(EditorialListActivity.this, "Item clicked "+position+editorialListArrayList.get(position).getEditorialHeading(), Toast.LENGTH_SHORT).show();

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
        dbHelperFirebase.fetchEditorialList(20 ,"" ,this , true);

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
        dbHelperFirebase.fetchEditorialList(20 ,editorialListArrayList.get(editorialListArrayList.size()-1).getEditorialID() ,this ,false);

        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

    }

    public void onFetchEditorialGeneralInfo(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist) {

        if(isSplashScreenVisible) {
            initializeActivity();
        }

        int insertPosition = editorialListArrayList.size();

        for (EditorialGeneralInfo editorialGeneralInfo : editorialGeneralInfoArraylist){
            editorialListArrayList.add(insertPosition ,editorialGeneralInfo);
        }
        mAdapter.notifyDataSetChanged();



        addMoreButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
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
    }

    private void onSettingClick() {
    }

    private void onAboutClick() {
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


}
