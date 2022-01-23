package com.example.pingpong;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Random;
import java.util.logging.Handler;

public class GameField extends SurfaceView implements SurfaceHolder.Callback {

    DrawThread drawThread;
    Resources res;

    Bitmap alien, putin2;

    float alien1X, alien1Y, putin2X, putin2Y;
    float koeff;
    float game_field_screen_height, game_field_screen_width;
    float touch_x, touch_y;
    float putin_speed = 0;
    float alien1_speedX, alien1_speedY;

    boolean isFirstDraw = true;
    boolean isPutinMotion = false;
    boolean isDefeat = false;

    Paint paint;

    Rect putinRect, alienRect;

    Random random = new Random();

    Context context;

    GameMap gameMap;


    public GameField(Context context) {
        super(context);
        this.context = context;
        getHolder().addCallback(this);
        res = getResources();
        paint = new Paint();
        alien = BitmapFactory.decodeResource(res, R.drawable.alien);
        putin2 = BitmapFactory.decodeResource(res, R.drawable.putin2);
        koeff = 30;

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (isFirstDraw){
            game_field_screen_height = canvas.getHeight();
            game_field_screen_width = canvas.getWidth();

            putin2X = game_field_screen_width/2 - putin2.getWidth();
            putin2Y = game_field_screen_height - (putin2.getHeight());
            putin_speed = 15;

            Random r = new Random();
            alien1X = r.nextInt((int)game_field_screen_width - alien.getWidth());
            alien1Y = r.nextInt((int)game_field_screen_height- 1000);

            gameMap = new GameMap((int)game_field_screen_width,(int)game_field_screen_height,
                    res);


            isFirstDraw = false;
            calculate_ball_trajectory();
        }

        if (!isDefeat)
            gameMap.draw(canvas);
        canvas.drawBitmap(putin2, putin2X, putin2Y, paint);
        canvas.drawBitmap(alien, alien1X, alien1Y, paint);

        alien1X += alien1_speedX;
        putin2X += putin_speed;
        alien1Y += alien1_speedY;


        putinRect = new Rect((int)putin2X, (int)putin2Y,
                (int)putin2X + putin2.getWidth(), (int)putin2Y + putin2.getHeight());
        alienRect = new Rect((int)alien1X, (int)alien1Y,
                (int)alien1X + alien.getWidth(), (int)alien1Y + alien.getHeight());


        checkPutinAlienCollision();
        checkPutinScreenCollision();
        checkAlienScreenCollision();
        checkEndGame();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            touch_x = event.getX();
            calculate_putin_motion((int) touch_x);
        }
        return true;
    }

    private void calculate_ball_trajectory(){
        double g = Math.sqrt((putin2X-alien1X)*(putin2X-alien1X)+(putin2Y - alien1Y)*(putin2Y - alien1Y));
        alien1_speedX = (float) (koeff * (putin2X-alien1X)/g);
        alien1_speedY = (float) (koeff * (putin2Y - alien1Y)/g);

    }

    private void calculate_putin_motion(int touch_x){
        if (touch_x > game_field_screen_width/2 || touch_x < game_field_screen_width/2){
            putin_speed = -putin_speed;
        }

    }

    private void checkPutinAlienCollision(){
        if (putinRect.intersect(alienRect)){
            alien1_speedY = -alien1_speedY;
            putin_speed = -putin_speed;
        }
    }

    private void checkPutinScreenCollision(){
        if (putin2X < 0 || putin2X + putin2.getWidth() > game_field_screen_width){
            putin_speed = - putin_speed;
        }
    }

    private void checkAlienScreenCollision(){
        if (alien1X < 0 || alien1X + alien.getWidth() > game_field_screen_width){
            alien1_speedX = -alien1_speedX;
        }
        if (alien1Y < 0 || alien1Y + alien.getHeight() > game_field_screen_height){
            alien1_speedY = -alien1_speedY;
        }
    }

    private void checkEndGame(){
        if (alien1Y + alien.getHeight() > game_field_screen_height){
            putin_speed = 0;
            alien1_speedY = 0;
            alien1_speedX = 0;
            isDefeat = false;
        }
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        drawThread = new DrawThread(this, getHolder());
        drawThread.setRun(true);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        boolean stop = true;
        drawThread.setRun(false);
        while(stop) {
            try {
                drawThread.join();
                stop = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
