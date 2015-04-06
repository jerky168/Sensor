package com.xunce.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    private SensorManager sensorManager;

    private TextView xAxis;
    private TextView yAxis;
    private TextView zAxis;

    private float xAxisValue = Float.NaN;
    private float yAxisValue = Float.NaN;
    private float zAxisValue = Float.NaN;

    private static final String TAG = "SENSOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xAxis = (TextView) findViewById(R.id.xAxis);
        yAxis = (TextView) findViewById(R.id.yAxis);
        zAxis = (TextView) findViewById(R.id.zAxis);

        xAxis.setEnabled(false);
        yAxis.setEnabled(false);
        zAxis.setEnabled(false);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Timer updateTimer = new Timer("Accelerometer");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGUI();
            }
        }, 0, 100);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor : sensorList) {
            if(Build.VERSION.SDK_INT > 20 ) {
                Log.d(TAG, sensor.getStringType() + ":" + sensor.getName());
            }
            else {
                Log.d(TAG, sensor.getType() + ":" + sensor.getName());
            }
        }
        registerAccelerometer();
    }

    final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    xAxisValue = sensorEvent.values[0];
                    yAxisValue = sensorEvent.values[1];
                    zAxisValue = sensorEvent.values[2];

                    break;
                default:
                    break;
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO React to a change in Sensor accuracy.
        }
    };

    private void registerAccelerometer() {
        int sensorType = Sensor.TYPE_ACCELEROMETER;
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(sensorType),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void updateGUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String unit = getResources().getString(R.string.degree);
                if (!Float.isNaN(xAxisValue)) {
                    xAxis.setText(xAxisValue + unit);
                }
                if (!Float.isNaN(yAxisValue)) {
                    yAxis.setText(yAxisValue + unit);
                }
                if (!Float.isNaN(zAxisValue)) {
                    zAxis.setText(zAxisValue + unit);
                }
            }
        });
    }
}
