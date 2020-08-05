package com.example.michael.whatsfordinner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    List<String> typeNames;
    SQLiteStatement statement = null;
    private static String DATABASE_NAME = "food_db";
    // Possible extras = image, Ingredient list
    private static String SQL_CREATE_INFO_TABLE =
            "CREATE TABLE IF NOT EXISTS infoTable (" +
                    "name TEXT NOT NULL PRIMARY KEY, " +
                    "URL TEXT);";
    private static String SQL_CREATE_TYPE_TABLE =
            "CREATE TABLE IF NOT EXISTS typeTable (" +
                    "name TEXT NOT NULL PRIMARY KEY, " +
                    "Vegetable INTEGER DEFAULT 0," +
                    "Meat INTEGER DEFAULT 0," +
                    "Carbs INTEGER DEFAULT 0);";
    private static String SQL_DROP_INFO_TABLE = "DROP TABLE IF EXISTS infoTable;";
    private static String SQL_DROP_TYPE_TABLE = "DROP TABLE IF EXISTS typeTable;";
    private static String SQL_INSERT_DEFAULT_INFO_VALUES =
            "INSERT INTO infoTable(name) VALUES('Broccoli'),('Meatloaf'),('Rice');";
    private static String SQL_INSERT_DEFAULT_TYPE_VALUES =
            "INSERT INTO typeTable(name,Vegetable,Meat,Carbs) VALUES" +
                    "('Broccoli',1,0,0),('Meatloaf',0,1,0),('Rice',0,0,1);";

    // Constructor
    public DBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version){
        super(context, dbName, factory, version);
    }

    // On create,
    public void onCreate(SQLiteDatabase db){
        Log.i("foo","0");
        db.execSQL(SQL_CREATE_INFO_TABLE);
        Log.i("foo","1");
        db.execSQL(SQL_CREATE_TYPE_TABLE);
        db.execSQL(SQL_INSERT_DEFAULT_INFO_VALUES);
        db.execSQL(SQL_INSERT_DEFAULT_TYPE_VALUES);
    }

    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DROP_INFO_TABLE);
        db.execSQL(SQL_DROP_TYPE_TABLE);
        onCreate(db);
    }

    public List<String> getTypeNames(SQLiteDatabase db){
        updateTypeNames(db);
        return typeNames;
    }

    public void updateTypeNames(SQLiteDatabase db){
        Cursor c = db.rawQuery("PRAGMA table_info(" + "typeTable" + ")", null);
        try {
            c.moveToFirst();
            c.moveToNext();
            typeNames = new ArrayList<String>();
            while(!c.isAfterLast()) {
                //Log.i("PRAGMA",c.getString(0));
                typeNames.add(c.getString(1));
                c.moveToNext();
            }
        } finally {
            c.close();
        }
    }

    //Adds a new row in the database for the new info
    public void addRow (SQLiteDatabase db, String name, SparseBooleanArray checkedPositions, String URL) {
        String addRowString = "INSERT OR REPLACE INTO typeTable(name,"; //Start of statement

        for (String s:getTypeNames(db)){ //Add all current type names in database
            addRowString = addRowString + s + ",";
        }
        addRowString = addRowString.substring(0,addRowString.length()-1); //Remove ,
        addRowString = addRowString + ") VALUES (\'" + name + "\',"; //add rest up to Values
        for (int i = 0; i < typeNames.size(); i++){//Add 1 if checked, 0 if not
            if (i != checkedPositions.keyAt(i)){
                addRowString = addRowString + "0,";
            }
            else{
                addRowString = addRowString + "1,";
            }
        }
        addRowString = addRowString.substring(0,addRowString.length()-1); //Remove ,
        addRowString = addRowString + ");"; //add rest of statement
        try {
            db.execSQL(addRowString);
            if (URL != null) {
                db.execSQL("INSERT OR REPLACE INTO infoTable(name,URL) VALUES (\'" + name + "\',\'" + URL + "\');");
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //Selects a single random row by name
    public String getRandomRow(SQLiteDatabase db, ArrayList<String> checkedFilters){
        String randomString = createSelectQueryFromFilters(checkedFilters);
        randomString = randomString + "ORDER BY RANDOM() LIMIT 1;";
        Cursor cursor = db.rawQuery(randomString, null);
        if (cursor.getCount() < 1){
            cursor.close();
            return "Nothing here";
        }
        else {
            cursor.moveToFirst();
            String result = cursor.getString(0);
            cursor.close();
            return result;
        }
    }

    //Takes a list of checked filters and constructs a SELECT query to choose all items with those types
    public String createSelectQueryFromFilters (ArrayList<String> checkedFilters){
        // SELECT name FROM typeTable WHERE type = "<>" OR type = "<>"
        String queryString = "SELECT DISTINCT name FROM typeTable WHERE ";
        if (checkedFilters == null || checkedFilters.size() == 0){
            queryString = queryString.substring(0, queryString.length() - 6);
        }
        else{
            for (String s : checkedFilters){
                queryString = queryString + s + "=1 OR ";
            }
            queryString = queryString.substring(0, queryString.length() - 3);
        }
        return queryString;
    }

    /**
     * Gets a list of all of the checked filters based on their positions
     * @param db
     * @param checkedFilters
     * @param filterList
     * @return
     */
    public ArrayList<String> getTrueFiltersList (SQLiteDatabase db, SparseBooleanArray checkedFilters, ArrayList<String> filterList){
        ArrayList<String> trueFiltersStrings = new ArrayList<String>();
        if (checkedFilters.size() != 0){
            for (int i = 0; i < checkedFilters.size(); i++){
                boolean value = checkedFilters.valueAt(i);
                if (value == true){
                    String trueFilter = filterList.get(checkedFilters.keyAt(i));
                    trueFiltersStrings.add(trueFilter);
                }
            }
        }
        return trueFiltersStrings;
    }

    //Gets a list of all the types that an item has
    public ArrayList<String> getItemsAllTypes (SQLiteDatabase db, String item){
        //Log.i("CHECK", "IAMHERE");
        ArrayList<String> types = new ArrayList<String>();
        String queryString = "SELECT * FROM typeTable WHERE name = " + "'" + item + "';";
        Cursor cursor = db.rawQuery(queryString, null);
        cursor.moveToFirst();
        int i = 1;
        int val;
        for (String type : typeNames){
            val = cursor.getInt(i);
            if (val == 1) {
                types.add(type);
                //Log.i("CHECK", types.toString());
            }
            i++;
        }
        if (types.size() == 0){
            types.add("This item does not have any filters.");
        }
        cursor.close();
        return types;
    }

    //Get a list of all items based on filters
    public ArrayList<String> getFilteredStringList(SQLiteDatabase db, ArrayList<String> checkedFilters){
        ArrayList<String> filteredStringList = new ArrayList<String>();
        try{
            String queryString = createSelectQueryFromFilters(checkedFilters);
            Cursor cursor = db.rawQuery(queryString, null);
            if (cursor.getCount() < 1){
                cursor.close();
            }
            else {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    filteredStringList.add(cursor.getString(0));
                    cursor.moveToNext();
                }
                cursor.close();
            }

            return filteredStringList;
        }
        catch (Exception e){
            e.printStackTrace();
            filteredStringList.add("Error Occurred");
            return filteredStringList;
        }

    }

    //Gets a list of every item in the database
    public ArrayList<String> getAllList (SQLiteDatabase db){
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM typeTable;", null);
        if (cursor.getCount() < 1){
            cursor.close();
            return list;
        }
        else{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                String name = cursor.getString(0);
                list.add(name);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        }
    }

//    public void get(SQLiteDatabase db, ArrayList<String> checkedFilters){
//        String getQuery = "SELECT name"
//        Cursor cursor = db.rawQuery(randomString, null);
//        if (cursor.getCount() < 1){
//            cursor.close();
//            return "Nothing here";
//        }
//        else {
//            cursor.moveToFirst();
//            String result = cursor.getString(0);
//            cursor.close();
//            return result;
//        }
//    }

}
