package com.googleplaybot.api;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Patterns;

import com.googleplaybot.data.Db;
import com.googleplaybot.events.services.PlayEvent;
import com.googleplaybot.models.Command;
import com.googleplaybot.utils.APIUtil;
import com.googleplaybot.utils.EventUtil;
import com.googleplaybot.utils.LogUtil;

import java.util.List;

class PlayHandler extends APIHandler {

    @SuppressWarnings("ConstantConditions")
    public static boolean run(Command command, Context context) {
        switch (command.getAPI()) {
            case API.GOOGLE_SIGN_IN:
                if (command.getFirstParameter() != null && command.getSecondParameter() != null) {
                    if (Patterns.EMAIL_ADDRESS.matcher(command.getFirstParameter()).matches()) {
                        EventUtil.post(new PlayEvent(Db.RECORD_GOOGLE_ACCOUNT_SIGN_IN, command));
                    } else {
                        onIncorrectParameters(RecordHandler.class.getClass());
                    }
                } else {
                    onMissedParameters(RecordHandler.class.getClass());
                }
                return true;
            case API.SIGN_OUT:
                return true;
            case API.INSTALL_APPLICATION:
                if (command.getFirstParameter() != null) {
                    EventUtil.post(new PlayEvent(Db.RECORD_GOOGLE_PLAY_INSTALLATION, command));
                } else {
                    onMissedParameters(RecordHandler.class.getClass());
                }
                return true;
            case API.UNINSTALL_APPLICATION:
                String parameter = command.getFirstParameter();
                if (parameter == null) {
                    return true;
                }
                if (parameter.startsWith("http://") || parameter.startsWith("https://")) {
                    Uri uri = Uri.parse(parameter);
                    parameter = uri.getQueryParameter("id");
                }
                int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
                PackageManager packageManager = context.getPackageManager();
                List<PackageInfo> packagesInfo = packageManager.getInstalledPackages(0);
                for (PackageInfo packageInfo : packagesInfo) {
                    if (!packageInfo.packageName.equals(parameter)) {
                        continue;
                    }
                    LogUtil.print("Found application with this package name");
                    try {
                        ApplicationInfo applicationInfo = packageManager
                                .getApplicationInfo(packageInfo.packageName,
                                        PackageManager.GET_META_DATA);
                        if ((applicationInfo.flags & mask) != 0) {
                            LogUtil.print("This is a system app. It cannot be deleted here");
                            return true;
                        }
                        LogUtil.print("This is not a system app. Continue");
                        APIUtil.exec(
                                "rm -rf /data/app/" + parameter + "*",
                                "rm -rf /data/data/" + parameter,
                                "am broadcast -a " + Intent.ACTION_PACKAGE_REMOVED +
                                        " -d \"package:" + parameter + "\"" +
                                        " --ez android.intent.extra.REMOVED_FOR_ALL_USERS true" +
                                        " --ez " + Intent.EXTRA_DATA_REMOVED + " true" +
                                        " --ez " + Intent.EXTRA_DONT_KILL_APP + " false" +
                                        " --ei " + Intent.EXTRA_UID + " " + applicationInfo.uid +
                                        " --ei android.intent.extra.user_handle 0"
                        );
                        return true;
                    } catch (PackageManager.NameNotFoundException e) {
                        LogUtil.e(PlayHandler.class.getClass(), e.getMessage());
                        return true;
                    }
                }
                LogUtil.w(PlayHandler.class.getClass(),
                        "Application with this package name not found");
                return true;
            default:
                break;
        }
        return false;
    }
}
