package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LinearLayout homeLinearLayout, searchLocationImageLL;
    private ProgressBar mainProgressBar;
    private TextView locationNameTextView, temperatureTextView, conditionTextView, localtimeTextView, sunriseTimeTextView, sunsetTimeTextView;
    private RecyclerView weatherRecyclerView, dayWeatherRecyclerView;
    private EditText locationTextInputEditText;
    private ImageView iconImageView, sunriseImageView, sunsetImageView, notFoundImageView;
    private ImageButton getWeatherButton;
    private ConstraintLayout mainConstraintLayout;

    private ArrayList<WeatherModal> weatherModalArrayList;
    private WeatherAdapter weatherAdapter;

    private ArrayList<DayWeatherModal> dayWeatherModalArrayList;
    private DayWeatherAdapter dayWeatherAdapter;

    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;

    private String locationName;
    private String adminArea;
    private String subAdminArea;

    private String location_name;
    private String location_region;
    private String location_country;
    private String location_tz_id;

    public Map<String, String> dictList;
    public List<String> dataList;

    double lat;
    double lng;
    LatLng latLng;

    private Animation slideAnimation, scaleUpAnimation, scaleDownAnimation;

    private String tempFOWM, tempFeelsOWM, temp_minOWM, temp_maxOWM, date_timeOWM, loc_nameOWM, descriptionOWM, countryCodeOWM, sunriseOWM, sunsetOWM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        mainProgressBar = findViewById(R.id.mainProgressBar);
        locationNameTextView = findViewById(R.id.locationNameTextView);
        localtimeTextView = findViewById(R.id.localTimeTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        conditionTextView = findViewById(R.id.conditionTextView);
        weatherRecyclerView = findViewById(R.id.weatherRecyclerView);
        dayWeatherRecyclerView = findViewById(R.id.dayWeatherRecyclerView);
        locationTextInputEditText = findViewById(R.id.locationTextInputEditText);
        //backImageView = findViewById(R.id.backImageView);
        iconImageView = findViewById(R.id.iconImageView);
        searchLocationImageLL = findViewById(R.id.searchLocationImageLL);
        getWeatherButton = findViewById(R.id.getWeatherButton);
        sunriseTimeTextView = findViewById(R.id.sunriseTimeTextView);
        sunsetTimeTextView = findViewById(R.id.sunsetTimeTextView);
        sunriseImageView = findViewById(R.id.sunriseImageView);
        sunsetImageView = findViewById(R.id.sunsetImageView);
        notFoundImageView = findViewById(R.id.notFoundImageView);

        dictList = new HashMap<String, String>();
        dataList = new ArrayList();

        mainProgressBar.setVisibility(View.GONE);
        notFoundImageView.setVisibility(View.GONE);

        //locationTextInputEditText.setText("");

        getLocation();

        slideAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
        scaleUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_up);
        scaleDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down);

        View view = getWindow().getDecorView().findViewById(android.R.id.content);

        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            private long time = 0;

            @Override
            public void run() {
                // do stuff then
                // can call h again after work!
                iconImageView.startAnimation(slideAnimation);
                time += 1000;
                h.postDelayed(this, 6000);
            }
        }, 6000); // 1 second delay (takes millis)

        mainConstraintLayout = findViewById(R.id.mainConstraintLayout);

        weatherModalArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModalArrayList);
        weatherRecyclerView.setAdapter(weatherAdapter);

        dayWeatherModalArrayList = new ArrayList<>();
        dayWeatherAdapter = new DayWeatherAdapter(this, dayWeatherModalArrayList);
        dayWeatherRecyclerView.setAdapter(dayWeatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);

        }

        mainConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(MainActivity.this);
            }
        });

        try {
            latLng = new LatLng(lat, lng);
            System.out.println(latLng.latitude + " - " + latLng.longitude);
            locationName = getLocationName(lat, lng);
            getWeatherData(locationName, true);
        }catch (Exception e){
            System.out.println("Error 196: " + e.getLocalizedMessage());
        }

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationTextInputEditText.getText().toString();
                if (location.isEmpty()) {
                    locationNameTextView.setText(locationName);
                    getWeatherData(locationName, true);
                } else {
                    locationNameTextView.setText(location);
                    getWeatherData(location, false);
                }
            }
        });

        searchLocationImageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationTextInputEditText.getText().toString();
                if (location.isEmpty()) {
                    locationNameTextView.setText(locationName);
                    getWeatherData(locationName, true);
                } else {
                    locationNameTextView.setText(location);
                    getWeatherData(location, false);
                }
            }
        });

        locationTextInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String location = locationTextInputEditText.getText().toString();
                if (location.isEmpty()) {
                    locationNameTextView.setText(locationName);
                    getWeatherData(locationName, true);
                } else {
                    locationNameTextView.setText(location);
                    getWeatherData(location, false);
                }
                return false;
            }
        });

        searchLocationImageLL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                locationTextInputEditText.setText("");
                return false;
            }
        });

        getWeatherButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                locationTextInputEditText.setText("");
                return false;
            }
        });

        locationNameTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(v, location_name + ", " + location_region + ", " + location_country, Snackbar.LENGTH_LONG).show();
                return false;
            }
        });
    }

    protected void getLocation() {
        homeLinearLayout.setVisibility(View.GONE);
        //You can still do this if you like, you might get lucky:
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }else{
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                //Toast.makeText(MainActivity.this, "latitude:" + lat + " longitude:" + lng, Toast.LENGTH_SHORT).show();
            }
            else{
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }else{
                Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getLocationName(double latitude, double longitude){
        String locationName = "Location not found!";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addressList = gcd.getFromLocation(latitude, longitude, 3);
            String location = addressList.get(0).getSubAdminArea();
            subAdminArea = location;
            adminArea = addressList.get(0).getAdminArea();
            System.out.println(location + "***");
            if(location != null && !location.equals("")){
                locationName = location;
            }else{
                Toast.makeText(this, "Location not found!", Toast.LENGTH_SHORT).show();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return locationName;
    }

    private void getWeatherData(String locationName, boolean isLastKnownLocation){

        mainProgressBar.setVisibility(View.VISIBLE);
        notFoundImageView.setVisibility(View.GONE);
        homeLinearLayout.setVisibility(View.GONE);

        locationName = locationName.toUpperCase();
        String url = String.format("https://api.weatherapi.com/v1/forecast.json?key={yourApiKey}&q=%s&days=3&aqi=yes&alerts=yes", locationName);
        System.out.println(url + "");

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mainProgressBar.setVisibility(View.GONE);
                homeLinearLayout.setVisibility(View.VISIBLE);
                weatherModalArrayList.clear();
                dayWeatherModalArrayList.clear();
                dayWeatherAdapter.notifyDataSetChanged();
                weatherAdapter.notifyDataSetChanged();

                try {
                    System.out.println("1");
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    String conditionCode = response.getJSONObject("current").getJSONObject("condition").getString("code");
                    String locationName = response.getJSONObject("location").getString("name");
                    String localtime = response.getJSONObject("location").getString("localtime");
                    String sunrise = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("astro").getString("sunrise");
                    String sunset = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("astro").getString("sunset");

                    location_name = response.getJSONObject("location").getString("name");
                    location_region = response.getJSONObject("location").getString("region");
                    location_country = response.getJSONObject("location").getString("country");
                    location_tz_id = response.getJSONObject("location").getString("tz_id");

                    SimpleDateFormat output = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date;
                    try {
                        date = input.parse(localtime);
                        localtime = output.format(date);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    double tmp = Double.valueOf(temperature);
                    int temp = (int) tmp;

                    sunsetTimeTextView.setText(sunset);
                    sunriseTimeTextView.setText(sunrise);
                    localtimeTextView.setText(localtime);
                    locationNameTextView.setText(locationName);

                    if(isLastKnownLocation){
                        locationNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_location, 0);
                    }else{
                        locationNameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }

                    temperatureTextView.setText(temp + "°");
                    System.out.println(temperature + " temperature");

                    if(isDay == 1){
                        int resID = getResources().getIdentifier("d" + conditionCode , "drawable", getPackageName());
                        iconImageView.setImageResource(resID);

                        int strID = getResources().getIdentifier("d" + conditionCode , "string", getPackageName());
                        conditionTextView.setText(getString(strID));

                    }else{
                        int resID = getResources().getIdentifier("n" + conditionCode , "drawable", getPackageName());
                        iconImageView.setImageResource(resID);

                        int strID = getResources().getIdentifier("n" + conditionCode , "string", getPackageName());
                        conditionTextView.setText(getString(strID));

                    }


                    sunriseImageView.startAnimation(scaleUpAnimation);
                    sunsetImageView.startAnimation(scaleDownAnimation);

                    JSONObject forecastObject = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObject.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for(int i = 0; i < hourArray.length(); i++){
                        JSONObject hourObject = hourArray.getJSONObject(i);
                        String time = hourObject.getString("time");
                        String hour_temperature = hourObject.getString("temp_c");
                        String icon = hourObject.getJSONObject("condition").getString("icon");
                        String condition_code = hourObject.getJSONObject("condition").getString("code");
                        String wind_speed = hourObject.getString("wind_kph");
                        int is_day = hourObject.getInt("is_day");

                        weatherModalArrayList.add(new WeatherModal(time, hour_temperature, icon, wind_speed, is_day, condition_code));
                    }
                    weatherAdapter.notifyDataSetChanged();

                    for(int i = 0; i < 3; i++){
                        JSONObject forecast = forecastObject.getJSONArray("forecastday").getJSONObject(i);
                        JSONObject dayObject = forecast.getJSONObject("day");

                        String dayDate = forecast.getString("date");
                        int avgHumidity = dayObject.getInt("avghumidity");
                        String dayMaxTemp = dayObject.getString("maxtemp_c");
                        String dayMinTemp = dayObject.getString("mintemp_c");
                        String dayConditionCode = dayObject.getJSONObject("condition").getString("code");

                        SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        Date day_date;
                        try {
                            day_date = sdf2.parse(dayDate);
                            dayDate = sdf1.format(day_date);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        dayWeatherModalArrayList.add(new DayWeatherModal(dayDate, String.valueOf(avgHumidity), dayMaxTemp, dayMinTemp, dayConditionCode));
                        //System.out.println(dayDate + " " + avgHumidity + "%" + " " + dayMaxTemp + " " + dayMinTemp + " " + dayConditionCode);
                    }
                    dayWeatherAdapter.notifyDataSetChanged();

                }catch (Exception e){
                    System.out.println("hata1: " + e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error message: " + error.getMessage());
                //getWeatherData(adminArea, true);
                mainProgressBar.setVisibility(View.GONE);
                notFoundImageView.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, R.string.location_not_found, Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        locationManager.removeUpdates(this);

        //open the map:
        lat = location.getLatitude();
        lng = location.getLongitude();
        Toast.makeText(MainActivity.this, "latitude:" + lat + " longitude:" + lng, Toast.LENGTH_SHORT).show();
    }
}