package ru.zzsdeo.smsshoppinglist;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Andrew on 12.11.2014.
 */
public class MySimpleCursorAdapter extends SimpleCursorAdapter {

    String[] mFrom;
    int[] mTo;

    public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mFrom = from;
        mTo = to;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(mTo[0]);
        CheckBox cb = (CheckBox) view.findViewById(mTo[1]);

        tv.setText(cursor.getString(cursor.getColumnIndex(mFrom[0])));
        if (cursor.getInt(cursor.getColumnIndex(mFrom[1])) == 1) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }
    }
}
