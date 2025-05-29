package com.example.sensor_sense;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrientationActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private TextView textViewAzimuth, textViewPitch, textViewRoll;
    private LinearLayout layoutSpecs;
    private Button btnToggleRecord;
    private Button btnExport;
    private boolean isRecording = false;
    private List<String> recordedData = new ArrayList<>();
    private EditText etFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        TextView tvSensorTitle = findViewById(R.id.tvSensorTitle);
        textViewAzimuth = findViewById(R.id.tvAxis1);
        textViewPitch = findViewById(R.id.tvAxis2);
        textViewRoll = findViewById(R.id.tvAxis3);
        layoutSpecs = findViewById(R.id.layoutSpecs);
        btnToggleRecord = findViewById(R.id.btnToggleRecord);
        btnExport = findViewById(R.id.btnExport);
        etFileName = findViewById(R.id.etFileName);

        tvSensorTitle.setText("方向传感器");

        // 初始化传感器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        layoutSpecs.removeAllViews();

        // 填充参数详情
        if (orientationSensor != null) {
            addSpecItem("传感器类型", "方向传感器");
            addSpecItem("名称", orientationSensor.getName());
            addSpecItem("制造商", orientationSensor.getVendor());
            addSpecItem("版本", String.valueOf(orientationSensor.getVersion()));
            addSpecItem("最大量程", orientationSensor.getMaximumRange() + " °");
            addSpecItem("分辨率", "0.001 °");
            addSpecItem("功耗", orientationSensor.getPower() + " mA");
            addSpecItem("最小延迟", orientationSensor.getMinDelay() + " μs");
        } else {
            addSpecItem("状态", "设备不支持方向传感器");
        }

        // 设置初始数据
        textViewAzimuth.setText("方位角: 0.00 °");
        textViewPitch.setText("俯仰角: 0.00 °");
        textViewRoll.setText("翻滚角: 0.00 °");

        // 注册监听器
        if (orientationSensor != null) {
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        btnToggleRecord.setOnClickListener(v -> {
            isRecording = !isRecording;
            if (isRecording) {
                btnToggleRecord.setText("停止记录");
                recordedData.clear();
            } else {
                btnToggleRecord.setText("开始记录");
            }
        });

        btnExport.setOnClickListener(v -> saveDataToCSV());
    }

    private void addSpecItem(String title, String value) {
        TextView textView = new TextView(this);
        textView.setText(String.format("%s: %s", title, value));
        textView.setTextSize(16);
        textView.setPadding(0, 8, 0, 0);
        layoutSpecs.addView(textView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuth = event.values[0];
        float pitch = event.values[1];
        float roll = event.values[2];

        textViewAzimuth.setText(String.format("方位角: %.2f °", azimuth));
        textViewPitch.setText(String.format("俯仰角: %.2f °", pitch));
        textViewRoll.setText(String.format("翻滚角: %.2f °", roll));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 处理精度变化
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (orientationSensor != null) {
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * 将记录的数据保存为 CSV 文件
     */
    private void saveDataToCSV() {
        if (recordedData.isEmpty()) {
            return;
        }
        try {

            String fileName = etFileName.getText().toString().trim();
            if (fileName.isEmpty()) {
                fileName = "orientation_sensor_data.csv"; // 使用默认文件名
            }
            File internalDir = getFilesDir();
            File csvFile = new File(internalDir, fileName);
            FileWriter writer = new FileWriter(csvFile);
            writer.append("方位角,俯仰角,翻滚角\n");
            for (String data : recordedData) {
                writer.append(data).append("\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}