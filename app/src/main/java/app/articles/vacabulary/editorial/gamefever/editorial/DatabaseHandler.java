package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by gamef on 24-02-2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "VacabularySystem";

    // Contacts table name
    private static final String TABLE_DICTIONARY = "Dictionary";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_WORD = "word";
    private static final String KEY_MEANING = "meaning";
    private static final String KEY_PART_OF_SPEECH = "partofspeech";
    private static final String KEY_SYNONYMS = "synonyms";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_DICTIONARY + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_WORD + " TEXT,"
                + KEY_MEANING + " TEXT,"
                + KEY_PART_OF_SPEECH + " TEXT,"
                + KEY_SYNONYMS + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARY);

        // Create tables again
        onCreate(db);
    }


    public void addToDictionary(Dictionary dictionary) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_DICTIONARY, new String[]{KEY_ID,
                        KEY_WORD}, KEY_WORD + "=?",
                new String[]{dictionary.getWord()}, null, null, null, null);
        if (cursor != null && cursor.getCount() ==0 ) {

            ContentValues values = new ContentValues();
            values.put(KEY_WORD, dictionary.getWord());
            values.put(KEY_MEANING, dictionary.getWordMeaning());
            values.put(KEY_PART_OF_SPEECH, dictionary.getWordPartOfSpeech());
            String synonyms = "";
            for (String s : dictionary.getWordsynonym()) {
                synonyms = synonyms + s + ",";
            }
            values.put(KEY_SYNONYMS, synonyms);


            // Inserting Row
            db.insert(TABLE_DICTIONARY, null, values);
            db.close(); // Closing database connection
        }
    }

    // Getting single contact
    public Dictionary getDictionaryWord(String word) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DICTIONARY, new String[]{KEY_ID,
                        KEY_WORD, KEY_MEANING, KEY_PART_OF_SPEECH, KEY_SYNONYMS}, KEY_WORD + "=?",
                new String[]{word}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Dictionary dictionary = new Dictionary();
        dictionary.setWord(cursor.getString(1));
        dictionary.setWordMeaning(cursor.getString(2));
        dictionary.setWordPartOfSpeech(cursor.getString(3));
        dictionary.setWordsynonym(new String[]{cursor.getString(4)});


        // return contact
        return dictionary;
    }


    public ArrayList<Dictionary> getAllDictionaryWord() {
        ArrayList<Dictionary> dictionaryArrayList = new ArrayList<Dictionary>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DICTIONARY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Dictionary dictionary = new Dictionary();
                dictionary.setWord(cursor.getString(1));
                dictionary.setWordMeaning(cursor.getString(2));
                dictionary.setWordPartOfSpeech(cursor.getString(3));
                dictionary.setWordsynonym(new String[]{cursor.getString(4)});
                dictionaryArrayList.add(dictionary);
            } while (cursor.moveToNext());
        }

        // return contact list
        return dictionaryArrayList;
    }

    public boolean deleteDictionaryWord(String word){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_DICTIONARY, KEY_WORD + "=?", new String[]{word});


        return true;
    }


}
