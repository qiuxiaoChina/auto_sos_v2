package com.autosos.yd.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.autosos.yd.view.AccountActivity;
import com.autosos.yd.view.LoginActivity;
import com.autosos.yd.view.PasswordActivity;
import com.autosos.yd.view.SetingActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.makeramen.rounded.RoundedImageView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import com.autosos.yd.Constants;
import com.autosos.yd.R;
import com.autosos.yd.model.Person;
import com.autosos.yd.task.AsyncBitmapDrawable;
import com.autosos.yd.task.AuthGetJSONObjectAsyncTask;
import com.autosos.yd.task.ImageLoadTask;
import com.autosos.yd.util.JSONUtil;
import com.autosos.yd.util.ScaleMode;

public class PersonFragment extends Fragment implements PullToRefreshBase.OnRefreshListener<ScrollView>{

    private View rootView;
    private static final String TAG = "PersonFragment";
    private View progressBar;
    private View empty;
    private TextView nameView;
    private TextView name_person;
    private TextView countView;
    private TextView rateView;
    private RelativeLayout seting;
    private RelativeLayout account;
    private RatingBar ratingbarView;
    private RoundedImageView avatarView;
    private com.autosos.yd.model.Person person = null;
    private PullToRefreshScrollView scrollView;

    public static PersonFragment newInstance() {
        return new PersonFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_person2, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        empty = rootView.findViewById(R.id.empty);
        nameView = (TextView)rootView.findViewById(R.id.name);
        name_person = (TextView) rootView.findViewById(R.id.name_person);
//        phoneView = (TextView) rootView.findViewById(R.id.phone);
        countView = (TextView) rootView.findViewById(R.id.count);
        rateView = (TextView) rootView.findViewById(R.id.rate);
        seting = (RelativeLayout) rootView.findViewById(R.id.seting);
        account = (RelativeLayout) rootView.findViewById(R.id.account);
        ratingbarView =(RatingBar) rootView.findViewById(R.id.ratingbar_person);
        avatarView = (RoundedImageView) rootView.findViewById(R.id.user_avatar);
        avatarView.setCornerRadius(1000);
        scrollView = (PullToRefreshScrollView) rootView.findViewById(R.id.personne_list);
        seting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PasswordActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        });


        if(!Constants.DEBUG && false)
            rootView.findViewById(R.id.seting).setVisibility(View.GONE);
        scrollView.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {

        new GetPersonTask(getActivity().getApplicationContext()).executeOnExecutor(com.autosos.yd.Constants.INFOTHEADPOOL,
                com.autosos.yd.Constants.PERSON_URL);
        super.onResume();
    }

    //刷新
    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        new GetPersonTask(rootView.getContext()).executeOnExecutor(com.autosos.yd.Constants.INFOTHEADPOOL,
                com.autosos.yd.Constants.PERSON_URL);

    }

    private class GetPersonTask extends com.autosos.yd.task.AuthGetJSONObjectAsyncTask {
        public GetPersonTask(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                String jsonStr = com.autosos.yd.util.JSONUtil.getStringFromUrl(getActivity().getApplicationContext(), params[0]);
                if (com.autosos.yd.util.JSONUtil.isEmpty(jsonStr)) {
                    return null;
                }
                Log.e(TAG, jsonStr);
                return new JSONObject(jsonStr);
            } catch (IOException | JSONException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progressBar.setVisibility(View.GONE);
            scrollView.onRefreshComplete();
            person = new com.autosos.yd.model.Person(result);
            if (person != null) {
                name_person.setText(person.getRealname());
                nameView.setText(person.getCompany_name());
                nameView.setTextColor(0x7fffffff);
//                phoneView.setText(person.getMobile());
                countView.setText(String.valueOf(person.getAccepted_count()));
                rateView.setText(String.valueOf(person.getAverage_rate()));
                ratingbarView.setRating((float)person.getAverage_rate());
                com.autosos.yd.task.ImageLoadTask task = new com.autosos.yd.task.ImageLoadTask(avatarView, null, 0);
                com.autosos.yd.task.AsyncBitmapDrawable image = new com.autosos.yd.task.AsyncBitmapDrawable(rootView.getResources(),
                        com.autosos.yd.Constants.PLACEHOLDER_AVATAR, task);
                task.loadImage(person.getAvatar(), avatarView.getMeasuredWidth(), com.autosos.yd.util.ScaleMode.WIDTH, image);
            }
            empty.setVisibility(View.GONE);
            super.onPostExecute(result);
        }
    }

}
