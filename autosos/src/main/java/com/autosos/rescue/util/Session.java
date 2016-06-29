package com.autosos.rescue.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


import com.autosos.rescue.Constants;
import com.autosos.rescue.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class Session {

    private User currentUser;
    private Time time;
    private Session() {

    }

    public static Session getInstance() {
        return SessionHolder.INSTANCE;
    }

    private static class SessionHolder {
        private static final Session INSTANCE = new Session();
}

    public User getCurrentUser(Context context) {
        if (currentUser == null) {
            try {
                InputStream in = context.openFileInput(Constants.USER_FILE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }
                currentUser = new User(new JSONObject(stream.toString()));
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e) {
                return null;
            } catch (JSONException e) {
                return null;
            }
        }
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setCurrentUser(Context context, JSONObject user) {
        if (context == null) {
            return;
        }
        try {
            User u = new User(user);
            if (this.currentUser != null) {
                u.setToken(this.currentUser.getToken());
                user.put("token", this.getCurrentUser(context).getToken());
                Log.e("SESSION",u.getExpire()+"");
            }
            this.currentUser = u;

            SharedPreferences sharedPreferences =
                    context.getSharedPreferences("time_token", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("time", u.getExpire());
            editor.commit();

            FileOutputStream fileOutputStream = context.openFileOutput(
                    Constants.USER_FILE, Context.MODE_PRIVATE);
            if (fileOutputStream != null) {
                OutputStreamWriter out = new OutputStreamWriter(
                        fileOutputStream);
                out.write(user.toString());
                out.flush();
                out.close();
                fileOutputStream.close();
            }
            getCurrentUser(context);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void logout(Context context) {
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeAllCookie();
        context.deleteFile(Constants.USER_FILE);
       // context.deleteFile(Constants.LOCATION_FILE);
        this.currentUser = null;
        Log.e("Session","delete");
    }
    public boolean cherkSession(Context context){
        long now_time = System.currentTimeMillis() / 1000;
        long end_time = context.getSharedPreferences("time_token", Context.MODE_PRIVATE).getLong("time", 0);
       // Log.e("now_TIME:", now_time + "        end_time:" + end_time);
        if(end_time < now_time + 2){
            return false;
        }
        return true;
    }
    public String getNewToken(Context context){
        User u = getCurrentUser(context);
        String last_token = u.getToken();
        String new_token ="connect net_work";
        setCurrentUser(context,new JSONObject());
        new_token = getCurrentUser(context).getToken();
        return new_token ;
    }
}
