package com.example.duck_automation_game;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameState gameState = new GameState();
        Log.d("GAD", "GameState created!");


        ArrayList<CustomListModel> resourceArr = new ArrayList<>();
        Map<String, Double> playerResource=gameState.getPlayerResource();
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.iron);

        for ( String key : playerResource.keySet() ) {
            //clm=custom list object
            Log.d("GAD", key);
            CustomListModel obj=new CustomListModel(key,playerResource.get(key));
            resourceArr.add(obj);
        }
//        ResourceArr.add(new CustomListModel("iron",playerResource.get("iron")));

        Log.d("GAD", "afterparty");
        CustomListAdapter adapter = new CustomListAdapter(this,0,0,resourceArr );
        ListView lvResources = (ListView) findViewById(R.id.lvResourceList);
        lvResources.setOnItemClickListener(this);
        lvResources.setAdapter(adapter);


        //adapter.add(new CustomListModel("iron",playerResource.get("iron")));
    }




    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //adapterView.getItemAtPosition(i)
        //((CustomListModel)view).getResourceAmount();
        //LIST[i].getresource

        showToast("value",Toast.LENGTH_SHORT);
    }

    public void showToast(String message,int length) {
        Toast.makeText(this, message,
                length).show();
    }

//    public void update(Map<String, Double> playerResource){
//        Map<String, Double> newResource
//        playerResource.put()
//    }
}