package com.example.michael.whatsfordinner;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    //Hello 2
    Toolbar mainToolbar;
    TextView suggestionTextView;
    Button nextSuggestButton;
    Button manualSearchButton;
    GridView filterGridView;
    ArrayAdapter<String> filterGridAdapter;
    ArrayList<String> filterList = null;

    DBHelper helper = null;
    SQLiteDatabase db = null;
    ContentValues values = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declarations
        suggestionTextView = (TextView) findViewById(R.id.suggestionTextView);
        nextSuggestButton = (Button) findViewById(R.id.nextSuggestButton);
        manualSearchButton = (Button) findViewById(R.id.manualSearchButton);
        filterGridView = (GridView) findViewById(R.id.filterGridView);
        filterGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        registerForContextMenu(suggestionTextView);
        // Reading in csv file and creating database if it exists
        try {
//            File path = getExternalFilesDir(null);
//            //File sd = new File("/storage/9C33-6BBD/Android/data/com.example.michael.whatsfordinner/files");
//            File spreadsheetCSV = new File(path, "dinner list.csv");
//
//            if (!spreadsheetCSV.exists()){
//                //Log.i("foo", "doesn't exist");
//            }
//            else {
//                //Log.i("foo", "does exist");
//                String line;
//                FileReader fr = new FileReader(spreadsheetCSV);
//                BufferedReader bfr = new BufferedReader(fr);
//
//                // Getting the first line of the file which should always be name,filter,filter,filter etc
//                line = bfr.readLine();
//                filterList = new ArrayList<>(Arrays.asList(line.split(",")));
//                filterList.remove(0);

                // Creating database
                helper = new DBHelper(this, "food_db", null, 1);
//                helper.setColList(filterList);
            Log.i("foo","bruh");
                db = helper.getWritableDatabase();
                values = new ContentValues();

//                // Reading the rest of the file into the database
//                while((line = bfr.readLine()) != null){
//                    String[] splitLine = line.split(",");
//                    helper.addRow(db, splitLine);
//                }

                // Linking database filters to the grid view
                filterList = new ArrayList<>(helper.getTypeNames(db));
                filterGridAdapter = new ArrayAdapter<>(this, R.layout.filter_grid_layout, filterList);
                filterGridView.setAdapter(filterGridAdapter);
                suggestionTextView.setText(helper.getRandomRow(db, null));
//            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public void onDestroy(){
        super.onDestroy();
        helper.close();
    }

    public void onNextSuggestClick(View view){
        ArrayList<String> trueFiltersStrings = helper.getTrueFiltersList(db, filterGridView.getCheckedItemPositions(), filterList);
        suggestionTextView.setText(helper.getRandomRow(db, trueFiltersStrings));
    }


    public void onManualSearchClick(View view){
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("filterList", filterList);
        startActivity(intent);
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu_item_longpress, menu);
    }

    public boolean onContextItemSelected (final MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.show_filters:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View layout = getLayoutInflater().inflate(R.layout.filter_dialog_layout, null);

                builder.setView(layout);
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
                String itemName = suggestionTextView.getText().toString();
                builder.setTitle("Filters for " + itemName);

                ArrayList<String> itemsTrueFiltersList = helper.getItemsTrueFilters(db, itemName);

                final ArrayAdapter<String> dialogFilterGridAdapter = new ArrayAdapter<String>(this, R.layout.filter_grid_layout_no_check, itemsTrueFiltersList);
                final GridView dialogFilterGridview = (GridView) layout.findViewById(R.id.dialogFilterGridView);
                dialogFilterGridview.setAdapter(dialogFilterGridAdapter);


                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create();
                builder.show();
                return true;
        }
        return super.onContextItemSelected(menuItem);
    }

    public boolean onLinkClick(View view){
        return true;
    }
}
