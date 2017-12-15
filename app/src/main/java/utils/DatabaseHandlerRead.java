package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import app.articles.vacabulary.editorial.gamefever.editorial.EditorialGeneralInfo;

/**
 * Created by bunny on 06/12/17.
 */

public class DatabaseHandlerRead extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "EditorialManager";

    // Contacts table name
    private static final String TABLE_READ_FEEDS = "readfeeds";


    // Contacts Table Columns names
    private static final String KEY_PUSHID = "pushid";
    private static final String KEY_LINK = "link";
    private static final String KEY_HEADING = "heading";
    private static final String KEY_DATE = "editorialdate";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_IS_LIKED = "editoriallike";


    public DatabaseHandlerRead(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATEREAD_FEED = "CREATE TABLE " + TABLE_READ_FEEDS + "("
                + KEY_PUSHID + " TEXT PRIMARY KEY,"
                + KEY_LINK + " TEXT,"
                + KEY_HEADING + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_SOURCE + " INTEGER,"
                + KEY_CATEGORY + " INTEGER,"
                + KEY_IS_LIKED + " BOOLEAN"
                + ")";


        db.execSQL(CREATEREAD_FEED);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READ_FEEDS);


        // Create tables again
        onCreate(db);
    }

    // Adding new Feed
    public void addReadNews(EditorialGeneralInfo editorialGeneralInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PUSHID, editorialGeneralInfo.getEditorialID());
        values.put(KEY_LINK, editorialGeneralInfo.getEditorialSourceLink() + "");
        values.put(KEY_HEADING, editorialGeneralInfo.getEditorialHeading());
        values.put(KEY_DATE, editorialGeneralInfo.getEditorialDate());
        values.put(KEY_CATEGORY, editorialGeneralInfo.getEditorialCategoryIndex());
        values.put(KEY_SOURCE, editorialGeneralInfo.getEditorialSourceIndex());
        values.put(KEY_IS_LIKED, false);



            // Inserting Row
        db.insert(TABLE_READ_FEEDS, null, values);

        // db.close(); // Closing database connection
    }


    public boolean getNewsReadStatus(String pushID) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_READ_FEEDS, new String[]{KEY_PUSHID,
                        KEY_HEADING}, KEY_PUSHID + "=?",
                new String[]{pushID}, null, null, null, null);

        if (cursor == null) {

            return false;
        } else {

            if (cursor.moveToFirst()) {
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }

        }

    }


}
