package com.example.duck_automation_game.engine;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class GameState {
    GameMap map;
    public Map<String, Double> playerResource;
    public Map<String, Double> playerProduction;
    Resource[] resourcesList;


    public GameState() {
        this.playerResource = new HashMap<>();
        this.playerProduction = new HashMap<>();

        //TODO: load the catalog from a file
        String[] resourcesNames = new String[]{"iron", "watermelons", "ducks", "pancakes", "wood", "spice melange"};

        this.resourcesList = new Resource[resourcesNames.length];
        for (int i = 0; i < resourcesList.length; i++) {
            // Create the resource and add it to the catalog
            this.resourcesList[i] = new Resource(resourcesNames[i]);

            // set this same resource in the player starting resources
            this.playerResource.put(this.resourcesList[i].name, 0.0);
            this.playerProduction.put(this.resourcesList[i].name, 0.0);
        }


        // TODO: load the mapSize from a config file and pass it to the GameMap constructor
        this.map = new GameMap(20, resourcesList);

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
