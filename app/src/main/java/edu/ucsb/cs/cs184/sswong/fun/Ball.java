package edu.ucsb.cs.cs184.sswong.fun;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by swong on 12/14/17.
 */

class Ball implements Serializable {

    private float x, y, dx, dy, startX, startY;
    private float radius;
    int timeAlive;
    private int alpha, red, green, blue;

    public Ball(float x, float y) {

        this.x = x;
        this.y = y;
        startX = x;
        startY = y;

        // velocity = ([-15,15], [-15,15])
        Random rand = new Random();
        dx = 30*rand.nextFloat()-15;
        dy = 30*rand.nextFloat()-15;

        // radius = [15, 30]
        radius = rand.nextInt(15)+15;

        timeAlive = rand.nextInt(100);

        alpha = rand.nextInt(255);
        red = rand.nextInt(255);
        green = rand.nextInt(255);
        blue = rand.nextInt(255);
    }

    public void updatePosition() {

        float nextX = x + dx;
        float nextY = y + dy;

        if(MainActivity.walls) {

            if(nextX > MainActivity.screenWidth-radius || nextX < 0+radius ) {
                dx = -dx;
                nextX = x + dx;
            }

            if(nextY > MainActivity.screenHeight-radius || nextY < 0+radius ) {
                dy = -dy;
                nextY = y + dy;
            }
        }

        if(MainActivity.gravity) {
            dy+= 0.5;
        }

        x = nextX;
        y = nextY;

        timeAlive++;
    }

    public void drawBall(Canvas c) {

        if(false) {
            Random r = new Random();
            radius = r.nextInt(15)+15;
        }

        if(MainActivity.flicker) {
            randomColor();
        }
        Paint paint = new Paint();
        paint.setARGB(alpha, red, green, blue);

        c.drawCircle(x, y, radius, paint);

        if(MainActivity.lines) {
            paint.setStrokeWidth(1);
            c.drawLine(startX, startY, x, y, paint);
        }
    }

    public boolean isOut() {

        return (x > MainActivity.screenWidth-radius || x < 0+radius ||
                y > MainActivity.screenHeight-radius || y < 0+radius);
    }

    private void randomColor() {

        Random rand = new Random();

        alpha = rand.nextInt(255);
        red = rand.nextInt(255);
        green = rand.nextInt(255);
        blue = rand.nextInt(255);
    }
}