package ru.zzsdeo.smsshoppinglist;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import wei.mark.standout.StandOutWindow;

public class ImportFromSmsActivity extends Activity {

    Cursor c;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (c != null) {
            c.close();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StandOutWindow.show(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                StandOutWindow.show(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
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
        String[] from = new String[]{"_id", "body", "address"};
        c = getContentResolver().query(uriSms, from, null, null, null);
        ImportSmsCursorAdapter adapter = new ImportSmsCursorAdapter(this, c, 0);
        smsList.setAdapter(adapter);

        smsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (c.moveToPosition(i)) {
                    Bundle smsBundle = new Bundle();
                    smsBundle.putString("SMS", c.getString(c.getColumnIndex("body")));
                    startService(new Intent(getApplicationContext(), SmsParser.class).putExtras(smsBundle).setAction("insert"));
                    finish();
                }
            }
        });
    }
}
