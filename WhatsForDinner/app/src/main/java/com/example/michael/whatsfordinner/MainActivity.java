package com.example.michael.whatsfordinner;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    //Hello 2
    Toolbar mainToolbar;
    TextView suggestionTextView;
    Button nextSuggestButton;
    Button previousSuggestButton;
    Button viewRecipeButton;
    GridView filterGridView;
    ArrayAdapter<String> filterGridAdapter;
    ArrayList<String> filterList = null;
    FloatingActionButton fab;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    TextView fab2TextView;
    TextView fab3TextView;
    FABMenu fabMenu;

    DBHelper helper = null;
    SQLiteDatabase db = null;
    ContentValues values = null;

    List<String> suggestionList;
    int suggestListIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declarations
        suggestionTextView = (TextView) findViewById(R.id.suggestionTextView);
        nextSuggestButton = (Button) findViewById(R.id.nextSuggestButton);
        previousSuggestButton = (Button) findViewById(R.id.previousSuggestButton);
        viewRecipeButton = (Button) findViewById(R.id.viewRecipeButton);
        filterGridView = (GridView) findViewById(R.id.filterGridView);
        filterGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        fab = (FloatingActionButton) findViewById(R.id.mainMenuFAB);
        fab2 = (FloatingActionButton) findViewById(R.id.mainSearchListFAB);
        fab3 = (FloatingActionButton) findViewById(R.id.mainEditListFAB);
        fab2TextView = (TextView) findViewById(R.id.mainSearchListTextView);
        fab3TextView = (TextView) findViewById(R.id.mainEditListTextView);

        suggestionList = new ArrayList<>();
        suggestListIndex = 0;

        registerForContextMenu(suggestionTextView);

        //Set up FAB menu
        fabMenu = new FABMenu(this, fab);
        fabMenu.addSubFAB(fab2, fab2TextView);
        fabMenu.addSubFAB(fab3, fab3TextView);
        //FAB click listeners
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.toggleFABMenu();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() { //Go to searchActivity
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                //intent.putExtra("filterList", filterList);
                startActivity(intent);
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() { //Go to editActivity
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                //intent.putExtra("filterList", filterList);
                startActivity(intent);
            }
        });

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
                db = helper.getWritableDatabase();
                helper.onUpgrade(db,1,2);
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
                String randomVal = helper.getRandomRow(db, null);
                suggestionList.add(randomVal);
                suggestionTextView.setText(randomVal);
//            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    //When app is closed
    public void onDestroy(){
        super.onDestroy();
        helper.close();
    }

    //When the NEXT button is clicked
    public void onNextSuggestClick(View view){
        if (suggestListIndex == suggestionList.size()-1){
            ArrayList<String> trueFiltersStrings = helper.getTrueFiltersList(db, filterGridView.getCheckedItemPositions(), filterList);
            String randomVal = helper.getRandomRow(db, trueFiltersStrings);

            if (suggestionList.contains(randomVal)){
                suggestionList.remove(randomVal);
            }
            else{
                suggestListIndex += 1;
            }
            suggestionList.add(randomVal);
            suggestionTextView.setText(randomVal);
            Log.i("STACK", suggestionList.toString());
        }
        else {
            suggestListIndex += 1;
            suggestionTextView.setText(suggestionList.get(suggestListIndex));
        }
    }

    //When the PREVIOUS button is clicked
    public void onPreviousSuggestClick(View view){
        if (suggestListIndex > 0) {
            suggestListIndex -= 1;
            suggestionTextView.setText(suggestionList.get(suggestListIndex));
        }
        Log.i("STACK",suggestionList.toString());
    }

    //Context menu when item is long-pressed
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu_item_longpress, menu);
    }

    //When item in context menu is selected
    public boolean onContextItemSelected (final MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.show_filters:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View layout = getLayoutInflater().inflate(R.layout.filter_dialog_layout, null);

                builder.setView(layout);
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
                String itemName = suggestionTextView.getText().toString();
                builder.setTitle("Filters for " + itemName);

                ArrayList<String> itemsAllTypes = helper.getItemsAllTypes(db, itemName);

                final ArrayAdapter<String> dialogFilterGridAdapter = new ArrayAdapter<String>(this, R.layout.filter_grid_layout_no_check, itemsAllTypes);
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

    public boolean onViewRecipeClick(View view) {
        return true;
    }
}
