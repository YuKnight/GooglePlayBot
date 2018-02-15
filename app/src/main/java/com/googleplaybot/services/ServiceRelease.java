package com.googleplaybot.services;

import android.content.Intent;

import com.googleplaybot.models.Keycode;
import com.googleplaybot.services.base.ServiceReceivers;
import com.googleplaybot.utils.LogUtil;

import java.util.ArrayList;

public class ServiceRelease extends ServiceReceivers {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(getClass(), "Starting the release service");
		showForegroundNotification("Device mode is RELEASE");
		startTimer(30);
		return START_NOT_STICKY;
	}

	@Override
	public void onTicTac() {

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