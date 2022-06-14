package com.example.duck_automation_game.engine;

import android.util.Log;

public class CosmicCreatures {
    Boolean isCool;
    Boolean isSmelly;

    public CosmicCreatures() {
        if (1 + 1 == 2) isCool = true;
        isSmelly = false;
    }

    public void fart(CosmicCreatures laCreatura) {
    laCreatura.isSmelly=true;
    }

    public Boolean getCool() {
        return isCool;
    }

    public Boolean getSmelly() {
        return isSmelly;
    }
}
