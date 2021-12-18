package com.example.duck_automation_game.engine;

import java.util.Map;

public class Factory {
    String name;
    Map<String,Double> ProductionMap;
    int instancesBuilt;


    public Factory(String name, Map<String, Double> productionMap, int instancesBuilt) {
        this.name = name;
        this.ProductionMap = productionMap;
        this.instancesBuilt = instancesBuilt;
    }

    public String getName() {
        return name;
    }

    public Map<String, Double> getProductionMap() {
        return ProductionMap;
    }

    public int getInstancesBuilt() {
        return instancesBuilt;
    }

    public void setProductionMap(Map<String, Double> productionMap) {
        ProductionMap = productionMap;
    }
}
