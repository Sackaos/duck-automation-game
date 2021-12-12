package com.example.duck_automation_game.ui;

import android.graphics.Bitmap;
import android.view.View;

public class CustomListItemModel {
    // Bitmap bitmap;
    String resourceName;
    String resourceProduction;
    Double resourceAmount;

    public CustomListItemModel(String resourceName, Double resourceAmount,String resourceProduction) {
        // this.bitmap=bitmap;
        this.resourceName = resourceName;
        this.resourceProduction=resourceProduction;
        this.resourceAmount = resourceAmount;
    }


    public String getResourceName() {
        return this.resourceName;
    }
    public String getResourceProduction() {
        return this.resourceProduction;
    }
    public Double getResourceAmount() {
        return this.resourceAmount;
    }

}

