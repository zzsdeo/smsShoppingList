package ru.zzsdeo.smsshoppinglist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Andrey on 11.11.2014.
 */
public class ListDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "listtable.db";
    private static final int DATABASE_VERSION = 1;

    public ListDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        ListTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        ListTable.onUpgrade(database, oldVersion, newVersion);
    }

}
