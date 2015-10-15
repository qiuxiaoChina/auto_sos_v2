package com.autosos.yd.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.autosos.yd.R;

public class RatingView extends LinearLayout implements View.OnClickListener {

    public ImageButton ratingView1;
    public ImageButton ratingView2;
    public ImageButton ratingView3;
    public ImageButton ratingView4;
    public ImageButton ratingView5;
    public OnRatingChangeListener onRatingChangeListener;
    public int rating;

    public RatingView(Context context) {
        super(context);
        init(context);
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RatingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context mContext) {
        View view=inflate(mContext, R.layout.rating_layout,this);
        ratingView1 = (ImageButton) view.findViewById(R.id.rating1);
        ratingView2 = (ImageButton) view.findViewById(R.id.rating2);
        ratingView3 = (ImageButton) view.findViewById(R.id.rating3);
        ratingView4 = (ImageButton) view.findViewById(R.id.rating4);
        ratingView5 = (ImageButton) view.findViewById(R.id.rating5);
        if(ratingView1!=null) {
            ratingView1.setOnClickListener(this);
        }
        if(ratingView2!=null) {
            ratingView2.setOnClickListener(this);
        }
        if(ratingView3!=null) {
            ratingView3.setOnClickListener(this);
        }
        if(ratingView4!=null) {
            ratingView4.setOnClickListener(this);
        }
        if(ratingView5!=null) {
            ratingView5.setOnClickListener(this);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rating1:
                rating = 1;
                break;
            case R.id.rating2:
                rating = 2;
                break;
            case R.id.rating3:
                rating = 3;
                break;
            case R.id.rating4:
                rating = 4;
                break;
            case R.id.rating5:
                rating = 5;
                break;
            default:
                break;
        }
        if(onRatingChangeListener!=null){
            onRatingChangeListener.onRatingChanged(rating);
        }
        ratingOff(rating);
        ratingOn(rating);
    }

    public void setRating(int rating) {
        this.rating = rating;
        if(onRatingChangeListener!=null){
            onRatingChangeListener.onRatingChanged(rating);
        }
        ratingOff(rating);
        ratingOn(rating);
    }

    public int getRating() {
        return rating;
    }

    private void ratingOff(int rating) {
        switch (rating) {
            case 0:
                if (ratingView1.getTag() != null && (Boolean) ratingView1.getTag()) {
                    ratingView1.setImageResource(R.drawable.icon_rating_star_off);
                    ratingView1.setTag(false);
                }
            case 1:
                if (ratingView2.getTag() != null && (Boolean) ratingView2.getTag()) {
                    ratingView2.setImageResource(R.drawable.icon_rating_star_off);
                    ratingView2.setTag(false);
                }
            case 2:
                if (ratingView3.getTag() != null && (Boolean) ratingView3.getTag()) {
                    ratingView3.setImageResource(R.drawable.icon_rating_star_off);
                    ratingView3.setTag(false);
                }
            case 3:
                if (ratingView4.getTag() != null && (Boolean) ratingView4.getTag()) {
                    ratingView4.setImageResource(R.drawable.icon_rating_star_off);
                    ratingView4.setTag(false);
                }
            case 4:
                if (ratingView5.getTag() != null && (Boolean) ratingView5.getTag()) {
                    ratingView5.setImageResource(R.drawable.icon_rating_star_off);
                    ratingView5.setTag(false);
                }
                break;

        }
    }

    private void ratingOn(int rating) {
        switch (rating) {
            case 5:
                if (ratingView5.getTag() == null || !(Boolean) ratingView5.getTag()) {
                    ratingView5.setImageResource(R.drawable.icon_rating_star_on);
                    ratingView5.setTag(true);
                }
            case 4:
                if (ratingView4.getTag() == null || !(Boolean) ratingView4.getTag()) {
                    ratingView4.setImageResource(R.drawable.icon_rating_star_on);
                    ratingView4.setTag(true);
                }
            case 3:
                if (ratingView3.getTag() == null || !(Boolean) ratingView3.getTag()) {
                    ratingView3.setImageResource(R.drawable.icon_rating_star_on);
                    ratingView3.setTag(true);
                }
            case 2:
                if (ratingView2.getTag() == null || !(Boolean) ratingView2.getTag()) {
                    ratingView2.setImageResource(R.drawable.icon_rating_star_on);
                    ratingView2.setTag(true);
                }
            case 1:
                if (ratingView1.getTag() == null || !(Boolean) ratingView1.getTag()) {
                    ratingView1.setImageResource(R.drawable.icon_rating_star_on);
                    ratingView1.setTag(true);
                }
                break;

        }
    }

    public interface OnRatingChangeListener{
        public void onRatingChanged(int rating);
    }

    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }
}
