package com.firstapp.go2meet_map;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Dataset {
    private static final String TAG = "TAGListOfItems, Dataset";
    private List<Item> listofitems;

    private List<String> eventTypes;

    public Dataset() {
        Log.d(TAG, "Dataset() called");
        listofitems = new ArrayList<>();
        eventTypes =new ArrayList<>();
    }

    public void fill(List<Item> items){
        listofitems=items;
    }

    public void clear(){
        listofitems.clear();
    }

    public int size(){
        return listofitems.size();
    }

    public void addElement(Item item){
        this.listofitems.add(item);
    }

    public void addEventType(String type){
        this.eventTypes.add(type);
    }

    public Item getItemAtPosition(int position){
        return listofitems.get(position);
    }
}
