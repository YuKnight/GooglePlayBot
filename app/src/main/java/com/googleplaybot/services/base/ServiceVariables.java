package com.googleplaybot.services.base;

import com.googleplaybot.data.Db;
import com.googleplaybot.data.Prefs;
import com.googleplaybot.models.Keycode;
import com.googleplaybot.models.TCP;
import com.googleplaybot.utils.APIUtil;
import com.googleplaybot.utils.DeviceUtil;
import com.googleplaybot.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public abstract class ServiceVariables extends ServiceBase {

	public boolean isWorking = false;
    public boolean isPlaying = false;

    private static final int MIN_ENOUGH_BATTERY_LEVEL = 20;
    public boolean canWork = false;
    public boolean canPlay = false;
    protected boolean hasInternetConnection = false;
    protected boolean hasEnoughBatteryLevel = false;
	protected boolean hasScreenTurnedOn = false;

	public int record = Db.RECORD_NONE;

	private static final int REPEAT_RECORD_MAX_COUNT = 1;
	public int repeatRecordCount = 0;

	private static final int WAIT_RECORD_EVENT_SECONDS = 5;
	public int waitRecordEventSeconds = 0;

	public ArrayList<Keycode> keycodes = new ArrayList<>();
	public int keycodeIndex = 0;

	private static final int REPEAT_KEYCODE_MAX_COUNT = 10;
	public int repeatKeycodeCount = 0;

	private static final int WAIT_TCP_CONSTANT_SECONDS = 2;
	public HashMap<String, TCP> packagesTCPBytes = new HashMap<>();
	public int waitTCPConstantSeconds = 0;

    protected static final int LOG_DURATION = 3500;
    protected ArrayList<CharSequence> logText = new ArrayList<>();
    protected long logCreateTime = System.currentTimeMillis();

    @Override
    public void onCreate() {
        super.onCreate();
        checkCanWorkAndPlay(true);
	}

    public void checkCanWorkAndPlay(boolean logExtra) {
        if (!hasInternetConnection) {
            hasInternetConnection = DeviceUtil.hasInternetConnection(getApplicationContext());
            if (!hasInternetConnection) {
                LogUtil.w(getClass(), "No internet connection");
            } else {
                LogUtil.d(getClass(), "Has internet connection");
            }
        }
        if (!hasEnoughBatteryLevel) {
            hasEnoughBatteryLevel = DeviceUtil.getBatteryLevel(getApplicationContext())
                    >= MIN_ENOUGH_BATTERY_LEVEL;
            if (!hasEnoughBatteryLevel) {
                LogUtil.w(getClass(), "Not enough battery level");
            } else if (logExtra) {
                LogUtil.d(getClass(), "Has enough battery level");
            } else {
                Timber.d("Has enough battery level");
            }
        }
        if (!hasScreenTurnedOn) {
            hasScreenTurnedOn = DeviceUtil.isScreenTurnedOn(getApplicationContext());
            if (!hasScreenTurnedOn) {
                LogUtil.w(getClass(), "Screen is turned off");
            } else {
                LogUtil.d(getClass(), "Screen is turned on");
            }
        }
        if (hasInternetConnection && hasEnoughBatteryLevel) {
            if (hasScreenTurnedOn) {
                if (!canPlay && isPlaying) {
                    onTicTac();
                }
                canPlay = true;
            } else {
                canPlay = false;
                APIUtil.exec("input keyevent " + Db.CODE_POWER);
            }
            if (!canWork && isWorking) {
                onTicTac();
            }
            canWork = true;
        } else {
            canPlay = canWork = false;
        }
        logCanWorkAndPlay();
    }

    private void logCanWorkAndPlay() {
        boolean consoleIsActive = prefs.getBoolean(Prefs.CONSOLE_IS_ACTIVE);
        String log;
        if (canWork) {
            if (canPlay) {
                log = "Service can work and play";
            } else {
                log = "Service can work but can't play";
            }
        } else {
            if (canPlay) {
                log = "Service can play but can't work";
            } else {
                log = "Service can't work and play";
            }
        }
        if (consoleIsActive) {
            Timber.d(log);
        } else {
            LogUtil.d(getClass(), log);
        }
    }
}