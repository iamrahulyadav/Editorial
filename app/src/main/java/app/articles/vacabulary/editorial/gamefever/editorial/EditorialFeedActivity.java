package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.Locale;

public class EditorialFeedActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener{

    String editorialText = "this is a demo9 editorialo text nothing moch jus to show how this will look in this activty and other things \n some more text is shown for testing purpose and other thins \n please dont read it and waste yourtime instead go n watch some cool movies or go for a walk ,chill out with buddy \n this will go on and on and on for ever\n this is a demo9 editorialo text nothing moch jus to show how this will look in this activty and other things \n" +
            " some more text is shown for testing purpose and other thins \n" +
            " please dont read it and waste yourtime instead go n watch some cool movies or go for a walk ,chill out with buddy \n" +
            " this will go on and on and on for ever";

    private TextToSpeech tts;

    TextView translateText ;
    private BottomSheetBehavior mBottomSheetBehavior;
    public String selectedWord = "null" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editorial_feed);

        tts = new TextToSpeech(this, this);

        translateText =(TextView)findViewById(R.id.editorial_feed_cardview_textview);

        init(editorialText);

        View bottomSheet = findViewById( R.id.editorial_activity_bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    private void init(String textToShow) {
        String definition = textToShow;
        TextView definitionView = (TextView) findViewById(R.id.editorial_text_textview);
        definitionView.setMovementMethod(LinkMovementMethod.getInstance());
        definitionView.setText(definition, TextView.BufferType.SPANNABLE);
        Spannable spans = (Spannable) definitionView.getText();
        BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
        iterator.setText(definition);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                .next()) {
            String possibleWord = definition.substring(start, end);
            if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
                ClickableSpan clickSpan = getClickableSpan(possibleWord);
                spans.setSpan(clickSpan, start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public void fetchEditorial(String id){
      //call to fetch editorial object from firebase db
    }

    public void updateEditorialFetch(String editorial){

        TextView tv ;
        tv=(TextView)findViewById(R.id.editorial_heading_textview);
//initialize heading
        tv =(TextView)findViewById(R.id.editorial_date_textview);
//initialize date
        tv =(TextView)findViewById(R.id.editorial_source_textview);
//initialize source
        tv =(TextView)findViewById(R.id.editorial_text_textview);
//initialize text
        init(editorialText);



    }

    private ClickableSpan getClickableSpan(final String word) {
        return new ClickableSpan() {
            final String mWord;

            {
                mWord = word;
            }

            @Override
            public void onClick(View widget) {
                Log.d("tapped on:", mWord);

                onWordTap(mWord);
            }

            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(ds.linkColor);
                //super.updateDrawState(ds);
            }
        };
    }

    private void onWordTap(String mWord) {

        speakOutWord(mWord);
        Translation translation =new Translation(mWord);
        translation.fetchTranslation(this);

        translateText.setText(mWord);
        selectedWord =mWord;
    }

    public  void updateTranslateText(Translation translation){
        if(translation.word.equalsIgnoreCase(translateText.getText().toString().trim())) {
            translateText.setText(translation.word + " = " + translation.wordTranslation);
        }
    }

    public void updateDictionaryText(Dictionary dictionary){
        TextView tv ;
        tv=(TextView)findViewById(R.id.editorial_bottomsheet_meaning_textview);
        tv.setText(dictionary.getWordMeaning());
        tv=(TextView)findViewById(R.id.editorial_bottomsheet_partspeech_textview);
        tv.setText(dictionary.getWordPartOfSpeech());
        tv=(TextView)findViewById(R.id.editorial_bottomsheet_synonyms_textview);
        String synonymstring="";
        for(int i =0 ; i<dictionary.getWordsynonym().length ;i++) {
            synonymstring=synonymstring+dictionary.getWordsynonym()[i] +" , ";
        }
        tv.setText(synonymstring);
    }

    public void onDictionaryClick(View v){
        //Intent i =new Intent(this ,);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        openBottomSheet(true);
        Dictionary dictionary = new Dictionary(selectedWord);
        dictionary.fetchWordMeaning(selectedWord ,this);

        TextView tv = (TextView)findViewById(R.id.editorial_bottomsheet_heading_textview);
        tv.setText(translateText.getText());


    }

    private void openBottomSheet(boolean b) {

        TextView tv ;
        tv=(TextView)findViewById(R.id.editorial_bottomsheet_meaning_textview);
        tv.setText("Loading...");
        tv=(TextView)findViewById(R.id.editorial_bottomsheet_partspeech_textview);
        tv.setText("Loading...");
        tv=(TextView)findViewById(R.id.editorial_bottomsheet_synonyms_textview);

        tv.setText("Loading...");
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
               // btnSpeak.setEnabled(true);
                speakOutWord("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOutWord(String speakWord) {


        tts.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);
    }

}
