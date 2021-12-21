package com.example.duck_automation_game.ui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.duck_automation_game.R;
import com.example.duck_automation_game.engine.Factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomFactoryListAdapter extends ArrayAdapter<Factory> {
    Context context;
    List<Factory> objects;

    public CustomFactoryListAdapter(Context context, int resource, int textViewResourceId, ArrayList<Factory> factoryList) {
        super(context, resource, textViewResourceId, factoryList);
        this.context = context;
        this.objects = factoryList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutinflater = ((Activity) context).getLayoutInflater();

        View view = layoutinflater.inflate(R.layout.custom_factory_model, parent, false);

        TextView tvFactoryName = (TextView) view.findViewById(R.id.tvFactoryName);
        TextView tvFactoryCost = (TextView) view.findViewById(R.id.tvFactoryCost);
        TextView tvFactoryAmount = (TextView) view.findViewById(R.id.tvFactoryAmount);

        Factory temp = objects.get(position);
        tvFactoryName.setText(temp.getFactoryName());
        Map<String, Double> costMap = temp.getCostMap();
        String costText = "";
        for (String key : costMap.keySet()) {
            String str = key + ": " + costMap.get(key);
            costText = costText + "\n" + str;
        }
        tvFactoryCost.setText(costText);
        tvFactoryAmount.setText(String.valueOf(temp.getFactoryAmount()));

        //tvFactoryAmount.setText(temp.get);
        return view;
    }


}
