package com.googleplaybot.models;

import com.googleplaybot.api.API;
import com.googleplaybot.data.Db;
import com.googleplaybot.utils.ShellUtil;

import java.util.List;

public class Keycode {

	public long id;

	public int record;

	public int code;

	public String pckg;

	public String activity;

	public Keycode() {}

	public Keycode(int record, int code) {
		this.record = record;
		this.code = code;
		if (code != Db.CODE_NONE) {
			String[] currentWindow = ShellUtil.parsePackageWithActivity();
			this.pckg = currentWindow[0];
			this.activity = currentWindow[1];
		}
	}

    @Override
    public String toString() {
        return "Keycode{" +
                "id=" + id +
                ", record=" + record +
                ", code=" + code +
                ", pckg='" + pckg + '\'' +
                ", activity='" + activity + '\'' +
                '}';
    }

	public static String code2Char(int code) {
		String character;
		switch (code) {
			// Special keys
			case Db.CODE_GMAIL:
				character = API.KEY_GMAIL;
				break;
			case Db.CODE_PASSWORD:
				character = API.KEY_PASSWORD;
				break;
			// Common keys
			case Db.CODE_UP:
				character = API.KEY_UP;
				break;
			case Db.CODE_DOWN:
				character = API.KEY_DOWN;
				break;
			case Db.CODE_LEFT:
				character = API.KEY_LEFT;
				break;
			case Db.CODE_RIGHT:
				character = API.KEY_RIGHT;
				break;
			case Db.CODE_TAB:
				character = API.KEY_TAB;
				break;
			case Db.CODE_ENTER:
				character = API.KEY_ENTER;
				break;
			case Db.CODE_MENU:
				character = API.KEY_MENU;
				break;
			default:
				character = String.valueOf(code);
		}
		return character;
	}

	public static String record2String(int record) {
		switch (record) {
			case Db.RECORD_GOOGLE_PLAY_INSTALLATION:
				return "GOOGLE_PLAY_INSTALLATION";
			case Db.RECORD_GOOGLE_ACCOUNT_SIGN_IN:
				return "GOOGLE_ACCOUNT_SIGN_IN";
			default:
				return "UNKNOWN";
		}
	}

    public static String describeRecord(int record, List<Keycode> keycodes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(record2String(record));
        stringBuilder.append(": ");
        for (int i = 0; i < keycodes.size(); i++) {
            if (i != 0) {
                stringBuilder.append(" -> ");
            }
            stringBuilder.append(code2Char(keycodes.get(i).code));
        }
        return stringBuilder.toString();
    }
}