package com.example.duck_automation_game;

import android.graphics.Bitmap;
import android.view.View;

public class CustomListModel {
   // Bitmap bitmap;
    String resourceName;
    double resourceAmount;

    public CustomListModel( String resourceName, double resourceAmount){
       // this.bitmap=bitmap;
        this.resourceName=resourceName;
        this.resourceAmount=resourceAmount;
    }
    public String getResourceName() {
        return resourceName;
    }
    public Double    getResourceAmount() {
        return resourceAmount;
    }

}

