package com.autosos.yd.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.Record;
import com.autosos.yd.task.NewHttpPostTask;
import com.autosos.yd.task.OnHttpRequestListener;
import com.autosos.yd.util.DownLoad;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.UpdateStateServe;
import com.autosos.yd.util.XmlParse;
import com.autosos.yd.view.LoginActivity;
import com.autosos.yd.view.NoticeView;
import com.autosos.yd.widget.CherkInternet;
import com.autosos.yd.widget.CherkVersion;
import com.autosos.yd.widget.DialogView;

public class MoreFragment extends Fragment implements View.OnClickListener {
    private static com.autosos.yd.model.Version version;
    private static final String TAG = "MoreFragment";
    private View rootView;
    private Dialog dialog;
    private SharedPreferences sharedPreferences;
    private RelativeLayout aboutEView;
    private RelativeLayout noticeView;
    private RelativeLayout cherkView;
    private RelativeLayout handbookView;
    private RelativeLayout version_View;
    private int now_verCode;
    private ProgressBar progressBarView;
    private File APKFile;
    private TextView progress_textView;
    private RelativeLayout QR_codeView;
    public static com.autosos.yd.fragment.MoreFragment newInstance() {
        return new com.autosos.yd.fragment.MoreFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_more, container, false);
        TextView versionView = (TextView) rootView.findViewById(R.id.more_version);
        //testView = (RelativeLayout) rootView.findViewById(R.id.test);
        noticeView = (RelativeLayout) rootView.findViewById(R.id.notice);
        cherkView = (RelativeLayout) rootView.findViewById(R.id.cherk);
        version_View = (RelativeLayout) rootView.findViewById(R.id.version);
        handbookView = (RelativeLayout) rootView.findViewById(R.id.handbook);
        progressBarView =(ProgressBar)rootView.findViewById(R.id.progressBar_download);
        progress_textView = (TextView)rootView.findViewById(R.id.progress_text);
        QR_codeView = (RelativeLayout)rootView.findViewById(R.id.QR_code);
//        QR_codeView.setVisibility(View.GONE);
        String versionNumber = null;
        try {
            versionNumber = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {

        }
        aboutEView = (RelativeLayout)rootView.findViewById(R.id.aboutE);
        aboutEView.setOnClickListener(this);
        versionView.setText(String.format(getString(R.string.label_version), versionNumber));
        Button callUsView = (Button) rootView.findViewById(R.id.callUs);
        Button logoutView = (Button) rootView.findViewById(R.id.logout);
        callUsView.setOnClickListener(this);
        logoutView.setOnClickListener(this);
        version_View.setOnClickListener(this);
        QR_codeView.setOnClickListener(this);
        handbookView.setOnClickListener(this);
        //testView.setOnClickListener(this);
        noticeView.setOnClickListener(this);
        if(!Constants.DEBUG){
            rootView.findViewById(R.id.onlytest).setVisibility(View.INVISIBLE);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.callUs:
                try {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:" + getString(R.string.label_support)));
                    startActivity(phoneIntent);
                } catch (Exception e) {
                }
                break;
            case R.id.logout:
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_msg_notice,
                        null);
                dialog = new Dialog(rootView.getContext(), R.style.bubble_dialog);
                Button tvConfirm = (Button) view.findViewById(R.id.btn_notice_confirm);
                Button tvCancel = (Button) view.findViewById(R.id.btn_notice_cancel);
                TextView tvMsg = (TextView) view.findViewById(R.id.tv_notice_msg);
                tvMsg.setText(R.string.msg_logout);

                tvConfirm.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        quitUserLocationInfo();
                        UpdateStateServe.UpdateStateServeActive = false;
                        if(UpdateStateServe.UpdateChangeTime == UpdateStateServe.UpdateNowStateTen)
                            UpdateStateServe.UpdateChangeTime = UpdateStateServe.UpdateChangeTimeSixty;
                        /* stop UpdateServe
                        Intent iService=new Intent(rootView.getContext(),UpdateStateServe.class);
                        iService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        rootView.getContext().stopService(iService);
*/
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.putExtra("logout", true);
                        Intent stopIntent = new Intent(rootView.getContext(), UpdateStateServe.class);
                        rootView.getContext().stopService(stopIntent);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.activity_anim_default);
                        getActivity().setResult(getActivity().RESULT_OK, intent);
                        getActivity().finish();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(false);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(rootView.getContext());
                params.width = Math.round(point.x * 5 / 7);
                window.setAttributes(params);
                dialog.show();
                break;
            case R.id.aboutE :
                showpop(rootView.getContext(),"aboutE");
                break;
            case R.id.QR_code:
                showpop(rootView.getContext(),"QR_code");
                break;
            case R.id.handbook:
                showDialog(2);
                break;
            case R.id.notice:
                Intent intent = new Intent(rootView.getContext(), NoticeView.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
            case R.id.version:
                if(!CherkInternet.cherkInternet(rootView.getContext())){
                    DialogView dialogView = new DialogView();
                    dialogView.dialogInternet(getActivity());
                }
                else {
                    cherkView.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CherkVersion cherkVersion = new CherkVersion();
                            String updateMessage = cherkVersion.cherkVersion(rootView.getContext());
                            version = cherkVersion.getVersion();
                            Message message = new Message();
                            if (updateMessage.equals(CherkVersion.Must_Update)) {
                                message.what = 1;
                            } else if (updateMessage.equals(CherkVersion.No_Update)) {
                                message.what = 0;
                            }
                            handler.sendMessage(message);
                        }
                    }).start();
                }
                /*CherkVersion cherkVersion = new CherkVersion();
                String update_msg = cherkVersion.cherkVersion(rootView.getContext());
                Log.e(TAG, "msg" + update_msg);
                version_View.setClickable(false);
                if(update_msg.equals(CherkVersion.Must_Update)){
                    DialogView dialogView =new DialogView();
                    dialogView.dialog(R.string.msg_update,R.string.label_confirm,getActivity());
                    */
               break;
        }
    }

    private void quitUserLocationInfo() {
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        String clientId = sharedPreferences.getString("clientid", null);
        if (clientId != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("getui_cid", clientId);
            map.put("lat",UpdateStateServe.latitude);
            map.put("lng",UpdateStateServe.longitude);
            //map.put("status", "4");
            new NewHttpPostTask(rootView.getContext(), new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    Log.e(TAG, obj.toString());
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            }).execute(Constants.USER_LOGOUT_URL, map);
        }
    }

    private void showpop(final Context context,String type){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopWindow =null;
        switch (type){
            case "aboutE":
                vPopWindow  = inflater.inflate(R.layout.about_e, null);
                TextView aboutE_versionView = (TextView)vPopWindow.findViewById(R.id.aboutE_version);
                aboutE_versionView.setText(String.format(rootView.getContext().getString(R.string.label_more_version), Constants.APP_VERSION));
                break;
            case "QR_code":
                vPopWindow  = inflater.inflate(R.layout.pop_qr_code, null);
                break;
        }

        final PopupWindow popWindow =new PopupWindow(vPopWindow,ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        Drawable drawable = rootView.getResources().getDrawable(R.color.color_white);
        popWindow.setBackgroundDrawable(drawable);
        popWindow.setFocusable(true);
        popWindow.setAnimationStyle(R.style.popwin_anim_style);
        popWindow.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
        RelativeLayout aboutE_returnView =(RelativeLayout) vPopWindow.findViewById(R.id.aboutE_return);
        /*aboutE_returnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popWindow.isShowing())
                    popWindow.dismiss();
            }
        });*/
        ((RelativeLayout) vPopWindow.findViewById(R.id.back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popWindow.isShowing())
                    popWindow.dismiss();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    cherkView.setVisibility(View.GONE);
                    showDialog("NoUpdate");
                    break;
                case 1:
                    cherkView.setVisibility(View.GONE);
                    showDialog(1);
                    break;
                case 2:
                    int progress = (int )msg.obj;
                    progressBarView.setProgress(progress);
                    progress_textView.setText("" + progress + "%");
                    //Log.e(TAG, "progress" + "%");
                    if(progress>98) {
                        progressBarView.setVisibility(View.GONE);
                        progress_textView.setVisibility(View.GONE);
                    }
                    break;
                case 3:
                    Log.e(TAG, "progress" + "ok");
                    showDialog("Instal");
                    break;
            }
        }
    };
    private void installApk() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(APKFile), "application/vnd.android.package-archive");
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    private void showDialog(int type){
        String msg =null;
        View v2 = getActivity().getLayoutInflater().inflate(R.layout.dialog_msg_notice,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        Button tvCancel = (Button) v2.findViewById(R.id.btn_notice_cancel);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        if(type == 1) {
            if(Constants.DEBUG && true){
                v2 = getActivity().getLayoutInflater().inflate(R.layout.dialog_msg_update,
                        null);
                dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
                 tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
                 tvCancel = (Button) v2.findViewById(R.id.btn_notice_cancel);
                 tvMsg = (TextView) v2.findViewById(R.id.update_msg);
                 tvMsg.setText(version.getUpdate_data());
                (v2.findViewById(R.id.update_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        version_View.setClickable(true);
                    }
                });
            }
           else {
                msg = String.format(getString(R.string.msg_update));
                tvMsg.setText(msg);
            }
            tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    version_View.setClickable(true);
                    progress_textView.setVisibility(View.VISIBLE);
                    progressBarView.setVisibility(View.VISIBLE);
                    download();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    version_View.setClickable(true);
                }
            });
        }
        else if(type == 2){
            msg = String.format(getString(R.string.msg_handbook));
            tvConfirm.setText(String.format(getString(R.string.label_handbook_tuoche)));
            tvCancel.setText(String.format(getString(R.string.label_handbook_nottuoche)));
            tvMsg.setText(msg);
            tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri  uri = Uri.parse("http://www.auto-sos.cn/instructions1.html");
                    Intent  i = new  Intent(Intent.ACTION_VIEW, uri);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    dialog.dismiss();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri  uri = Uri.parse("http://www.auto-sos.cn/instructions2.html");
                    Intent  i = new  Intent(Intent.ACTION_VIEW, uri);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                    dialog.dismiss();
                }
            });
        }

        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(rootView.getContext());
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }
    private void showDialog(String type){
        String msg =null;
        msg = String.format(rootView.getContext().getResources().getString(R.string.msg_download));
        View v2 = getActivity().getLayoutInflater().inflate(R.layout.dialog_msg_content,
                null);
        dialog = new Dialog(v2.getContext(), R.style.bubble_dialog);
        Button tvConfirm = (Button) v2.findViewById(R.id.btn_notice_confirm);
        TextView tvMsg = (TextView) v2.findViewById(R.id.tv_notice_msg);
        switch (type){
            case "NoUpdate":
                msg = String.format(rootView.getContext().getResources().getString(R.string.msg_noupdate));
                tvMsg.setText(msg);
                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        version_View.setClickable(true);
                    }
                });
                break;
            case "Instal":
                msg = String.format(rootView.getContext().getResources().getString(R.string.msg_download));
                tvMsg.setText(msg);
                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        version_View.setClickable(true);
                        progress_textView.setVisibility(View.GONE);
                        progressBarView.setVisibility(View.GONE);
                        installApk();
                        getActivity().finish();

                    }
                });
                break;
        }

        dialog.setContentView(v2);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(rootView.getContext());
        params.width = Math.round(point.x * 5 / 7);
        window.setAttributes(params);
        dialog.show();
    }

    private void download(){
        Runnable runnable= new Runnable() {
            @Override
            public void run() {
                try{
                    UUID uuid  =  UUID.randomUUID();
                    String s = UUID.randomUUID().toString();
                    version.setUrl(version.getUrl()+"?v="+s);
                    URL url = new URL(version.getUrl());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is =conn.getInputStream();
                    File file =new File(Environment.getExternalStorageDirectory()+"/com.autosos.jd/");
                    if(!file.exists()){
                        file.mkdir();
                    }
                    String apkFile = Environment.getExternalStorageDirectory()+"/com.autosos.jd/"+version.getName()+".apk";
                    APKFile = new File(apkFile);
                    FileOutputStream fos = new FileOutputStream(APKFile);
                    int count = 0;
                    byte buff[] = new byte[1024];
                    while(true){
                        int numread = is.read(buff);
                        count += numread;
                        int progress =(int)(((float)count / length) * 100);
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = progress;
                        handler.sendMessage(msg);
                        //Handler.sendEmptyMessage(DOWN_UPDATE);
                        if(numread <= 0){
                            Message msg2 = new Message();
                            msg2.what = 3;
                            handler.sendMessage(msg2);
                            break;
                        }
                        fos.write(buff,0,numread);
                    }//while(!interceptFlag);
                    fos.close();
                    is.close();
                }catch (Exception e){
                    Log.e(TAG, "ERRor" + e.toString());
                }
            }
        };
        new Thread(runnable).start();
    }
}
