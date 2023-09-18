package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class NutrientNoInputAdapter extends RecyclerView.Adapter<NutrientNoInputAdapter.NutrientViewHolder> {

    private List<String> dataList;

    public NutrientNoInputAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    public void updateList(List<String> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NutrientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_input_text, parent, false);
        return new NutrientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NutrientViewHolder holder, int position) {
        holder.textView.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class  NutrientViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public NutrientViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tvTitle);
        }
    }
}