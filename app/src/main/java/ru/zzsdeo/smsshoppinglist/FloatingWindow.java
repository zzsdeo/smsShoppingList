package ru.zzsdeo.smsshoppinglist;

import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FloatingWindow extends StandOutWindow implements Loader.OnLoadCompleteListener<Cursor> {

    private ShoppingListCursorAdapter adapter;
    CursorLoader mCursorLoader;
    Cursor c1, c2, c3;
    SharedPreferences preferences;

    @Override
	public String getAppName() {
		return getString(R.string.app_name);
	}

	@Override
	public int getAppIcon() {
        return R.drawable.ic_launcher;
	}

	@Override
	public String getTitle(int id) {
		return "";
	}

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("layout_prefs", MODE_PRIVATE);
        String[] projection = { ListTable.COLUMN_ID, ListTable.COLUMN_ITEM, ListTable.COLUMN_CHECKED };
        mCursorLoader = new CursorLoader(this, ShoppingListContentProvider.CONTENT_URI_LIST, projection, null, null, ListTable.COLUMN_CHECKED);
        mCursorLoader.registerListener(0, this);
        mCursorLoader.startLoading();
    }

    @Override
	public void createAndAttachView(int id, FrameLayout frame) {
		// create a new layout from body.xml
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.body, frame, true);

        final AutoCompleteTextView addItemInputText = (AutoCompleteTextView) view.findViewById(R.id.addItemInputText);
        final ImageButton addItemBtn = (ImageButton) view.findViewById(R.id.addItemBtn);
        final ListView shoppingList = (ListView) view.findViewById(R.id.shoppingList);

        c1 = getContentResolver().query(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, new String[] {ProductsTable.COLUMN_ITEM, ProductsTable.COLUMN_ID}, null, null, null);
        c2 = getContentResolver().query(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, null, null, null, null);

        final SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                c1,
                new String[] {ProductsTable.COLUMN_ITEM, ProductsTable.COLUMN_ID},
                new int[] {android.R.id.text1},
                0);
        addItemInputText.setAdapter(scAdapter);
        scAdapter.setStringConversionColumn(c2.getColumnIndexOrThrow(ProductsTable.COLUMN_ITEM));
        scAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                String partialValue = null;
                if (constraint != null) {
                    partialValue = constraint.toString();
                }
                c3 = getContentResolver().query(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, null, ProductsTable.COLUMN_ITEM + " like " + '"' + "%" + partialValue + "%" + '"', null, null);
                return c3;
            }
        });
        addItemInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                scAdapter.getFilter().filter(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = addItemInputText.getText().toString().toLowerCase().trim();
                if (item.length() == 0) {
                    return;
                }
                ContentValues values = new ContentValues();
                values.put(ListTable.COLUMN_ITEM, item);
                values.put(ListTable.COLUMN_CHECKED, 0);
                getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI_LIST, values);
                values.clear();
                values.put(ProductsTable.COLUMN_ITEM, item);
                getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, values);
                addItemInputText.setText("");
            }
        });

        mCursorLoader.forceLoad();
        adapter = new ShoppingListCursorAdapter(this, null, 0);
        shoppingList.setAdapter(adapter);
	}

	// every window is initially same size
	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		return new StandOutLayoutParams(id,
                preferences.getInt("layout_width", 500),
                preferences.getInt("layout_height", 500),
                preferences.getInt("layout_x", StandOutLayoutParams.AUTO_POSITION),
                preferences.getInt("layout_y", StandOutLayoutParams.AUTO_POSITION),
                100, 100);
	}

	// we want the system window decorations, we want to drag the body, we want
	// the ability to hide windows, and we want to tap the window to bring to
	// front
	@Override
	public int getFlags(int id) {
		return StandOutFlags.FLAG_DECORATION_SYSTEM
				| StandOutFlags.FLAG_BODY_MOVE_ENABLE
				| StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
				| StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
                | StandOutFlags.FLAG_DECORATION_CLOSE_DISABLE
                | StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE
				| StandOutFlags.FLAG_WINDOW_PINCH_RESIZE_ENABLE;
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        startService(new Intent(getApplicationContext(), RestartIntentService.class));
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onClose(int id, Window window) {
        SharedPreferences.Editor e = preferences.edit();
        int x = window.getLayoutParams().x,
            y = window.getLayoutParams().y;
        e.putInt("layout_x", x);
        e.putInt("layout_y", y);
        e.putInt("layout_width", window.getWidth());
        e.putInt("layout_height", window.getHeight());
        e.apply();
        return super.onClose(id, window);
    }

    @Override
	public String getPersistentNotificationTitle(int id) {
		return getAppName();
	}

	@Override
	public String getPersistentNotificationMessage(int id) {
		return getString(R.string.running);
	}

    @Override
    public boolean onCloseAll() {
        if (c1 != null){
            c1.close();
        }
        if (c2 != null) {
            c2.close();
        }
        if (c3 != null) {
            c3.close();
        }
        return super.onCloseAll();
    }

    @Override
	public int getHiddenIcon() {
		return android.R.drawable.ic_menu_upload;
	}

	@Override
	public String getHiddenNotificationTitle(int id) {
		return getString(R.string.hidden);
	}

	@Override
	public String getHiddenNotificationMessage(int id) {
		return getString(R.string.click_to_restore);
	}

	// return an Intent that restores the MultiWindow
	@Override
	public Intent getHiddenNotificationIntent(int id) {
		return StandOutWindow.getShowIntent(this, FloatingWindow.class, id);
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the cursor loader
        if (mCursorLoader != null) {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
    }

    @Override
	public Animation getShowAnimation(int id) {
		if (isExistingId(id)) {
			// restore
			return AnimationUtils.loadAnimation(this,
					android.R.anim.slide_in_left);
		} else {
			// show
			return super.getShowAnimation(id);
		}
	}

	@Override
	public Animation getHideAnimation(int id) {
		return AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right);
	}

    @Override
    public PopupWindow getDropDown(int id) {
        final List<DropDownListItem> items;
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        List<DropDownListItem> dropDownListItems = getDropDownItems(id);
        if (dropDownListItems != null) {
            items = dropDownListItems;
        } else {
            items = new ArrayList<DropDownListItem>();
        }

        // add default drop down items
        items.add(new DropDownListItem(
                android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.quit),
            new Runnable() {

            @Override
            public void run() {
                closeAll();
            }
        }));

        // turn item list into views in PopupWindow
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);

        final PopupWindow dropDown = new PopupWindow(list,
                StandOutLayoutParams.WRAP_CONTENT,
                StandOutLayoutParams.WRAP_CONTENT, true);

        for (final DropDownListItem item : items) {
            ViewGroup listItem = (ViewGroup) mLayoutInflater.inflate(
                    wei.mark.standout.R.layout.drop_down_list_item, null);
            list.addView(listItem);

            ImageView icon = (ImageView) listItem.findViewById(wei.mark.standout.R.id.icon);
            icon.setImageResource(item.icon);

            TextView description = (TextView) listItem
                    .findViewById(wei.mark.standout.R.id.description);
            description.setText(item.description);

            listItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    item.action.run();
                    dropDown.dismiss();
                }
            });
        }

        Drawable background = getResources().getDrawable(
                android.R.drawable.editbox_dropdown_dark_frame);
        dropDown.setBackgroundDrawable(background);
        return dropDown;

    }

    @Override
	public List<DropDownListItem> getDropDownItems(int id) {
		List<DropDownListItem> items = new ArrayList<DropDownListItem>();

        items.add(new DropDownListItem(android.R.drawable.ic_menu_send,
                getString(R.string.import_from_sms), new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent (getApplicationContext(), ImportFromSmsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }));

        items.add(new DropDownListItem(android.R.drawable.ic_menu_set_as,
                getString(R.string.delete_checked), new Runnable() {

            @Override
            public void run() {
                if (getContentResolver().delete(ShoppingListContentProvider.CONTENT_URI_LIST, ListTable.COLUMN_CHECKED + "=" + 1, null) != 0) {
                    Toast.makeText(FloatingWindow.this,
                            getString(R.string.checked_deleted), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }));

        items.add(new DropDownListItem(android.R.drawable.ic_menu_delete,
                getString(R.string.delete_all), new Runnable() {

            @Override
            public void run() {
                if (getContentResolver().delete(ShoppingListContentProvider.CONTENT_URI_LIST, null, null) != 0) {
                    Toast.makeText(FloatingWindow.this,
                            getString(R.string.list_cleared), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }));

		items.add(new DropDownListItem(android.R.drawable.ic_menu_preferences,
				getString(R.string.settings), new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent (getApplicationContext(), SettingsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
		}));

		return items;
	}

    @Override
    public void onLoadComplete(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }
}
