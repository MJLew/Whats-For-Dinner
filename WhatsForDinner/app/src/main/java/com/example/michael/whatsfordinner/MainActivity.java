package com.example.michael.whatsfordinner;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
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
import java.util.Random;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {
    Random rng = new Random();
    TextView suggestionTextView;
    Button nextSuggestButton;
    Button manualSearchButton;
    GridView filterGridView;
    ArrayAdapter<String> filterGridAdapter;

    ArrayList<String> filterList = null;
    ArrayList<FoodItem> allItemsList = null;


    DBHelper helper = null;
    SQLiteDatabase db = null;
    ContentValues values = null;

    @Override
    //TODO Change all db functionality into just OO
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
            File path = getExternalFilesDir(null);
            //File sd = new File("/storage/9C33-6BBD/Android/data/com.example.michael.whatsfordinner/files");
            File spreadsheetCSV = new File(path, "dinner list.csv");

            //TODO Make this a seperate function because it shouldn't reload every time
            if (!spreadsheetCSV.exists()){
                //Log.i("foo", "doesn't exist");
            }
            else {
                //Log.i("foo", "does exist");
                String line;
                FileReader fr = new FileReader(spreadsheetCSV);
                BufferedReader bfr = new BufferedReader(fr);

                // Getting the first line of the file which should always be name,filter,filter,filter etc
                line = bfr.readLine();
                filterList = new ArrayList<>(Arrays.asList(line.split(",")));
                filterList.remove(0);

                // Creating database
                helper = new DBHelper(this, "recipe_db", null, 1);
                helper.setColList(filterList);
                db = helper.getWritableDatabase();
                values = new ContentValues();

                // Reading the rest of the file into a list of foodItems
                while((line = bfr.readLine()) != null){
                    String[] splitLine = line.split(",");
                    FoodItem item = new FoodItem(splitLine[0]); //Construct using name of the item
                    item.setFiltersMap(Arrays.copyOfRange(splitLine,1,splitLine.length), filterList);
                    allItemsList.add(item);
                }

                // Linking filters to the grid view
                filterGridAdapter = new ArrayAdapter<>(this, R.layout.filter_grid_layout, filterList);
                filterGridView.setAdapter(filterGridAdapter);

                // Randomly select default choice
                rng.nextInt();
                suggestionTextView.setText(allItemsList.get(rng.nextInt()).getName());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public void onDestroy(){
        super.onDestroy();
        helper.close();
    }

    //On click "Next", pulls up another random suggestion
    public void onNextSuggestClick(View view){
        SparseBooleanArray checkedFilters = filterGridView.getCheckedItemPositions();
        ArrayList<String> checkedFiltersStrings = new ArrayList<String>();

        // Check the strings of the filters that are checked
        if (checkedFilters.size() != 0) {
            for (int i = 0; i < checkedFilters.size(); i++) {
                boolean value = checkedFilters.valueAt(i);
                if (value == true) {
                    String trueFilter = filterList.get(checkedFilters.keyAt(i));
                    checkedFiltersStrings.add(trueFilter);
                }
            }
        }

        // Choose a random item based on filter
        // Keep choosing random ones until all filters match
        FoodItem randItem;
        do {
            int rand = rng.nextInt() % allItemsList.size();
            randItem = allItemsList.get(rand);
        } while (randItem.checkIfFilters(checkedFiltersStrings) == TRUE);

        suggestionTextView.setText(randItem.getName());
    }


    //On click go to manual search activity
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

    //On longpress create context menu that shows item's filters
    //TODO Make it so that related items are shown here as well
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
}
