package com.example.sensor_sense;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
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

/**
 * 光照传感器活动类，用于显示光照传感器的数据和参数信息
 */
public class LightSensorActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private TextView textViewLight;
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
        textViewLight = findViewById(R.id.tvAxis1);
        layoutSpecs = findViewById(R.id.layoutSpecs);
        btnToggleRecord = findViewById(R.id.btnToggleRecord);
        btnExport = findViewById(R.id.btnExport);
        etFileName = findViewById(R.id.etFileName);

        tvSensorTitle.setText("光照传感器");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        layoutSpecs.removeAllViews();

        // 传感器参数详情
        if (lightSensor != null) {
            addSpecItem("传感器类型", "光照传感器");
            addSpecItem("名称", lightSensor.getName());
            addSpecItem("制造商", lightSensor.getVendor());
            addSpecItem("版本", String.valueOf(lightSensor.getVersion()));
            addSpecItem("最大量程", lightSensor.getMaximumRange() + " lx");
            addSpecItem("分辨率", lightSensor.getResolution() + " lx");
            addSpecItem("功耗", lightSensor.getPower() + " mA");
            addSpecItem("最小延迟", lightSensor.getMinDelay() + " μs");
        } else {
            addSpecItem("状态", "设备不支持光照传感器");
        }

        textViewLight.setText("光照强度: 0.00 lx");

        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_GAME);
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

    /**
     * 向布局中添加传感器参数项
     * @param title 参数标题
     * @param value 参数值
     */
    private void addSpecItem(String title, String value) {
        TextView textView = new TextView(this);
        textView.setText(String.format("%s: %s", title, value));
        textView.setTextSize(16);
        textView.setPadding(0, 8, 0, 0);
        layoutSpecs.addView(textView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float light = event.values[0];

        textViewLight.setText(String.format("光照强度: %.2f lx", light));

        if (isRecording) {
            String data = String.format("%.2f", light);
            recordedData.add(data);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 处理传感器精度变化事件
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当活动暂停时，取消注册传感器事件监听器
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
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
                fileName = "light_sensor_data.csv"; // 使用默认文件名
            }

            File internalDir = getFilesDir();
            File csvFile = new File(internalDir, fileName);
            FileWriter writer = new FileWriter(csvFile);
            writer.append("光照强度(lx)\n");
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