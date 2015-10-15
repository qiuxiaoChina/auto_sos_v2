package com.autosos.yd.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.autosos.yd.R;
import com.autosos.yd.widget.*;
import com.autosos.yd.widget.CustomImage;

public class RoundProgressDialog extends Dialog {
    private long maxLength;
    private View complateView;
    private TextView msgView;
    private TextView valueView;
    private com.autosos.yd.widget.CustomImage customImage;
    private ScaleAnimation scale_out;
    private OnUpLoadComplate onUpLoadComplate;

    public RoundProgressDialog(Context context, int theme) {
        super(context, theme);
        initView();
    }

    public RoundProgressDialog(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = getLayoutInflater().inflate(R.layout.dialog_round_progress,
                null);
        customImage = (com.autosos.yd.widget.CustomImage) view.findViewById(R.id.customImage);
        valueView = (TextView) view.findViewById(R.id.value);
        complateView = view.findViewById(R.id.complate);
        msgView = (TextView) view.findViewById(R.id.msg);
        setContentView(view);
        scale_out = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale_out.setDuration(500);
        scale_out.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
                if (onUpLoadComplate != null) {
                    onUpLoadComplate.onUpLoadCompleted();
                }
            }
        });

    }

    public void reset() {
        complateView.setVisibility(View.GONE);
        valueView.setVisibility(View.VISIBLE);
        customImage.reset();
        setCancelable(true);
    }

    public void setMax(long max) {
        maxLength = max;
    }

    public void setMessage(String message) {
        msgView.setText(message);
    }

    public void setProgress(long value) {
        int progress = 0;
        if (maxLength > 0) {
            progress = Math.round(value * 100 / maxLength);
        }
        valueView.setText(String.valueOf(progress));
        if (progress == 0) {
            customImage.reset();
        } else {
            customImage.setupprogress(progress);
        }
    }

    public void onLoadComplate() {
        valueView.setVisibility(View.GONE);
        complateView.setVisibility(View.VISIBLE);
    }

    public void onComplate() {
        complateView.startAnimation(scale_out);
    }

    public void setOnUpLoadComplate(OnUpLoadComplate onUpLoadComplate) {
        this.onUpLoadComplate = onUpLoadComplate;
    }

    public interface OnUpLoadComplate {

        void onUpLoadCompleted();

    }

}