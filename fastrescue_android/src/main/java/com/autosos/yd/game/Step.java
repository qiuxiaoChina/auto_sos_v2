package com.autosos.yd.game;

/**
 * Created by Administrator on 2015/9/28.
 */
public class Step{
    int x,y,d;
    public Step(int x,int y,int d) {
        this.x = x;//横坐标
        this.y = y;//纵坐标
        this.d = d;//方向
    }
    public Step(int x,int y) {
        this.x = x;//横坐标
        this.y = y;//纵坐标
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

    public void setX(int x) {
        this.x = x;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }
}