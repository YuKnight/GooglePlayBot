package com.googleplaybot.services.base;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.googleplaybot.R;
import com.googleplaybot.data.DbManager;
import com.googleplaybot.data.Prefs;
import com.googleplaybot.remote.RequestBase;
import com.googleplaybot.services.ServiceDebug;
import com.googleplaybot.services.ServiceRelease;
import com.googleplaybot.ui.ActivityConsole;
import com.googleplaybot.utils.LogUtil;
import com.googleplaybot.utils.PermissionUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class ServiceBase extends Service {

	private static final int NOTIFICATION_ID = 1;

	private PowerManager.WakeLock wakeLock;

	private ScheduledFuture timer;

	public DbManager db;

	public Prefs prefs;

	public RequestBase request;

	@Nullable
	public static Intent getIntent(Context context) {
		Class serviceClass = getModeServiceClass(context);
		return serviceClass == null ? null : new Intent(context, serviceClass);
	}

	@SuppressWarnings("deprecation")
	public static boolean isRunning(Context context) {
		Class serviceClass = getModeServiceClass(context);
		if (serviceClass == null) {
			LogUtil.w(ServiceBase.class, "Unknown device mode");
			return false;
		}
		ActivityManager manager = (ActivityManager)
				context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service :
				manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	private static Class getModeServiceClass(Context context) {
		int mode = new Prefs(context).getDeviceMode();
		return mode == Prefs.MODE_DEBUG ? ServiceDebug.class : (mode == Prefs.MODE_RELEASE ?
				ServiceRelease.class : null);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				getString(R.string.app_name));
		wakeLock.acquire();
		db = new DbManager(getApplicationContext());
		prefs = new Prefs(getApplicationContext());
		request = new RequestBase();
		PermissionUtil.accessFileSystem(getApplicationContext());
	}

	public void showForegroundNotification(String title) {
		Intent intent = new Intent(getApplicationContext(), ActivityConsole.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new NotificationCompat.Builder(getApplicationContext(),
				getString(R.string.app_name))
				.setSmallIcon(R.drawable.ic_shop_white_24dp)
				.setContentTitle(title)
				.setContentIntent(pendingIntent)
				.build();
		startForeground(NOTIFICATION_ID, notification);
	}

	public void startTimer(int seconds) {
		if (timer != null) {
			LogUtil.w(getClass(), "Already has started the timer");
			return;
		}
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		timer = executorService.scheduleWithFixedDelay(new TimerRunnable(), 0,
				seconds, TimeUnit.SECONDS);
	}

	protected abstract void onTicTac();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.d(getClass(), "Destroying the service");
		if (timer != null) {
			timer.cancel(true);
		}
		wakeLock.release();
		stopForeground(true);
		stopSelf();
	}
}