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

import com.example.duck_automation_game.engine.Factory;
import com.example.duck_automation_game.engine.GameState;
import com.example.duck_automation_game.engine.Resource;
import com.example.duck_automation_game.engine.Update;
import com.example.duck_automation_game.ui.CustomFactoryListAdapter;
import com.example.duck_automation_game.ui.CustomResourceListAdapter;
import com.example.duck_automation_game.ui.CustomResourceModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    GameState gameState;
    Resource[] resourcesList;
    CustomResourceListAdapter resourceAdapter;
    CustomFactoryListAdapter factoryAdapter;
    ArrayList<Factory> factoryList;
    ArrayList<CustomResourceModel> resourceArrList;
    Update updater;
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
        createResourceAdapter();
        factoryList = gameState.getPlayerfactories();
        createFactoryAdapter();
        initiateUpdater(this);
        Log.d(TAG, "on create: completed");
    }

    private void initiateUpdater(MainActivity main) {
        Long lastLogin = sharedPref.getLong(LAST_TIME_LOGGED, System.currentTimeMillis());
        Long timeSinceLastLogin = (System.currentTimeMillis() - lastLogin) / 1000;
        gameState.update(timeSinceLastLogin);
        Handler h = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                notifyResourceAdapter();
            }
        };
        updater = new Update(h, gameState, this);
        updater.start();
    }

    private void createFactoryAdapter() {
        factoryAdapter = new CustomFactoryListAdapter(this, 0, 0, gameState.getPlayerfactories());
        ListView lvFactories = (ListView) findViewById(R.id.lvFactories);
        lvFactories.setOnItemClickListener(this);
        lvFactories.setAdapter(factoryAdapter);

    }

    public void createResourceAdapter() {
        resourceAdapter = new CustomResourceListAdapter(this, 0, 0, resourceArrList);
        ListView lvResources = (ListView) findViewById(R.id.lvResourceList);
        lvResources.setOnItemClickListener(this);
        lvResources.setAdapter(resourceAdapter);

//if want add new resource mid-game :
//        playerProduction.put("NEWRESOURCE",0d);
//        playerResource.put("NEWRESOURCE",0d);
//        resourceArr.add(new CustomListItemModel("NEWRESOURCE",playerResource.get("NEWRESOURCE"),formatProductionValue(playerProduction.get("NEWRESOURCE"))));
//        adapter.notifyDataSetChanged();

    }


    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getAdapter() instanceof CustomResourceListAdapter) {
            Log.d(TAG, "onItemClick: ITS WROKING");

            String resourceName = resourcesList[i].getName();
            CustomResourceModel currentItem = resourceArrList.get(i);
            Double newProduction = currentItem.getResourceProduction() + 50.015D / (i + 1);
            currentItem.setResourceProduction(newProduction);
        }

        if (adapterView.getAdapter() instanceof CustomFactoryListAdapter) {
            Log.d(TAG, "onItemClick: ITS REALLY WROKING  " + factoryList.get(i).getFactoryName());

            gameState.BuildFactory(i);


        }


        notifyResourceAdapter();
//        if (resourceName.equals("iron")) updater.interrupt();
//
//        for (String key : playerResource.keySet()) {
//            Log.d(TAG, "PlayerResource(map): " +playerResource.get(key));
//        }
    }

    /**
     * every time theres a change in the data the view needs to get updated which is what notifyAdapter does
     */
    public void notifyResourceAdapter() {
//        for (int i = 0; i < resourcesList.length; i++) {
//            CustomListItemModel currentItem = resourceArrList.get(i);
//            String currentItemName = currentItem.getResourceName();
//            CustomListItemModel item = new CustomListItemModel(currentItemName, playerResource.get(currentItemName), playerProduction.get(currentItemName));
//            resourceArrList.set(i, item);
//        }
        resourceAdapter.notifyDataSetChanged();

    }

    public void notifyFactoryAdapter() {
        factoryAdapter.notifyDataSetChanged();
    }

    public void fart() {
        HashMap<String, Double> testHashMap = new HashMap<String, Double>();
        testHashMap.put("lets say iron ", 0.5);
        testHashMap.put("maybe copper!", 0.1);
        testHashMap.put("that might be spice", 432.1);
        testHashMap.put("or is that worm poop", 23.1);
        testHashMap.put("IG", 0.21);

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(testHashMap);
    }

    private void hashmaptest() {
        //create test hashmap
        HashMap<String, Double> testHashMap = new HashMap<String, Double>();
        testHashMap.put("lets say iron ", 0.5);
        testHashMap.put("maybe copper!", 0.1);
        testHashMap.put("that might be spice", 432.1);
        testHashMap.put("or is that worm poop", 23.1);
        testHashMap.put("IG", 0.21);

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
        saveDataToSharedPref();


    }
    public void saveDataToSharedPref(){
        Long date = System.currentTimeMillis();
        sharedPref.edit().putLong(LAST_TIME_LOGGED, date).apply();
        saveResourceListToString();
        saveFactoryListToString();
    }

    private void saveFactoryListToString() {
        Gson gson = new Gson();
        String factoryListString = gson.toJson(factoryList);
        //save in shared prefs
        sharedPref.edit().putString(PLAYER_FACTORY_PREFKEY, factoryListString).apply();
    }
    public ArrayList<Factory> getFactoryListFromString(String arrayString){

        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<ArrayList<Factory>>() {
        }.getType();
        ArrayList<Factory> factoryArray = gson.fromJson(arrayString, type);
        return factoryArray;
    }
    public void saveResourceListToString(){
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

    public void notifyProductionChange() {
        Map<String, Double> finalProductionMap = gameState.calculateProduction();
        for (int i = 0; i < this.resourceArrList.size(); i++) {
            CustomResourceModel item = resourceArrList.get(i);
            String itemName = item.getResourceName();
            Double currentItemProduction = finalProductionMap.get(itemName);
            item.setResourceProduction(currentItemProduction);
            //Log.d(TAG, "notifyProductionChange: "+item.getResourceProduction());
            notifyResourceAdapter();
        }
    }
}