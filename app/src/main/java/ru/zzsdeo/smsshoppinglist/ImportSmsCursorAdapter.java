package ru.zzsdeo.smsshoppinglist;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Andrew on 12.11.2014.
 */
public class ImportSmsCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public ImportSmsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.import_sms_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Cursor cCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER},
                null, null, null);

        TextView tv1 = (TextView) view.findViewById(R.id.smsAddressItem);
        tv1.setText(cursor.getString(cursor.getColumnIndex("address")));
        if (cCursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex("address")).replaceAll("\\D", "")
                        .equals(cCursor.getString(cCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\D", ""))) {
                    tv1.setText(cCursor.getString(cCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    break;
                }
            } while (cCursor.moveToNext());
        }
        TextView tv2 = (TextView) view.findViewById(R.id.smsBodyItem);
        tv2.setText(cursor.getString(cursor.getColumnIndex("body")));

        cCursor.close();
    }
}