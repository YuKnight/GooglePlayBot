package com.googleplaybot.api;

import android.content.Context;
import android.support.annotation.ArrayRes;

import com.github.pwittchen.kirai.library.Formatter;
import com.github.pwittchen.kirai.library.Kirai;
import com.github.pwittchen.kirai.library.html.HtmlPiece;
import com.googleplaybot.R;
import com.googleplaybot.events.ui.LogEvent;
import com.googleplaybot.models.Command;
import com.googleplaybot.utils.LogUtil;
import com.googleplaybot.utils.DeviceUtil;

import org.greenrobot.eventbus.EventBus;

class UtilsHandler extends APIHandler {

    @SuppressWarnings("ConstantConditions")
    public static boolean run(Command command, Context context) {
        switch (command.getAPI()) {
            case API.PRINT_HELP:
                if (!EventBus.getDefault().hasSubscriberForEvent(LogEvent.class)) {
                    return true;
                }
                Formatter formatter = new Formatter() {
                    @Override
                    public CharSequence format(String input) {
                        return LogUtil.fromHtml(input);
                    }
                };
                EventBus.getDefault().post(new LogEvent(context.getString(R.string.app_name) +
                        ", версия " + DeviceUtil.getVersionName(context) +
                        System.getProperty("line.separator") +
                        "Разработчик: Калюжный Влад (https://андроидовщик.рф)"));
                printConstants(context, R.array.api_keycodes, formatter);
                printConstants(context, R.array.api_records, formatter);
                printConstants(context, R.array.api_modes, formatter);
                printGroups(context, R.array.api_tools_play, formatter);
                printGroups(context, R.array.api_tools_device, formatter);
                printGroups(context, R.array.api_tools_record, formatter);
                printGroups(context, R.array.api_utils, formatter);
                return true;
            default:
                break;
        }
        return false;
    }

    private static void printGroups(Context context, @ArrayRes int array, Formatter formatter) {
        String groupName = array == R.array.api_tools_play ?
                context.getString(R.string.api_tools_play_name) : null;
        groupName = array == R.array.api_tools_device ?
                context.getString(R.string.api_tools_device_name) : groupName;
        groupName = array == R.array.api_tools_record ?
                context.getString(R.string.api_tools_record_name) : groupName;
        groupName = array == R.array.api_utils ? context.getString(R.string.api_utils_name) :
                groupName;
        EventBus.getDefault().post(new LogEvent(Kirai.from("{group}")
                .put(HtmlPiece.put("group", "> " + groupName)
                        .bold().color(LogUtil.GREEN))
                .format(formatter)));
        String[] commands = context.getResources().getStringArray(array);
        if (commands.length % 4 != 0) {
            LogUtil.w(UtilsHandler.class.getClass(), "Error parsing group commands");
            return;
        }
        for (int i = 0; i < commands.length; i += 4) {
            EventBus.getDefault().post(new LogEvent(Kirai
                    .from("~$ {cmd_with_params} {translation} {description}")
                    .put(HtmlPiece.put("cmd_with_params", (commands[i] + " " + commands[i + 1]).trim())
                            .bold().color(LogUtil.BLUE))
                    .put(HtmlPiece.put("translation", "(" + commands[i + 2] + ")"))
                    .put(HtmlPiece.put("description", commands[i + 3]))
                    .format(formatter)));
        }
    }

    private static void printConstants(Context context, @ArrayRes int array, Formatter formatter) {
        String constantsName = array == R.array.api_keycodes ?
                context.getString(R.string.api_keycodes_name) : null;
        constantsName = array == R.array.api_records ? context.getString(R.string.api_records_name) :
                constantsName;
        constantsName = array == R.array.api_modes ? context.getString(R.string.api_modes_name) :
                constantsName;
        EventBus.getDefault().post(new LogEvent(Kirai.from("{constants}")
                .put(HtmlPiece.put("constants", "> " + constantsName)
                        .bold().color(LogUtil.GREEN))
                .format(formatter)));
        String[] constants = context.getResources().getStringArray(array);
        if (constants.length % 2 != 0) {
            LogUtil.w(UtilsHandler.class.getClass(), "Error parsing constants");
            return;
        }
        for (int i = 0; i < constants.length; i += 2) {
            EventBus.getDefault().post(new LogEvent(Kirai
                    .from("{number_or_abbreviation} {keycode}")
                    .put(HtmlPiece.put("number_or_abbreviation", constants[i]))
                    .put(HtmlPiece.put("keycode", constants[i + 1])
                            .bold().color(LogUtil.BLUE))
                    .format(formatter)));
        }
    }
}
