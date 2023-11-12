package com.firstapp.go2meet_map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Item {
    private Date startDate;
    private Date endDate;
    private String weekdays;
    private String eventName;
    private boolean isFree;
    private long latitude;
    private long longitude;
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

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    private Long key;
    public void setStartDate(String startDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD");
        try {
            this.startDate= formatter.parse(startDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public void setEndDate(String endDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD");
        try {
            this.startDate= formatter.parse(endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD");
        try {
            this.startDate= formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public void setKey(Long key) {
        this.key = key;
    }
    public Item() {
        eventName="";
        key=0L;
        startDate=null;
        endDate=null;

    }

    public Item(String EventName, String date, long key){
        this.eventName = eventName;
        SimpleDateFormat formatter = new SimpleDateFormat("DD/MM/YYYY");
        try {
            this.startDate= formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.key=key;
    }

    public String getEventName() {
        return eventName;
    }
}
