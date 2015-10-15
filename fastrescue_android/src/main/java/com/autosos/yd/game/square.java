package com.autosos.yd.game;

/**
 * Created by Administrator on 2015/9/24.
 */
public class square {
    private int x;
    private int y;
    private int status;
    public  static  final  int STATUS_ON = 1;
    public  static  final  int STATUS_OFF = 0;
    public  static  final  int STATUS_IN = 9;
    public  static  final  int STATUS_Boot = 6;
    public static final int NPC_HS = 15;
    public static final int NPC_TALK1 = -100;
    public static final int NPC_TALK2 = -110;
    public square(int x,int y){
        super();
        this.x = x;
        this.y = y;
    }
    public square(int x,int y,int status){
        super();
        this.x = x;
        this.y = y;
        this.status = status;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setXY(int x ,int y){
        this.x = x;
        this.y = y;
    }
    public void setXY(int x ,int y ,int status){
        this.x = x;
        this.y = y;
        this.status = status;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
