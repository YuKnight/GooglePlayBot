package com.googleplaybot.remote.server;

import android.content.Context;

import com.googleplaybot.data.Prefs;
import com.googleplaybot.models.remote.Download;
import com.googleplaybot.utils.LogUtil;
import com.googleplaybot.utils.DeviceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JSONRequestBuilder extends JSONObject {

    private static final String LIMIT = " LIMIT (SELECT COUNT(*) FROM devices)";

    private static final String ORDER_BY_ACCOUNTS = " ORDER BY successes ASC";

    private static final String SELECT_DEVICE_ID = " (SELECT id FROM devices WHERE name = :device_name)";

    private String deviceName;

    private JSONArray requests;

    public JSONRequestBuilder() {}

    public JSONRequestBuilder(Context context) {
        Prefs prefs = new Prefs(context);
        deviceName = prefs.getDeviceName();
        JSONRequestBuilder device = new JSONRequestBuilder();
        device.add("name", deviceName);
        JSONRequestBuilder deviceInfo = new JSONRequestBuilder();
        deviceInfo.add("battery", DeviceUtil.getBatteryLevel(context));
        deviceInfo.add("ram", 0);
        device.add("info", deviceInfo);
        add("device", device);
        requests = new JSONArray();
    }

    public JSONRequestBuilder selectTasks() {
        JSONRequestBuilder request = new JSONRequestBuilder();
        request.add("query", "SELECT * FROM tasks WHERE successes < downloads AND failures = 0" +
                " AND suspend = 0 ORDER BY time ASC" + LIMIT);
        requests.put(request);
        return this;
    }

    public JSONRequestBuilder selectActiveTask() {
        JSONRequestBuilder request = new JSONRequestBuilder();
        request.add("query", "SELECT * FROM downloads_executable WHERE device =" + SELECT_DEVICE_ID +
                " ORDER BY time ASC LIMIT 1");
        JSONRequestBuilder params = new JSONRequestBuilder();
        params.add(":device_name", deviceName);
        request.add("params", params);
        requests.put(request);
        return this;
    }

    public JSONRequestBuilder selectFromAllAccounts() {
        JSONRequestBuilder request = new JSONRequestBuilder();
        request.add("query", "SELECT * FROM accounts WHERE failures = 0" + ORDER_BY_ACCOUNTS + LIMIT);
        requests.put(request);
        return this;
    }

    public JSONRequestBuilder selectFromUnusedAccounts() {
        JSONRequestBuilder request = new JSONRequestBuilder();
        request.add("query", "SELECT * FROM accounts WHERE failures = 0 AND id NOT IN " +
                "INNER JOIN downloads_executable ON Orders.CustomerID=Customers.CustomerID" + ORDER_BY_ACCOUNTS + LIMIT);
        requests.put(request);
        return this;
    }

    /*public JSONRequestBuilder selectDownloadAccounts(String table, long task) {
        JSONRequestBuilder request = new JSONRequestBuilder();
        request.add("query", "SELECT account FROM " + table + " WHERE task = :task");
        JSONRequestBuilder params = new JSONRequestBuilder();
        params.add(":task", task);
        request.add("params", params);
        requests.put(request);
        return this;
    }*/

    public JSONRequestBuilder tryInsertExecutableDownload(Download analyzedDownload) {
        JSONRequestBuilder request = new JSONRequestBuilder();
        request.add("query", "INSERT INTO downloads_executable (device, account, task, filename)" +
                " SELECT" + SELECT_DEVICE_ID + ", :account, :task, :filename" +
                " WHERE (SELECT COUNT(*) FROM downloads_executable WHERE task = :task) +" +
                " (SELECT successes FROM tasks WHERE id = :task) <" +
                " (SELECT downloads FROM tasks WHERE id = :task)");
        JSONRequestBuilder params = getInsertDownloadParams(analyzedDownload);
        params.add(":device_name", deviceName);
        params.add(":filename", analyzedDownload.filename);
        request.add("params", params);
        requests.put(request);
        return this;
    }

    public JSONRequestBuilder insertSuccessfulDownload(Download executableDownload) {
        JSONRequestBuilder request = new JSONRequestBuilder();
        request.add("query", "INSERT INTO downloads_successful (device, account, task) " +
                "VALUES (:device, :account, :task)");
        JSONRequestBuilder params = getInsertDownloadParams(executableDownload);
        params.add(":device", executableDownload.device);
        request.add("params", params);
        requests.put(request);
        plusOne("tasks", true, executableDownload.task);
        plusOne("accounts", true, executableDownload.account);
        deleteExecutableDownload(executableDownload.id);
        return this;
    }

    public JSONRequestBuilder insertFailedDownload(Download executableDownload, boolean signInFailed,
                                                   boolean downloadFailed) {
        JSONRequestBuilder request = new JSONRequestBuilder();
        request.add("query", "INSERT INTO downloads_failed (device, account, task, filename) " +
                "VALUES (:device, :account, :task, :filename)");
        JSONRequestBuilder params = getInsertDownloadParams(executableDownload);
        params.add(":device", executableDownload.device);
        params.add(":filename", executableDownload.filename);
        request.add("params", params);
        requests.put(request);
        if (signInFailed) {
            plusOne("accounts", false, executableDownload.account);
        }
        if (downloadFailed) {
            plusOne("tasks", false, executableDownload.task);
        }
        deleteExecutableDownload(executableDownload.id);
        return this;
    }

    private void plusOne(String table, boolean success, long id) {
        String setQuery = success ? " SET successes = successes + 1" : " SET failures = failures + 1";
        JSONRequestBuilder request = new JSONRequestBuilder();
        request.add("query", "UPDATE " + table + setQuery + " WHERE id = :id");
        JSONRequestBuilder params = new JSONRequestBuilder();
        params.add(":id", id);
        request.add("params", params);
        requests.put(request);
    }

    private void deleteExecutableDownload(long id) {
        JSONRequestBuilder request = new JSONRequestBuilder();
        request.add("query", "DELETE FROM downloads_executable WHERE id = :id");
        JSONRequestBuilder params = new JSONRequestBuilder();
        params.add(":id", id);
        request.add("params", params);
        requests.put(request);
    }

    private void add(String name, Object value) {
        try {
            if (has(name)) {
                remove(name);
            }
            put(name, value);
        } catch (JSONException e) {
            LogUtil.e(getClass(), e.getMessage());
        }
    }

    private JSONRequestBuilder getInsertDownloadParams(Download download) {
        JSONRequestBuilder params = new JSONRequestBuilder();
        params.add(":account", download.account);
        params.add(":task", download.task);
        return params;
    }

    public JSONRequestBuilder clear() {
        Iterator<String> keys = keys();
        while (keys.hasNext()) {
            remove(keys.next());
        }
        return this;
    }

    public String build() {
        add("requests", requests);
        return toString();
    }
}