package com.googleplaybot.api;

public interface API {

    // Special keys
    String KEY_GMAIL = "g";
    String KEY_PASSWORD = "p";
    // Common keys
    String KEY_UP = "u";
    String KEY_DOWN = "d";
    String KEY_LEFT = "l";
    String KEY_RIGHT = "r";
    String KEY_TAB = "t";
    String KEY_ENTER = "e";
    String KEY_MENU = "m";

    // Utils
    String PRINT_HELP = "help";

    // Device tools
    String SET_DEVICE_NAME = "setname";
    String PRINT_DEVICE_NAME = "name";
    String SET_DEVICE_MODE = "setmode";
    String PRINT_DEVICE_MODE = "mode";
    String SET_TELEGRAM_BOT_TOKEN = "settoken";
    String PRINT_TELEGRAM_BOT_TOKEN = "token";

    // Play tools
    String GOOGLE_SIGN_IN = "login";
    String SIGN_OUT = "logout";
    String INSTALL_APPLICATION = "install";
    String UNINSTALL_APPLICATION = "uninstall";

    // Record tools
    String PRESS_KEYCODE = "press";
    String INSERT_KEYCODE = "insert";
    String DELETE_KEYCODE = "delete";
    String PRINT_RECORD = "record";
    String CLEAR_RECORD = "clear";
}
