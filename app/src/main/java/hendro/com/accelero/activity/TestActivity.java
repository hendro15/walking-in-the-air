package hendro.com.accelero.activity;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

import hendro.com.accelero.R;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

import com.opencsv.CSVWriter;


public class TestActivity extends Activity implements SensorEventListener {


    private SensorManager sensorManager;
    private File path;
    private static final int request = 1;

    private Sensor accel;
    private Sensor prox;

    public NaiveBayes model;
    private double aktivitas;

    private float xVal, yVal, zVal;
    private ArrayList<Float> X, Y, Z;
    private int window = 16;
    private boolean started = false;

    float currentDis;
    String sx, sy, sz;

    CSVWriter writer;

    protected Instance instance;

    private Instances data;
    private static String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    TextView tv_xValue;
    TextView tv_yValue;
    TextView tv_zValue;
    Button btn_start;
    Button btn_stop;
    TextView tv_testStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        if (shouldAskPermissions()) {
            verifyStoragePermissions(this);
        }

        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);

        tv_xValue = (TextView)findViewById(R.id.tv_xValue);
        tv_yValue = (TextView)findViewById(R.id.tv_yValue);
        tv_zValue = (TextView)findViewById(R.id.tv_zValue);
        tv_testStatus = (TextView)findViewById(R.id.tv_testStatus);
        btn_start = (Button)findViewById(R.id.btnStart);
        btn_stop = (Button)findViewById(R.id.btnStop);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        prox = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        X = new ArrayList<Float>();
        Y = new ArrayList<Float>();
        Z = new ArrayList<Float>();

        Classifier bayes = new NaiveBayes();
        try{
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(path + File.separator + "dataset.csv"); //dataset
            data = source.getDataSet();
            if (data.classIndex() == -1)
                data.setClassIndex(data.numAttributes() - 1);
            bayes.buildClassifier(data);

            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream (path + File.separator + "bayes-model"));
            oos.writeObject(bayes);
            oos.flush();
            oos.close();

            model = (NaiveBayes) SerializationHelper.read(new FileInputStream(path + File.separator + "bayes-model"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Attribute att = data.classAttribute();
        for(int i = 0; i < data.numClasses();i++) {
            Log.d("test",att.value(i));
        }

        btnOnClik(this);
    }

    protected void btnOnClik(final SensorEventListener listener){
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener(listener, prox,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    aktivitas = model.classifyInstance(instance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tv_testStatus.setText("You are " + data.classAttribute().value((int)aktivitas) + " now");

                sensorManager.unregisterListener(listener, prox);
            }
        });
    }


    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

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

                stdDevX = (float) Math.sqrt(stdX / (window - 1));
                stdDevY = (float) Math.sqrt(stdY / (window - 1));
                stdDevZ = (float) Math.sqrt(stdZ / (window - 1));

                double[] val = new double[] {meanX, meanY, meanZ, stdDevX, stdDevY, stdDevZ, Collections.max(X), Collections.max(Y), Collections.max(Z), Collections.min(X),  Collections.min(Y), Collections.min(Z)};
                instance = new DenseInstance(window, val);
                instance.setDataset(data);

                X.clear();
                Y.clear();
                Z.clear();
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            currentDis = event.values[0];
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

}