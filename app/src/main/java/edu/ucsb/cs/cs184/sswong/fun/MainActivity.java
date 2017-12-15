package edu.ucsb.cs.cs184.sswong.fun;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Fun/Simon";

    private DrawingThread drawingThread;

    private int fps = 30;
    // how long balls have been moving
    private int seconds = 0;

    private ArrayList<FireworksView.Ball> balls = new ArrayList<>();

    // are there balls on the screen to animate
    private boolean animating = false;

    // so we know when to kill ball
    private static int screenWidth;
    private static int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.main);
        layout.addView(new FireworksView(this));

        Display display = getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();

        try {
            animating = (boolean) savedInstanceState.get("animating");
            balls = (ArrayList<FireworksView.Ball>) savedInstanceState.get("balls");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("animating", animating);
        outState.putSerializable("balls", balls);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                balls.clear();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FireworksView extends View {

        public FireworksView(Context context) {
            super(context);

            drawingThread = new DrawingThread(this, fps);
            drawingThread.start();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if(animating) {
                Paint p = new Paint();
                Random r = new Random();
                Iterator<Ball> ball = balls.iterator();
                while(ball.hasNext()) {
                    Ball b = ball.next();
                    randomColor(p, r);
                    b.drawBall(canvas, p);
                    b.updatePosition();
                    if(b.isOut()) {
                        Log.d(TAG, "Ball removed");
                        ball.remove();
                    }
                }
                seconds++;

                // delete balls after 10 seconds
                if(seconds > fps*10) {
                    animating = false;
                    seconds = 0;
                    balls.clear();
                }
            }
        }

        private void randomColor(Paint p, Random r) {
            int alpha = r.nextInt(255);
            int red = r.nextInt(255);
            int green = r.nextInt(255);
            int blue = r.nextInt(255);
            p.setARGB(alpha, red, green, blue);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            animating = true;
            // reset time
            seconds = 0;

            float x = event.getX();
            float y = event.getY();

            switch(event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    createBalls(x, y);
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    createBalls(x, y);
                    break;

                case MotionEvent.ACTION_MOVE:
                    createBalls(x, y);
                    break;
            }
            return true;
        }


        private void createBalls(float x, float y) {
            // clear array
            // balls.clear();

            for(int i = 0; i < 15; i++) {
                Ball b = new Ball(x, y);
                balls.add(b);
            }
        }


        private class Ball implements Serializable {
            float x, y, dx, dy, xx, yy;

            private Ball(float x, float y) {
                this.x = x;
                this.y = y;
                xx = x;
                yy = y;

                // velocity = ([-20,20], [-20,20])
                Random rand = new Random();
                this.dx = 30*rand.nextFloat()-15;
                this.dy = 30*rand.nextFloat()-15;
            }

            private void updatePosition() {
                x = x + dx;
                y = y + dy;
            }

            private void drawBall(Canvas c, Paint p) {
                Random r = new Random();
                c.drawCircle(x, y, r.nextInt(15)+15, p);
//                p.setStrokeWidth(1);
//                c.drawLine(xx, yy, x, y, p);
            }

            public boolean isOut() {
                return (x > MainActivity.screenWidth || x < 0 ||
                        y > MainActivity.screenHeight|| y < 0 );
            }
        }
    }
}
