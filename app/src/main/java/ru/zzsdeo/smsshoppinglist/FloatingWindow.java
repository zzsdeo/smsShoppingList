package ru.zzsdeo.smsshoppinglist;

import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
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


/**
 * This implementation provides multiple windows. You may extend this class or
 * use it as a reference for a basic foundation for your own windows.
 * 
 * <p>
 * Functionality includes system window decorators, moveable, resizeable,
 * hideable, closeable, and bring-to-frontable.
 * 
 * <p>
 * The persistent notification creates new windows. The hidden notifications
 * restores previously hidden windows.
 * 
 * @author Mark Wei <markwei@gmail.com>
 * 
 */
public class FloatingWindow extends StandOutWindow implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter adapter;

	@Override
	public String getAppName() {
		return getString(R.string.app_name);
	}

	@Override
	public int getAppIcon() {
		return android.R.drawable.ic_menu_add;
	}

	@Override
	public String getTitle(int id) {
		return "";
	}

	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		// create a new layout from body.xml
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.body, frame, true);

        final AutoCompleteTextView addItemInputText = (AutoCompleteTextView) view.findViewById(R.id.addItemInputText);
        final ImageButton addItemBtn = (ImageButton) view.findViewById(R.id.addItemBtn);
        final ListView shoppingList = (ListView) view.findViewById(R.id.shoppingList);

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
                getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI, values);
            }
        });

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] { ListTable.COLUMN_ITEM };
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.item };
        //initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.list_item, null, from, to, 0);
        shoppingList.setAdapter(adapter);
	}

	// every window is initially same size
	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		return new StandOutLayoutParams(id, 400, 300,
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
		return android.R.drawable.ic_menu_info_details;
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
                android.R.drawable.ic_menu_close_clear_cancel, "Quit "
                , new Runnable() {

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
		return items;
	}

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = { ListTable.COLUMN_ID, ListTable.COLUMN_ITEM, ListTable.COLUMN_CHECKED };
        return new CursorLoader(this, ShoppingListContentProvider.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
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
