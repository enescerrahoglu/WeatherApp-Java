package com.example.weatherapp;

public class DayWeatherModal {
    private String dayDate;
    private String avgHumidity;
    private String maxTemp;
    private String minTemp;
    private String conditionCode;

    public DayWeatherModal(String dayDate, String avgHumidity, String maxTemp, String minTemp, String conditionCode) {
        this.dayDate = dayDate;
        this.avgHumidity = avgHumidity;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.conditionCode = conditionCode;
    }

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public String getAvgHumidity() {
        return avgHumidity;
    }

    public void setAvgHumidity(String avgHumidity) {
        this.avgHumidity = avgHumidity;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }
}
