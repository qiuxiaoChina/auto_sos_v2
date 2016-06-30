package com.autosos.rescue.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.autosos.rescue.Constants;
import com.autosos.rescue.R;
import com.autosos.rescue.task.NewHttpPostTask;
import com.autosos.rescue.task.OnHttpRequestListener;
import com.autosos.rescue.util.Session;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForSetting extends Fragment implements View.OnClickListener{

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

    private Button logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_for_setting, null);
        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.logout:
                new NewHttpPostTask(getActivity().getApplicationContext(), new OnHttpRequestListener() {
                    @Override
                    public void onRequestCompleted(Object obj) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(obj.toString());
                            int result = jsonObject.getInt("result");
                            if(result ==1){

                                Session.getInstance().logout(getActivity().getApplicationContext());
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
            default:
                break;
        }
    }
}
