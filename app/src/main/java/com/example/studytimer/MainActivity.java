package com.example.studytimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    ImageButton start, pause, stop;
    TextView lastSession;
    String session_name;
    Chronometer chronometer;
    Handler handler;
    EditText taskType;
    String currentTime;
    long tmillisec, tstart, tBuff, tUpdate = 0L;
    int sec, min, millisec;

    int kamonya;
    public static final String SHARES_PREFERENCE = "shared_preferences";
    public static final String PREVIOUS_TIME = "previous_time";
    public static final String LAST_TASK_NAME = "last_task_name";
    public static final String TASK_NAME = "task_name";

    String _prev_time;
    String _prev_task_name;
    String _taskname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chronometer = findViewById(R.id.timer);
        start = findViewById(R.id.startTimer);
        pause = findViewById(R.id.pauseTimer);
        stop = findViewById(R.id.stopTimer);
        taskType = findViewById(R.id.sessionName);
        lastSession = findViewById(R.id.lastSession);
        handler = new Handler();


        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                tstart = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                chronometer.start();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tBuff += tmillisec;
                handler.removeCallbacks(runnable);
                chronometer.stop();
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                tBuff += tmillisec;
                handler.removeCallbacks(runnable);
                chronometer.stop();

                session_name = taskType.getText().toString();
                currentTime = chronometer.getText().toString();
                lastSession.setText("You spent " + min + ":" + sec + ":" + millisec + " on " + session_name + " last time");

            }
        });

        loadData();
        updateViews();

    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tmillisec = SystemClock.uptimeMillis() - tstart;
            tUpdate = tBuff + tmillisec;
            sec = (int) (tUpdate / 1000);
            min = sec / 60;
            sec = sec % 60;
            millisec = (int) (tUpdate % 100);
            chronometer.setText(String.format("%02d", min) + ":" + String.format("%02d", sec) + ":" + String.format("%02d", millisec));
            handler.postDelayed(this, 60);
            saveData();
        }
    };

    //save state
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("container", lastSession.getText().toString());
        outState.putString("chronometer_time", chronometer.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastSession.setText(savedInstanceState.getString("container"));
        chronometer.setText(savedInstanceState.getString("chronometer_time"));
    }

    //save data in shared preferences
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARES_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_TASK_NAME,session_name);
        editor.putString(PREVIOUS_TIME,chronometer.getText().toString());
        editor.apply();
    }

    public void loadData (){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARES_PREFERENCE, Context.MODE_PRIVATE);
        _prev_time = sharedPreferences.getString(PREVIOUS_TIME,"");
        _prev_task_name = sharedPreferences.getString(LAST_TASK_NAME,"");
    }

    public void updateViews(){
        lastSession.setText("You spent " + _prev_time + " on " + _prev_task_name + "task");
    }

}
