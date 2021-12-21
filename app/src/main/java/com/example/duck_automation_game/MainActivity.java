package com.example.duck_automation_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.duck_automation_game.engine.GameState;
import com.example.duck_automation_game.engine.Resource;
import com.example.duck_automation_game.engine.Update;
import com.example.duck_automation_game.ui.CustomListAdapter;
import com.example.duck_automation_game.ui.CustomListItemModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    GameState gameState;
    Resource[] resourcesList;
    CustomListAdapter adapter;
    Update updater;
    ArrayList<CustomListItemModel> resourceArrList;
    SharedPreferences sharedPref;
    public String PLAYER_FACTORY_PREFKEY = "factoryprefkey";
    public String LAST_TIME_LOGGED = "dateprefkey";
    String TAG = "GAD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        gameState = new GameState(this);
        resourcesList = gameState.getResourceList();
        resourceArrList = gameState.getResourceArrList();
        Log.d(TAG, "on create: GameState created!");
        createAdapter();
        initiateUpdater(this);


    }

    private void initiateUpdater(MainActivity main) {
        Long lastLogin = sharedPref.getLong(LAST_TIME_LOGGED, System.currentTimeMillis());
        Long timeSinceLastLogin = (System.currentTimeMillis() - lastLogin) / 1000;
        gameState.update(timeSinceLastLogin);
        Handler h = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                notifyAdapter();
            }
        };
        updater = new Update(h, gameState, this);
        updater.start();
    }

    public void createAdapter() {
        adapter = new CustomListAdapter(this, 0, 0, resourceArrList);
        ListView lvResources = (ListView) findViewById(R.id.lvResourceList);
        lvResources.setOnItemClickListener(this);
        lvResources.setAdapter(adapter);

//if want add new resource mid-game :
//        playerProduction.put("NEWRESOURCE",0d);
//        playerResource.put("NEWRESOURCE",0d);
//        resourceArr.add(new CustomListItemModel("NEWRESOURCE",playerResource.get("NEWRESOURCE"),formatProductionValue(playerProduction.get("NEWRESOURCE"))));
//        adapter.notifyDataSetChanged();

    }


    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //changes the data
        String resourceName = resourcesList[i].getName();
        CustomListItemModel currentItem = resourceArrList.get(i);
        Double newProduction = currentItem.getResourceProduction()+50.015D;
        currentItem.setResourceProduction(newProduction);

        notifyAdapter();
//        if (resourceName.equals("iron")) updater.interrupt();
//
//        for (String key : playerResource.keySet()) {
//            Log.d(TAG, "PlayerResource(map): " +playerResource.get(key));
//        }
    }

    /**
     * every time theres a change in the data the view needs to get updated which is what notifyAdapter does
     */
    public void notifyAdapter() {
        for (int i = 0; i < resourcesList.length; i++) {
//            CustomListItemModel currentItem = resourceArrList.get(i);
//            String currentItemName = currentItem.getResourceName();
//            CustomListItemModel item = new CustomListItemModel(currentItemName, playerResource.get(currentItemName), playerProduction.get(currentItemName));
//            resourceArrList.set(i, item);
        }
        adapter.notifyDataSetChanged();

    }


    private void hashmaptest() {
        //create test hashmap
        HashMap<String, Double> testHashMap = new HashMap<String, Double>();
        testHashMap.put("key1", 0.5);
        testHashMap.put("key2", 0.1);

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(testHashMap);
        Log.d("GAD", "hashmapt!est: " + hashMapString);
        //save in shared prefs
        sharedPref.edit().putString("hashString", hashMapString).apply();

        //get from shared prefs
        String storedHashMapString = sharedPref.getString("hashString", "oopsDintWork");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Double>>() {
        }.getType();
        HashMap<String, Double> testHashMap2 = gson.fromJson(storedHashMapString, type);

        //use values
        String toastString = testHashMap2.get("key1") + " | " + testHashMap2.get("iron");
        Log.d(TAG, "hashmaptest!: " + toastString);
        Toast.makeText(this, toastString, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        updater.interrupt();
        //SystemClock.sleep(201);
        Long date = System.currentTimeMillis();
        sharedPref.edit().putLong(LAST_TIME_LOGGED, date).apply();

        for (int i = 0; i < resourcesList.length; i++) {
            String currentName = resourceArrList.get(i).getResourceName();
            String currentAmount = "" + resourceArrList.get(i).getResourceAmount();
            sharedPref.edit().putString(currentName, currentAmount).apply();
        }
    }

    public Map<String, Double> getMapFromPrefs(String prefKey) {
        Gson gson = new Gson();
        //get from shared prefs
        String storedHashMapString = sharedPref.getString(prefKey, null);
        if (storedHashMapString == null) return null;
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Double>>() {
        }.getType();
        HashMap<String, Double> hashMap = gson.fromJson(storedHashMapString, type);
        return hashMap;
    }

    public String getStringFromPref(String prefKey, String defaultValue) {
        return sharedPref.getString(prefKey, defaultValue);
    }

}