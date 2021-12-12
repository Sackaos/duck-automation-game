package com.example.duck_automation_game.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.duck_automation_game.R;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<CustomListItemModel> {
    Context context;
    List<CustomListItemModel> objects;

    public CustomListAdapter(Context context, int resource, int textViewResourceId, ArrayList<CustomListItemModel> modelList) {
        super(context, 0, 0, modelList);
        this.context = context;
        this.objects = modelList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutinflater = ((Activity) context).getLayoutInflater();

        View view = layoutinflater.inflate(R.layout.custom_list_item, parent, false);

        TextView tvResourceName = (TextView) view.findViewById(R.id.tvResourceName);
        TextView tvResourceAmount = (TextView) view.findViewById(R.id.tvResourceAmount);
        TextView tvResourceProduction = (TextView) view.findViewById(R.id.tvResourceProduction);

        CustomListItemModel temp = objects.get(position);

        tvResourceName.setText(String.valueOf(temp.getResourceName()));
        tvResourceAmount.setText(String.valueOf(temp.getResourceAmount()));
        tvResourceProduction.setText(String.valueOf(temp.getResourceProduction()));
        return view;

    }
}
