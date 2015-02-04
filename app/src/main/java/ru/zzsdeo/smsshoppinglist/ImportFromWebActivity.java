package ru.zzsdeo.smsshoppinglist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import wei.mark.standout.StandOutWindow;

public class ImportFromWebActivity extends Activity {

    public static final String JSON_TAG_LISTS = "lists";
    public static final String JSON_TAG_ID = "_id";
    public static final String JSON_TAG_LIST = "list";
    public static final String JSON_TAG_CREATED_AT = "created_at";
    public static final String JSON_TAG_HAS_READ = "has_read";
    public static final String JSON_TAG_SUCCESS = "success";
    private static final String URL_TO_JSON = "http://shoppinglist.mamarada.su/get_all_lists.php?email=";
    private static final String URL_TO_MARK_LIST_AS_READ = "http://shoppinglist.mamarada.su/mark_list_as_read.php";
    private SharedPreferences mainPreferences;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StandOutWindow.show(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                StandOutWindow.show(this, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_sms);

        StandOutWindow.closeAll(this, FloatingWindow.class);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        mainPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        new GetShoppingListsFromWeb().execute(URL_TO_JSON + mainPreferences.getString("email", ""));

    }

    private class GetShoppingListsFromWeb extends AsyncTask <String, Void, JSONObject> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ImportFromWebActivity.this);
            dialog.setMessage(getString(R.string.loading_lists));
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(strings[0]);
            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        builder.append(line);
                    }
                } else {
                    Log.e(GetShoppingListsFromWeb.class.toString(), "Failed to get JSON");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return new JSONObject(builder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            dialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int success = -1;
                    try {
                        success = jsonObject.getInt(JSON_TAG_SUCCESS);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ImportFromWebActivity.this, getString(R.string.error) + e.toString(), Toast.LENGTH_LONG).show();
                    }
                    if (success == 1) {
                        final ListView list = (ListView) findViewById(R.id.smsList);
                        final ImportWebAdapter adapter = new ImportWebAdapter(ImportFromWebActivity.this, jsonObject);
                        list.setAdapter(adapter);

                        list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
                        list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                            @Override
                            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                                actionMode.setTitle(getString(R.string.selected) + " " + list.getCheckedItemCount());
                            }

                            @Override
                            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                                MenuInflater inflater = getMenuInflater();
                                inflater.inflate(R.menu.context_menu, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                                return false;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.itemDelete:
                                        long[] id = list.getCheckedItemIds();
                                        if (id.length != 0) {
                                            /*for (long l : id) {
                                                getContentResolver().delete(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, ProductsTable.COLUMN_ID + "=" + l, null);
                                            }*/
                                            try {
                                                JSONArray ja = jsonObject.getJSONArray(JSON_TAG_LISTS);
                                                for (long l : id) {
                                                    for (int i = 0; i < ja.length(); i++) {
                                                        if (((JSONObject) ja.get(i)).getLong(JSON_TAG_ID) == l) {
                                                            ja.remove(i);
                                                        }
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        actionMode.finish();
                                        return true;
                                    case R.id.selectAll:
                                        for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                            list.setItemChecked(i, true);
                                        }
                                        return true;
                                    default:
                                        return false;
                                }
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode actionMode) {

                            }
                        });

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                JSONObject jo = (JSONObject) adapter.getItem(i);
                                try {
                                    Bundle smsBundle = new Bundle();
                                    smsBundle.putString("SMS", jo.getString(JSON_TAG_LIST).replaceAll("\\^", mainPreferences.getString("divider_setting", ",") + " "));
                                    startService(new Intent(getApplicationContext(), SmsParser.class).putExtras(smsBundle).setAction("insert"));
                                    new MarkListAsRead().execute(l);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else if (success == 0) {
                        Toast.makeText(ImportFromWebActivity.this, getString(R.string.no_lists_found), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private class MarkListAsRead extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL_TO_MARK_LIST_AS_READ);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("_id", String.valueOf(longs[0])));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpClient.execute(httpPost);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class DeleteList extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            return null;
        }
    }
}
