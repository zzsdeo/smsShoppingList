package ru.zzsdeo.smsshoppinglist;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import wei.mark.standout.StandOutWindow;

public class RestartIntentService extends IntentService {


    public RestartIntentService() {
        super("RestartIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        StandOutWindow.closeAll(getApplicationContext(), FloatingWindow.class);
        StandOutWindow.show(getApplicationContext(), FloatingWindow.class, StandOutWindow.DEFAULT_ID);
    }
}
