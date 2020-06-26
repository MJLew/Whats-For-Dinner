package com.example.michael.whatsfordinner;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.michael.whatsfordinner.ui.main.MyAdapter;
import com.example.michael.whatsfordinner.ui.main.SectionsPagerAdapter;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);

        MyAdapter adapter = new MyAdapter(this, getSupportFragmentManager(), 3);
        viewPager.setAdapter(adapter);

        tabs.setupWithViewPager(viewPager);

        tabs.getTabAt(0).setText("Add");
        tabs.getTabAt(1).setText("Delete");
        tabs.getTabAt(2).setText("Edit");

        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}