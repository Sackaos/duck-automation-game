package com.example.duck_automation_game.engine;

import android.util.Log;

import com.example.duck_automation_game.MainActivity;
import com.example.duck_automation_game.ui.CustomResourceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameState {
    GameMap map;
    Resource[] resourcesList;
    public ArrayList<Factory> factoryList;
    ArrayList<CustomResourceModel> resourceArrList;
    String[] resourcesNames;
    MainActivity main;

    public GameState(MainActivity main) {
        this.main = main;
        this.resourcesNames = new String[]{"iron", "watermelons", "ducks", "pancakes", "wood", "spice melange", "rightful vengeance", "resource1", "reeeesource2", "resource3"};
        this.resourcesList = new Resource[this.resourcesNames.length];
        this.resourceArrList = new ArrayList<>();
        createStarterFactories();
        initiateResourceArrList();
        //main.getMapFromPrefs(main.PLAYER_FACTORY_PREFKEY);


        // TODO: load the mapSize from a config file and pass it to the GameMap constructor
        this.map = new GameMap(20, resourcesList);

    }

    private void createStarterFactories() {
        factoryList = new ArrayList<>();
        HashMap<String, Double> factoryMap = new HashMap<>();
        factoryMap.put("iron", 5D);
        HashMap<String, Double> costMap = new HashMap<>();
        costMap.put("iron", 10.0D);
        Factory newFactory = new Factory("iron Miner", factoryMap, costMap);
        factoryList.add(newFactory);

        HashMap<String, Double> factoryMap1 = new HashMap<>();
        factoryMap1.put("iron", -2D);
        factoryMap1.put("watermelons", 1D);
        Factory newFactory1 = new Factory("watermelon Factory", factoryMap1, costMap);
        factoryList.add(newFactory1);

        HashMap<String, Double> factoryMap2 = new HashMap<>();
        factoryMap2.put("watermelons", -0.5D);
        factoryMap2.put("pancakes", 0.1D);
        Factory newFactory2 = new Factory("pancake Factory", factoryMap2, costMap);
        factoryList.add(newFactory2);

        HashMap<String, Double> factoryMap3 = new HashMap<>();
        factoryMap3.put("pancakes", -0.1D);
        factoryMap3.put("spice melange", 0.05D);
        Factory newFactory3 = new Factory("spice melange Factory", factoryMap3, costMap);
        factoryList.add(newFactory3);
    }

    private void initiateResourceArrList() {
        Map<String, Double> finalProductionMap = calculateProduction();

        for (int i = 0; i < this.resourcesNames.length; i++) {
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.iron);

            String currentName = this.resourcesNames[i];
            String amountStr = this.main.getStringFromPref(currentName, "0.0");
            Double currentAmount = Double.parseDouble(amountStr);
            Double currentProductionAmount = finalProductionMap.get(currentName);

            CustomResourceModel item = new CustomResourceModel(currentName, currentAmount, currentProductionAmount);
            this.resourceArrList.add(item);

            this.resourcesList[i] = new Resource(currentName);
        }
    }

    public Map<String, Double> calculateProduction() {
        //sets finalproduction map to be 0
        Map<String, Double> finalProductionMap = new HashMap<>();

        for (int i = 0; i < this.resourcesNames.length; i++) {
            finalProductionMap.put(this.resourcesNames[i], 0.0D);
        }


        for (int i = 0; i < factoryList.size(); i++) {
            //gets one factory type
            Factory currentFactory = factoryList.get(i);

            //for every resourced produced/consumed by the factory
            for (String key : currentFactory.productionMap.keySet()) {
                Double currentProduction = finalProductionMap.get(key);
                Double newProduction = currentProduction + (currentFactory.productionMap.get(key) * currentFactory.getFactoryAmount());
                finalProductionMap.put(key, newProduction);
            }
        }
        return finalProductionMap;

    }


    /**
     * update the gameState per seconds
     *
     * @param timeOffset in seconds
     */
    public void update(double timeOffset) {
        // if we had 1 w00d in player res, and we have +1.5 wood proEducation
        // and we have timeOffset of 3 seconds we want that player's resource to be
        // 1 + (3*1.5)
        //in other words amount + (production*time)
        //==> www.burgerIsBadFOrYOuButWe-<3-itAnyroads.gov.zulu
        for (int i = 0; i < resourcesList.length; i++) {

            CustomResourceModel currentItem = this.resourceArrList.get(i);
            Double currentResourceValue = currentItem.getResourceAmount();
            Double currentProductionValue = currentItem.getResourceProduction();
            Double newResourceAmount = currentResourceValue + (currentProductionValue * timeOffset);
            currentItem.setResourceAmount(newResourceAmount);

        }
//        for (String key : this.playerResource.keySet()) {
//            Double currentResourceValue = this.playerResource.get(key);
//            Double currentProdcution = this.playerProduction.get(key);
//
//            Double newResourceAmount = currentResourceValue + (currentProdcution * timeOffset);
//            this.playerResource.put(key, newResourceAmount);

        //Log.d("GAD", "update: "+playerResource.get(key));
        //notifyAdapter();

    }


    public ArrayList<Factory> getPlayerfactories() {
        return this.factoryList;
    }

    public Resource[] getResourceList() {

        return this.resourcesList;
    }

    public ArrayList<CustomResourceModel> getResourceArrList() {
        return this.resourceArrList;
    }

    public void BuildFactory(int i) {
        Factory factory = factoryList.get(i);
        Boolean canAfford = true;
        //checks if player can afford factory
        for (String key : factory.costMap.keySet()) {
            CustomResourceModel currentItem = null;
            for (int j = 0; j < resourceArrList.size(); j++) {
                if (resourceArrList.get(j).getResourceName().equals(key))
                    currentItem = resourceArrList.get(j);

            }

            if (currentItem.getResourceAmount() < factory.costMap.get(key)) {
                canAfford = false;
            }
        }

        //makes the player pay for his factory
        if (canAfford == true) {
            for (String key : factory.costMap.keySet()) {
                CustomResourceModel currentItem = null;
                for (int j = 0; j < resourceArrList.size(); j++) {
                    if (resourceArrList.get(j).getResourceName().equals(key))
                        currentItem = resourceArrList.get(j);
                    currentItem.setResourceAmount(currentItem.getResourceAmount() - factory.costMap.get(key));
                }
            }

        }
        factory.addFactoryCount();
        main.notifyProductionChange();
        main.notifyFactoryAdapter();
    }
}


