package com.example.sensor_sense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SensorListActivity extends AppCompatActivity implements SensorAdapter.OnSensorClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);

        // Initialize the sensor list data
        List<SensorModel> sensorList = new ArrayList<>();
        sensorList.add(new SensorModel(1, "加速度计传感器", Sensor.TYPE_ACCELEROMETER));
        sensorList.add(new SensorModel(2, "陀螺仪传感器", Sensor.TYPE_GYROSCOPE));
        sensorList.add(new SensorModel(3, "光照传感器", Sensor.TYPE_LIGHT));
        sensorList.add(new SensorModel(4, "距离传感器", Sensor.TYPE_PROXIMITY));
        sensorList.add(new SensorModel(5, "方向传感器", Sensor.TYPE_ROTATION_VECTOR));

        // Check the data
        for (SensorModel sensor : sensorList) {
            Log.d("SensorListActivity", "Sensor title: " + sensor.getTitle());
        }

        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SensorAdapter adapter = new SensorAdapter(sensorList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSensorClick(SensorModel sensor) {
        // Navigate to the corresponding page based on the sensor type
        Intent intent = null;
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                intent = new Intent(this, AccelerationActivity.class);
                break;
            case Sensor.TYPE_LIGHT:
                intent = new Intent(this, LightSensorActivity.class);
                break;
            case Sensor.TYPE_GYROSCOPE:
                intent = new Intent(this, GyroscopeActivity.class);
                break;
            case Sensor.TYPE_PROXIMITY:
                intent = new Intent(this, ProximityActivity.class);
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                intent = new Intent(this, OrientationActivity.class);
                break;
        }
        if (intent != null) {
            intent.putExtra("sensor_type", sensor.getType());
            startActivity(intent);
        }
    }
}