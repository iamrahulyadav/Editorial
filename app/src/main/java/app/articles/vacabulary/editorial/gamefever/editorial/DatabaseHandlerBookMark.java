package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by harsh on 18-03-2017.
 */

public class DatabaseHandlerBookMark extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "BookMarkSystem";

    // Contacts table name
    private static final String TABLE_BOOKMARK = "BookMark";

    // Contacts Table Columns names

    private static final String KEY_EDITORIAL_ID = "editorialid";
    private static final String KEY_EDITORIAL_HEADING = "heading";
    private static final String KEY_EDITORIAL_DATE = "date";
    private static final String KEY_EDITORIAL_SOURCE = "source";
    private static final String KEY_EDITORIAL_TAG = "tag";
    private static final String KEY_EDITORIAL_SUB_HEADING = "subheading";
    private static final String KEY_EDITORIAL_TEXT = "text";


    public DatabaseHandlerBookMark(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOKMARKS_TABLE = "CREATE TABLE " + TABLE_BOOKMARK + "("

                + KEY_EDITORIAL_ID + " TEXT PRIMARY KEY,"
                + KEY_EDITORIAL_HEADING + " TEXT,"
                + KEY_EDITORIAL_SUB_HEADING + " TEXT,"
                + KEY_EDITORIAL_SOURCE + " TEXT,"
                + KEY_EDITORIAL_DATE + " TEXT,"
                + KEY_EDITORIAL_TAG + " TEXT,"
                + KEY_EDITORIAL_TEXT + " TEXT"

                + ")";
        db.execSQL(CREATE_BOOKMARKS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARK);

        // Create tables again
        onCreate(db);
    }


    public void addToBookMark(EditorialGeneralInfo editorialGeneralInfo, EditorialExtraInfo editorialExtraInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_BOOKMARK, new String[]{KEY_EDITORIAL_ID}, KEY_EDITORIAL_ID + "=?",
                new String[]{editorialGeneralInfo.getEditorialID()}, null, null, null, null);
        if (cursor != null && cursor.getCount() == 0) {

            ContentValues values = new ContentValues();
            values.put(KEY_EDITORIAL_ID, "'" + editorialGeneralInfo.getEditorialID() + "'");
            values.put(KEY_EDITORIAL_HEADING,  editorialGeneralInfo.getEditorialHeading() );
            values.put(KEY_EDITORIAL_SUB_HEADING,   editorialGeneralInfo.getEditorialSourceLink() );
            values.put(KEY_EDITORIAL_SOURCE, editorialGeneralInfo.getEditorialSource() );
            values.put(KEY_EDITORIAL_TAG, "'" + editorialGeneralInfo.getEditorialTag() + "'");
            values.put(KEY_EDITORIAL_DATE, "'" + editorialGeneralInfo.getEditorialDate() + "'");
            values.put(KEY_EDITORIAL_TEXT, "'" + editorialExtraInfo.getEditorialText() + "'");


            // Inserting Row
            db.insert(TABLE_BOOKMARK, null, values);
            // db.close(); // Closing database connection
            cursor.close();
        }
    }

    // Getting single contact
    public EditorialExtraInfo getBookMarkEditorial(String editorialID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BOOKMARK, new String[]{KEY_EDITORIAL_ID,
                        KEY_EDITORIAL_TEXT}, KEY_EDITORIAL_ID + "=?",
                new String[]{editorialID}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        EditorialExtraInfo editorialExtraInfo = new EditorialExtraInfo(cursor.getString(0), cursor.getString(1));

        cursor.close();
        // return contact
        return editorialExtraInfo;
    }


    public ArrayList<EditorialGeneralInfo> getAllBookMarkEditorial() {
        ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " +
                KEY_EDITORIAL_ID + "," +
                KEY_EDITORIAL_HEADING + "," +
                KEY_EDITORIAL_SUB_HEADING + "," +
                KEY_EDITORIAL_SOURCE + "," +
                KEY_EDITORIAL_TAG + "," +
                KEY_EDITORIAL_DATE +

                " FROM " + TABLE_BOOKMARK;

        /*KEY_EDITORIAL_ID +","+
                KEY_EDITORIAL_HEADING +","+
                KEY_EDITORIAL_SUB_HEADING +","+
                KEY_EDITORIAL_SOURCE +","+
                KEY_EDITORIAL_TAG +","+
                KEY_EDITORIAL_DATE +*/

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                EditorialGeneralInfo editorialGeneralInfo = new EditorialGeneralInfo();
                editorialGeneralInfo.setEditorialID(cursor.getString(0));
                editorialGeneralInfo.setEditorialHeading(cursor.getString(1));
                editorialGeneralInfo.setEditorialSubHeading(cursor.getString(2));
                editorialGeneralInfo.setEditorialSource(cursor.getString(3));
                editorialGeneralInfo.setEditorialTag(cursor.getString(4));
                editorialGeneralInfo.setEditorialDate(cursor.getString(5));

                editorialGeneralInfoArrayList.add(editorialGeneralInfo);


            } while (cursor.moveToNext());
        }

        // return contact list

        cursor.close();
        return editorialGeneralInfoArrayList;
    }


    public boolean deleteBookMarkEditorial(String editorialID) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_BOOKMARK, KEY_EDITORIAL_ID + "=?", new String[]{editorialID});
        // db.close();

        return true;

    }


}
