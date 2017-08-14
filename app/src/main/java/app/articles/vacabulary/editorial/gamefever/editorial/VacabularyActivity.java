package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class VacabularyActivity extends AppCompatActivity {

    ArrayList<Dictionary> dictionaryArrayList;
    ArrayList<String> arrayListword = new ArrayList<String>();
    ArrayAdapter<String> mAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacabulary);

        Toolbar toolbar = (Toolbar) findViewById(R.id.vacabulary_activity_toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("vocabulary");
            //getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListView listView = (ListView) findViewById(R.id.vacabulary_activity_listview);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListword);

        listView.setAdapter(mAdapter);
        fetchDictionaryword();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                openBottomSheet(dictionaryArrayList.get(position));
            }
        });

        View bottomSheet = findViewById(R.id.vacabulary_activity_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        setActivityTheme();

    }

    private void setActivityTheme() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("theme_list", "Day");

    }

    private void fetchDictionaryword() {
        DatabaseHandler databasehandler = new DatabaseHandler(this);
        dictionaryArrayList = databasehandler.getAllDictionaryWord();
        for (Dictionary dictionary : dictionaryArrayList) {
            arrayListword.add(dictionary.getWord());
        }

        mAdapter.notifyDataSetChanged();

    }

    public void openBottomSheet(Dictionary dictionary) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        TextView tv;
        tv = (TextView) findViewById(R.id.vacabulary_bottomsheet_heading_textview);
        tv.setText(dictionary.getWord());
        tv = (TextView) findViewById(R.id.vacabulary_bottomsheet_meaning_textview);
        tv.setText(dictionary.getWordMeaning());
        tv = (TextView) findViewById(R.id.vacabulary_bottomsheet_partspeech_textview);
        tv.setText(dictionary.getWordPartOfSpeech());
        tv = (TextView) findViewById(R.id.vacabulary_bottomsheet_synonyms_textview);
        String synonymstring = "";
        for (int i = 0; i < dictionary.getWordsynonym().length; i++) {
            synonymstring = synonymstring + dictionary.getWordsynonym()[i] + " , ";
        }
        tv.setText(synonymstring);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.activity_editorial_list_actions, menu);

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
        Intent i = new Intent(this, VacabularyActivity.class);
        startActivity(i);

    }

    private void onShareClick() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the app and Start reading");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, EditorialListWithNavActivity.shareLink);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void onSettingClick() {
    }

    private void onAboutClick() {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    private void onRefreashClick() {


    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
