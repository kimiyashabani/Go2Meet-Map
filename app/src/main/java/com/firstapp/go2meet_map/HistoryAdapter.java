/*******************************************************************************
 * Copyright (c) 1999, 2016 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 */
package com.firstapp.go2meet_map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<String> history;

    public HistoryAdapter(ArrayList<String> dataSet) {
        history = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);

        return new ViewHolder(view);
    }

    public void add(String data) {
        history.add(data);
        this.notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(history.get(position));
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.row_text);
        }
    }

}
