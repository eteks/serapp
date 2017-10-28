package com.example.user.mahindra;

/**
 * Created by ets-prabu on 25/10/17.
 */

public class UsersAuth {
    @com.google.gson.annotations.SerializedName("ID")
    public String user_id;
    @com.google.gson.annotations.SerializedName("username")
    public String username;
    @com.google.gson.annotations.SerializedName("user_password")
    public String password;
    @com.google.gson.annotations.SerializedName("usertype")
    public String usertype;

    public UsersAuth(String username, String usertype, String password) {
        this.setUsername(username);
        this.setUsertype(usertype);
        this.setPassword(password);
    }

    public UsersAuth() {

    }

    @Override
    public String toString() {
        return getText();
    }

    public String getText() {
        return password;
    }

    public final void setUsername(String text) {
        username = text;
    }

    public final void setUsertype(String text) {
        usertype = text;
    }
    public final void setPassword(String text) {
        password = text;
    }
}

