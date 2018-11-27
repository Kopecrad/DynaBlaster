package com.kopecrad.dynablaster.game.objects.graphics.spritesheet;

import android.util.Log;

import java.util.Map;

public class SpritesheetDataPool {

    public static final String DEFAULT_SPRITE = "player_anim";

    private Map<String, SpritesheetData> data;

    public SpritesheetDataPool() {
        SpritesheetDataLoader loader= new SpritesheetDataLoader();
        data= loader.loadData();
    }

    public SpritesheetData getData(String identifier) {
        if(data.containsKey(identifier))
            return data.get(identifier);

        Log.d("kek", "Failed to load anim");
        return data.get(DEFAULT_SPRITE);
    }
}
