package com.example.health;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * @author Luis M. Claramunt
 * Adapter for the RecyclerView that shows HealthData
 * June 2021
 */
public class HealthDataRecyclerViewAdapter extends RecyclerView.Adapter<HealthDataRecyclerViewAdapter.SymptomsViewHolder> implements Filterable {
    private ArrayList<HealthData> healthData;
    private ArrayList<HealthData> healthDataFullList;      //List used for search
    private OnItemClickListener listener;

    /**
     * OnItemClickListener for which item is clicked in the RecyclerView
     */
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    /**
     * Set OnClickListener for the rows that are clicked on the RecyclerView
     * @param clickListener - OnItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public static class SymptomsViewHolder extends RecyclerView.ViewHolder{
        public TextView tvDate, tvBpm, tvRespRate, tvNausea, tvHeadache, tvDiarrhea, tvSoreThroat,
        tvFever, tvMusAche, tvSmellTaste, tvCough, tvBreath, tvTired;

        public SymptomsViewHolder(@NonNull View itemView, OnItemClickListener clickListener) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvBpm = itemView.findViewById(R.id.tvBPM);
            tvRespRate = itemView.findViewById(R.id.tvRespRate);
            tvNausea = itemView.findViewById(R.id.tvNausea);
            tvHeadache = itemView.findViewById(R.id.tvHeadache);
            tvDiarrhea = itemView.findViewById(R.id.tvDiarrhea);
            tvSoreThroat = itemView.findViewById(R.id.tvSoreThroat);
            tvFever = itemView.findViewById(R.id.tvFever);
            tvMusAche = itemView.findViewById(R.id.tvMuscleAche);
            tvSmellTaste = itemView.findViewById(R.id.tvSmellTaste);
            tvCough = itemView.findViewById(R.id.tvCough);
            tvBreath = itemView.findViewById(R.id.tvShortnessBr);
            tvTired = itemView.findViewById(R.id.tvTired);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(clickListener != null && position != RecyclerView.NO_POSITION){
                    clickListener.onItemClick(position);
                }
            });
        }
    }

    public HealthDataRecyclerViewAdapter(ArrayList<HealthData> symptomArrayList){
        healthData = symptomArrayList;
        healthDataFullList = new ArrayList<>(healthData);
    }

    /**
     * Pass the layout of a row to the adapter
     */
    @NonNull
    @Override
    public SymptomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_symptom_row, parent, false);
        return new SymptomsViewHolder(view, listener);
    }

    /**
     * Pass Symptom's values to TextViews displayed in the RecyclerView rows
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SymptomsViewHolder holder, int position) {
        HealthData hd = healthData.get(position);
        holder.tvDate.setText(hd.getDate());
        holder.tvBpm.setText(Integer.toString(hd.getBpm()));
        holder.tvRespRate.setText(Integer.toString(hd.getRespRate()));
        holder.tvNausea.setText(Integer.toString(hd.getNausea()));
        holder.tvHeadache.setText(Integer.toString(hd.getHeadache()));
        holder.tvDiarrhea.setText(Integer.toString(hd.getDiarrhea()));
        holder.tvSoreThroat.setText(Integer.toString(hd.getSoreThroat()));
        holder.tvFever.setText(Integer.toString(hd.getFever()));
        holder.tvMusAche.setText(Integer.toString(hd.getMuscleAche()));
        holder.tvSmellTaste.setText(Integer.toString(hd.getSmellTaste()));
        holder.tvCough.setText(Integer.toString(hd.getCough()));
        holder.tvBreath.setText(Integer.toString(hd.getShortnessBreath()));
        holder.tvTired.setText(Integer.toString(hd.getTired()));
    }


    /**
     * The number of items in the RecyclerView
     * @return - ArrayList size
     */
    @Override
    public int getItemCount() {
        return healthData.size();
    }

    @Override
    public Filter getFilter(){
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<HealthData> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(healthDataFullList);
            }else{
                String pattern = constraint.toString().toLowerCase().trim();

                for(HealthData symptom: healthDataFullList){
                    String date = symptom.getDate();
                    if(date.toLowerCase().contains(pattern)){
                        filteredList.add(symptom);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            healthData.clear();
            healthData.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
