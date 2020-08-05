package com.example.michael.whatsfordinner.ui.main;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.michael.whatsfordinner.DBHelper;
import com.example.michael.whatsfordinner.R;

import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment implements View.OnClickListener {
    Button confirmAddButton;
    EditText addNameEditText;
    EditText addURLEditText;
    GridView addFilterGridView;

    DBHelper helper = null;
    SQLiteDatabase db = null;
    ArrayAdapter<String> filterGridAdapter;
    ArrayList<String> filterList = null;
    ContentValues values = null;

    public AddFragment(){
        //Empty Constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        confirmAddButton = getActivity().findViewById(R.id.confirmAddButton);
        addNameEditText = getActivity().findViewById(R.id.addNameEditText);
        addURLEditText = getActivity().findViewById(R.id.addURLEditText);
        addFilterGridView = getActivity().findViewById(R.id.addFilterGridView);

        confirmAddButton.setOnClickListener(this);

        //Connect to database
        helper = new DBHelper(getActivity(), "food_db", null, 1);
        db = helper.getWritableDatabase();
        values = new ContentValues();

        // Linking database filters to the grid views
        addFilterGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        filterList = new ArrayList<>(helper.getTypeNames(db));
        filterGridAdapter = new ArrayAdapter<>(getActivity(), R.layout.filter_grid_layout, filterList);
        addFilterGridView.setAdapter(filterGridAdapter);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.confirmAddButton:
                onConfirmAddClick(view);
        }
    }

    public void onConfirmAddClick(View view){
        String name = addNameEditText.getText().toString();
        String URL = addURLEditText.getText().toString();
        ArrayList<String> checkedFiltersStrings = helper.getTrueFiltersList(db, addFilterGridView.getCheckedItemPositions(), filterList);
        helper.addRow(db, name, addFilterGridView.getCheckedItemPositions(), URL);

        List<String> list = helper.getAllList(db);
        for (String item : list){
            Log.d("Foo", item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

}
