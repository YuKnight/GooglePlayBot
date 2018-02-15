package com.googleplaybot.ui.views;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

public class ProgrammableSwitchCompat extends SwitchCompat {

    public boolean isCheckedProgrammatically = false;

    public ProgrammableSwitchCompat(final Context context) {
        super(context);
    }

    public ProgrammableSwitchCompat(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgrammableSwitchCompat(final Context context, final AttributeSet attrs,
                                    final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        isCheckedProgrammatically = false;
        super.setChecked(checked);
    }

    public void setCheckedProgrammatically(boolean checked) {
        isCheckedProgrammatically = true;
        super.setChecked(checked);
    }
}