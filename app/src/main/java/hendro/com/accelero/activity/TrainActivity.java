package hendro.com.accelero.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.Manifest;
import android.os.Vibrator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import android.view.View;

import com.opencsv.CSVWriter;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import hendro.com.accelero.R;

public class TrainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private File path;
    private static final int request = 1;

    private static String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    TextView tv_xValue;
    TextView tv_yValue;
    TextView tv_zValue;
    Spinner spinner;
    CheckBox cb_newDataset;
    CheckBox cb_oldDataset;
    Button btn_start;
    Button btn_stop;
    TextView tv_trainStatus;

    private float xVal, yVal, zVal;
    private ArrayList<Float> X, Y, Z;
    private int window = 16;
    private boolean started = false;
    private String condition;

    float currentDis;
    String sx, sy, sz, def;

    protected ArrayAdapter<String> adapter;
    private Sensor accel;
    private Sensor prox;
    private String data;
    protected List<String> list;
    protected boolean newStatus = true;

    CSVWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        ButterKnife.bind(this);

        if (shouldAskPermissions()) {
            verifyStoragePermissions(this);
        }
        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);

        X = new ArrayList<Float>();
        Y = new ArrayList<Float>();
        Z = new ArrayList<Float>();

        tv_xValue = (TextView)findViewById(R.id.tv_xValue);
        tv_yValue = (TextView)findViewById(R.id.tv_yValue);
        tv_zValue = (TextView)findViewById(R.id.tv_zValue);
        spinner = (Spinner)findViewById(R.id.spinner);
        cb_newDataset = (CheckBox)findViewById(R.id.cb_newDataset);
        cb_oldDataset = (CheckBox)findViewById(R.id.cb_oldDataset);
        btn_start = (Button)findViewById(R.id.btnStart);
        btn_stop = (Button)findViewById(R.id.btnStop);
        tv_trainStatus = (TextView)findViewById(R.id.tv_trainStatus);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        prox = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        spinnerElement();
        onCheckedChange();
        btnOnClick(this);
    }

    private void spinnerElement() {
        list = new ArrayList<>();
        list.add("Jogging");
        list.add("Walking");
        list.add("Standing");
        list.add("Sitting");
        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.support_simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
    }

    protected void onCheckedChange() {
        cb_newDataset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newStatus = true;
                    cb_oldDataset.setChecked(false);
                }
            }
        });
        cb_oldDataset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newStatus = false;
                    cb_newDataset.setChecked(false);
                }
            }
        });
    }

    protected void btnOnClick(final SensorEventListener listener){
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                condition = spinner.getSelectedItem().toString();
                if(newStatus){
                    data = "MeanX;MeanY;MeanZ;StdDevX;StdDevY;StdDevZ;MaxX;MaxY;MaxZ;MinX;MinY;MinZ;Class";
                    if(writeDataset(false)){
                        sensorManager.registerListener(listener, prox,
                                SensorManager.SENSOR_DELAY_NORMAL);
                        tv_trainStatus.setText("SUCCESS CREATE DATASED");
                    }
                    else{
                        tv_trainStatus.setText("FAILED CREATE DATASED");
                    }
                } else {
                    sensorManager.registerListener(listener, prox,
                            SensorManager.SENSOR_DELAY_NORMAL);
                    tv_trainStatus.setText("DATA ADDED TO DATASET");
                }
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(listener, prox);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            xVal = event.values[0];
            yVal = event.values[1];
            zVal = event.values[2];

            if (started) {
                tv_xValue.setText(String.valueOf(xVal));
                tv_yValue.setText(String.valueOf(yVal));
                tv_zValue.setText(String.valueOf(zVal));
                X.add(xVal);
                Y.add(yVal);
                Z.add(zVal);
            } else {
                tv_xValue.setText("X Axis Value");
                tv_yValue.setText("Y Axis Value");
                tv_zValue.setText("Z Axis Value");
            }

            if (X.size() == window) {

                float sumX = 0, sumY = 0, sumZ = 0;
                float stdX = 0, stdY = 0, stdZ = 0;
                float stdDevX = 0, stdDevY = 0, stdDevZ = 0;
                float meanX = 0, meanY = 0, meanZ = 0;

                for (int j = 0; j < X.size(); j++) {
                    sumX += X.get(j);
                    sumY += Y.get(j);
                    sumZ += Z.get(j);
                }
                meanX = sumX / window;
                meanY = sumY / window;
                meanZ = sumZ / window;

                for (int j = 0; j < X.size(); j++) {
                    stdX += (X.get(j) - meanX) * (X.get(j) - meanX);
                    stdY += (Y.get(j) - meanY) * (Y.get(j) - meanY);
                    stdZ += (Z.get(j) - meanZ) * (Z.get(j) - meanZ);
                }
                Collections.max(X);
                stdDevX = (float) Math.sqrt(stdX / (window - 1));
                stdDevY = (float) Math.sqrt(stdY / (window - 1));
                stdDevZ = (float) Math.sqrt(stdZ / (window - 1));
                data = String.valueOf(meanX) + ';' + String.valueOf(meanY) + ';' + String.valueOf(meanZ) + ';' + String.valueOf(stdDevX) + ';' + String.valueOf(stdDevY) + ';' + String.valueOf(stdDevZ)
                        + ';' + String.valueOf(Collections.max(X)) + ';' + String.valueOf(Collections.max(Y)) + ';' + String.valueOf(Collections.max(Z)) + ';' + String.valueOf(Collections.min(X))
                        + ';' + String.valueOf(Collections.min(Y)) + ';' + String.valueOf(Collections.min(Z)) + ';' + condition;
                Log.d("a", data);

                writeDataset(true);

                X.clear();
                Y.clear();
                Z.clear();

                Vibrator vibrate = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                vibrate.vibrate(100);
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            currentDis = event.values[0];
            def = "You Can't See This";

            if (currentDis < 5) {
                started = true;
            } else {
                started = false;
            }
        }
    }


    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int ijin = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (ijin != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity, permission, request);
        }
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected boolean writeDataset(boolean newData) {
        try {
            writer = new CSVWriter(new FileWriter(path + File.separator + "dataset.csv", newData), ',');
            String[] entries = data.split(";"); // array of your values
            writer.writeNext(entries);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}



