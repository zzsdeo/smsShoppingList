package ru.zzsdeo.smsshoppinglist;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

/**
 * Created by Andrew on 16.11.2014.
 */
public class SmsParser extends IntentService {

    public SmsParser() {
        super("SmsParser");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean isList = false;
        String sms = intent.getExtras().getString("SMS");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] parsedSms = sms.split(settings.getString("divider_setting", ","));
        Cursor c = getContentResolver().query(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, null, null, null, null);
        for (String s : parsedSms) {
            if (c.moveToFirst()) {
                do {
                    if (c.getString(c.getColumnIndex(ProductsTable.COLUMN_ITEM)).equals(s.trim().toLowerCase())) {
                        isList = true;
                        break;
                    }
                } while (c.moveToNext());
            }
            if (isList) {
                break;
            }
        }
        if (isList) {
            ContentValues values = new ContentValues();
            for (String s : parsedSms) {
                values.put(ListTable.COLUMN_ITEM, s.trim().toLowerCase());
                values.put(ListTable.COLUMN_CHECKED, 0);
                getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI_LIST, values);
                values.clear();
                values.put(ProductsTable.COLUMN_ITEM, s.trim().toLowerCase());
                getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, values);
                values.clear();
            }
            Intent i = new Intent(this, StandOutActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
}
