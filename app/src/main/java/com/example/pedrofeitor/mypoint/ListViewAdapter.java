package com.example.pedrofeitor.mypoint;

import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<BusNumber> busnumberList = null;
    private ArrayList<BusNumber> arraylist;

    public ListViewAdapter(Context context, List<BusNumber> animalNamesList) {
        mContext = context;
        this.busnumberList = animalNamesList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<BusNumber>();
        this.arraylist.addAll(animalNamesList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return busnumberList.size();
    }

    @Override
    public BusNumber getItem(int position) {
        return busnumberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(busnumberList.get(position).getBusNumber());
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        busnumberList.clear();
        if (charText.length() == 0) {
            busnumberList.addAll(arraylist);
        } else {
            for (BusNumber wp : arraylist) {
                if (wp.getBusNumber().toLowerCase(Locale.getDefault()).contains(charText)) {
                    busnumberList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}