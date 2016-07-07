package com.autosos.rescue.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.model.Version;
import com.autosos.rescue.task.NewHttpPostTask;
import com.autosos.rescue.task.OnHttpRequestListener;
import com.autosos.rescue.util.CherkVersion;
import com.autosos.rescue.util.JSONUtil;
import com.autosos.rescue.util.Session;
import com.autosos.rescue.view.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForSetting extends Fragment implements View.OnClickListener {

    private static FragmentForSetting fragment = null;

    public static Fragment newInstance() {
        if (fragment == null) {
            synchronized (FragmentForSetting.class) {
                if (fragment == null) {
                    fragment = new FragmentForSetting();

                }
            }
        }
        return fragment;
    }


    public FragmentForSetting() {
        // Required empty public constructor
    }

    private View logout, checkVersion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_for_setting, null);
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(this);
        checkVersion = view.findViewById(R.id.checkVersion);
        checkVersion.setOnClickListener(this);
        return view;
    }

    String updateMessage = "";
    private Version version;
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.logout:
                new NewHttpPostTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(obj.toString());
                            int result = jsonObject.getInt("result");
                            if (result == 1) {

                                Session.getInstance().logout(getActivity().getApplicationContext());
                                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onRequestFailed(Object obj) {

                    }
                }
                ).execute(Constants.USER_LOGOUT_URL);
                break;
            case R.id.checkVersion:
                CherkVersion cherkVersion = new CherkVersion();
                updateMessage = cherkVersion.cherkVersion(getActivity().getApplicationContext());
                version = cherkVersion.getVersion();
                if(updateMessage.equals(CherkVersion.Must_Update) || updateMessage.equals(CherkVersion.Can_Update)){
                    showDialog("Install");
                }else {

                    showDialog("NoUpdate");
                }
                break;
            default:
                break;
        }
    }

   private Dialog dialog;
    private void showDialog(String type){
        String msg =null;
        View v2 = getActivity().getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        switch (type){
            case "NoUpdate":
                msg = "为最新版本,版本号"+Constants.APP_VERSION;
                tvMsg.setText(msg);
                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
                break;
            case "Install":
                msg = "有新的版本,请重启APP后更新";
                tvMsg.setText(msg);
                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        getActivity().finish();

                    }
                });
                break;
        }

        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(getActivity());
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
}
