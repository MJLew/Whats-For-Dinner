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
    private static String DATABASE_NAME = "recipe_db";
    ArrayList<String> colList;
    SQLiteStatement statement = null;

    public DBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version){
        super(context, dbName, factory, version);
    }

    public void onCreate(SQLiteDatabase db){
        String createString = "CREATE TABLE IF NOT EXISTS recipeTable (Name TEXT NOT NULL,";
        for (String s : colList){
            Log.i("foo", s);
            createString = createString + s + " INTEGER NOT NULL,";
        }
        createString = createString + " UNIQUE (Name));";
        db.execSQL(createString);


    }

    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        String dropString = "DROP TABLE IF EXISTS recipeTable;";
        db.execSQL(dropString);
        onCreate(db);
    }

    public List<String> getColumnNames(SQLiteDatabase db){
        Cursor c = db.rawQuery("SELECT * FROM recipeTable WHERE 0", null);
        try {
            List<String> columnNames = Arrays.asList(c.getColumnNames());
            return columnNames;
        } finally {
            c.close();
        }
    }

    public void setColList(List<String> filterList){
        this.colList = new ArrayList<>(filterList);
    }

    public void addRow (SQLiteDatabase db, String[] rowStrings){
        String addRowString = "INSERT OR IGNORE INTO recipeTable VALUES (";
        try {
            for (String s : rowStrings){
                    if (s.equals("1") || s.equals("0")){
                        addRowString = addRowString + s + ",";
                    }
                    else{
                        addRowString = addRowString + "'" + s + "',";
                    }
            }
            addRowString = addRowString.substring(0, addRowString.length() - 1);
            addRowString = addRowString + ");";
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }
        db.execSQL(addRowString);
    }

    public String getRandomRow(SQLiteDatabase db, ArrayList<String> checkedFilters){
        String randomString = createSelectQueryFromFilters(checkedFilters);
        randomString = randomString + " ORDER BY RANDOM() LIMIT 1;";

        Cursor cursor = db.rawQuery(randomString, null);
        if (cursor.getCount() < 1){
            cursor.close();
            return "Nothing here";
        }
        else {
            cursor.moveToFirst();
            String result = cursor.getString(cursor.getColumnIndex("Name"));
            cursor.close();
            return result;
        }
    }

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
                    filteredStringList.add(cursor.getString(cursor.getColumnIndex("Name")));
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

    public ArrayList<String> getAllList (SQLiteDatabase db){
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM recipeTable", null);
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

    public String createSelectQueryFromFilters (ArrayList<String> checkedFilters){
        String queryString = "SELECT Name FROM recipeTable WHERE ";
        if (checkedFilters == null || checkedFilters.size() == 0){
            queryString = queryString.substring(0, queryString.length() - 6);
        }
        else{
            for (String s : checkedFilters){
                queryString = queryString + s + " = 1 AND ";
            }
            queryString = queryString.substring(0, queryString.length() - 4);
        }
        return queryString;
    }

    public ArrayList<String> getItemsTrueFilters (SQLiteDatabase db, String item){
        ArrayList<String> trueColumns = new ArrayList<String>();
        String queryString = "SELECT * FROM recipeTable WHERE Name = " + "'" + item + "'";
        Cursor cursor = db.rawQuery(queryString, null);

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getColumnCount(); i++){
            if (cursor.getInt(i) == 1){
                trueColumns.add(cursor.getColumnName(i));
            }
        }
        if (trueColumns.size() == 0){
            trueColumns.add("This item does not have any filters.");
        }
        return trueColumns;
    }
}
