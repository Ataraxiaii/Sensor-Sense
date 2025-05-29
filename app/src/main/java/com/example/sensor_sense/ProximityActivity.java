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

public class ProximityActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private TextView textViewProximity;
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
        textViewProximity = findViewById(R.id.tvAxis1);
        layoutSpecs = findViewById(R.id.layoutSpecs);
        btnToggleRecord = findViewById(R.id.btnToggleRecord);
        btnExport = findViewById(R.id.btnExport);
        etFileName = findViewById(R.id.etFileName);

        tvSensorTitle.setText("距离传感器");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        layoutSpecs.removeAllViews();

        if (proximitySensor != null) {
            addSpecItem("传感器类型", "距离传感器");
            addSpecItem("名称", proximitySensor.getName());
            addSpecItem("制造商", proximitySensor.getVendor());
            addSpecItem("版本", String.valueOf(proximitySensor.getVersion()));
            addSpecItem("最大量程", proximitySensor.getMaximumRange() + " cm");
            addSpecItem("分辨率", "0.001 cm");
            addSpecItem("功耗", proximitySensor.getPower() + " mA");
            addSpecItem("最小延迟", proximitySensor.getMinDelay() + " μs");
        } else {
            addSpecItem("状态", "设备不支持距离传感器");
        }

        textViewProximity.setText("距离: 0.00 cm");

        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_GAME);
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
        float proximity = event.values[0];
        textViewProximity.setText(String.format("距离: %.2f cm", proximity));
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
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
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