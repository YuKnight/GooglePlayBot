package com.googleplaybot.listeners;

import android.accounts.Account;
import android.accounts.OnAccountsUpdateListener;

import com.googleplaybot.events.listeners.AddedAccountEvent;
import com.googleplaybot.utils.EventUtil;

import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;

public class AccountListener implements OnAccountsUpdateListener {

    public ArrayList<Account> accounts;

    public AccountListener() {
        accounts = new ArrayList<>();
    }

    @Override
    public void onAccountsUpdated(Account[] accountsArray) {
        boolean sendEvent = false;
        if (accounts.isEmpty()) {
            sendEvent = accountsArray.length > 0;
        } else {
            for (Account account : accountsArray) {
                if (sendEvent) {
                    break;
                }
                for (int i = 0; i < accounts.size(); i++) {
                    if (!accounts.get(i).name.equals(account.name)) {
                        sendEvent = true;
                        break;
                    }
                }
            }
        }
        accounts.clear();
        accounts.addAll(Arrays.asList(accountsArray));
        if (sendEvent) {
            EventUtil.post(new AddedAccountEvent());
        }
    }
}