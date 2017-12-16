package edu.ucsb.cs.cs184.sswong.fun;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by swong on 12/15/17.
 */

public class Ripple implements Serializable{

    private float x, y;
    private float radius;
    int timeAlive;

    public Ripple(float x, float y) {

        this.x = x;
        this.y = y;
        radius = 0;

        Random rand = new Random();
        timeAlive = rand.nextInt(100);
    }

    public void updateRadius() {
        radius++;
    }

    public void drawRipple(Canvas c, Paint p) {

        p.setStyle(Paint.Style.STROKE);

        Random r = new Random();
        c.drawCircle(x, y, r.nextInt(15)+15, p);

        p.setStyle(Paint.Style.FILL_AND_STROKE);

    }
}
