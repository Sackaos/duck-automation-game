package com.example.duck_automation_game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.duck_automation_game.engine.GameState;
import com.example.duck_automation_game.engine.Resource;
import com.example.duck_automation_game.ui.CustomListAdapter;
import com.example.duck_automation_game.ui.CustomListItemModel;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    GameState gameState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         gameState = new GameState();
        Log.d("GAD", "GameState created!");
        createResourcesListView(gameState);


        //adapter.add(new CustomListModel("iron",playerResource.get("iron")));
    }

    public void createResourcesListView(GameState gameState) {
        ArrayList<CustomListItemModel> resourceArr = new ArrayList<>();
        Map<String, Double> playerResource = gameState.getPlayerResource();
        Map<String, Double> playerProduction = gameState.getPlayerProduction();

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.iron);

        for (String key : playerResource.keySet()) {
            //clm=custom list object
            Log.d("GAD", key);

            CustomListItemModel obj = new CustomListItemModel(key, playerResource.get(key),formatProductionValue(playerProduction.get(key)));
            resourceArr.add(obj);
        }
        Log.d("GAD","!!!"+formatProductionValue(playerProduction.get("iron")));

//        ResourceArr.add(new CustomListModel("iron",playerResource.get("iron")));

        Log.d("GAD", "afterparty");
        CustomListAdapter adapter = new CustomListAdapter(this, 0, 0, resourceArr);
        ListView lvResources = (ListView) findViewById(R.id.lvResourceList);
        lvResources.setOnItemClickListener(this);
        lvResources.setAdapter(adapter);
    }




    public void showToast(String message, int length) {
        Toast.makeText(this, message,
                length).show();
    }

//    public void update(Map<String, Double> playerResource){
//        Map<String, Double> newResource
//        playerResource.put()
//    }

    public String formatProductionValue(Double production){
        if (production>0)return "+"+production;
        //unnecessary else lmao #flex
        else if (production<0)return "-"+production;
        return ""+production;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Resource[] resourcelist = gameState.getResourceList();
        Map<String, Double> playerProduction = gameState.getPlayerProduction();
        playerProduction.put(resourcelist[i].getName(),playerProduction.get(resourcelist[i].getName()));
        Log.d("GAD", String.valueOf(playerProduction.get(resourcelist[i].getName())));

        String toastMessage="name:"+resourcelist[i].getName();
        showToast(toastMessage, Toast.LENGTH_SHORT);
    }






}