package ru.zzsdeo.smsshoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ShoppingListCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    SharedPreferences preferences;

    public ShoppingListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preferences = context.getSharedPreferences("font_prefs", Context.MODE_PRIVATE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.item);
        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkBoxItem);

        tv.setTextSize(preferences.getInt("font_size", 18));
        if (cursor.getInt(cursor.getColumnIndex(ListTable.COLUMN_CHECKED)) == 1) {
            cb.setChecked(true);
            SpannableStringBuilder sp = new SpannableStringBuilder(cursor.getString(cursor.getColumnIndex(ListTable.COLUMN_ITEM)));
            sp.setSpan(new StrikethroughSpan(), 0, sp.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(sp);
        } else {
            cb.setChecked(false);
            tv.setText(cursor.getString(cursor.getColumnIndex(ListTable.COLUMN_ITEM)));
        }

        cb.setTag(cursor.getInt(cursor.getColumnIndex(ListTable.COLUMN_ID)));
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object obj = cb.getTag();
                if (obj != null && obj instanceof Integer) {
                    ContentValues values = new ContentValues();
                    if (cb.isChecked()) {
                        values.put(ListTable.COLUMN_CHECKED, 1);
                    } else {
                        values.put(ListTable.COLUMN_CHECKED, 0);
                    }
                    context.getContentResolver().update(ShoppingListContentProvider.CONTENT_URI_LIST, values, "_id = " + obj, null);
                    values.clear();
                }
            }
        });
    }
}
