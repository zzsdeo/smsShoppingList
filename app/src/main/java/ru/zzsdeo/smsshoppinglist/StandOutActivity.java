package ru.zzsdeo.smsshoppinglist;

import android.app.Activity;
import android.os.Bundle;

import wei.mark.standout.StandOutWindow;

public class StandOutActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StandOutWindow.closeAll(this, FloatingWindow.class);
		StandOutWindow.show(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
		finish();
	}
}