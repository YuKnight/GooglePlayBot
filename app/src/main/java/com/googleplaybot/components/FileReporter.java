package com.googleplaybot.components;

import android.content.Context;
import android.support.annotation.NonNull;

import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

public class FileReporter implements ReportSender {

    @Override
    public void send(@NonNull Context context, @NonNull CrashReportData report) throws ReportSenderException {

    }
}