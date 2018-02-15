package com.googleplaybot.utils;

import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public final class APIUtil {

    public static final int EXEC_FAILED = -1;
    public static final int EXEC_SUCCESS = 0;

    @Nullable
    public static Process exec(String... commands) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                outputStream.writeBytes(command + System.getProperty("line.separator"));
            }
            outputStream.writeBytes("exit" + System.getProperty("line.separator"));
            outputStream.flush();
        } catch (IOException e) {
            LogUtil.e(APIUtil.class.getClass(), e.getMessage());
        }
        return process;
    }

    @Nullable
    public static String output(@Nullable Process process) {
        if (process == null) {
            LogUtil.w(APIUtil.class.getClass(), "Process for output is null");
            return null;
        }
        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder logcat = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logcat.append(line);
                logcat.append(System.getProperty("line.separator"));
            }
            return logcat.toString();
        } catch (IOException e) {
            LogUtil.e(APIUtil.class.getClass(), e.getMessage());
        }
        return null;
    }

    public static int wait(@Nullable Process process) {
        try {
            if (process != null) {
                if (process.waitFor() == 0) {
                    return EXEC_SUCCESS;
                }
            } else {
                LogUtil.w(APIUtil.class.getClass(), "Process to wait is null");
            }
        } catch (InterruptedException e) {
            LogUtil.e(APIUtil.class.getClass(), e.getMessage());
        }
        return EXEC_FAILED;
    }

    public static void stop(@Nullable Process process) {
        if (process != null) {
            process.destroy();
        } else {
            LogUtil.w(APIUtil.class.getClass(), "Process to stop is null");
        }
    }
}
