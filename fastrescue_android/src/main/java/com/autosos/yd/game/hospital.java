package com.autosos.yd.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.autosos.yd.R;
import com.autosos.yd.util.GetuiSdkMsgReceiver;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Administrator on 2015/9/24.
 */
public class hospital extends SurfaceView implements SurfaceHolder.Callback,View.OnTouchListener{
    private final String TAG = "GameTest";
    private int Width;
    private int Height;
    private int Row = 62;
    private int Col = 32;
    private square last;
    private square person;
    private square TALK;
    private square NPC_HS;
    private square matrix[][];
    public hospital(Context context){
        super(context);
        getHolder().addCallback(this);
        matrix = new square[Row][Col];
        backg = new square[Row][Col];
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Width = wm.getDefaultDisplay().getWidth() / Col ;
        Height = wm.getDefaultDisplay().getHeight() / Row;

        for (int i=0;i<Row;i++)
        {
            for (int j=0;j<Col;j++)
            {
                matrix[i][j]=new square(j,i);
                backg[i][j] = new square(j,i);
            }
        }
        setOnTouchListener(this);
        initGame();
    }
    private  square[][] backg;
    private  void initGame() {

        for (int i = 0; i < Row; i++) {
            for (int j = 0; j < Col; j++) {
                backg[i][j].setStatus(square.STATUS_OFF);
                if (((i > Row / 2 - 5) && (i < Row / 2 + 5)) && ((j > Col / 2 - 7) && (j < Col / 2 + 7)) || i == 0 || i == Row - 1 || j == 0 || j == Col - 1)
                    backg[i][j].setStatus(square.STATUS_ON);
            }
        }
        for (int i = 0; i < Row; i++) {
            for (int j = 0; j < Col; j++) {
                matrix[i][j].setStatus(square.STATUS_OFF);
                if (((i > Row / 2 - 5) && (i < Row / 2 + 5)) && ((j > Col / 2 - 7) && (j < Col / 2 + 7)) || i == 0 || i == Row - 1 || j == 0 || j == Col - 1)
                    matrix[i][j].setStatus(square.STATUS_ON);
            }
        }
        NPC_HS = new square(25,57,square.NPC_HS);
        TALK = new square(25,57,square.NPC_HS);
        iniNPC(NPC_HS,square.NPC_HS);
        person = getSquare(4, 5);
        person.setXY(4, 5);
        last = person;
        person.setStatus(square.STATUS_IN);
    }
    private void iniNPC(square NPC,int state){
        NPC = getSquare(25, 57);
        NPC.setXY(25, 57);

        for(int i=25;i<=25+2;i++){
            for (int j =57;j<=57 + 2;j++){
                matrix[j][i].setStatus(square.STATUS_ON);
            }
        }
        NPC.setStatus(state);
    }
    private  void redraw()
    {
        iniNPC(NPC_HS,square.NPC_HS);
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.YELLOW);
        Paint paint=new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        for (int i=0;i<Row;i++)
        {
            for (int j=0;j<Col;j++)
            {
                square  one =getSquare(j, i);
                switch (one.getStatus())
                {
                    case square.STATUS_OFF:
                        if(one.getX() - person.getX() <3 && one.getX() -person.getX() >=0 && one.getY() - person.getY() < 3 && one.getY() - person.getY() >=0){

                        }
                        else {
                            paint.setColor(Color.WHITE);
                            canvas.drawRect(new RectF(one.getX() * Width + 2, one.getY() * Height + 2, (one.getX() + 1) * Width - 2, (one.getY() + 1) * Height - 2
                            ), paint);
                        }
                        break;
                    case square.STATUS_ON:
                        if((one.getX() - NPC_HS.getX() <3 && one.getX() -NPC_HS.getX() >=0 && one.getY() - NPC_HS.getY() < 3 && one.getY() - NPC_HS.getY() >=0 ) ||
                                one.getX() - TALK.getX() <4 && one.getX() -TALK.getX() >=0 && one.getY() - TALK.getY() < 1&& one.getY() - TALK.getY() >=0 &&TALK.getStatus() <-100    ){

                        }else {
                            paint.setColor(Color.BLACK);
                            canvas.drawRect(new RectF(one.getX() * Width + 2, one.getY() * Height + 2, (one.getX() + 1) * Width - 2, (one.getY() + 1) * Height - 2

                            ), paint);
                        }
                        break;
                    case square.STATUS_IN:
                        paint.setColor(Color.RED);
                        BitmapFactory.Options opt = new BitmapFactory.Options();
                        opt.inSampleSize = 10;
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.katong,opt);
                        opt.inSampleSize = bitmap.getWidth()*4/(Width);
                         bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.katong,opt);
                        canvas.drawBitmap(bitmap,one.getX() * Width,one.getY() * Height, paint);
                        break;
                    case square.STATUS_Boot:
                        paint.setColor(Color.GREEN);
                        canvas.drawRect(new RectF(one.getX() * Width + 2, one.getY() * Height + 2, (one.getX() + 1) * Width - 2, (one.getY() + 1) * Height - 2

                        ), paint);
                        break;
                    case square.NPC_HS:
                        paint.setColor(Color.RED);
                        BitmapFactory.Options opt2 = new BitmapFactory.Options();
                        opt2.inSampleSize = 10;
                        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.katong2,opt2);
                        opt2.inSampleSize = bitmap2.getHeight()*4/(Height);
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.katong2,opt2);
                        canvas.drawBitmap(bitmap,one.getX() * Width,one.getY() * Height, paint);
                    case square.NPC_TALK1:
                        Log.e(TAG,"----------1");
                        paint.setColor(Color.GREEN);
                        canvas.drawText("come here", TALK.getX() * Width * 4, TALK.getY() * Height, paint);
                        break;
                    case square.NPC_TALK2:
                        Log.e(TAG, "----------2");

                        canvas.drawText("well done", TALK.getX() * Width * 4, TALK.getY() * Height, paint);
                        break;
                }
            }
        }
        paint.setColor(Color.GREEN);
        canvas.drawText("well done",300,400,paint);
        getHolder().unlockCanvasAndPost(canvas);
    }
    private square getSquare(int x, int y)
    {
        return matrix[y][x];
    }

    private boolean isClickNPC(int x,int y){
        if(x >= NPC_HS.getX() && x<= NPC_HS.getX() +3 && y >= NPC_HS.getY() && y<= NPC_HS.getY() +3){
            return  true;
        }
        else
            return false;
    }
    private boolean isNearNPC(int x, int y){
        int distanceX = NPC_HS.getX() - 3;
        int distanceY = NPC_HS.getY() - 3;
        if(NPC_HS.getX() - x<3 &&NPC_HS.getX() - x >0 &&NPC_HS.getY() -y <3&&NPC_HS.getY()-y>0 )
            return true;
        else
            return false;
    }

    private void talk(int type){
        if(type == 1)
        {
            TALK = getSquare(25, 53);
            TALK.setXY(25, 57);

            for(int i=25;i<=25;i++){
                for (int j =53;j<=53 + 2;j++){
                    matrix[j][i].setStatus(square.STATUS_ON);
                }
            }
            TALK.setStatus(square.NPC_TALK1);
            redraw();
        }
        else {
            TALK = getSquare(25, 53);
            TALK.setXY(25, 53);

            for(int i=25;i<=25+2;i++){
                for (int j =53;j<=53 + 2; j++) {
                    matrix[j][i].setStatus(square.STATUS_ON);
                }
            }
            TALK.setStatus(square.NPC_TALK2);
            redraw();
        }
    }
    private boolean ismoving;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(ismoving)
            return false;
        int touchX = (int) event.getX()  ;
        int touchY = (int) event.getY() ;
        touchX = touchX  / Width  > 31 ? 31: touchX / Width  ;
        touchY = touchY  / Height  >61 ? 61: touchY/Height ;
        if(isClickNPC(touchX,touchY)){
            Log.e(TAG,"----NPCｃｌｉｃｋ；");
            if(isNearNPC(touchX,touchY)){
                talk(1);
                return false;
            }else{
                talk(2);
                return false;
            }
        }
        if(matrix[touchY][touchX].getStatus() == square.STATUS_ON)
            return false;
    //    matrix[person.getY()][person.getX()].setStatus(square.STATUS_OFF);
   //     person.setXY(touchX, touchY,square.STATUS_IN);
    //    matrix[touchY][touchX] = person;

    try {
        paintRoute(last, new square(touchX, touchY, square.STATUS_ON));
    }catch (Exception e ){
     e.printStackTrace();
    }
        return false;
    }
    private int x,y;
    public void paintRoute(square start,square end){
        Log.e(TAG, "-" + start.getX() + "-" + start.getY() + "----" + end.getX() + "-" + end.getY());
        final ArrayList<Step> steps = RouteUtil.path(backg, start.getY(), start.getX(), end.getY(), end.getX());

        int length = steps.size();
        ismoving = true;
        y=length-1;
        for(x=0;x<length+1;x++) {
            int delay = x*150;
            if(x == length){
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                            ismoving = false;
                    }
                }, delay);
            }
            else {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        person.setStatus(square.STATUS_OFF);
                        person = getSquare(steps.get(y).getY(), steps.get(y).getX());
                        person.setXY(steps.get(y).getY(), steps.get(y).getX());
                        person.setStatus(square.STATUS_IN);
                        last = person;
                        y--;
                        redraw();
                    }
                }, delay);
            }
        }
       // for(int i=0;i<steps.size();i++){
       //     matrix[steps.get(i).getX()][steps.get(i).getY()].setStatus( square.STATUS_Boot);
      //  }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        redraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


}
