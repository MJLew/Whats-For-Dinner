package com.example.michael.whatsfordinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class FoodItem {
    private String name;
    private HashMap<String, Boolean> filtersMap;
    private ArrayList<FoodItem> subItems;

    //Basic Constructor
    //Only requires a name of the item
    public FoodItem(String name){
        this.name = name;
        this.filtersMap = new HashMap<String, Boolean>();
        this.subItems = new ArrayList<FoodItem>();
    }

    //Fill in the all filters in map for item
    public void setFiltersMap(String[] filterVals, ArrayList<String> filterList) {
        for (int i = 0, j = 0; i < filterList.size(); i++, j++){
            if (filterVals[i].equals("1")){
                this.filtersMap.put(filterList.get(j), TRUE);
            }
            else{
                this.filtersMap.put(filterList.get(j), FALSE);
            }
        }
    }

    public String getName(){
        return this.name;
    }

    // Takes a list of filters to check and checks to see if all of them are TRUE
    public Boolean checkIfFilters(ArrayList<String> filtersToCheck){
        for (int i = 0; i < filtersToCheck.size(); i++){
            if (this.filtersMap.get(filtersToCheck.get(i)) == FALSE){
                return FALSE;
            }
        }
        return TRUE;
    }

}
