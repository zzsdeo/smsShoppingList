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
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FloatingWindow extends StandOutWindow implements Loader.OnLoadCompleteListener<Cursor>{

    private ShoppingListCursorAdapter adapter;
    CursorLoader mCursorLoader;

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

        //ArrayAdapter<String> acAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[] {"11qwe", "11rty", "11asd", "11fgh", "11zxc", "11vbn", "11123", "456", "poi"});
        //SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, getContentResolver().query(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, new String[] {"item"}, null, null, null), new String[] {"item"}, null, 0);
        //addItemInputText.setAdapter(scAdapter);

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = addItemInputText.getText().toString();
                if (item.length() == 0) {
                    return;
                }
                ContentValues values = new ContentValues();
                values.put(ListTable.COLUMN_ITEM, item);
                values.put(ListTable.COLUMN_CHECKED, 0);
                getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI_LIST, values);
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
		return new StandOutLayoutParams(id, 500, 500,
				StandOutLayoutParams.AUTO_POSITION,
				StandOutLayoutParams.AUTO_POSITION, 100, 100);
	}

	// we want the system window decorations, we want to drag the body, we want
	// the ability to hide windows, and we want to tap the window to bring to
	// front
	@Override
	public int getFlags(int id) {
		return StandOutFlags.FLAG_DECORATION_SYSTEM
				| StandOutFlags.FLAG_BODY_MOVE_ENABLE
				| StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
				//| StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP
				| StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
                | StandOutFlags.FLAG_DECORATION_CLOSE_DISABLE
                | StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE
                //| StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE
				| StandOutFlags.FLAG_WINDOW_PINCH_RESIZE_ENABLE;
	}

	@Override
	public String getPersistentNotificationTitle(int id) {
		return getAppName();
	}

	@Override
	public String getPersistentNotificationMessage(int id) {
		return getString(R.string.running);
	}

/*// return an Intent that creates a new MultiWindow
	@Override
	public Intent getPersistentNotificationIntent(int id) {
		return StandOutWindow.getShowIntent(this, getClass(), getUniqueId());
	}*/

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
		items.add(new DropDownListItem(android.R.drawable.ic_menu_help,
				"About", new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								FloatingWindow.this,
								getAppName()
										+ " is a demonstration of StandOut.",
								Toast.LENGTH_SHORT).show();
					}
				}));
		items.add(new DropDownListItem(android.R.drawable.ic_menu_preferences,
				"Settings", new Runnable() {

					@Override
					public void run() {
						Toast.makeText(FloatingWindow.this,
								"There are no settings.", Toast.LENGTH_SHORT)
								.show();
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
		return items;
	}

    @Override
    public void onLoadComplete(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

	/*@Override
	public void onReceiveData(int id, int requestCode, Bundle data,
			Class<? extends StandOutWindow> fromCls, int fromId) {
		// receive data from WidgetsWindow's button press
		// to show off the data sending framework
		switch (requestCode) {
			case WidgetsWindow.DATA_CHANGED_TEXT:
				Window window = getWindow(id);
				if (window == null) {
					String errorText = String.format(Locale.US,
							"%s received data but Window id: %d is not open.",
							getAppName(), id);
					Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
					return;
				}
				String changedText = data.getString("changedText");
				TextView status = (TextView) window.findViewById(R.id.id);
				status.setTextSize(20);
				status.setText("Received data from WidgetsWindow: "
						+ changedText);
				break;
			default:
				Log.d("MultiWindow", "Unexpected data received.");
				break;
		}
	}*/
}
