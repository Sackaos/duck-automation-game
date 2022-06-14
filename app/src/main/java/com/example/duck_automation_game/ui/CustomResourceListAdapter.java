package com.example.duck_automation_game.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.duck_automation_game.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomResourceListAdapter extends ArrayAdapter<CustomResourceModel> {
    Context context;
    List<CustomResourceModel> objects;

    public CustomResourceListAdapter(Context context, int resource, int textViewResourceId, ArrayList<CustomResourceModel> modelList) {
        super(context, 0, 0, modelList);
        this.context = context;
        this.objects = modelList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutinflater = ((Activity) context).getLayoutInflater();

        View view = layoutinflater.inflate(R.layout.custom_resource_model, parent, false);

        TextView tvResourceName = (TextView) view.findViewById(R.id.tvResourceName);
        TextView tvResourceAmount = (TextView) view.findViewById(R.id.tvResourceAmount);
        TextView tvResourceProduction = (TextView) view.findViewById(R.id.tvResourceProduction);

        CustomResourceModel temp = objects.get(position);

        tvResourceName.setText(String.valueOf(temp.getResourceName()));

        tvResourceAmount.setText(formatResouceValue(temp.getResourceAmount()));
        //String.valueOf());
        tvResourceProduction.setText(formatProductionValue(temp.getResourceProduction()));
        return view;

    }

    public String formatProductionValue(Double productionAmount) {
        productionAmount = Double.parseDouble(new DecimalFormat("#####.##").format(productionAmount));
        String s = "";

        if (productionAmount > 0) s = "+";
        else if (productionAmount < 0) s = "-";

        if (productionAmount >= 1000000|| productionAmount <= -1000000) {
            Double d = Double.parseDouble(new DecimalFormat("###.##").format(productionAmount / 1000000));
            s = d + "M";
        }
        if (productionAmount >= 1000 || productionAmount <= -1000) {
            productionAmount = Double.parseDouble(new DecimalFormat("#####.##").format(productionAmount / 1000));
            s = s + productionAmount + "K";
        } else s = s + productionAmount;
        return s;
    }

    private String formatResouceValue(Double resourceAmount) {
        String s;
        if (resourceAmount >= 1000000) {
            Double d = Double.parseDouble(new DecimalFormat("###.##").format(resourceAmount / 1000000));
            s = d + "M";
        }

        else if (resourceAmount >= 1000) {
            Double d = Double.parseDouble(new DecimalFormat("###.##").format(resourceAmount / 1000));
            s = d + "K";
        }

        else {
            Double d = Double.parseDouble(new DecimalFormat("###.##").format(resourceAmount));
            s = "" + d;
        }
        return s;
    }
}
