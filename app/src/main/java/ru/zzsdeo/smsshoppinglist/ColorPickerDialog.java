package ru.zzsdeo.smsshoppinglist;

import android.app.AlertDialog;

public class ColorPickerDialog extends AlertDialog implements
        ColorPickerView.OnColorChangedListener {

    private ColorPickerView mColorPicker;

    private ColorPanelView mOldColor;
    private ColorPanelView mNewColor;

    private OnColorChangedListener mListener;

    public ColorPickerDialog(Context myDrawingMenuOptionEventsListener, int initialColor) {
        super(myDrawingMenuOptionEventsListener);

        init(initialColor);
    }

    private void init(int color) {
        // To fight color branding.
        getWindow().setFormat(PixelFormat.RGBA_8888);

        setUp(color);

    }

    private void setUp(int color) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_color_picker, null);
        layout.setBackgroundColor(Color.WHITE);
        setView(layout);

        setTitle("Choose a Color");
        // setIcon(android.R.drawable.ic_dialog_info);

        mColorPicker = (ColorPickerView) layout
                .findViewById(R.id.color_picker_view);
        mOldColor = (ColorPanelView) layout.findViewById(R.id.old_color_panel);
        mNewColor = (ColorPanelView) layout.findViewById(R.id.new_color_panel);

        ((LinearLayout) mOldColor.getParent()).setPadding(Math
                .round(mColorPicker.getDrawingOffset()), 0, Math
                .round(mColorPicker.getDrawingOffset()), 0);

        mColorPicker.setOnColorChangedListener(this);

        mOldColor.setColor(color);
        mColorPicker.setColor(color, true);

    }

    @Override
    public void onColorChanged(int color) {

        mNewColor.setColor(color);

        if (mListener != null) {
            mListener.onColorChanged(color);
        }

    }

    public void setAlphaSliderVisible(boolean visible) {
        mColorPicker.setAlphaSliderVisible(visible);
    }

    public int getColor() {
        return mColorPicker.getColor();
    }

}