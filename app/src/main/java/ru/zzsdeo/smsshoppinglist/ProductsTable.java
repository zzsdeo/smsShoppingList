package ru.zzsdeo.smsshoppinglist;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Andrey on 11.11.2014.
 */
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
    private static String[] defaultProducts = {
            "молоко",
            "хлеб",
            "булка",
            "сахар",
            "мясо",
            "яйца",
            "огурцы",
            "помидоры",
            "картофель",
            "сыр"};

    public static void onCreate(SQLiteDatabase database) {
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

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ProductsTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(database);
    }
}
