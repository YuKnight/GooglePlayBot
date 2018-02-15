package com.googleplaybot.utils;

import android.content.Context;

import com.googleplaybot.listeners.NotificationListener;

public final class PermissionUtil {

    public static void accessAccounts(Context context) {
        APIUtil.exec("pm grant " + context.getPackageName() + " android.permission.GET_ACCOUNTS");
    }

    public static void accessFileSystem(Context context) {
        APIUtil.exec("pm grant " + context.getPackageName() +
                " android.permission.WRITE_EXTERNAL_STORAGE");
    }

    public static void enableNotificationListener(Context context) {
        String enabledNotificationListeners = getNotificationListeners().trim();
        if (!enabledNotificationListeners.contains(context.getPackageName())) {
            LogUtil.d(PermissionUtil.class.getClass(), "Enabling access to notifications");
            APIUtil.exec("settings put secure enabled_notification_listeners " +
                    (enabledNotificationListeners.equals("null") ? "" :
                            enabledNotificationListeners + ":") + context.getPackageName() + "/" +
                    NotificationListener.class.getName());
        }
    }

    private static String getNotificationListeners() {
        return APIUtil.output(APIUtil.exec("settings get secure enabled_notification_listeners"));
    }
}
