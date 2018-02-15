package com.googleplaybot.services.base;

import com.googleplaybot.events.services.TimerEvent;
import com.googleplaybot.utils.EventUtil;

class TimerRunnable implements Runnable {

    @Override
    public void run() {
        EventUtil.post(new TimerEvent());
    }
}
