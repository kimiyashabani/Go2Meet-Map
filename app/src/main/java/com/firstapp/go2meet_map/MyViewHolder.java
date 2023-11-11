package com.firstapp.go2meet_map;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{
    TextView title;
    private static final String TAG = "TAGParse, MyViewHolder";

    public MyViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
    }

    void bindValues(Item item) {
        // give values to the elements contained in the item view.
        // formats the title's text color depending on the "isSelected" argument.
        title.setText(item.getEventName());
    }
}
