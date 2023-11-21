package com.firstapp.go2meet_map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

public class DBHelper extends SQLiteOpenHelper{
    SQLiteDatabase db;
    private static final String DB_NAME = "GO2MEETDB";
    private static final String TABLE_NAME = "DATASET";

    // below int is our database version
    private static final int DB_VERSION = 1;
    private final String ID_COL= "id";
    private final String startDate_COL= "startDate";
    private final String endDate_COL="endDate";
    private final String weekdays_COL= "weekdays";
    private final String eventName_COL="eventName";
    private final String isFree_COL="isFree";
    private final String latitude_COL="latitude";
    private final String longitude_COL="longitude";
    private final String time_COL="time";
    private final String url_COL="url";
    private final String place_COL="place";
    private final String type_COL="type";

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + startDate_COL + " TEXT,"
                + endDate_COL + " TEXT,"
                + weekdays_COL + " TEXT,"
                + eventName_COL + " TEXT,"
                + isFree_COL + " BOOL,"
                + latitude_COL + " DOUBLE,"
                + longitude_COL + " DOUBLE,"
                + time_COL + " TEXT,"
                + url_COL + " TEXT,"
                + place_COL+ " TEXT,"
                + type_COL+ " TEXT)";
        Log.d("DATABASE: Create",query);
        // at last we are calling a exec sql
        // method to execute above sql query
        query = "CREATE TABLE " + "DBDATE" +" ("+ "LAST_UPDATE" + " TEXT)";
        db.execSQL(query);
    }
    public void addItem(String startDate,String endDate,String weekdays,String eventName, String isFree,
                        String latitude,String longitude,String time,String url,String place, String type) {
        db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair
        values.put(startDate_COL, startDate);
        values.put(endDate_COL, endDate);
        values.put(weekdays_COL,weekdays);
        values.put(eventName_COL, eventName);
        values.put(isFree_COL, isFree);
        values.put(latitude_COL, latitude);
        values.put(longitude_COL,longitude);
        values.put(time_COL,time);
        values.put(url_COL,url);
        values.put(place_COL,place);
        values.put(type_COL, type);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME,null, values);
    }
    public int getItems(List<Item> items, List<String> types){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                null,    //Get all columns
                null,   //Where (collumns)
                null,   // = (values)
                null,
                null,
                null
        );
        if(cursor.getCount()<10)return -1;
        while (cursor.moveToNext()){
            Item item=new Item();
            item.setStartDate(cursor.getString(1));
            item.setEndDate(cursor.getString(2));
            item.setWeekdays(cursor.getString(3));
            item.setEventName(cursor.getString(4));
            item.setFree(cursor.getString(5));
            item.setLatitude(cursor.getString(6));
            item.setLongitude(cursor.getString(7));
            item.setTime(cursor.getString(8));
            item.setUrl(cursor.getString(9));
            item.setPlace(cursor.getString(10));
            String tipo = cursor.getString(11);
            item.setType(tipo);
            if(!types.contains(tipo)){
                types.add(tipo);
            }
            items.add(item);
        }
        cursor.close();
        return 0;
    }
    public void DBClose(){
        if(db!=null && db.isOpen())db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
