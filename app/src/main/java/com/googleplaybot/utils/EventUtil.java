package com.googleplaybot.utils;

import org.greenrobot.eventbus.EventBus;

public final class EventUtil {

    public static void post(Object object) {
        if (object == null) {
            return;
        }
        if (EventBus.getDefault().hasSubscriberForEvent(object.getClass())) {
            EventBus.getDefault().post(object);
        }
    }
}
