package ru.zzsdeo.smsshoppinglist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        String name = mObjects.get(position).loadLabel(mContext.getPackageManager()).toString();
        holder.textView.setText(name);
        new ImageFetcher(name, holder.imageView, mObjects.get(position)).execute();

        return rowView;
    }

    private static class ViewHolder {
        public TextView textView1;
        public TextView textView2;
    }
}
