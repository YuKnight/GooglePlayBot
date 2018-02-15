package com.googleplaybot.services.base;

import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.googleplaybot.R;
import com.googleplaybot.data.Db;
import com.googleplaybot.data.Prefs;
import com.googleplaybot.events.services.PlayEvent;
import com.googleplaybot.events.services.TimerEvent;
import com.googleplaybot.events.services.KeycodesEvent;
import com.googleplaybot.events.listeners.AddedAccountEvent;
import com.googleplaybot.events.receivers.BatteryEvent;
import com.googleplaybot.events.receivers.NetworkEvent;
import com.googleplaybot.events.listeners.DownloadNotificationEvent;
import com.googleplaybot.events.receivers.ScreenEvent;
import com.googleplaybot.events.remote.telegram.OutboxEvent;
import com.googleplaybot.events.ui.LogEvent;
import com.googleplaybot.listeners.AccountListener;
import com.googleplaybot.models.Keycode;
import com.googleplaybot.utils.EventUtil;
import com.googleplaybot.utils.LogUtil;
import com.googleplaybot.utils.PermissionUtil;
import com.googleplaybot.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public abstract class ServiceReceivers extends ServiceVariables {

	private AccountManager accountManager;

	private AccountListener accountListener;

	@Override
	public void onCreate() {
		super.onCreate();
		EventBus.getDefault().register(this);
		PermissionUtil.enableNotificationListener(getApplicationContext());
		PermissionUtil.accessAccounts(getApplicationContext());
		accountManager = AccountManager.get(getApplicationContext());
		accountListener = new AccountListener();
		accountManager.addOnAccountsUpdatedListener(accountListener, null, true);
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onTimerEvent(TimerEvent event) {
		if (System.currentTimeMillis() - logCreateTime >= LOG_DURATION && !logText.isEmpty()) {
			EventUtil.post(new LogEvent(null));
		}
		onTicTac();
	}

	/* Hardware receiver events */

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onNetworkEvent(NetworkEvent event) {
		hasInternetConnection = false;
		checkCanWorkAndPlay(false);
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onBatteryEvent(BatteryEvent event) {
		hasEnoughBatteryLevel = false;
		checkCanWorkAndPlay(false);
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onScreenEvent(ScreenEvent event) {
		hasScreenTurnedOn = false;
		checkCanWorkAndPlay(false);
	}

	/* Listener events */

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onAddedAccountEvent(AddedAccountEvent event) {
		Timber.d("New Google account(s) added");
		afterAddedAccount();
	}

	public abstract void afterAddedAccount();

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onDownloadNotificationEvent(DownloadNotificationEvent event) {
		Timber.d("Download notification posted");
		afterAppInstalled(event.finished);
	}

	public abstract void afterAppInstalled(boolean finished);

	/* Play event */

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onPlayEvent(PlayEvent event) {
		record = event.record;
		db.selectKeycodes(event.record, event.firstParameter, event.secondParameter, true, false);
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onKeycodesEvent(KeycodesEvent event) {
		if (event.keycodes.size() > 0) {
			switch (event.record) {
				case Db.RECORD_GOOGLE_PLAY_INSTALLATION:
					String pckg = event.firstParameter;
					if (pckg.startsWith("http://") || pckg.startsWith("https://")) {
						Uri uri = Uri.parse(pckg);
						pckg = uri.getQueryParameter("id");
					}
					startActivity(new Intent(Intent.ACTION_VIEW,
							Uri.parse("market://details?id=" + pckg)));
					break;
				case Db.RECORD_GOOGLE_ACCOUNT_SIGN_IN:
					Intent accountIntent = new Intent(Settings.ACTION_ADD_ACCOUNT)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					accountIntent.putExtra(Settings.EXTRA_ACCOUNT_TYPES,
							new String[] {"com.google"});
					startActivity(accountIntent);
					break;
				default:
					LogUtil.w(getClass(), "Unknown record. Continue");
					return;
			}
			afterKeycodesLoaded(event.keycodes);
		} else {
			record = Db.RECORD_NONE;
			LogUtil.w(getClass(), "No keycodes found for record");
		}
	}

	public abstract void afterKeycodesLoaded(ArrayList<Keycode> keycodes);

	/* Logcat event */

	@Subscribe(threadMode = ThreadMode.MAIN)
	@SuppressWarnings("unused")
	public void onLogEvent(LogEvent event) {
		if (prefs.getBoolean(Prefs.CONSOLE_IS_ACTIVE)) {
			return;
		}
		if (event.log == null && logText.isEmpty()) {
			return;
		}
		if (System.currentTimeMillis() - logCreateTime < LOG_DURATION) {
			if (event.log != null && (logText.isEmpty() || !LogUtil.isDuplicatedLog(event.log,
					logText.get(logText.size() - 1)))) {
				logText.add(event.log);
			}
			return;
		}
		logCreateTime = System.currentTimeMillis();
		Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
		View toastView = toast.getView();
		int padding = ViewUtil.dp2px(12);
		toastView.setPadding(padding, padding, padding, padding);
		TextView message = toastView.findViewById(android.R.id.message);
		message.setTextSize(16);
		message.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayText));
		for (int i = 0; i < logText.size(); i++) {
			message.append(logText.get(i));
			if (i != logText.size() - 1 || event.log != null) {
				message.append(System.getProperty("line.separator"));
			}
		}
		if (event.log != null && (logText.isEmpty() || !LogUtil.isDuplicatedLog(event.log,
				logText.get(logText.size() - 1)))) {
			message.append(event.log);
		}
		logText.clear();
		toastView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast));
		toast.show();
	}

	/* Telegram event */

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onOutboxEvent(OutboxEvent event) {
		if (!prefs.has(Prefs.TELEGRAM_CHAT_ID)) {
			LogUtil.w(getClass(), "No Telegram chat id is found");
			return;
		}
		LogUtil.d(getClass(), "Sending for @TelegramBot message");
		HashMap<String, String> params = new HashMap<>();
		params.put("chat_id", String.valueOf(prefs.getLong(Prefs.TELEGRAM_CHAT_ID)));
		params.put("text", event.text);
		request.asyncPost("https://api.telegram.org/bot" + prefs.getTelegramBotToken() +
				"/sendMessage", null, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		accountManager.removeOnAccountsUpdatedListener(accountListener);
		EventBus.getDefault().unregister(this);
	}
}