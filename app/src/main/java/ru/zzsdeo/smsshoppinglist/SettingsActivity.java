package ru.zzsdeo.smsshoppinglist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import wei.mark.standout.StandOutWindow;

/**
 * Created by Andrey on 14.11.2014.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StandOutWindow.closeAll(this, FloatingWindow.class);
        StandOutWindow.show(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
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
        setContentView(R.layout.activity_fragment);

        StandOutWindow.hide(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new SettingsFragment();
            fm.beginTransaction().add(android.R.id.content, fragment).commit();
        }
    }
}
