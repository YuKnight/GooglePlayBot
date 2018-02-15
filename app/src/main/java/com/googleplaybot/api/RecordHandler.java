package com.googleplaybot.api;

import com.googleplaybot.data.Db;
import com.googleplaybot.events.services.RecordEvent;
import com.googleplaybot.models.Command;
import com.googleplaybot.models.Keycode;
import com.googleplaybot.utils.APIUtil;
import com.googleplaybot.utils.LogUtil;
import com.googleplaybot.utils.EventUtil;

class RecordHandler extends APIHandler {

    @SuppressWarnings("ConstantConditions")
    public static boolean run(Command command) {
        int record = Db.RECORD_NONE;
        try {
            if (!command.getAPI().equals(API.PRESS_KEYCODE)
                    && command.getFirstParameter() != null) {
                record = Integer.parseInt(command.getFirstParameter());
            }
        } catch (NumberFormatException e) {
            LogUtil.e(RecordHandler.class.getClass(), e.getMessage());
        }
        switch (command.getAPI()) {
            case API.PRESS_KEYCODE:
                if (command.getFirstParameter() != null) {
                    int keycode = parseKeycode(command.getFirstParameter());
                    if (keycode == Db.CODE_NONE) {
                        onIncorrectParameters(RecordHandler.class.getClass());
                        return true;
                    } else if (keycode < Db.CODE_NONE) {
                        if (command.getSecondParameter() == null) {
                            onIncorrectParameters(RecordHandler.class.getClass());
                            return true;
                        }
                        APIUtil.exec("input text " + command.getSecondParameter());
                    } else {
                        APIUtil.exec("input keyevent " + keycode);
                    }
                } else {
                    onMissedParameters(RecordHandler.class.getClass());
                }
                return true;
            case API.INSERT_KEYCODE:
                if (command.getFirstParameter() != null && command.getSecondParameter() != null) {
                    int keycode = parseKeycode(command.getSecondParameter());
                    if (keycode == Db.CODE_NONE) {
                        onIncorrectParameters(RecordHandler.class.getClass());
                        return true;
                    }
                    if (record > Db.RECORD_NONE) {
                        EventUtil.post(new RecordEvent(RecordEvent.TYPE_INSERT_KEYCODE,
                                new Keycode(record, keycode)));
                    } else {
                        onIncorrectParameters(RecordHandler.class.getClass());
                    }
                } else {
                    onMissedParameters(RecordHandler.class.getClass());
                }
                return true;
            case API.DELETE_KEYCODE:
                if (command.getFirstParameter() != null) {
                    if (record > Db.RECORD_NONE) {
                        EventUtil.post(new RecordEvent(RecordEvent.TYPE_DELETE_KEYCODE,
                                new Keycode(record, Db.CODE_NONE)));
                    } else {
                        onIncorrectParameters(RecordHandler.class.getClass());
                    }
                } else {
                    onMissedParameters(RecordHandler.class.getClass());
                }
                return true;
            case API.PRINT_RECORD:
                if (command.getFirstParameter() != null) {
                    if (record > Db.RECORD_NONE) {
                        EventUtil.post(new RecordEvent(RecordEvent.TYPE_PRINT,
                                new Keycode(record, Db.CODE_NONE)));
                    } else {
                        onIncorrectParameters(RecordHandler.class.getClass());
                    }
                } else {
                    onMissedParameters(RecordHandler.class.getClass());
                }
                return true;
            case API.CLEAR_RECORD:
                if (command.getFirstParameter() != null) {
                    if (record > Db.RECORD_NONE) {
                        EventUtil.post(new RecordEvent(RecordEvent.TYPE_CLEAR,
                                new Keycode(record, Db.CODE_NONE)));
                    } else {
                        onIncorrectParameters(RecordHandler.class.getClass());
                    }
                } else {
                    onMissedParameters(RecordHandler.class.getClass());
                }
                return true;
            default:
                break;
        }
        return false;
    }

    private static int parseKeycode(String parameter) {
        int keycode = Db.CODE_NONE;
        try {
            keycode = Integer.parseInt(parameter);
        } catch (NumberFormatException e) {
            switch (parameter.trim().toLowerCase()) {
                // Special keys
                case API.KEY_GMAIL:
                    keycode = Db.CODE_GMAIL;
                    break;
                case API.KEY_PASSWORD:
                    keycode = Db.CODE_PASSWORD;
                    break;
                // Common keys
                case API.KEY_UP:
                    keycode = Db.CODE_UP;
                    break;
                case API.KEY_DOWN:
                    keycode = Db.CODE_DOWN;
                    break;
                case API.KEY_LEFT:
                    keycode = Db.CODE_LEFT;
                    break;
                case API.KEY_RIGHT:
                    keycode = Db.CODE_RIGHT;
                    break;
                case API.KEY_TAB:
                    keycode = Db.CODE_TAB;
                    break;
                case API.KEY_ENTER:
                    keycode = Db.CODE_ENTER;
                    break;
                case API.KEY_MENU:
                    keycode = Db.CODE_MENU;
                    break;
                default:
                    break;
            }
        }
        return keycode;
    }
}
