package com.example.duck_automation_game.engine;

import android.util.Log;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public class Factory {
    String name;
    Map<String, Double> productionMap;
    Map<String, Double> costMap;

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    LatLng position;
    int factoryAmount;

    Button btnBuy;

    public Factory(String name, Map<String, Double> productionMap, Map<String, Double> costMap) {
        this.name = name;
        this.productionMap = productionMap;
        this.costMap = costMap;
        this.factoryAmount = 1;
    }

    public Factory(String name, Map<String, Double> productionMap, Map<String, Double> costMap, int factoryAmount) {
        this.name = name;
        this.productionMap = productionMap;
        this.costMap = costMap;
        this.factoryAmount = factoryAmount;
    }


    public void addFactoryCount(int toAdd) {
        this.factoryAmount = this.factoryAmount + toAdd;
    }

    public String getFactoryName() {
        return name;
    }

    public Map<String, Double> getProductionMap() {
        return productionMap;
    }

    public Map<String, Double> getCostMap() {
        return costMap;
    }

    public void setProductionMap(Map<String, Double> productionMap) {
        this.productionMap = productionMap;
    }

    public int getFactoryAmount() {
        return this.factoryAmount;
    }

    public void destroyFactory() {
        this.factoryAmount = this.factoryAmount - 1;
    }
}
