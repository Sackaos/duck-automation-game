package com.example.duck_automation_game.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.duck_automation_game.MainActivity;
import com.example.duck_automation_game.R;
import com.example.duck_automation_game.engine.Factory;
import com.example.duck_automation_game.engine.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomFactoryListAdapter extends ArrayAdapter<Factory> {
    Context context;
    List<Factory> objects;
    GameState gameState;
    int sellbtnColor = R.color.purple_200;

    public CustomFactoryListAdapter(Context context, int resource, int textViewResourceId, ArrayList<Factory> factoryList, GameState gameState) {
        super(context, resource, textViewResourceId, factoryList);
        this.context = context;
        this.objects = factoryList;
        this.gameState = gameState;
    }

    @SuppressLint("ResourceAsColor")
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutinflater = ((Activity) context).getLayoutInflater();

        View view = layoutinflater.inflate(R.layout.custom_factory_model, parent, false);

        TextView tvFactoryName = (TextView) view.findViewById(R.id.tvFactoryName);
        TextView tvFactoryCost = (TextView) view.findViewById(R.id.tvFactoryCost);
        TextView tvFactoryAmount = (TextView) view.findViewById(R.id.tvFactoryAmount);
        TextView tvFactoryProduction = (TextView)view.findViewById(R.id.tvFactoryProduction);
        Button btnFactoryBuy = (Button) view.findViewById(R.id.btnBuy);
        Button btnFactorySell = (Button) view.findViewById(R.id.btnSell);
        Factory temp = objects.get(position);

        tvFactoryName.setText(temp.getFactoryName());
        Map<String, Double> costMap = temp.getCostMap();
        Map<String, Double> prodMap = temp.getProductionMap();
        String costText = "";
        String prodText="";
        for (String key : costMap.keySet()) {
            Double currentFactoryCost = costMap.get(key) * (temp.getFactoryAmount() * 1.5);
            String str = key + ": " + currentFactoryCost;
            costText = costText + "\n" + str;
        }
        for (String key : prodMap.keySet()) {
            Double currentFactoryProd = prodMap.get(key);
            String str = key + ": " + currentFactoryProd;
            prodText = prodText + "\n" + str;
        }
        tvFactoryCost.setText(costText);
        tvFactoryProduction.setText(prodText);
        tvFactoryAmount.setText("Amount: " + String.valueOf(temp.getFactoryAmount()));
        btnFactoryBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameState.buildFactory(position);
            }
        });
        btnFactorySell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameState.sellFactory(position);
            }
        });
        //tvFactoryAmount.setText(temp.get);
        btnFactorySell.setBackgroundColor(sellbtnColor);

        return view;
    }

    public void setSellbtnColor(int color){
        this.sellbtnColor = ContextCompat.getColor(getContext(), color);
    }

}
