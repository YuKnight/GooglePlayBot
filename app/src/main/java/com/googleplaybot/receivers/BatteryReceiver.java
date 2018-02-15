package com.googleplaybot.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.googleplaybot.events.receivers.BatteryEvent;
import com.googleplaybot.utils.EventUtil;

public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        EventUtil.post(new BatteryEvent());
    }
}
