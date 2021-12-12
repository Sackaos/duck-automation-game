package com.example.duck_automation_game;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<CustomListModel> {
    Context context;
    List<CustomListModel> objects;

    public CustomListAdapter(Context context, int resource, int textViewResourceId, ArrayList<CustomListModel> modelList) {
        super(context, 0, 0, modelList);
        this.context = context;
        this.objects = modelList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutinflater = ((Activity) context).getLayoutInflater();
        View view = layoutinflater.inflate(R.layout.custom_list_item, parent, false);
        TextView tvResourceName = (TextView) view.findViewById(R.id.tvResourceName);
        TextView tvResourceAmount = (TextView) view.findViewById(R.id.tvResourceAmount);
        CustomListModel temp = objects.get(position);
        tvResourceName.setText(String.valueOf(temp.getResourceName()));
        tvResourceAmount.setText(String.valueOf(temp.getResourceAmount()));
        return view;

    }
}
