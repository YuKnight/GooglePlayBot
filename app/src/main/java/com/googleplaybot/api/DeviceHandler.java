package com.googleplaybot.api;

import android.content.Context;

import com.googleplaybot.data.Prefs;
import com.googleplaybot.events.ui.ToggleEvent;
import com.googleplaybot.models.Command;
import com.googleplaybot.services.base.ServiceBase;
import com.googleplaybot.utils.EventUtil;
import com.googleplaybot.utils.LogUtil;

class DeviceHandler extends APIHandler {

    @SuppressWarnings("ConstantConditions")
    public static boolean run(Command command, Context context) {
        Prefs prefs = new Prefs(context);
        switch (command.getAPI()) {
            case API.SET_DEVICE_NAME:
                if (command.getFirstParameter() != null) {
                    prefs.putString(Prefs.DEVICE_NAME, command.getFirstParameter());
                    LogUtil.print("Device renamed successfully");
                } else {
                    onMissedParameters(DeviceHandler.class.getClass());
                }
                return true;
            case API.PRINT_DEVICE_NAME:
                LogUtil.print(prefs.getDeviceName());
                return true;
            case API.SET_DEVICE_MODE:
                if (command.getFirstParameter() != null) {
                    int deviceMode;
                    try {
                        deviceMode = Integer.parseInt(command.getFirstParameter());
                    } catch (NumberFormatException e) {
                        LogUtil.e(DeviceHandler.class.getClass(), e.getMessage());
                        onIncorrectParameters(DeviceHandler.class.getClass());
                        return true;
                    }
                    switch (deviceMode) {
                        case Prefs.MODE_DEBUG: case Prefs.MODE_RELEASE:
                            context.stopService(ServiceBase.getIntent(context));
                            EventUtil.post(new ToggleEvent());
                            prefs.putInt(Prefs.DEVICE_MODE, deviceMode);
                            LogUtil.print("Device mode changed successfully");
                            break;
                        default:
                            LogUtil.w(DeviceHandler.class.getClass(), "Unknown device mode");
                            break;
                    }
                } else {
                    onMissedParameters(DeviceHandler.class.getClass());
                }
                return true;
            case API.PRINT_DEVICE_MODE:
                switch (prefs.getDeviceMode()) {
                    case Prefs.MODE_DEBUG:
                        LogUtil.print("MODE_DEBUG");
                        break;
                    case Prefs.MODE_RELEASE:
                        LogUtil.print("MODE_RELEASE");
                        break;
                    default:
                        LogUtil.w(DeviceHandler.class.getClass(), "Unknown device mode");
                        break;
                }
                return true;
            case API.SET_TELEGRAM_BOT_TOKEN:
                if (command.getFirstParameter() != null) {
                    prefs.putString(Prefs.TELEGRAM_BOT_TOKEN, command.getFirstParameter());
                    LogUtil.print("Telegram bot token changed successfully");
                } else {
                    onMissedParameters(DeviceHandler.class.getClass());
                }
                return true;
            case API.PRINT_TELEGRAM_BOT_TOKEN:
                LogUtil.print(prefs.getTelegramBotToken());
                return true;
            default:
                break;
        }
        return false;
    }
}
