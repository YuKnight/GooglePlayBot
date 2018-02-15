package com.googleplaybot.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.googleplaybot.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_PATH_SUFFIX = "/databases/";

    public DbOpenHelper(Context context) {
        super(context, Db.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void openDatabase(Context context) {
        File file = context.getDatabasePath(Db.DATABASE_NAME);
        if (!file.exists()) {
            try {
                copyDatabaseFromAssets(context);
            } catch (IOException e) {
                LogUtil.e(getClass(), e.getMessage());
            }
        }
    }

    private void copyDatabaseFromAssets(Context context) throws IOException {
        InputStream input = context.getAssets().open(Db.DATABASE_NAME);
        String outFilename = getDatabasePath(context);
        File file = new File(context.getApplicationInfo().dataDir + DATABASE_PATH_SUFFIX);
        boolean made;
        if (!file.exists()) {
            made = file.mkdir();
            if (!made) {
                LogUtil.w(getClass(), "Cannot create database directory...");
                return;
            }
        }
        OutputStream output = new FileOutputStream(outFilename);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        output.flush();
        output.close();
        input.close();
    }

    private String getDatabasePath(Context context) {
        return context.getApplicationInfo().dataDir + DATABASE_PATH_SUFFIX + Db.DATABASE_NAME;
    }
}