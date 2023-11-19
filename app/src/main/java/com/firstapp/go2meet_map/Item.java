package com.firstapp.go2meet_map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Item {
    private String startDate;
    private String endDate;
    private String weekdays;
    private String eventName;
    private String isFree;
    private double latitude;
    private double longitude;
    private String time;
    private String url;
    private String place;
    private String type;

    public void setPlace(String place) {
        this.place = place;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLatitude(String latitude) {
        this.latitude =Double.parseDouble(latitude);
    }

    public void setLongitude(String longitude) {
        this.longitude =Double.parseDouble(longitude);
    }

    public String getEventName() {
        return eventName;
    }

    public void setFree(String free) {
        this.isFree=free;
    }

    private Long key;
    public void setStartDate(String startDate) {
            this.startDate= startDate;
    }
    public void setEndDate(String endDate) {
        this.endDate= endDate;
    }
    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setType(String type){
        this.type=type;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public String isFree() {
        return isFree;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    public String getPlace() {
        return place;
    }

    public String getType() {
        return type;
    }

    public Long getKey() {
        return key;
    }

    public Item() {
        eventName="";
        key=0L;
        startDate=null;
        endDate=null;

    }
}
