package com.example.myapplication.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.NutritionFacts;

import java.util.ArrayList;
import java.util.List;

public class NutrientAdapter extends RecyclerView.Adapter<NutrientAdapter.NutrientViewHolder> {
    private RecyclerView recyclerView;
    private List<String> dataList;

    public NutrientAdapter(List<String> dataList,RecyclerView recyclerView) {
        this.dataList = dataList;
        this.recyclerView = recyclerView;
    }

    public void updateList(List<String> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NutrientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nutrients, parent, false);
        return new NutrientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NutrientViewHolder holder, int position) {
        String[] parts = dataList.get(position).split(",");
        Log.d("test", "testL,dataList.get(position)" + dataList.get(position));
        Log.d("test", "testL,parts" + parts);
        Log.d("test", "testL,parts.length" + parts.length);

        if (parts.length < 3) {
            return;
        }
        holder.textView.setText(parts[0]);
        holder.etGram.setText(parts[1]);
        holder.tvGram.setText(parts[2]);
    }

    public NutritionFacts getAllEditTextValues() {
        int carbohydrates = 0;
        int protein = 0;
        int fat = 0;
        int vitamins = 0;
        int minerals = 0;
        int dietaryFiber = 0;
        int water = 0;

        for (int i = 0; i < dataList.size(); i++) {
            NutrientViewHolder holder = (NutrientViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                String editTextValue = holder.etGram.getText().toString();
                int value = Integer.parseInt(editTextValue);

                switch (i) {
                    case 0:
                        carbohydrates = value;
                        break;
                    case 1:
                        protein = value;
                        break;
                    case 2:
                        fat = value;
                        break;
                    case 3:
                        vitamins = value;
                        break;
                    case 4:
                        minerals = value;
                        break;
                    case 5:
                        dietaryFiber = value;
                        break;
                    case 6:
                        water = value;
                        break;
                    default:
                        break;
                }
            }
        }

        return new NutritionFacts("", carbohydrates, protein, fat, vitamins, minerals, dietaryFiber, water);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class  NutrientViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public EditText etGram;
        public TextView tvGram;

        public NutrientViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tvTitle);
            etGram = view.findViewById(R.id.etGram);
            tvGram = view.findViewById(R.id.tvGram);
        }
    }
}