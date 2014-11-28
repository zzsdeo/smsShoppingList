package ru.zzsdeo.smsshoppinglist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import wei.mark.standout.StandOutWindow;

public class SettingsActivity extends Activity {

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
        setContentView(R.layout.activity_fragment);

        StandOutWindow.closeAll(this, FloatingWindow.class);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = new SettingsFragment();
            fm.beginTransaction().replace(android.R.id.content, fragment).commit();
        }
    }
}
