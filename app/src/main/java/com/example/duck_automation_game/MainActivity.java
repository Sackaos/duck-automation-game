package com.example.duck_automation_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duck_automation_game.engine.BatteryReceiver;
import com.example.duck_automation_game.engine.CosmicCreatures;
import com.example.duck_automation_game.engine.CosmicDuck;
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
import java.util.function.Consumer;

interface StringFunction {
    String run(String str);
}


//just as a פרוטוקול i know this "game" is incomplete and very lacking but the foundation is here i just need to actually think of ways the factories combine well with each other and i need to add some other features like when seling a factory it will remove a factory on the map, the fact that the map currently is pretty useless, etc.
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
    private BatteryReceiver receiver;
    MainActivity main = this;

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
        receiver = new BatteryReceiver();
        createCosmicEntities();

    }

    private void createCosmicEntities() {
        CosmicCreatures creatures[] = new CosmicCreatures[2];
        creatures[0] = new CosmicDuck();
        creatures[1] = new CosmicDuck();
        creatures[0].fart(creatures[1]);
        if (creatures[1].getSmelly()) {
            Log.d("Cosmic", "sheesh he smelly");
            //are you happy now am i done with parents/children
            //also technically this (main) extends AppCompatActivity so i shouldnt even need to do that >:(
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initiateUpdater();

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        registerReceiver(receiver, filter);

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
        //map section
        Button mapBtn = findViewById(R.id.btn_Map);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToMapActivity(false, "setOnclicks:137");
            }
        });

        //settings section
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

        Button resetSharedPreferencesBtn = findViewById(R.id.btn_Settings_resetPref);
        resetSharedPreferencesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Consumer<Integer> whenAccept = (n) -> {
                    sharedPref.edit().clear().commit();
                    triggerRebirth(MainActivity.this, MainActivity.class);
                };

                Consumer<Integer> whenDeny = (n) -> {
                    showToast("K, be careful next time");
                };

                String description = "are you sure you want to reset All progress (and settings)?";

                displayDialog("Reset App?", description, "yes", whenAccept, "no", whenDeny);
            }
        });

        //Help section
        Button backBtnHelp = findViewById(R.id.btn_Help_Back);
        backBtnHelp.setOnClickListener(backToMenuListener);

        Button HelpBtn = findViewById(R.id.btn_Help);
        HelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLayoutMenu(R.id.ly_Help);
            }
        });

        TextView instructionTv = findViewById(R.id.tv_InstructionManual);
        instructionTv.setMovementMethod(new ScrollingMovementMethod());


        //reset section
        Button helpExitBtn = findViewById(R.id.btn_Help_Exit);
        helpExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Consumer<Integer> whenAccept = (n) -> {
                    finish();
                };

                Consumer<Integer> whenDeny = (n) -> {
                    showToast("phew, we dont do that here! no leaving!");
                };

                String description = "Do you want to close this application ?";

                displayDialog("Exit app?", description, "yes", whenAccept, "no", whenDeny);
            }
        });


        Button duckCreationBtn = findViewById(R.id.btn_Create_Cannon);
        duckCreationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pay 100 spice melange  for a duck
                for (CustomResourceModel resource : resourceArrList) {
                    String currentName = resource.getResourceName();
                    if (currentName.equals("spice melange") && resource.getResourceAmount() >= 100D) {
                        resource.setResourceAmount(resource.getResourceAmount() - 100D);
                        gameState.duckCounter++;
                        break;
                    }
                }
                checkForDuckWin();

            }
        });

        //end of Onclicks
    }

    private void checkForDuckWin() {
        ((TextView) findViewById(R.id.tv_CannonCounter)).setText("Anti-Duck Cannons on the planet: " + gameState.duckCounter);
        if (gameState.duckCounter >= 10) {
            showToast("OMG YOU WON");
            changeToWinActivity();
        }

    }

    private void changeToWinActivity() {
        Intent intent = new Intent(this, com.example.duck_automation_game.ui.WinActivity.class);
        startActivity(intent);
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
        //i know this is stupid but honestly, why bother its all gonna end just a few days from now and this works perfectly fine so meh IDC

        LinearLayout newLayout = findViewById(layoutID);
        LinearLayout oldLayout1 = findViewById(R.id.ly_MainMenu);
        LinearLayout oldLayout2 = findViewById(R.id.ly_Settings);
        LinearLayout oldLayout3 = findViewById(R.id.ly_Help);

        oldLayout1.setLayoutParams(getDefaultLayoutParamsWithWeight(0f));
        oldLayout2.setLayoutParams(getDefaultLayoutParamsWithWeight(0f));
        oldLayout3.setLayoutParams(getDefaultLayoutParamsWithWeight(0f));

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

                showToast("you came back without building a factory didnt you. THATS ILLEGAL");
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
        testHashMap.put("I Guess", 0.21);

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
        unregisterReceiver(receiver);

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


    /**
     * creates a dialog
     *
     * @param title       title
     * @param description description
     * @param acceptText  accept
     * @param cancelFunc  deny
     * @param acceptFunc  acceptfunction
     * @param cancelText  denyfunction
     */
    public void displayDialog(String title, String description, String acceptText, Consumer<Integer> acceptFunc, String cancelText, Consumer<Integer> cancelFunc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(description)
                .setCancelable(false)
                .setPositiveButton(acceptText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        acceptFunc.accept(0);
                    }
                })
                .setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.cancel();
                        cancelFunc.accept(0);
                    }
                }).setTitle(title);
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }
    //####################Menues stuff#########################

}


