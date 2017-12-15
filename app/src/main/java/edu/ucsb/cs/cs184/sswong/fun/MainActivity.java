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

    private ArrayList<FireworksView.Ball> balls = new ArrayList<>();

    // are there balls on the screen to animate
    private boolean animating = false;

    // so we know when to kill ball
    private static int screenWidth;
    private static int screenHeight;

    // some states we want to keep track of
    private static boolean lines = false;
    private static boolean bounce = false;

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
            case R.id.lines:
                lines = !lines;
                break;
            case R.id.bounce:
                bounce = !bounce;
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

                    if(b.isOut() || b.timeAlive > fps*10) {
                        Log.d(TAG, "Ball removed");
                        ball.remove();
                    }
                }

                if(balls.size() == 0) {
                    animating = false;
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

            for(int i = 0; i < 15; i++) {
                Ball b = new Ball(x, y);
                balls.add(b);
            }
        }


        private class Ball implements Serializable {

            float x, y, dx, dy, startX, startY;
            int timeAlive;

            private Ball(float x, float y) {

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

            private void updatePosition() {

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
                x = nextX;
                y = nextY;

                timeAlive++;
            }

            private void drawBall(Canvas c, Paint p) {

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
    }
}
