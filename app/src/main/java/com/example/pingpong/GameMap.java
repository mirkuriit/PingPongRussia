package com.example.pingpong;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class GameMap {
    int sizeTexture = 32;



    int mapArray[][];
    Bitmap textures[];

    public GameMap(int width, int height, Resources resources){
        Random random = new Random();
        mapArray = new int[height/sizeTexture][(width/sizeTexture)];
        try {
            InputStream is = resources.openRawResource(R.raw.ok);
            InputStreamReader isr =  new InputStreamReader(is, "utf8");
            BufferedReader br = new BufferedReader(isr);
            loadMapFromFile(br);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



//        for (int i = 0; i < mapArray.length; i++) {
//            for (int j = 0; j < mapArray[i].length; j++) {
//                mapArray[i][j] = random.nextInt(3)+1;
//            }
//        }


        textures = new Bitmap[5];
        textures[0] = BitmapFactory.decodeResource(resources, R.drawable.black_wall);
        textures[1] = BitmapFactory.decodeResource(resources, R.drawable.graw_wall2);
        textures[2] = BitmapFactory.decodeResource(resources, R.drawable.graw_wall3);
        textures[3] = BitmapFactory.decodeResource(resources, R.drawable.graw_wall);
        textures[4] = BitmapFactory.decodeResource(resources, R.drawable.red_wall);
    }


    public void draw(Canvas canvas){
        float x = 0, y = 0;
        Paint paint = new Paint();
        for (int i = 0; i < mapArray.length; i++) {
            for (int j = 0; j < mapArray[i].length; j++) {
                canvas.drawBitmap(textures[mapArray[i][j]], x, y, paint);
                x += sizeTexture;
            }
            y += sizeTexture;
            x = 0;
        }
        //changeMap();
    }

    private void loadMapFromFile(BufferedReader br){
        String s;
        String map_str = "";
        try {
            while ((s = br.readLine()) != null) {
                for (char c : s.toCharArray()) {
                    map_str += c;
                }
                map_str += "\n";
            }
            int counter = 0;
            for (int i = 0; i < mapArray.length; i++) {
                for (int j = 0; j < mapArray[i].length; j++) {
                    try{
                        mapArray[i][j] = Integer.parseInt(String.valueOf(map_str.charAt(counter)));
                        System.out.println(counter);
                        counter++;
                    }catch(NumberFormatException e){
                        counter++;
                        j--;
                    }
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeMap(){
        Random random = new Random();
        for (int i = mapArray.length-2; i >= 0; i--) {
            for (int j = 0; j < mapArray[i].length; j++) {
                mapArray[i+1][j] = mapArray[i][j];
            }
        }
        for (int j = 0; j < mapArray[0].length; j++) {
            mapArray[0][j] = random.nextInt(3)+1;
        }
    }




}
