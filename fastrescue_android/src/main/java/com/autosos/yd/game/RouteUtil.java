package com.autosos.yd.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;
import android.util.Log;

import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RouteUtil {
    public static int[][] move = {{0,1},{1,0},{0,-1},{-1,0}};
    public static ArrayList<Step> path(square[][] m,int startX,int startY,int endX,int endY){
        int maze[][]=new int[62][32];
        int route[][] = new int [62][32];
        for(int i=0;i<62;i++){
            for(int j=0;j<32;j++){
                route[i][j] = 999;
                if(m[i][j].getStatus()==square.STATUS_OFF || m[i][j].getStatus()==square.STATUS_IN ){
                    maze[i][j] = 0;
                }
                else{
                    maze[i][j] = 1;
                }
            }
        }
        route[startX][startY] = 0;
        Step temp = new Step(startX,startY,-1); //起点
        BlockingQueue<Step> s = new ArrayBlockingQueue<Step>(
                100000);
        s.add(temp);
        while(!s.isEmpty() ){
            temp =(Step) s.poll();
            int x = temp.x;
            int y = temp.y;
            int d = 0;
            while(d < 4){
                int i = x + move[d][0];
                int j = y + move[d][1];
                if( maze[i][j] == 0 ||(maze[i][j] == -1 && route[i][j] > route[x][y]+1 ) ){    //该点可达
                    temp = new Step(i,j,d); //到达新点
                    if(route[i][j] > route[x][y]+1){
                        route[i][j] = route[x][y]+1;
                    }
                    s.add(temp);
                  //  x = i;
                   // y = j;
                    maze[i][j] = -1;  //到达新点，标志已经到达
                    if(x == endX && y == endY){
                        route[x][y] = 99999;
                        s.clear();
                        return paint(route,startX,startY,endX,endY);  //到达出口，迷宫有路，返回1
                    }
                }
                    d++; //改变方向
            }
        }
        s.clear();
        return null;
    }
    public   static ArrayList<Step> paint(int route[][],int startX,int startY,int endX,int endY){
        ArrayList<Step> steps = new ArrayList<Step>();
        Log.e("migong ","=========================");
        for(int i= 0;i <62;i++){
            String x="";
            for(int j=0;j<32;j++){
                if(route[i][j]==99999){
                    x+=" "+"***";
                }
                else if(route[i][j]<10){
                    x+=" "+"00"+route[i][j];
                }
                else if(route[i][j]<100){
                    x+=" "+"0"+route[i][j];
                }
                else
                    x+=" "+route[i][j];
            }
            Log.e("migong ",""+x);
        }
        int length = route[endX][endY];
        int x = endX;
        int y = endY;
        for (;length > 0;length --){
            int d = 0;
            while(d < 4){
                int i = x + move[d][0];
                int j = y + move[d][1];
                if(i<62&&j<32&&route[i][j] == length-1){
                    steps.add(new Step(i,j));
                    x=i;y=j;
                    break;
                }
                else
                    d++;
            }
        }
        return steps;
    }

}