package com.firstapp.go2meet_map;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder{
    TextView title;
    TextView place;

    public MyViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        place = itemView.findViewById(R.id.place);
    }

    void bindValues(Item item) {
        // give values to the elements contained in the item view.
        // formats the title's text color depending on the "isSelected" argument.
        title.setText(item.getEventName());
        place.setText(item.getPlace());
        Log.d("ViewHolder", "New item printed");
    }
}
