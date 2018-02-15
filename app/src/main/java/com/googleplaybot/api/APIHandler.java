package com.googleplaybot.api;

import android.content.Context;

import com.googleplaybot.models.Command;
import com.googleplaybot.services.base.ServiceBase;
import com.googleplaybot.utils.LogUtil;

public class APIHandler {

    public static boolean run(Command command, Context context) {
        if (command.getAPI() == null) {
            return false;
        }
        LogUtil.cmd(command.user, command.text);
        if (DeviceHandler.run(command, context)) {
            return true;
        }
        if (UtilsHandler.run(command, context)) {
            return true;
        }
        if (ServiceBase.isRunning(context)) {
            if (PlayHandler.run(command, context)) {
                return true;
            }
            if (RecordHandler.run(command)) {
                return true;
            }
            LogUtil.w(APIHandler.class.getClass(), "Unknown command");
        } else {
            LogUtil.w(APIHandler.class.getClass(), "Unknown command. Try to enable service");
        }
        return false;
    }

    protected static void onMissedParameters(Class<?> handleClass) {
        LogUtil.w(handleClass, "Missed parameter(s)");
    }

    protected static void onIncorrectParameters(Class<?> handleClass) {
        LogUtil.w(handleClass, "Incorrect parameter(s)");
    }
}
