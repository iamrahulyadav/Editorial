package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import utils.ShortNotesManager;


public class NotesFeedActivity extends AppCompatActivity {
    ShortNotesManager shortNotesManager;
    String shortNotesText ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeActivity();

    }

    private void initializeActivity() {
        Intent intent =getIntent();

         shortNotesManager =(ShortNotesManager) intent.getSerializableExtra("shortNotes");
        shortNotesText =intent.getStringExtra("shortNotesText");
        if (shortNotesManager!= null){

            initializeView();

        }else{

        }

    }

    private void initializeView() {
        TextView textView = (TextView)findViewById(R.id.notesFeed_heading_textview);
        textView.setText(shortNotesManager.getShortNoteHeading());

        textView =(TextView)findViewById(R.id.notesFeed_date_textview);
        textView.setText(shortNotesManager.getNoteArticleDate());

        textView =(TextView)findViewById(R.id.notesFeed_source_textview);
        textView.setText(shortNotesManager.getNoteArticleSource());

        textView=(TextView)findViewById(R.id.notesFeed_text_textview);


        String notesString ="";
        for (String tempstr :shortNotesManager.getShortNotePointList().values()){
            tempstr ="• "+tempstr+"\n\n" ;
            notesString=notesString.concat(tempstr);
        }

        textView.setText(notesString);




    }


}
