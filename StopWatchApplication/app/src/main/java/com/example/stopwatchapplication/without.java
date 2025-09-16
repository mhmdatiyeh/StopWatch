package com.example.stopwatchapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class without extends AppCompatActivity {

    private TextView timeTextView;
    private Button startButton, stopButton, resetButton;

    private Handler handler;
    private long startTime, timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private boolean isRunning = false;

    Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000); // To show milliseconds too
            // int hours = mins / 60; // Uncomment if you need hours

            timeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", mins, secs, milliseconds / 10)); // Displaying centiseconds
            // For just seconds: timeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", mins, secs));
            handler.postDelayed(this, 0); // Update as fast as possible for smooth milliseconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_without);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        timeTextView = findViewById(R.id.timeTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        resetButton = findViewById(R.id.resetButton);

        handler = new Handler();

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!isRunning) {
                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(updateTimerThread, 0);
                    isRunning = true;
                    startButton.setText("Pause"); // Optional: Change button text
                } else { // It's running, so pause it
                    timeSwapBuff += timeInMilliseconds;
                    handler.removeCallbacks(updateTimerThread);
                    isRunning = false;
                    startButton.setText("Start"); // Optional: Change button text
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() { // We'll make stop button behave like pause for this example
            public void onClick(View view) {
                if (isRunning) { // Only stop if it's running
                    timeSwapBuff += timeInMilliseconds;
                    handler.removeCallbacks(updateTimerThread);
                    isRunning = false;
                    startButton.setText("Start");
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                handler.removeCallbacks(updateTimerThread);
                isRunning = false;
                startTime = 0L;
                timeInMilliseconds = 0L;
                timeSwapBuff = 0L;
                updatedTime = 0L;
                timeTextView.setText("00:00:00");
                startButton.setText("Start");
            }
        });
    }
}
