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
 * 陀螺仪活动类，用于显示陀螺仪传感器的数据和参数信息
 */
public class GyroscopeActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private TextView textViewX, textViewY, textViewZ;
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
        textViewX = findViewById(R.id.tvAxis1);
        textViewY = findViewById(R.id.tvAxis2);
        textViewZ = findViewById(R.id.tvAxis3);
        layoutSpecs = findViewById(R.id.layoutSpecs);
        btnToggleRecord = findViewById(R.id.btnToggleRecord);
        btnExport = findViewById(R.id.btnExport);
        etFileName = findViewById(R.id.etFileName);

        tvSensorTitle.setText("陀螺仪传感器");

        // 获取传感器管理器服务
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 获取陀螺仪传感器实例
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        layoutSpecs.removeAllViews();

        // 填充传感器参数详情
        if (gyroscope != null) {
            addSpecItem("传感器类型", "陀螺仪");
            addSpecItem("名称", gyroscope.getName());
            addSpecItem("制造商", gyroscope.getVendor());
            addSpecItem("版本", String.valueOf(gyroscope.getVersion()));
            addSpecItem("最大量程", gyroscope.getMaximumRange() + " rad/s");
            addSpecItem("分辨率", "0.001 rad/s");
            addSpecItem("功耗", gyroscope.getPower() + " mA");
            addSpecItem("最小延迟", gyroscope.getMinDelay() + " μs");
        } else {
            addSpecItem("状态", "设备不支持陀螺仪传感器");
        }

        textViewX.setText("X 轴: 0.00 rad/s");
        textViewY.setText("Y 轴: 0.00 rad/s");
        textViewZ.setText("Z 轴: 0.00 rad/s");

        // 注册传感器事件监听器
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
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
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        textViewX.setText(String.format("X 轴: %.2f rad/s", x));
        textViewY.setText(String.format("Y 轴: %.2f rad/s", y));
        textViewZ.setText(String.format("Z 轴: %.2f rad/s", z));

        if (isRecording) {
            String data = String.format("%.2f,%.2f,%.2f", x, y, z);
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
        // 当活动恢复时，重新获取陀螺仪传感器实例
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope != null) {
            // 重新注册传感器事件监听器
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
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
                fileName = "gyroscope_data.csv"; // 使用默认文件名
            }

            // 获取应用的内部存储目录
            File internalDir = getFilesDir();
            File csvFile = new File(internalDir, fileName);
            FileWriter writer = new FileWriter(csvFile);
            writer.append("X轴角速度,Y轴角速度,Z轴角速度\n");
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