package com.autosos.yd.task;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import com.autosos.yd.view.MainActivity;

public class AuthGetJSONObjectAsyncTask extends AsyncTask<String, Integer, JSONObject> {

    private Context context;

    public AuthGetJSONObjectAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        // 在这里判断是否401
        /*if (jsonObject != null && !jsonObject.isNull("status") &&
                JSONUtil.getString(jsonObject, "status").startsWith("401")) {
            Log.e("AuthAsyncTask", "Result of this auth task: " + jsonObject.toString());
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("auth_logout", true);
            context.startActivity(intent);
        }*/
        super.onPostExecute(jsonObject);
    }
}
