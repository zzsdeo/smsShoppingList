package ru.zzsdeo.smsshoppinglist;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import wei.mark.standout.StandOutWindow;

/**
 * Created by Andrew on 19.11.2014.
 */
public class ImportFromSmsActivity extends Activity {

    Cursor c;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StandOutWindow.show(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
        if (c != null) {
            c.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_sms);

        StandOutWindow.closeAll(this, FloatingWindow.class);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        ListView smsList = (ListView) findViewById(R.id.smsList);
        Uri uriSms = Uri.parse("content://sms/inbox");
        String[] from = new String[]{"_id", "body", "address", "person"};
        c = getContentResolver().query(uriSms, from, null, null, null);
        ImportSmsCursorAdapter adapter = new ImportSmsCursorAdapter(this, c, 0);
        smsList.setAdapter(adapter);

        smsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (c.moveToPosition(i)) {
                    Bundle smsBundle = new Bundle();
                    smsBundle.putString("SMS", c.getString(c.getColumnIndex("body")));
                    smsBundle.putString("action", "insert");
                    startService(new Intent(getApplicationContext(), SmsParser.class).putExtras(smsBundle));
                    finish();
                }
            }
        });
    }
}
