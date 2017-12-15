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
    int timeAlive;

    public Ball(float x, float y) {

        this.x = x;
        this.y = y;
        startX = x;
        startY = y;

        // velocity = ([-15,15], [-15,15])
        Random rand = new Random();
        dx = 30*rand.nextFloat()-15;
        dy = 30*rand.nextFloat()-15;

        timeAlive = rand.nextInt(100);
    }

    public void updatePosition() {

        float nextX = x + dx;
        float nextY = y + dy;

        if(MainActivity.bounce) {

            if(nextX > MainActivity.screenWidth || nextX < 0 ) {
                dx = -dx;
                nextX = x + dx;
            }

            if(nextY > MainActivity.screenHeight|| nextY < 0 ) {
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

    public void drawBall(Canvas c, Paint p) {

        Random r = new Random();
        c.drawCircle(x, y, r.nextInt(15)+15, p);

        if(MainActivity.lines) {
            p.setStrokeWidth(1);
            c.drawLine(startX, startY, x, y, p);
        }
    }

    public boolean isOut() {

        return (x > MainActivity.screenWidth || x < 0 ||
                y > MainActivity.screenHeight|| y < 0 );
    }
}