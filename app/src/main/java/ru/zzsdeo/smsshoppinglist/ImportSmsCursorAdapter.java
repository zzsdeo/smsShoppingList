package ru.zzsdeo.smsshoppinglist;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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

        String address = cursor.getString(cursor.getColumnIndex("address"));
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));
        Cursor mCursor = context.getContentResolver().query(lookupUri, new String[] {ContactsContract.Data.DISPLAY_NAME}, null, null, null);

        TextView tv1 = (TextView) view.findViewById(R.id.smsAddressItem);
        if (mCursor.moveToFirst()) {
            tv1.setText(mCursor.getString(0));
        } else {
            tv1.setText(address);
        }
        mCursor.close();

        TextView tv2 = (TextView) view.findViewById(R.id.smsBodyItem);
        tv2.setText(cursor.getString(cursor.getColumnIndex("body")));
    }
}