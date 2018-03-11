package com.dontblink.hack.dontblink;

import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;
import android.os.Handler;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity implements SensorEventListener{

    /**
     * {@link CardScrollView} to use as the main content view.
     */
    private CardScrollView mCardScroller;


    private View mView;
    private CardBuilder card;
    private SensorManager mSensorManager;
    private Sensor mAcc;
    private static final String TAG = "Jump";

    public TextView timerView;

    // Timer variables
    private Handler mHandler;
    private int timeToStart = 5;
    private boolean gameStarted = false;
    private int random;

    private long currentTime = System.currentTimeMillis();

    private float accx;
    private float accy;
    private float accz;
    private boolean hasJumped = false;

    private String[] commands = {"WAIT", "JUMP", "TAP", "EARTH IS FLAT"};


    private Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (timeToStart == -1) return;
                if (timeToStart > -1) {
                    timerView = (TextView) findViewById(R.id.timer_label);
                    timerView.setText(String.valueOf(timeToStart));
                }
                timeToStart--;
                if (timeToStart <= -1 ) {
                    timeToStart = -2;
                    mHandler.removeCallbacks(mStatusChecker);
                    timerView.setTextSize(TypedValue.COMPLEX_UNIT_SP,70);
                    if (System.currentTimeMillis() > currentTime + 1500) {
                        currentTime = System.currentTimeMillis();
                        if (timerView.getText().toString().equals("JUMP")){
                            if (!hasJumped) {
                                timerView.setText("YOU LOSE");
                                Log.i(TAG, "DID NOT JUMP");
                                timeToStart = -1;
                                return; //System.exit(0);
                            }
                        }
                        hasJumped = false;
                        random =  (int) (Math.random() * 3);
                        timerView.setText(commands[random]);
                        Log.i(TAG, String.valueOf(random) );

                    }

                    gameStarted = true;
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mHandler = new Handler();
        setContentView(R.layout.timer);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void startTimer() {
        timeToStart = 5;
        mStatusChecker.run();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }



    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accx = event.values[0];
            accy = event.values[1];
            accz = event.values[2];
            // Do something with this sensor value.
            //Log.v(TAG, "x = " + accx);
            Log.v(TAG, "y = " + accy);
            //Log.v(TAG, "z = " + accz);
            if (accy > 17 && timerView.getText().toString().equals("JUMP")) {
                Log.i(TAG, "JUMPED");
                hasJumped = true;
            }
        }


    }

}


