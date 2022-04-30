package com.example.weatherapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherModal> weatherModalArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherModal> weatherModalArrayList) {
        this.context = context;
        this.weatherModalArrayList = weatherModalArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {

        WeatherModal weatherModal = weatherModalArrayList.get(position);

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat output = new SimpleDateFormat("HH:mm");
        String timeClock = "";
        try {
            Date t = input.parse(weatherModal.getTime());
            holder.timeTV.setText(output.format(t));
            timeClock = output.format(t);
        }catch (ParseException e){
            e.printStackTrace();
        }

        holder.temperatureTV.setText(weatherModal.getTemperature() + " °C");
        holder.windSpeedTV.setText(weatherModal.getWindSpeed() + " km/h");

        System.out.println(weatherModal.getConditionCode() + " :concode");

        int strId;
        if(weatherModal.getIsDay() == 1){
            int resID = context.getResources().getIdentifier("d" + weatherModal.getConditionCode() , "drawable", context.getPackageName());
            strId = context.getResources().getIdentifier("d" + weatherModal.getConditionCode() , "string", context.getPackageName());
            holder.conditionIV.setImageResource(resID);
        }else{
            int resID = context.getResources().getIdentifier("n" + weatherModal.getConditionCode() , "drawable", context.getPackageName());
            strId = context.getResources().getIdentifier("n" + weatherModal.getConditionCode() , "string", context.getPackageName());
            holder.conditionIV.setImageResource(resID);
        }

        String finalTimeClock = timeClock;
        holder.weatherItemLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(v, finalTimeClock + " -> " + context.getString(strId), Snackbar.LENGTH_LONG).show();
                return false;
            }
        });

        /*Glide.with(context)
                .load("https:".concat(weatherModal.getIcon()))
                .override(1000, 1000)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.conditionIV);*/

    }

    @Override
    public int getItemCount() {
        return weatherModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView windSpeedTV, temperatureTV, timeTV;
        private ImageView conditionIV;
        private LinearLayout weatherItemLl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            windSpeedTV = itemView.findViewById(R.id.weatherWindSpeedTV);
            temperatureTV = itemView.findViewById(R.id.weatherTemperatureTV);
            timeTV = itemView.findViewById(R.id.weatherTimeTV);
            conditionIV = itemView.findViewById(R.id.weatherConditionIV);
            weatherItemLl = itemView.findViewById(R.id.weatherItemLl);
        }
    }
}
