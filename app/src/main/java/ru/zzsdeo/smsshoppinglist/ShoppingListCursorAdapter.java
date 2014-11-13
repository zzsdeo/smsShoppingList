package ru.zzsdeo.smsshoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Andrew on 12.11.2014.
 */
public class ShoppingListCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public ShoppingListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.item);
        CheckBox cb = (CheckBox) view.findViewById(R.id.checkBoxItem);

        tv.setText(cursor.getString(cursor.getColumnIndex(ListTable.COLUMN_ITEM)));

        Object obj = cursor.getInt(cursor.getColumnIndex(ListTable.COLUMN_ID));
        cb.setTag(obj);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton != null) {
                    Object obj = compoundButton.getTag();
                    if (obj != null && obj instanceof Integer) {
                        ContentValues values = new ContentValues();
                        if (b) {
                            values.put(ListTable.COLUMN_CHECKED, 1);
                        } else {
                            values.put(ListTable.COLUMN_CHECKED, 0);
                        }
                        context.getContentResolver().update(ShoppingListContentProvider.CONTENT_URI_LIST, values, "_id = " + obj, null);
                    }
                }
            }
        });

        if (cursor.getInt(cursor.getColumnIndex(ListTable.COLUMN_CHECKED)) == 1) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }
    }
}
