package com.example.duck_automation_game.engine;

import android.graphics.Color;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.duck_automation_game.MainActivity;
import com.example.duck_automation_game.R;
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
        this.resourcesNames = new String[]{"iron", "watermelons", "ducks", "pancakes", "wood", "spice melange", "rightful vengeance", "Oz Slaves", "cough", "cough 2 ", "MOAR RESOURCE", "EVEN MOAOOAOOAOAOR"};
        this.resourcesList = new Resource[this.resourcesNames.length];
        this.resourceArrList = new ArrayList<>();
        initiateFactoryList();
        initiateResourceArrList();


        // TODO: load the mapSize from a config file and pass it to the GameMap constructor
        this.map = new GameMap(20, resourcesList);

    }

    private void initiateFactoryList() {
        String factoryListString = main.getStringFromPref(main.PLAYER_FACTORY_PREFKEY, "");
        if (factoryListString.equals("")) createStarterFactories();
        else {
            factoryList = main.getFactoryListFromString(factoryListString);
        }

    }

    private void createStarterFactories() {
        factoryList = new ArrayList<>();
        HashMap<String, Double> factoryMap = new HashMap<>();
        factoryMap.put("iron", 5D);
        HashMap<String, Double> costMap = new HashMap<>();
        costMap.put("iron", 10.0D);
        costMap.put("watermelons", 5.0D);
        Factory newFactory = new Factory("iron Miner", factoryMap, costMap, 2);
        factoryList.add(newFactory);

        HashMap<String, Double> factoryMap1 = new HashMap<>();
        factoryMap1.put("iron", -2D);
        factoryMap1.put("watermelons", 1D);
        Factory newFactory1 = new Factory("watermelon Factory", factoryMap1, costMap, 3);
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

        for (String bigkey : finalProductionMap.keySet()) {
            if (finalProductionMap.get(bigkey) < 0) {
                for (String key : finalProductionMap.keySet()) {
                    finalProductionMap.put(key, 0.0D);
                }
                main.setSellbtnColor(R.color.btnSell);

                break;
            }
            else try{ main.setSellbtnColor(R.color.btnSell2);}
            catch (Exception e){
                Log.e("GAD", "calculateProduction: "+e.getMessage());
            }
        }
        return finalProductionMap;
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

    public CustomResourceModel findResourceByKey(String key) {
        CustomResourceModel item = null;

        for (int j = 0; j < resourceArrList.size(); j++) {
            if (resourceArrList.get(j).getResourceName().equals(key))
                item = resourceArrList.get(j);
        }
        return item;
    }

    public void buildFactory(int i) {
        Factory factory = factoryList.get(i);
        Boolean canAfford = true;
        //checks if player can afford factory
        for (String key : factory.costMap.keySet()) {
            Double currentFactoryCost=factory.costMap.get(key)*(factory.getFactoryAmount()*1.5);
            CustomResourceModel currentItem = findResourceByKey(key);
            if (currentItem.getResourceAmount() < currentFactoryCost) {
                canAfford = false;
                break;
            }
        }

        //makes the player pay for his factory
        if (canAfford == true) {
            for (String key : factory.costMap.keySet()) {
                CustomResourceModel currentItem = findResourceByKey(key);
                Double currentFactoryCost=factory.costMap.get(key)*(factory.getFactoryAmount()*1.5);
                currentItem.setResourceAmount(currentItem.getResourceAmount() - currentFactoryCost);
            }

            factory.addFactoryCount();
            main.notifyFactoryAdapter();
            main.notifyProductionChange();
        }
        else main.showToast("cant afford");
    }

    public void sellFactory(int i) {
        Factory factory = factoryList.get(i);
        if (factory.getFactoryAmount() > 0) {
            factory.destroyFactory();
            for (String key : factory.costMap.keySet()) {
                CustomResourceModel resource = findResourceByKey(key);
                resource.setResourceAmount(resource.getResourceAmount() + factory.costMap.get(key));
            }
            main.notifyProductionChange();
            main.notifyFactoryAdapter();
        } else main.showToast("cant sell; not enough factories");


    }
}



