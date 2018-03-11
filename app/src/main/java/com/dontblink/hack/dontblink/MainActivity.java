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

import com.google.android.glass.eye.EyeGesture;
import com.google.android.glass.eye.EyeGestureManager;
import com.google.android.glass.eye.EyeGestureManager.Listener;

import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


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

    private TextView timerLabel;

    // Timer variables
    private Handler mHandler;
    private int timeToStart = 5;
    private boolean gameStarted = false;

    private long currentTime = System.currentTimeMillis();

    private float accx;
    private float accy;
    private float accz;
    private boolean hasJumped = false;

    private String[] commands = {"WAIT", "JUMP", "THE GOVERNMENT IS LIZARDS"};


    private Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                //if (timeToStart == -1) return;
                timerView = (TextView)findViewById(R.id.timer_label);
                timerView.setText(String.valueOf(timeToStart));
                timeToStart--;
                if (timeToStart == -1) {
                    mHandler.removeCallbacks(mStatusChecker);
                    timerView.setTextSize(TypedValue.COMPLEX_UNIT_SP,70);
                    if (System.currentTimeMillis() > currentTime + 5000) {
                        currentTime = System.currentTimeMillis();
                        if (timerView.getText().toString().equals("JUMP")){
                            if (!hasJumped) {
                                timerView.setText("YOU LOSE");
                                finish();
                            }
                        }
                        timerView.setText(commands[(int) Math.random() * 2]);

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
        setContentView(R.layout.lobby);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void startTimer() {
        timeToStart = 5;
        mStatusChecker.run();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //startTimer();
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
            //Log.v(TAG, "y = " + accy);
            //Log.v(TAG, "z = " + accz);
            if (accy > 17 && timerView.getText().toString().equals("JUMP")) {
                Log.v(TAG, "JUMPED");
                hasJumped = true;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameStarted) {
                    Log.i(TAG, "y: " + accy);
                    timerView.setBackgroundColor(0xfff00000);
                    timerView.setText(String.valueOf(accy));
                    /*(new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setContentView(R.layout.lobby);
                            mSocket.disconnect();
                            mSocket.connect();
                        }
                    }, 1000); */

                }
            }
        });
    }

}


