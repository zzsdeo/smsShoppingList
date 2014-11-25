package ru.zzsdeo.smsshoppinglist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProductsTable {
    // Database table
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ITEM = "item";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PRODUCTS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ITEM + " text not null unique"
            + ");";

    public static void onCreate(Context context, SQLiteDatabase database) {
        String[] defaultProducts = context.getResources().getStringArray(R.array.default_products);
        database.execSQL(DATABASE_CREATE);
        for (String item : defaultProducts) {
            database.execSQL(
                    "insert into " + TABLE_PRODUCTS
                    + "(" + COLUMN_ITEM + ")"
                    + "values"
                    + "('" + item + "');"
            );
        }
    }

    public static void onUpgrade(Context context, SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ProductsTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(context, database);
    }
}
