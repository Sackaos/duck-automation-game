package com.example.duck_automation_game;

import java.util.HashMap;
import java.util.Map;

public class MapGenerator {

    private MapGenerator() {}

    public static MapTile[][] generateMap(int mapSize, Resource[] resourcesList ) {
        MapTile[][] mapTiles = new MapTile[mapSize][mapSize];

        Double[][] noiseMap = generateNoiseMap(mapSize);

        for (int i = 0; i < mapTiles.length; i++) {
            for (int j = 0; j < mapTiles[i].length; j++) {
                // TODO: check that  j,i is x,y (and they are not flipped)
                MapCoordinates mapCoordinates = new MapCoordinates(j, i);

                Map<String, Double> resourcesRichness = generateResourceRichness(resourcesList, noiseMap[i][j]);

                mapTiles[i][j] = new MapTile(mapCoordinates, resourcesRichness);
            }
        }

        return mapTiles;
    }

    private static Map<String, Double> generateResourceRichness(Resource[] resourcesList, double richnessValue ) {
        Map<String, Double> resourcesRichness = new HashMap<>();
        for (int i = 0; i < resourcesList.length; i++) {
            resourcesRichness.put(resourcesList[i].name, richnessValue);
        }
        return resourcesRichness;
    }

    private static Double[][] generateNoiseMap(int mapSize) {
        Double[][] noiseMap = new Double[mapSize][mapSize];
        for (int i = 0; i < noiseMap.length; i++) {
            for (int j = 0; j < noiseMap[i].length; j++) {
                // TODO: change here to an algorithm of perlin noise (use the i,j)
                noiseMap[i][j] = Math.random();
            }
        }
        return noiseMap;
    }
}
