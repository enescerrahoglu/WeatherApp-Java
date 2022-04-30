package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DayWeatherAdapter extends RecyclerView.Adapter<DayWeatherAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DayWeatherModal> dayWeatherModalArrayList;

    public DayWeatherAdapter(Context context, ArrayList<DayWeatherModal> dayWeatherModalArrayList) {
        this.context = context;
        this.dayWeatherModalArrayList = dayWeatherModalArrayList;
    }

    @NonNull
    @Override
    public DayWeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.daily_weather_item, parent, false);
        return new DayWeatherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayWeatherAdapter.ViewHolder holder, int position) {
        DayWeatherModal dayWeatherModal = dayWeatherModalArrayList.get(position);

        holder.dayDate.setText(dayWeatherModal.getDayDate());
        holder.avgHumidity.setText(dayWeatherModal.getAvgHumidity());
        holder.maxTemp.setText(dayWeatherModal.getMaxTemp() + "°");
        holder.minTemp.setText(dayWeatherModal.getMinTemp() + "°");

        int maxTempResID = context.getResources().getIdentifier("d" + dayWeatherModal.getConditionCode() , "drawable", context.getPackageName());
        int minTempResID = context.getResources().getIdentifier("n" + dayWeatherModal.getConditionCode() , "drawable", context.getPackageName());
        holder.maxTempIcon.setImageResource(maxTempResID);
        holder.minTempIcon.setImageResource(minTempResID);
    }

    @Override
    public int getItemCount() {
        return dayWeatherModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView dayDate, avgHumidity, maxTemp, minTemp;
        private ImageView maxTempIcon, minTempIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dayDate = itemView.findViewById(R.id.dayNameTextView);
            avgHumidity = itemView.findViewById(R.id.avgHumidityTextView);
            maxTemp = itemView.findViewById(R.id.maxTempTextView);
            minTemp = itemView.findViewById(R.id.minTempTextView);
            maxTempIcon = itemView.findViewById(R.id.maxTempImageView);
            minTempIcon = itemView.findViewById(R.id.minTempImageView);
        }
    }
}
