package com.autosos.rescue.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.autosos.rescue.R;

public class AboutUsActivity extends Activity implements View.OnClickListener ,GestureDetector.OnGestureListener {

    private ImageView back_button;
    private ViewFlipper flipper;
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        back_button = (ImageView) findViewById(R.id.back_button);
        back_button.setOnClickListener(this);

        detector = new GestureDetector(this);
        flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper1);

        flipper.addView(addTextView(R.drawable.one));
        flipper.addView(addTextView(R.drawable.two));
        flipper.addView(addTextView(R.drawable.three));
        flipper.addView(addTextView(R.drawable.four));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;

        }
    }

    private View addTextView(int id) {
        ImageView iv = new ImageView(this);
        iv.setImageResource(id);
        return iv;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return this.detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (e1.getX() - e2.getX() > 120) {
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
            this.flipper.showNext();
            return true;
        } else if (e1.getX() - e2.getX() < -120) {
            this.flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
            this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
            this.flipper.showPrevious();
            return true;
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }
}
