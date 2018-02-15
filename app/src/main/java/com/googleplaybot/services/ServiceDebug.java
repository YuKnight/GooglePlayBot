package com.googleplaybot.services;

import android.content.Intent;

import com.googleplaybot.api.APIHandler;
import com.googleplaybot.data.Prefs;
import com.googleplaybot.events.remote.ResponseEvent;
import com.googleplaybot.events.remote.telegram.InboxEvent;
import com.googleplaybot.events.services.RecordEvent;
import com.googleplaybot.models.Command;
import com.googleplaybot.models.Keycode;
import com.googleplaybot.remote.RequestBase;
import com.googleplaybot.services.base.ServiceReceivers;
import com.googleplaybot.utils.LogUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class ServiceDebug extends ServiceReceivers {

	private static final int TELEGRAM_ACTIVE_MESSAGE_DURATION = 60;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(getClass(), "Starting the debug service");
		showForegroundNotification("Device mode is DEBUG");
		startTimer(1);
		return START_NOT_STICKY;
	}

	@Override
	public void onTicTac() {
		if (!canWork || isWorking) {
			return;
		}
		isWorking = true;
		LogUtil.d(getClass(), "Checking for Telegram messages");
		request.asyncGet("https://api.telegram.org/bot" + prefs.getTelegramBotToken() +
				"/getUpdates?limit=1&offset=-1", RequestBase.TAG_GET_TELEGRAM_MESSAGES);
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onResponseEvent(ResponseEvent event) {
		if (!event.isSuccessful) {
			isWorking = false;
		}
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onInboxEvent(InboxEvent event) {
		prefs.putLong(Prefs.TELEGRAM_CHAT_ID, event.chatId);
		long lastUpdateId = prefs.getLong(Prefs.TELEGRAM_UPDATE_ID);
		if (lastUpdateId != event.updateId) {
			prefs.putLong(Prefs.TELEGRAM_UPDATE_ID, event.updateId);
			if ((System.currentTimeMillis() / 1000) - event.messageTime
					> TELEGRAM_ACTIVE_MESSAGE_DURATION) {
				// too old message
				isWorking = false;
				return;
			}
		} else {
			// no new message
			isWorking = false;
			return;
		}
		LogUtil.d(getClass(), "New Telegram message");
		APIHandler.run(new Command(Prefs.DEFAULT_TELEGRAM_USERNAME, event.text),
				getApplicationContext());
		isWorking = false;
	}

	@Subscribe(threadMode = ThreadMode.POSTING)
	@SuppressWarnings("unused")
	public void onRecordEvent(RecordEvent event) {
		switch (event.type) {
			case RecordEvent.TYPE_PRINT:
				db.selectKeycodes(event.keycode.record, null, null, false, true);
				break;
			case RecordEvent.TYPE_INSERT_KEYCODE:
				db.insertKeycode(getApplicationContext(), event.keycode, true);
				break;
            case RecordEvent.TYPE_DELETE_KEYCODE:
                db.deleteLastKeycode(getApplicationContext(), event.keycode.record, true);
                break;
            case RecordEvent.TYPE_CLEAR:
                db.deleteRecord(event.keycode.record, true);
                break;
			default:
				break;
		}
	}

	@Override
	public void afterKeycodesLoaded(ArrayList<Keycode> keycodes) {

	}

	/* Finish record events */

	@Override
	public void afterAddedAccount() {

	}

	@Override
	public void afterAppInstalled(boolean finished) {

	}
}