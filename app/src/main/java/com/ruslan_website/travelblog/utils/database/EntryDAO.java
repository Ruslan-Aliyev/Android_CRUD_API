package com.ruslan_website.travelblog.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class EntryDAO extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "entriessManager";

    // Entries table name
    private static final String TABLE_ENTRIES = "entries";

    // Entries Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_DATE = "date";
    private static final String KEY_PLACE = "place";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_IMAGE_URL = "imageUrl";

    public EntryDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ENTRIES_TABLE = "CREATE TABLE " + TABLE_ENTRIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERNAME + " TEXT,"
                + KEY_DATE + " TEXT," + KEY_PLACE + " TEXT,"
                + KEY_COMMENTS + " TEXT," + KEY_IMAGE_URL + " TEXT" + ")";
        db.execSQL(CREATE_ENTRIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);

        // Create tables again
        onCreate(db);
    }


    public void addEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, entry.getUsername());
        values.put(KEY_DATE, entry.getDate());
        values.put(KEY_PLACE, entry.getPlace());
        values.put(KEY_COMMENTS, entry.getComments());
        values.put(KEY_IMAGE_URL, entry.getImageUrl());

        db.insert(TABLE_ENTRIES, null, values);
        db.close();
    }

    public Entry getEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ENTRIES, new String[] { KEY_ID,
                        KEY_USERNAME, KEY_DATE, KEY_PLACE, KEY_COMMENTS, KEY_IMAGE_URL }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Entry entry = new Entry(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5));
        return entry;
    }

    public List<Entry> getAllEntries() {
        List<Entry> entryList = new ArrayList<Entry>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ENTRIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Entry entry = new Entry(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5)
                );

                // Adding entry to list
                entryList.add(entry);
            } while (cursor.moveToNext());
        }

        return entryList;
    }

    public int getEntriesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ENTRIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, entry.getUsername());
        values.put(KEY_DATE, entry.getDate());
        values.put(KEY_PLACE, entry.getPlace());
        values.put(KEY_COMMENTS, entry.getComments());
        values.put(KEY_IMAGE_URL, entry.getImageUrl());

        // updating row
        return db.update(TABLE_ENTRIES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(entry.getId()) });
    }

    public void deleteEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENTRIES, KEY_ID + " = ?",
                new String[] { String.valueOf(entry.getId()) });
        db.close();
    }
}
