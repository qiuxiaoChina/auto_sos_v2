package com.autosos.yd.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autosos.yd.R;
import com.autosos.yd.widget.CatchException;

public class AutososBackActivity extends Activity {

    private int barResID;
    private TextView titleLabel;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        CatchException catchException = CatchException.getInstance();
        catchException.init(getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#setContentView(int)
     */
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                barResID != 0 ? barResID : R.layout.bar_title_back);
        titleLabel = (TextView) findViewById(android.R.id.title);
        titleLabel.setText(getTitle());
        RelativeLayout homeButton = (RelativeLayout) findViewById(android.R.id.home);
        homeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void setHomeButtonGone(){
        RelativeLayout homeButton = (RelativeLayout) findViewById(android.R.id.home);
        homeButton.setVisibility(View.GONE);
    }

    public void setTitleLabelColor(int color){
        titleLabel.setTextColor(color);
    }

    public void setBackground(int color){
        RelativeLayout RelateiveLayout1 = (RelativeLayout) findViewById(R.id.RelateiveLayout1);
        RelateiveLayout1.setBackgroundResource(color);
    }

    public void setLineGone(){
        View line = findViewById(R.id.line);
        line.setVisibility(View.GONE);
    }

    public void setTakePhoto(){
        ImageView take_photo_inorder = (ImageView) findViewById(R.id.take_photo_inorder);
        take_photo_inorder.setVisibility(View.VISIBLE);
    }

    public ImageView getTakePhoto(){
        ImageView take_photo_inorder = (ImageView) findViewById(R.id.take_photo_inorder);
        return take_photo_inorder;
    }

    public void setArrow(int arrow){
        ImageView arrowicon = (ImageView) findViewById(R.id.arrow);
        arrowicon.setImageResource(arrow);
    }

    public void setSuggest(){
        TextView photo_suggest = (TextView) findViewById(R.id.photo_suggest);
        photo_suggest.setVisibility(View.VISIBLE);
    }

    public View getSuggest(){
        TextView photo_suggest = (TextView) findViewById(R.id.photo_suggest);
        return photo_suggest;
    }
        //test
    public void setBill(){
        TextView bill = (TextView) findViewById(R.id.bill);
        bill.setVisibility(View.VISIBLE);
    }

    public View getBill(){
        TextView bill = (TextView) findViewById(R.id.bill);
        return bill;
    }

    public void hideBackButton() {
        RelativeLayout backBtn = (RelativeLayout) findViewById(android.R.id.home);
        backBtn.setVisibility(View.GONE);
    }

    public void setBarLayoutID(int barResID) {
        this.barResID = barResID;
    }

    public void setOkButton(int resource) {
        ImageButton okBtn = (ImageButton) findViewById(R.id.action_ok);
        if (resource != 0) {
            okBtn.setImageDrawable(getResources().getDrawable(resource));
        } else {
            okBtn.setBackgroundResource(R.drawable.bg_btn_ok);
        }
        okBtn.setVisibility(View.VISIBLE);
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onOkButtonClick();
            }
        });
    }

    public void hideOkButton() {
        ImageButton okBtn = (ImageButton) findViewById(R.id.action_ok);
        okBtn.setVisibility(View.GONE);
    }

    public void showOkButton() {
        ImageButton okBtn = (ImageButton) findViewById(R.id.action_ok);
        okBtn.setVisibility(View.VISIBLE);
    }

    public String getOkText() {
        TextView okItem = (TextView) findViewById(R.id.item);
        return okItem.getText().toString();
    }

    public void setOkText(int resource) {
        TextView okItem = (TextView) findViewById(R.id.item);
        if (resource != 0) {
            okItem.setText(resource);
        }
        okItem.setVisibility(View.VISIBLE);
        okItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onOkButtonClick();
            }
        });
    }

    public void setTitle(CharSequence string) {
        TextView titleLabel = (TextView) findViewById(android.R.id.title);
        titleLabel.setVisibility(View.VISIBLE);
        titleLabel.setText(string);
    }

    public void onOkButtonClick() {

    }

    public void hideKeyboard(View v) {
        if (this.getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(this.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
