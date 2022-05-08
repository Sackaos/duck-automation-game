package com.example.duck_automation_game.ui;

import android.graphics.Bitmap;
import android.view.View;

public class CustomResourceModel {
    // Bitmap bitmap;
    String resourceName;
    Double resourceProduction;
    Double resourceAmount;

    public CustomResourceModel(String resourceName, Double resourceAmount, Double resourceProduction) {
        // this.bitmap=bitmap;
        this.resourceName = resourceName;
        this.resourceProduction=resourceProduction;
        this.resourceAmount = resourceAmount;
    }


    public String getResourceName() {
        return this.resourceName;
    }
    public Double getResourceProduction() {
        return this.resourceProduction;
    }
    public Double getResourceAmount() {
        return this.resourceAmount.doubleValue();
    }

    public void setResourceProduction(Double resourceProduction) {
        this.resourceProduction = resourceProduction;
    }

    public void setResourceAmount(Double resourceAmount) {
        this.resourceAmount = resourceAmount;
    }
}

