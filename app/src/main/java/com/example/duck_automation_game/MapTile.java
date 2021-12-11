package com.example.duck_automation_game;

import java.util.HashMap;
import java.util.Map;

public class MapTile {
    MapCoordinates mapCoordinates;
    Map<String, Double> resourcesRichness;

    public MapTile (MapCoordinates mapCoordinates, Map<String, Double> resourcesRichness) {
        this.mapCoordinates = mapCoordinates;
        this.resourcesRichness = resourcesRichness;
    }

}
