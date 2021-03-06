package ru.zzsdeo.smsshoppinglist;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import wei.mark.standout.StandOutWindow;

public class SmsParser extends IntentService {

    public SmsParser() {
        super("SmsParser");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String sms = intent.getExtras().getString("SMS");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] parsedSms = sms.split("\\" + settings.getString("divider_setting", ","));

        if (intent.getAction().equals("check_and_insert")) {
            if (checkSms(parsedSms)) {
                insertSmsInDb(parsedSms);
                StandOutWindow.show(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
                if (settings.getBoolean("minimize_autostart", false)) {
                    StandOutWindow.hide(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
                }
            }
        }

        if (intent.getAction().equals("insert")) {
            insertSmsInDb(parsedSms);
            StandOutWindow.show(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
        }
    }

    private boolean checkSms (String[] parsedSms) {
        boolean isList = false;
        Cursor c = getContentResolver().query(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, null, null, null, null);
        for (String s : parsedSms) {
            if (c.moveToFirst()) {
                do {
                    if (s.trim().toLowerCase().contains(c.getString(c.getColumnIndex(ProductsTable.COLUMN_ITEM)))) {
                        isList = true;
                        break;
                    }
                } while (c.moveToNext());
            }
            if (isList) {
                break;
            }
        }
        c.close();
        return isList;
    }

    private void insertSmsInDb (String[] parsedSms) {
        ContentValues values = new ContentValues();
        for (String s : parsedSms) {
            if (s.trim().length() > 0) {
                values.put(ListTable.COLUMN_ITEM, s.trim().toLowerCase());
                values.put(ListTable.COLUMN_CHECKED, 0);
                getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI_LIST, values);
                values.clear();
                values.put(ProductsTable.COLUMN_ITEM, s.trim().toLowerCase());
                getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, values);
                values.clear();
            }
        }
    }
}
