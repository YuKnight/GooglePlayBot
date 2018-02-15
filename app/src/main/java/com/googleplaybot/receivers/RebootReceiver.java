package com.googleplaybot.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.googleplaybot.ui.ActivityConsole;
import com.googleplaybot.utils.APIUtil;

public class RebootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent activity = new Intent(context, ActivityConsole.class);
		activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (APIUtil.wait(APIUtil.exec()) == APIUtil.EXEC_SUCCESS) {
			activity.putExtra(ActivityConsole.EXTRA_RUN, true);
		}
		context.startActivity(activity);
	}
}