package com.googleplaybot.ui.views;

import android.view.ViewTreeObserver;

import com.googleplaybot.events.ui.KeyboardEvent;
import com.googleplaybot.utils.EventUtil;

public class KeyboardObserver implements ViewTreeObserver.OnGlobalLayoutListener {

    @Override
    public void onGlobalLayout() {
        EventUtil.post(new KeyboardEvent());
    }
}
