package com.example.android.popularmusicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<ImageItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

//    private static final int VIEW_TYPE_COUNT = 1;
//    private static final int VIEW_GRID_STANDARD = 0;

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        ImageItem item = data.get(position);
        holder.imageTitle.setText(item.getTitle());
        holder.image.setImageBitmap(item.getImage());
        return row;
    }



    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getLayoutResourceId() {
        return layoutResourceId;
    }

    public void setLayoutResourceId(int layoutResourceId) {
        this.layoutResourceId = layoutResourceId;
    }

    public ArrayList<ImageItem> getData() {
        return data;
    }

    public void setData(ArrayList<ImageItem> data) {
        this.data = data;
    }

    public void addData(ArrayList<ImageItem> data) {
        this.data.addAll(data);

    }

    public static class ViewHolder {
        public final TextView imageTitle;
        public final ImageView image;

        public ViewHolder(View row) {
            imageTitle = (TextView) row.findViewById(R.id.text);
            image = (ImageView) row.findViewById(R.id.image);
        }
    }
}