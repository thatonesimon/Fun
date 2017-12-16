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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "Fun/Simon";

    private DrawingThread drawingThread;

    private int fps = 30;

    private ArrayList<Ball> balls = new ArrayList<>();

    // are there balls on the screen to animate
    private boolean animating = false;

    // so we know when to kill ball
    public static int screenWidth;
    public static int screenHeight;

    // some states we want to keep track of
    public static boolean paused = false;
    public static boolean lines = false;
    public static boolean walls = true;
    public static boolean gravity = true;
    public static boolean flicker = false;

    FireworksView fireworksView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.main);
        fireworksView = new FireworksView(this);
        layout.addView(fireworksView);

        Display display = getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();

        try {
            animating = (boolean) savedInstanceState.get("animating");
            balls = (ArrayList<Ball>) savedInstanceState.get("balls");
        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "" + layout.getWidth() + fireworksView.getWidth());
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

            case R.id.pause:
                paused = !paused;
                if(paused) {
                    item.setTitle("Play");
                } else {
                    item.setTitle("Pause");
                }
                break;
            case R.id.clear:
                balls.clear();
                break;
            case R.id.lines:
                lines = !lines;
                break;
            case R.id.walls:
                walls = !walls;
                break;
            case R.id.gravity:
                gravity = !gravity;
                break;
            case R.id.flicker:
                flicker = !flicker;
                break;
        }

        if(item.isCheckable()) {
            item.setChecked(!item.isChecked());
        }
        return super.onOptionsItemSelected(item);
    }

    public class FireworksView extends View {

        public FireworksView(Context context) {
            super(context);

            drawingThread = new DrawingThread(this, fps);
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
                    b.drawBall(canvas);
                    if(!paused) {
                        b.updatePosition();
                    }

                    if(b.isOut() || b.timeAlive > fps*10) {
                        Log.d(TAG, "Ball removed");
                        ball.remove();
                    }
                }

                if(balls.size() == 0) {
                    animating = false;
                    drawingThread.stop();
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            screenWidth = fireworksView.getWidth();
            screenHeight = fireworksView.getHeight();

            animating = true;
            drawingThread.start();

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
    }
}
