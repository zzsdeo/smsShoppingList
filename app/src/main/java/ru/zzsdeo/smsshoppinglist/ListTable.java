package ru.zzsdeo.smsshoppinglist;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Andrey on 11.11.2014.
 */
public class ListTable {
    // Database table
    public static final String TABLE_LIST = "list";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ITEM = "item";
    public static final String COLUMN_CHECKED = "checked";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_LIST
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ITEM + " text not null, "
            + COLUMN_CHECKED + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ListTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
        onCreate(database);
    }
}
