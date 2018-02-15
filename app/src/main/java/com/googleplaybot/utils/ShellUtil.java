package com.googleplaybot.utils;

public final class ShellUtil {

    @SuppressWarnings("ConstantConditions")
    public static String[] parsePackageWithActivity() {
        boolean specialCurrent = false;
        String dumpsys = APIUtil.output(APIUtil.exec("dumpsys window windows | " +
                "grep -E 'mCurrentFocus'"));
        if (!dumpsys.contains("/")) {
            specialCurrent = true;
            dumpsys = APIUtil.output(APIUtil.exec("dumpsys window windows | " +
                    "grep -E 'mObsuringWindow'"));
        }
        String[] firstParts = dumpsys.trim().split("/");
        return new String[] {
                firstParts[0].substring(firstParts[0].lastIndexOf(" ")).trim(),
                specialCurrent ? "window" : firstParts[1].replace("}", "")
        };
    }

    @SuppressWarnings("ConstantConditions")
    public static long getUidByPackage(String pckg) {
        try {
            String[] lines = APIUtil.output(APIUtil.exec("dumpsys package " + pckg +
                    " | grep -E 'userId'")).split(System.getProperty("line.separator"));
            return Long.parseLong(lines[0].replace("userId=", "").trim());
        } catch (NumberFormatException e) {
            LogUtil.e(ShellUtil.class.getClass(), e.getMessage());
            return -1;
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static long getTCPBytes(long uid, boolean receive) {
        try {
            return Long.parseLong(APIUtil.output(APIUtil.exec("cat proc/uid_stat/" + uid + "/"
                    + (receive ? "tcp_rcv" : "tcp_snd"))).trim());
        } catch (NumberFormatException e) {
            LogUtil.e(ShellUtil.class.getClass(), e.getMessage());
            return -1;
        }
    }
}
