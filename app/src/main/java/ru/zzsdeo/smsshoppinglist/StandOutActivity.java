package ru.zzsdeo.smsshoppinglist;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.os.Bundle;

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