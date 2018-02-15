package com.googleplaybot;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.googleplaybot.components.CrashReportingTree;
import com.googleplaybot.data.Prefs;
import com.googleplaybot.receivers.BatteryReceiver;
import com.googleplaybot.receivers.NetworkReceiver;
import com.googleplaybot.receivers.ScreenReceiver;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import timber.log.Timber;

/**
 * Developer: Vlad Kalyuzhnyu
 * Email: vladkalyuzhnyu@gmail.com
 */
@ReportsCrashes(mailTo = "@",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.CUSTOM_DATA,
                ReportField.STACK_TRACE,
                ReportField.LOGCAT
        },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.message_on_crash)
public class GooglePlayBot extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            new Prefs(getApplicationContext()).printAll();
        } else {
            Timber.plant(new CrashReportingTree());
        }
        registerReceiver(new NetworkReceiver(), ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new BatteryReceiver(), Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(new ScreenReceiver(), Intent.ACTION_SCREEN_ON, Intent.ACTION_SCREEN_OFF);
    }

    private void registerReceiver(BroadcastReceiver receiver, String... actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        registerReceiver(receiver, filter);
    }
}
