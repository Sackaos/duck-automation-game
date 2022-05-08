package com.example.duck_automation_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.duck_automation_game.engine.Factory;
import com.example.duck_automation_game.engine.GameState;
import com.example.duck_automation_game.engine.Resource;
import com.example.duck_automation_game.engine.Update;
import com.example.duck_automation_game.ui.CustomFactoryListAdapter;
import com.example.duck_automation_game.ui.CustomResourceListAdapter;
import com.example.duck_automation_game.ui.CustomResourceModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String DUCK_COUNTER_PREFKEY = "duckcounterprefkey";
    private static final int MAP_ACTIVITY_RESULT_CODE = 15;
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
    Boolean powerSavingMode = false;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        gameState = new GameState(this);
        checkForDuckWin();
        resourcesList = gameState.getResourceList();
        resourceArrList = gameState.getResourceArrList();
        createResourceAdapter();
        factoryList = gameState.getPlayerfactories();
        createFactoryAdapter();
        initiateUpdater();
        builder = new AlertDialog.Builder(this);
        setOnClicks();
        Log.d(TAG, "on create: completed");

    }

    @Override
    protected void onResume() {
        super.onResume();
        initiateUpdater();
    }

    private void initiateUpdater() {
        Long lastLogin = sharedPref.getLong(LAST_TIME_LOGGED, System.currentTimeMillis());
        Long timeSinceLastLogin = (System.currentTimeMillis() - lastLogin) / 1000;
        gameState.update(timeSinceLastLogin);
        newUpdater();

    }

    private void newUpdater() {
        Handler h = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                notifyResourceAdapter();
            }
        };
        Double updateTime;
        if (powerSavingMode == true) updateTime = 1D;
        else updateTime = 0.2D;
        if (updater != null) {
            updater.interrupt();
        }
        updater = new Update(h, gameState, this, updateTime);
        updater.start();
    }

    //####################UI stuff#########################
    private void createFactoryAdapter() {
        factoryAdapter = new CustomFactoryListAdapter(this, 0, 0, gameState.getPlayerfactories(), gameState);
        ListView lvFactories = (ListView) findViewById(R.id.lv_Factories);
        lvFactories.setOnItemClickListener(this);
        lvFactories.setAdapter(factoryAdapter);

    }

    public void createResourceAdapter() {
        resourceAdapter = new CustomResourceListAdapter(this, 0, 0, resourceArrList);
        ListView lvResources = (ListView) findViewById(R.id.lv_ResourceList);
        lvResources.setOnItemClickListener(this);
        lvResources.setAdapter(resourceAdapter);

//if want add new resource mid-game :
//        playerProduction.put("NEWRESOURCE",0d);
//        playerResource.put("NEWRESOURCE",0d);
//        resourceArr.add(new CustomListItemModel("NEWRESOURCE",playerResource.get("NEWRESOURCE"),formatProductionValue(playerProduction.get("NEWRESOURCE"))));
//        adapter.notifyDataSetChanged();

    }

    private void setOnClicks() {
        Button mapBtn = findViewById(R.id.btn_Map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToMapActivity(false, "setOnclicks:137");
            }
        });


        Button backBtnSettings = findViewById(R.id.btn_Settings_Back);
        View.OnClickListener backToMenuListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLayoutMenu(R.id.ly_MainMenu);
            }
        };
        backBtnSettings.setOnClickListener(backToMenuListener);

        Button settingsBtn = findViewById(R.id.btn_Settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLayoutMenu(R.id.ly_Settings);
            }
        });

        Switch powerSavingSwitch = findViewById(R.id.switch_Settings_Power);
        powerSavingSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerSavingMode = powerSavingSwitch.isActivated();
                newUpdater();
            }
        });

        Button exitBtn = findViewById(R.id.btn_Exit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("Do you want to close this application ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                showToast("phew");
                            }
                        }).setTitle("Exit Tialoge");
                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        Button resetSharedPreferencesBtn = findViewById(R.id.btn_Settings_resetPref);
        resetSharedPreferencesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.setMessage("are you sure you want to reset All progress (and settings)?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sharedPref.edit().clear().commit();
                                triggerRebirth(MainActivity.this, MainActivity.class);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                showToast("K, be careful next time");
                            }
                        }).setTitle("Reset App?");
                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        Button duckCreationBtn = findViewById(R.id.btn_Create_Duck);
        duckCreationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pay 1000 iron for a duck
                for (CustomResourceModel resource:resourceArrList) {
                    String currentName = resource.getResourceName();
                    if (currentName.equals("spice melange")){
                        resource.setResourceAmount(resource.getResourceAmount()-100D);
                    break;
                    }
                }
                gameState.duckCounter++;
                checkForDuckWin();

            }
        });

        //end of Onclicks
    }

    private void checkForDuckWin() {
        if(gameState.duckCounter>=10){
            showToast("OMG YOU WON");
        changeToWinActivity();
        }

    }

    private void changeToWinActivity() {
        Intent intent = new Intent(this, com.example.duck_automation_game.ui.WinActivity.class);
        else startActivity(intent);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getAdapter() instanceof CustomResourceListAdapter) {
            String resourceName = resourcesList[i].getName();
            if (resourceName.equals("iron") || resourceName.equals("watermelons")) {
                Log.d(TAG, "onItemClick: resource clicked");
                CustomResourceModel currentItem = resourceArrList.get(i);
                Double newAmount = currentItem.getResourceAmount() + 1D;
                currentItem.setResourceAmount(newAmount);

            }
        }

        if (adapterView.getAdapter() instanceof CustomFactoryListAdapter) {
            Log.d(TAG, "onItemClick: " + factoryList.get(i).getFactoryName());
        }

        notifyResourceAdapter();
    }

    public static void triggerRebirth(Context context, Class myClass) {
        Intent intent = new Intent(context, myClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        Runtime.getRuntime().exit(0);
    }

    private void showLayoutMenu(int layoutID) {
        LinearLayout newLayout = findViewById(layoutID);
        LinearLayout oldLayout = findViewById(R.id.ly_MainMenu);

        oldLayout.setLayoutParams(getDefaultLayoutParamsWithWeight(0f));
        newLayout.setLayoutParams(getDefaultLayoutParamsWithWeight(0.4f));

    }

    private LinearLayout.LayoutParams getDefaultLayoutParamsWithWeight(float weight) {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                weight
        );
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

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void notifyProductionChange() {
        Map<String, Double> finalProductionMap = gameState.calculateProduction();
        for (int i = 0; i < this.resourceArrList.size(); i++) {
            CustomResourceModel item = resourceArrList.get(i);

            String itemName = item.getResourceName();
            Double currentItemProduction = finalProductionMap.get(itemName);

            item.setResourceProduction(currentItemProduction);
            notifyResourceAdapter();
        }
    }


//####################...Activities Stuff?#########################


    public void changeToMapActivity(Boolean canClick, String factoryName) {

        Intent intent = new Intent(this, com.example.duck_automation_game.ui.MapsActivity.class);
        intent.putExtra("canClick", canClick);
        intent.putExtra("factoryName", factoryName);


        if (canClick == true) startActivityForResult(intent, MAP_ACTIVITY_RESULT_CODE);
        else startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_ACTIVITY_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (!data.hasExtra("did_buy"))
                    showToast("something wrong happened -on activity result-");

                Boolean didBuy = data.getBooleanExtra("did_buy", false);
                if (didBuy) {
                    LatLng pos = data.getParcelableExtra("factory_postion");
                    gameState.setLastFactoryPos(pos);
                } else gameState.destroyLastFactory();
            }
            String factoryName = data.getStringExtra("factoryName");
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result

                showToast("you came back from the dead didnt you");
                changeToMapActivity(true, factoryName);
            }
        }
    }

//####################Saving  Stuff#########################

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

    public void saveDataToSharedPref() {
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

    public ArrayList<Factory> getFactoryListFromString(String arrayString) {

        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<ArrayList<Factory>>() {
        }.getType();
        ArrayList<Factory> factoryArray = gson.fromJson(arrayString, type);
        return factoryArray;
    }

    public void saveResourceListToString() {
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

    //####################Menues stuff#########################

}