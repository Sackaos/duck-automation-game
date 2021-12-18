package com.example.duck_automation_game.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.duck_automation_game.MainActivity;

import android.content.SharedPreferences;
import android.preference.Preference;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GameState {
    GameMap map;
    public Map<String, Double> playerResource;
    public Map<String, Double> playerProduction;
    Resource[] resourcesList;
    private SharedPreferences sharedPref;


    public GameState(MainActivity main) {
        this.playerResource = new HashMap<>();
        this.playerProduction = new HashMap<>();

        //TODO: load the catalog from a file
        String[] resourcesNames = new String[]{"iron", "watermelons", "ducks", "pancakes", "wood", "spice melange", "rightful vengeance", "resource1", "resource2", "resource3"};
        playerResource = main.getMapFromPrefs(main.PLAYER_RESOURCE_PREFKEY);
        if (playerResource == null) {
            this.playerResource = new HashMap<>();
        }
        this.resourcesList = new Resource[resourcesNames.length];

        for (int i = 0; i < resourcesList.length; i++) {
            String currentName = resourcesNames[i];
            Double value;

            // Create the resource and add it to the catalog
            this.resourcesList[i] = new Resource(currentName);

        if (playerResource.get(currentName)!=null) value = playerResource.get(currentName);
        else value=0.0;
            //Log.d("GAD", "GameState1: " + value + playerResource + currentName);

            // set this same resource in the player starting resources
            this.playerResource.put(currentName, value);
            this.playerProduction.put(currentName, 0.0);

        }


        // TODO: load the mapSize from a config file and pass it to the GameMap constructor
        this.map = new GameMap(20, resourcesList);

    }


    /**
     * update the game state per seconds
     *
     * @param timeOffset in seconds
     */
    public void update(double timeOffset) {
        // if we had 1 w00d in player res, and we have +1.5 wood producation
        // and we have timeOffset of 3 seconds we want that player's resource is gonna be
        // 1 + (3*1.5)
        //==> www.burgerIsBadFOrYOuButWe<3itAnyroads.gov.zulu

        for (String key : this.playerResource.keySet()) {
            Double currentResourceValue = this.playerResource.get(key);
            Double currentProdcution = this.playerProduction.get(key);

            Double newResourceAmount = currentResourceValue + (currentProdcution * timeOffset);
            this.playerResource.put(key, newResourceAmount);
            //Log.d("GAD", "update: "+playerResource.get(key));
            //notifyAdapter();

        }
    }


    public Map<String, Double> getPlayerResource() {
        return this.playerResource;
    }

    public Map<String, Double> getPlayerProduction() {
        return this.playerProduction;
    }

    public Resource[] getResourceList() {

        return resourcesList;
    }


}
