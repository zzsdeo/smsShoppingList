package ru.zzsdeo.smsshoppinglist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        Object[] messages = (Object[]) bundle.get("pdus");
        SmsMessage[] sms = new SmsMessage[messages.length];

        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < messages.length; n++) {
            sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
            sb.append(sms[n].getMessageBody());
        }

        Bundle smsBundle = new Bundle();
        smsBundle.putString("SMS", sb.toString());
        context.startService(new Intent(context, SmsParser.class).putExtras(smsBundle).setAction("check_and_insert"));
    }
}