package com.example.duck_automation_game.engine;

import android.util.Log;

import java.util.Map;

public class Factory {
    String name;
    Map<String, Double> productionMap;
    Map<String, Double> costMap;
    int factoryAmount;

    public Factory(String name, Map<String, Double> productionMap, Map<String, Double> costMap) {
        this.name = name;
        this.productionMap = productionMap;
        this.costMap = costMap;
        this.factoryAmount=1;
    }

    public void addFactoryCount() {
        this.factoryAmount = this.factoryAmount+1;
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
}
