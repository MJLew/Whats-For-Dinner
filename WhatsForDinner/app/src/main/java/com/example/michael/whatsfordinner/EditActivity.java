package com.example.michael.whatsfordinner;


import android.app.Fragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.example.michael.whatsfordinner.ui.main.MyAdapter;
import java.util.ArrayList;
import java.util.List;


public class EditActivity extends AppCompatActivity {
    Fragment addFragment;
    Fragment deleteFragment;
    Fragment editFragment;
    Fragment filterFragment;

    ViewPager viewPager;
    MyAdapter adapter;
    TabLayout tabs;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Declarations
        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);
        fab = findViewById(R.id.editMenuFAB);

        //Set up tab layout
        adapter = new MyAdapter(this, getSupportFragmentManager(), 4);
        viewPager.setAdapter(adapter);

        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setText("Add");
        tabs.getTabAt(1).setText("Delete");
        tabs.getTabAt(2).setText("Edit");
        tabs.getTabAt(3).setText("Filters");
        //tabs.getTabAt(3).setText("Save/Load");
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        //Set up FABs
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}