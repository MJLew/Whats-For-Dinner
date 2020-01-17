package com.example.michael.whatsfordinner;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    DBHelper helper = null;
    SQLiteDatabase db = null;
    ListView searchListView;
    Button useFilterButton;
    FloatingActionButton fab;
    Toolbar searchToolbar;
    ArrayList<String> foodList;
    ArrayList<String> uniqueFoodList;
    ArrayAdapter<String> searchListAdapter;
    ArrayList<String> filterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            filterList = extras.getStringArrayList("filterList");
        }

        searchListView = (ListView) findViewById(R.id.searchListView);
        useFilterButton = (Button) findViewById(R.id.useFilterButton);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab = findViewById(R.id.fab);



        helper = new DBHelper(this, "food_db", null, 1);
        db = helper.getWritableDatabase();

        foodList = helper.getAllList(db);
        uniqueFoodList = helper.getFilteredStringList(db,null);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabDialog();
            }
        });

        registerForContextMenu(searchListView);
        searchListAdapter = new ArrayAdapter<String>(this, R.layout.search_list_layout, uniqueFoodList);
        searchListView.setAdapter(searchListAdapter);
    }

    public void onUseFilterClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = getLayoutInflater().inflate(R.layout.filter_dialog_layout, null);

        builder.setTitle("Filters");
        builder.setView(layout);

        final ArrayAdapter<String> dialogFilterGridAdapter = new ArrayAdapter<String>(this, R.layout.filter_grid_layout, filterList);
        final GridView dialogFilterGridview = (GridView) layout.findViewById(R.id.dialogFilterGridView);
        dialogFilterGridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        dialogFilterGridview.setAdapter(dialogFilterGridAdapter);

        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> trueFiltersList = helper.getTrueFiltersList(db, dialogFilterGridview.getCheckedItemPositions(), filterList);
                ArrayList<String> filteredStringList = helper.getFilteredStringList(db, trueFiltersList);
                searchListAdapter.clear();
                searchListAdapter.addAll(filteredStringList);
                searchListAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();

    }

    public void onCreateContextMenu(ContextMenu menu,View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu_item_longpress, menu);
    }

    public boolean onContextItemSelected (final MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.show_filters:
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    View layout = getLayoutInflater().inflate(R.layout.filter_dialog_layout, null);

                    builder.setView(layout);
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
                    String itemName = ((TextView)info.targetView).getText().toString();
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
                catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Error Occurred", Toast.LENGTH_LONG).show();
                }
        }
        return super.onContextItemSelected(menuItem);
    }

    public void fabDialog(){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View layout = getLayoutInflater().inflate(R.layout.add_dialog_layout, null);

            builder.setView(layout);
            builder.setTitle("Add New Item");


//            final ArrayAdapter<String> dialogFilterGridAdapter = new ArrayAdapter<String>(this, R.layout.filter_grid_layout_no_check, helper.getTypeNames(db));
//            final GridView dialogFilterGridview = (GridView) layout.findViewById(R.id.dialogFilterGridView);
//            dialogFilterGridview.setAdapter(dialogFilterGridAdapter);


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create();
            builder.show();
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Error Occurred", Toast.LENGTH_LONG).show();
        }
    }

}
