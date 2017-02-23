package app.articles.vacabulary.editorial.gamefever.editorial;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        ListView listView =(ListView)findViewById(R.id.vacabulary_activity_listview);
        mAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayListword);

        listView.setAdapter(mAdapter);
        fetchDictionaryword();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                openBottomSheet(dictionaryArrayList.get(position));
            }
        });

        View bottomSheet = findViewById( R.id.vacabulary_activity_bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


    }

    private void fetchDictionaryword() {
    DatabaseHandler databasehandler = new DatabaseHandler(this);
         dictionaryArrayList= databasehandler.getAllDictionaryWord();
        for(Dictionary dictionary : dictionaryArrayList){
            arrayListword.add(dictionary.getWord());
        }

        mAdapter.notifyDataSetChanged();

    }

    public void openBottomSheet(Dictionary dictionary){
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        TextView tv ;
        tv=(TextView)findViewById(R.id.vacabulary_bottomsheet_heading_textview);
        tv.setText(dictionary.getWord());
        tv=(TextView)findViewById(R.id.vacabulary_bottomsheet_meaning_textview);
        tv.setText(dictionary.getWordMeaning());
        tv=(TextView)findViewById(R.id.vacabulary_bottomsheet_partspeech_textview);
        tv.setText(dictionary.getWordPartOfSpeech());
        tv=(TextView)findViewById(R.id.vacabulary_bottomsheet_synonyms_textview);
        String synonymstring="";
        for(int i =0 ; i<dictionary.getWordsynonym().length ;i++) {
            synonymstring=synonymstring+dictionary.getWordsynonym()[i] +" , ";
        }
        tv.setText(synonymstring);


    }

}
