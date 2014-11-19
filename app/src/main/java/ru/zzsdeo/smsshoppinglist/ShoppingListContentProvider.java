package ru.zzsdeo.smsshoppinglist;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Andrey on 11.11.2014.
 */
public class ShoppingListContentProvider extends ContentProvider {
    // database
    private ListDatabaseHelper database;

    // used for the UriMacher
    private static final int LIST_ITEMS = 10;
    private static final int LIST_ITEM_ID = 20;
    private static final int PRODUCTS_ITEMS = 30;
    private static final int PRODUCTS_ITEM_ID = 40;

    private static final String AUTHORITY = "ru.zzsdeo.smsshoppinglist.contentprovider";

    private static final String LIST_PATH = "list";
    private static final String PRODUCTS_PATH = "products";
    public static final Uri CONTENT_URI_LIST = Uri.parse("content://" + AUTHORITY
            + "/" + LIST_PATH);
    public static final Uri CONTENT_URI_PRODUCTS = Uri.parse("content://" + AUTHORITY
            + "/" + PRODUCTS_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/list";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/item";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, LIST_PATH, LIST_ITEMS);
        sURIMatcher.addURI(AUTHORITY, LIST_PATH + "/#", LIST_ITEM_ID);
        sURIMatcher.addURI(AUTHORITY, PRODUCTS_PATH, PRODUCTS_ITEMS);
        sURIMatcher.addURI(AUTHORITY, PRODUCTS_PATH + "/#", PRODUCTS_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        database = new ListDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case LIST_ITEMS:
                queryBuilder.setTables(ListTable.TABLE_LIST);
                break;
            case PRODUCTS_ITEMS:
                queryBuilder.setTables(ProductsTable.TABLE_PRODUCTS);
                break;
            case LIST_ITEM_ID:
                // Set the table
                queryBuilder.setTables(ListTable.TABLE_LIST);
                // adding the ID to the original query
                queryBuilder.appendWhere(ListTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case PRODUCTS_ITEM_ID:
                // Set the table
                queryBuilder.setTables(ProductsTable.TABLE_PRODUCTS);
                // adding the ID to the original query
                queryBuilder.appendWhere(ProductsTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;
        String path;
        switch (uriType) {
            case LIST_ITEMS:
                id = sqlDB.insert(ListTable.TABLE_LIST, null, values);
                path = LIST_PATH;
                break;
            case PRODUCTS_ITEMS:
                id = sqlDB.insertWithOnConflict(ProductsTable.TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                path = PRODUCTS_PATH;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(path + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted;
        String id;
        switch (uriType) {
            case LIST_ITEMS:
                rowsDeleted = sqlDB.delete(ListTable.TABLE_LIST, selection,
                        selectionArgs);
                break;
            case LIST_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ListTable.TABLE_LIST,
                            ListTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(ListTable.TABLE_LIST,
                            ListTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case PRODUCTS_ITEMS:
                rowsDeleted = sqlDB.delete(ProductsTable.TABLE_PRODUCTS, selection,
                        selectionArgs);
                break;
            case PRODUCTS_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ProductsTable.TABLE_PRODUCTS,
                            ProductsTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(ProductsTable.TABLE_PRODUCTS,
                            ProductsTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated;
        String id;
        switch (uriType) {
            case LIST_ITEMS:
                rowsUpdated = sqlDB.update(ListTable.TABLE_LIST,
                        values,
                        selection,
                        selectionArgs);
                break;
            case LIST_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ListTable.TABLE_LIST,
                            values,
                            ListTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(ListTable.TABLE_LIST,
                            values,
                            ListTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case PRODUCTS_ITEMS:
                rowsUpdated = sqlDB.update(ProductsTable.TABLE_PRODUCTS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case PRODUCTS_ITEM_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ProductsTable.TABLE_PRODUCTS,
                            values,
                            ProductsTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(ProductsTable.TABLE_PRODUCTS,
                            values,
                            ProductsTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {
                ListTable.COLUMN_ID,
                ListTable.COLUMN_ITEM,
                ListTable.COLUMN_CHECKED,
                ProductsTable.COLUMN_ID,
                ProductsTable.COLUMN_ITEM
        };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
