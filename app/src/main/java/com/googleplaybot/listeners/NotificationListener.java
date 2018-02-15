package com.googleplaybot.listeners;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.googleplaybot.events.listeners.DownloadNotificationEvent;
import com.googleplaybot.services.base.ServiceBase;
import com.googleplaybot.utils.LogUtil;
import com.googleplaybot.utils.EventUtil;

public class NotificationListener extends NotificationListenerService {

    public static final String COM_ANDROID_PROVIDERS_DOWNLOADS = "com.android.providers.downloads";
    public static final String COM_ANDROID_VENDING = "com.android.vending";

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onNotificationPosted(StatusBarNotification notification) {
        if (!ServiceBase.isRunning(getApplicationContext())) {
            return;
        }
        switch (notification.getPackageName()) {
            case COM_ANDROID_PROVIDERS_DOWNLOADS:
                EventUtil.post(new DownloadNotificationEvent(false));
                break;
            case COM_ANDROID_VENDING:
                EventUtil.post(new DownloadNotificationEvent(true));
                break;
            default:
                LogUtil.d(getClass(), "Notification posted with package name '" +
                        notification.getPackageName() + "'");
                break;
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification notification) {}
}
