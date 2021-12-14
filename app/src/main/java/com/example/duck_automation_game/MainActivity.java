package com.example.duck_automation_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    GameState gameState;
    Resource[] resourcesList;
    Map<String, Double> playerResource;
    Map<String, Double> playerProduction;
    CustomListAdapter adapter;
    Update updater;
    ArrayList<CustomListItemModel> resourceArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameState = new GameState();
        resourcesList = gameState.getResourceList();
        Log.d("GAD", "GameState created!");
        createResourcesListView(gameState);


        initiateUpdate(this);


    }

    private void initiateUpdate(MainActivity main) {
        Handler h = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                notifyAdapter();
            }
        };
        updater = new Update(h, gameState, this);
        updater.start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i <10 ; i++) {
//                    Log.d("GAD", "Thread: " + i);
//                    SystemClock.sleep(1000);
//                    gameState.update(1);
//                    main.notifyAdapter();
//
//                }
//            }
//        }).start();
    }

    public void createResourcesListView(GameState gameState) {

        resourceArr = new ArrayList<>();
        playerResource = gameState.getPlayerResource();
        playerProduction = gameState.getPlayerProduction();

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.iron);

//        for (String key : playerResource.keySet()) {
//            //clm=custom list object
//            Log.d("GAD", key);
//
//            CustomListItemModel obj = new CustomListItemModel(key, playerResource.get(key),formatProductionValue(playerProduction.get(key)));
//            resourceArr.add(obj);
//        }
        for (int i = 0; i < resourcesList.length; i++) {
            CustomListItemModel obj = new CustomListItemModel(resourcesList[i].getName(), playerResource.get(resourcesList[i].getName()), formatProductionValue(playerProduction.get(resourcesList[i].getName())));
            resourceArr.add(obj);
        }

        Log.d("GAD", "afterparty");
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

//    public void update(Map<String, Double> playerResource){
//        Map<String, Double> newResource
//        playerResource.put()
//    }

    public String formatProductionValue(Double production) {
        if (production > 0) return "+" + production;
            //unnecessary else lmao #flex
        else if (production < 0) return "-" + production;
        return "" + production;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //changes the data
        Map<String, Double> playerProductionMap = gameState.getPlayerProduction();
        String resourceName = resourcesList[i].getName();
        playerProductionMap.put(resourceName, playerProductionMap.get(resourceName) + 1.0d);
        //logs the action
        Log.d("GAD", "changeProduction: " + resourceName + ": " + playerProductionMap.get(resourceName));
        String toastMessage = "name:" + resourceName;
        showToast(toastMessage, Toast.LENGTH_SHORT);
        //notifies the listView
        CustomListItemModel item = new CustomListItemModel(resourceName, playerResource.get(resourceName), formatProductionValue(playerProduction.get(resourceName)));
        resourceArr.set(i, item);
        notifyAdapter();
        if (resourceName.equals("iron")) updater.interrupt();

        for (String key : playerResource.keySet()) {
            Log.d("GAD", "PlayerResource(map): " +playerResource.get(key));
        }
        for (int J = 0; J < resourcesList.length; J++) {
            Log.d("GAD","resourceList: "+J+resourcesList[J].getName()+"    ResourceARR     "+
            resourceArr.get(J).getResourceName());
        }
        }

    /**
     * either dont send anything to update all the list
     * or send the item that needs to be changed and what it needs to be changed into
     */
    public void notifyAdapter() {
        int i = 0;
        for (String key : playerResource.keySet()) {
            CustomListItemModel item = new CustomListItemModel(key, playerResource.get(key), formatProductionValue(playerProduction.get(key)));
            resourceArr.set(i, item);
            i++;
        }
        adapter.notifyDataSetChanged();

    }


}