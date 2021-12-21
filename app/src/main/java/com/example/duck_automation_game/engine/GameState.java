package com.example.duck_automation_game.engine;

import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.example.duck_automation_game.MainActivity;
import com.example.duck_automation_game.ui.CustomListItemModel;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Handler;

public class GameState {
    GameMap map;
    public Map<String, Double> playerResource;
    public Map<String, Double> playerProduction;
    Resource[] resourcesList;
    Factory[] factoryList;
    private SharedPreferences sharedPref;
    ArrayList<CustomListItemModel> resourceArrList;

    public GameState(MainActivity main) {
        String[] resourcesNames = new String[]{"iron", "watermelons", "ducks", "pancakes", "wood", "spice melange", "rightful vengeance", "resource1", "reeeesource2", "resource3"};
        this.resourcesList = new Resource[resourcesNames.length];
        resourceArrList = new ArrayList<>();
        for (int i = 0; i < resourcesNames.length; i++) {
            String currentName = resourcesNames[i];

            this.resourcesList[i] = new Resource(currentName);
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.iron);
            String n = main.getStringFromPref(currentName, "0.0");
            Double currentAmount = Double.parseDouble(n);
            CustomListItemModel item = new CustomListItemModel(currentName, currentAmount, 0.0);
            resourceArrList.add(item);
        }

        //main.getMapFromPrefs(main.PLAYER_FACTORY_PREFKEY);



        // TODO: load the mapSize from a config file and pass it to the GameMap constructor
        this.map = new GameMap(20, resourcesList);

    }


    /**
     * update the game state per seconds
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

            CustomListItemModel currentItem = resourceArrList.get(i);
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


    public Map<String, Double> getPlayerResource() {
        return this.playerResource;
    }

    public Map<String, Double> getPlayerProduction() {
        return this.playerProduction;
    }

    public Factory[] getPlayerfactories() {
        return this.factoryList;
    }

    public Resource[] getResourceList() {

        return resourcesList;
    }

    public ArrayList<CustomListItemModel> getResourceArrList() {
        return resourceArrList;
    }
}
