package com.googleplaybot.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.googleplaybot.R;
import com.googleplaybot.api.APIHandler;
import com.googleplaybot.data.Prefs;
import com.googleplaybot.events.ui.LogEvent;
import com.googleplaybot.events.ui.KeyboardEvent;
import com.googleplaybot.events.ui.ToggleEvent;
import com.googleplaybot.models.Command;
import com.googleplaybot.services.base.ServiceBase;
import com.googleplaybot.utils.APIUtil;
import com.googleplaybot.utils.LogUtil;
import com.googleplaybot.ui.views.KeyboardObserver;
import com.googleplaybot.ui.views.ProgrammableSwitchCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ActivityConsole extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener, TextView.OnEditorActionListener {

    public static final String EXTRA_RUN = "run";

    @BindView(R.id.mainLayout)
    ViewGroup mainLayout;
    @BindView(R.id.console)
    TextView console;
    @BindView(R.id.input)
    EditText input;

    private MenuItem controller;

    private KeyboardObserver keyboardObserver;
    private int maxRootViewHeight = 0;
    private int currentRootViewHeight;

    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.activity_main_title);
        ButterKnife.bind(this);
        prefs = new Prefs(getApplicationContext());
        mainLayout.requestFocus();
        keyboardObserver = new KeyboardObserver();
        mainLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(keyboardObserver);
        console.setMovementMethod(new ScrollingMovementMethod());
        console.setText(System.getProperty("line.separator"));
        input.setOnEditorActionListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        prefs.putBoolean(Prefs.CONSOLE_IS_ACTIVE, true);
        EventBus.getDefault().register(this);
        if (getIntent() != null && getIntent().hasExtra(EXTRA_RUN)) {
            toggleController(true);
            toggleService(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onLogEvent(LogEvent event) {
        if (event.log == null) {
            return;
        }
        if (LogUtil.isDuplicatedLog(event.log, console.getText())) {
            return;
        }
        boolean beforeCanScrollBottom = console.canScrollVertically(1);
        console.append(event.log);
        console.append(System.getProperty("line.separator"));
        while (currentRootViewHeight >= maxRootViewHeight &&
                !beforeCanScrollBottom && console.canScrollVertically(1)) {
            console.scrollBy(0, 10);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    @SuppressWarnings("unused")
    public void onKeyboardEvent(KeyboardEvent event) {
        currentRootViewHeight = console.getHeight();
        if (currentRootViewHeight > maxRootViewHeight) {
            maxRootViewHeight = currentRootViewHeight;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onToggleEvent(ToggleEvent event) {
        toggleController(ServiceBase.isRunning(getApplicationContext()));
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        prefs.putBoolean(Prefs.CONSOLE_IS_ACTIVE, false);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainLayout.getViewTreeObserver()
                .removeOnGlobalLayoutListener(keyboardObserver);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
        if (((ProgrammableSwitchCompat) compoundButton).isCheckedProgrammatically) {
            return;
        }
        toggleController(false);
        if (!on) {
            toggleService(false);
            return;
        }
        LogUtil.d(getClass(), "Requesting root permissions");
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return APIUtil.wait(APIUtil.exec()) == APIUtil.EXEC_SUCCESS;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) {
                        if (!success) {
                            LogUtil.w(getClass(), "Root permissions denied");
                            return;
                        }
                        LogUtil.d(getClass(), "Root permissions submitted");
                        toggleController(true);
                        toggleService(true);
                    }
                });
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        APIHandler.run(new Command(prefs.getDeviceName(), input.getText()
                .toString().trim()), getApplicationContext());
        input.setText("");
        return false;
    }

    private void toggleController(boolean on) {
        if (controller != null) {
            ((ProgrammableSwitchCompat) controller.getActionView())
                    .setCheckedProgrammatically(on);
        }
    }

    private void toggleService(boolean on) {
        boolean isServiceRunning = ServiceBase.isRunning(getApplicationContext());
        Intent serviceStartIntent = ServiceBase.getIntent(getApplicationContext());
        if (!on && isServiceRunning) {
            stopService(serviceStartIntent);
        } else if (on && !isServiceRunning) {
            startService(serviceStartIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        ProgrammableSwitchCompat programmableSwitchCompat =
                new ProgrammableSwitchCompat(new ContextThemeWrapper(getApplicationContext(),
                        R.style.AppTheme));
        programmableSwitchCompat.setTextOn(getString(R.string.app_name));
        programmableSwitchCompat.setTextOff(getString(R.string.app_name));
        programmableSwitchCompat.setOnCheckedChangeListener(this);
        controller = menu.findItem(R.id.controller);
        controller.setActionView(programmableSwitchCompat);
        toggleController(ServiceBase.isRunning(getApplicationContext()));
        return true;
    }
}
