package com.example.pingpong;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class DrawThread extends Thread{
    GameField gameField;
    SurfaceHolder surfaceHolder;
    boolean isRun = false;
    long nowTime, prevTime, ellapsedTime;


    public DrawThread(GameField gameField, SurfaceHolder surfaceHolder) {
        this.gameField = gameField;
        this.surfaceHolder = surfaceHolder;
        prevTime = System.currentTimeMillis();
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    @Override
    public void run() {
        Canvas canvas;
        while (isRun){
            if (!surfaceHolder.getSurface().isValid()){
                continue;
            }
            canvas = null;
            nowTime = System.currentTimeMillis();
            ellapsedTime = nowTime - prevTime;
            if (ellapsedTime > 30){
                prevTime = nowTime;
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder){
                    gameField.draw(canvas);
                }
                if (canvas != null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
