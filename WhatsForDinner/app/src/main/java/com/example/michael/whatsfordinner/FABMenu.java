package com.example.michael.whatsfordinner;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class FABMenu {
    FloatingActionButton mainFAB;
    ArrayList<FloatingActionButton> subFABs;
    ArrayList<TextView> labels;
    Context context;
    float indent1;
    float indent2;
    boolean isFABOpen;

    public FABMenu (Context context, FloatingActionButton mainFAB){
        this.context = context;
        this.mainFAB = mainFAB;
        subFABs = new ArrayList<FloatingActionButton>();
        labels = new ArrayList<TextView>();
        isFABOpen = false;
        indent1 = -context.getResources().getDimension(R.dimen.standard_58);
        indent2 = -context.getResources().getDimension(R.dimen.standard_48);
    }

    public void addSubFAB(FloatingActionButton fab, TextView label){
        subFABs.add(fab);
        labels.add(label);
        label.setVisibility(View.INVISIBLE);
    }

    public void toggleFABMenu(){
        if (!isFABOpen){
            Log.i("BUH", "show");
            mainFAB.setImageResource(R.drawable.closeicon);
            isFABOpen = true;

            for (int i = 0; i < subFABs.size(); i++){
                subFABs.get(i).animate().translationY(indent1 + (i*indent2));
                labels.get(i).animate().translationY(indent1 + (i*indent2));
                labels.get(i).setVisibility(View.VISIBLE);
            }
        }
        else {
            Log.i("BUH", "close");
            mainFAB.setImageResource(R.drawable.menuicon);
            isFABOpen = false;
            for (int i = 0; i < subFABs.size(); i++) {
                labels.get(i).setVisibility(View.INVISIBLE);
                subFABs.get(i).animate().translationY(0);
                labels.get(i).animate().translationY(0);
            }
        }
    }
}
