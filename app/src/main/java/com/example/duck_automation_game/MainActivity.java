package com.example.duck_automation_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.renderscript.Sampler;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    GameState gameState;
    Resource[] resourcesList;
    Map<String, Double> playerResource;
    Map<String, Double> playerProduction;
    CustomListAdapter adapter;
    Update updater;
    ArrayList<CustomListItemModel> resourceArr;
    SharedPreferences sharedPref;
    public String PLAYER_RESOURCE_PREFKEY = "resourceprefkey";
    String TAG = "GAD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        gameState = new GameState(this);
        resourcesList = gameState.getResourceList();
        Log.d(TAG, "GameState created!");
        createResourcesListView(gameState);
        initiateUpdater(this);


    }

    private void initiateUpdater(MainActivity main) {
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

    public void createResourcesListView(GameState gameState) {

        resourceArr = new ArrayList<>();
        playerResource = gameState.getPlayerResource();
        playerProduction = gameState.getPlayerProduction();

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.iron);

        for (int i = 0; i < resourcesList.length; i++) {
            String currentResourceName = resourcesList[i].getName();
            CustomListItemModel item = new CustomListItemModel(currentResourceName, formatResouceValue((currentResourceName)), formatProductionValue(currentResourceName));
            resourceArr.add(item);
        }

        Log.d(TAG, "afterparty");
        adapter = new CustomListAdapter(this, 0, 0, resourceArr);
        ListView lvResources = (ListView) findViewById(R.id.lvResourceList);
        lvResources.setOnItemClickListener(this);
        lvResources.setAdapter(adapter);

//if want add new resource mid-game :
//        playerProduction.put("NEWRESOURCE",0d);
//        playerResource.put("NEWRESOURCE",0d);
//        resourceArr.add(new CustomListItemModel("NEWRESOURCE",playerResource.get("NEWRESOURCE"),formatProductionValue(playerProduction.get("NEWRESOURCE"))));
//        adapter.notifyDataSetChanged();

    }


    public void showToast(String message, int length) {
        Toast.makeText(this, message,
                length).show();
    }


    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //changes the data
        Map<String, Double> playerProductionMap = gameState.getPlayerProduction();
        String resourceName = resourcesList[i].getName();
        playerProductionMap.put(resourceName, playerProductionMap.get(resourceName) + 50.0001d);
        //Log.d(TAG, "changeProduction: " + resourceName + ": " + playerProductionMap.get(resourceName));
        //notifies the listView
        CustomListItemModel item = new CustomListItemModel(resourceName, formatResouceValue(resourceName), formatProductionValue(resourceName));
        resourceArr.set(i, item);
        notifyAdapter();
//        if (resourceName.equals("iron")) updater.interrupt();
//
//        for (String key : playerResource.keySet()) {
//            Log.d(TAG, "PlayerResource(map): " +playerResource.get(key));
//        }
//        for (int J = 0; J < resourcesList.length; J++) {
//            Log.d(TAG,"resourceList: "+J+resourcesList[J].getName()+"    ResourceARR     "+
//            resourceArr.get(J).getResourceName());
//        }
    }

    /**
     * every time theres a change in the data the view needs to get updated which is what notifyAdapter does
     */
    public void notifyAdapter() {
        for (int i = 0; i < resourcesList.length; i++) {
            CustomListItemModel currentItem = resourceArr.get(i);
            String currentItemName = currentItem.getResourceName();
            String currentItemResource = formatResouceValue(currentItemName);
            String currentItemProduction = formatProductionValue(currentItemName);
            CustomListItemModel item = new CustomListItemModel(currentItemName, currentItemResource, currentItemProduction);
            resourceArr.set(i, item);
        }
        adapter.notifyDataSetChanged();

    }

    public String formatProductionValue(String key) {
        Double production = playerProduction.get(key);
        production = Double.parseDouble(new DecimalFormat("#####.##").format(production));
        String s = "";

        if (production > 0) s = "+";
        else if (production < 0) s = "-";

        if (production >= 1000 || production <= -1000) {
            production = Double.parseDouble(new DecimalFormat("#####.##").format(production / 1000));
            s = s + production + "K";
        } else s = s + production;
        return s;
    }

    private String formatResouceValue(String key) {
        Double resourceVal = playerResource.get(key);
        String s;
        if (resourceVal >= 1000) {
            Double d = Double.parseDouble(new DecimalFormat("#####.##").format(resourceVal / 1000));
            s = d + "K";
        } else {
            Double d = Double.parseDouble(new DecimalFormat("#####.##").format(resourceVal));
            s = "" + d;
        }
        return s;
    }

    private void hashmaptest() {
        //create test hashmap
        HashMap<String, Double> testHashMap = new HashMap<String, Double>();
        testHashMap.put("key1", 0.5);
        testHashMap.put("key2", 0.1);

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(testHashMap);
        Log.d("GAD", "hashmaptest: "+hashMapString);
        //save in shared prefs
        sharedPref.edit().putString("hashString", hashMapString).apply();

        //get from shared prefs
        String storedHashMapString = sharedPref.getString("hashString", "oopsDintWork");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Double>>() {
        }.getType();
        HashMap<String, Double> testHashMap2 = gson.fromJson(storedHashMapString, type);

        //use values
        String toastString = testHashMap2.get("key1") + " | " + testHashMap2.get("iron");
        Log.d(TAG, "hashmaptest: " + toastString);
        Toast.makeText(this, toastString, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        updater.interrupt();
        SystemClock.sleep(500);
        //save to shared pref

        //convert to string using gson
        Gson gson = new Gson();
        String resourceMapString = gson.toJson(playerResource);
        Log.d("GAD", "onDestroy!!!!!!: " + resourceMapString);
        //save in shared prefs
        sharedPref.edit().putString(PLAYER_RESOURCE_PREFKEY, resourceMapString).apply();
        hashmaptest();
//        playerProduction;
//        playerResource;
//        String value = sharedPref.getString(PLAYER_RESOURCE_PREFKEY, "");

        super.onDestroy();
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

}