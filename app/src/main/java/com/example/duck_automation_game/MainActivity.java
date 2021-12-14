package com.example.duck_automation_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
    ArrayList<CustomListItemModel> resourceArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiateUpdate();
        gameState = new GameState();
        resourcesList = gameState.getResourceList();
        Log.d("GAD", "GameState created!");
        createResourcesListView(gameState);


        //adapter.add(new CustomListModel("iron",playerResource.get("iron")));
    }

    private void initiateUpdate() {
        Handler h = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };
        Update updater = new Update(h);
        updater.start();
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
//

        Log.d("GAD", "afterparty");
        adapter = new CustomListAdapter(this, 0, 0, resourceArr);
        ListView lvResources = (ListView) findViewById(R.id.lvResourceList);
        lvResources.setOnItemClickListener(this);
        lvResources.setAdapter(adapter);
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
        Map<String, Double> playerProduction = gameState.getPlayerProduction();
        playerProduction.put(resourcesList[i].getName(), playerProduction.get(resourcesList[i].getName()));

        Log.d("GAD", String.valueOf(resourcesList[i].getName()));
        changeProductionValue(playerProduction,resourcesList[i].getName(), playerProduction.get(resourcesList[i].getName())+1d,i);

        String toastMessage = "name:" + resourcesList[i].getName();
        showToast(toastMessage, Toast.LENGTH_SHORT);


    }

    private void changeProductionValue(Map <String,Double> map, String key, Double value,int pos) {
        map.put(key, value);
        Log.d("GAD", "changeProduction: "+map.get(key));

        resourceArr.set(pos,new CustomListItemModel(key,playerResource.get(key),formatProductionValue(value)));
        adapter.notifyDataSetChanged();

        updateProduction();
    }

    private void updateProduction() {

    }


}