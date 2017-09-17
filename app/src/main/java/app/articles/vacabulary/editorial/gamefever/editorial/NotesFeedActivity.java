package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import utils.AuthenticationManager;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.activity_notes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {


            case R.id.notes_action_delete:
                // help action
                onDeleteClicked();
                return true;

            default:
                return super.onOptionsItemSelected(item);


        }
    }

    private void onDeleteClicked() {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this Notes")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteNotes();
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

    private void deleteNotes() {
        final ProgressDialog pd = ProgressDialog.show(this,"Deleting","Please wait");

        DBHelperFirebase dbHelperFirebase =new DBHelperFirebase();
        dbHelperFirebase.deleteShortNotes(AuthenticationManager.getUserUID(this), shortNotesManager.getNoteArticleID(), new DBHelperFirebase.OnShortNoteListListener() {
            @Override
            public void onShortNoteList(ArrayList<ShortNotesManager> shortNotesManagerArrayList, boolean isSuccessful) {

            }

            @Override
            public void onShortNoteUpload(boolean isSuccessful) {

                pd.dismiss();
            }
        });
    }


}
