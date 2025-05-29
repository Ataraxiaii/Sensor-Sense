package com.example.sensor_sense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {

    private final List<SensorModel> sensorList;
    private final OnSensorClickListener listener;

    public interface OnSensorClickListener {
        void onSensorClick(SensorModel sensor);
    }

    public SensorAdapter(List<SensorModel> sensorList, OnSensorClickListener listener) {
        this.sensorList = sensorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_sensor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SensorModel sensor = sensorList.get(position);
        //holder.ivIcon.setImageResource(sensor.getIconRes());
        holder.tvTitle.setText(sensor.getTitle());

        holder.itemView.setOnClickListener(v -> listener.onSensorClick(sensor));
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //ImageView ivIcon;
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            //ivIcon = itemView.findViewById(R.id.ivSensorIcon);
            tvTitle = itemView.findViewById(R.id.tvSensorTitle);
        }
    }
}