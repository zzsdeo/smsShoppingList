package ru.zzsdeo.smsshoppinglist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImportWebAdapter extends BaseAdapter {

    private JSONObject mJSONObject;
    private Context mContext;

    public ImportWebAdapter (Context context, JSONObject jsonObject) {
        mJSONObject = jsonObject;
        mContext = context;
    }

    @Override
    public int getCount() {
        try {
            return mJSONObject.getJSONArray(ImportFromWebActivity.JSON_TAG_LISTS).length();
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        try {
            return mJSONObject.getJSONArray(ImportFromWebActivity.JSON_TAG_LISTS).get(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        try {
            JSONObject jo = (JSONObject) mJSONObject.getJSONArray(ImportFromWebActivity.JSON_TAG_LISTS).get(i);
            return jo.getLong(ImportFromWebActivity.JSON_TAG_ID);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        View rowView = view;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            rowView = inflater.inflate(R.layout.import_sms_item, null, true);
            holder = new ViewHolder();
            holder.textView1 = (TextView) rowView.findViewById(R.id.smsAddressItem);
            holder.textView2 = (TextView) rowView.findViewById(R.id.smsBodyItem);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("mm.dd.yy HH:mm");
        JSONObject jo = (JSONObject) getItem(i);
        try {
            Date date = new Date(jo.getLong(ImportFromWebActivity.JSON_TAG_CREATED_AT));
            holder.textView1.setText(sdf.format(date));
            if (jo.getInt(ImportFromWebActivity.JSON_TAG_HAS_READ) == 0) {
                holder.textView2.setTypeface(null, Typeface.BOLD);
            } else {
                holder.textView2.setTypeface(null, Typeface.NORMAL);
            }
            String list = jo.getString(ImportFromWebActivity.JSON_TAG_LIST);
            holder.textView2.setText(list.replaceAll("\\^", ", "));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rowView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        public TextView textView1;
        public TextView textView2;
    }
}
