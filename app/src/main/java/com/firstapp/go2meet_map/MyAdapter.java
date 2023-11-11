package com.firstapp.go2meet_map;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private static final String TAG = "TAGParse, MyAdapter";
    private final Dataset dataset; // reference to the dataset
    public MyAdapter(Dataset dataset) {
        super();
        Log.d(TAG, "MyAdapter() called");
        this.dataset = dataset;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // this method has to actually inflate the item view and return the view holder.
        // it does not give values to the elements of the view holder.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // this method gives values to the elements of the view holder 'holder'
        // (values corresponding to the item in 'position')

        final Item item = dataset.getItemAtPosition(position);

        Log.d(TAG, "onBindViewHolder() called for element in position " + position);
        holder.bindValues(item);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
