package com.googleplaybot.models.remote;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleAccount {

    @SerializedName("id")
    public long id;
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;

    public GoogleAccount() {}

    public static String bindArray(List<GoogleAccount> accounts) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" (");
        for (int i = 0; i < accounts.size(); i++) {
            if (i != 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(accounts.get(i).id);
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}