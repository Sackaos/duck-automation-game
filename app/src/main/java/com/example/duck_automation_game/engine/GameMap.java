package com.example.duck_automation_game.engine;

public class GameMap {
    MapTile[][] mapTiles;

    public GameMap(int mapSize, Resource[] resourcesList) {
        this.mapTiles = MapGenerator.generateMap(mapSize, resourcesList);
    }
}
