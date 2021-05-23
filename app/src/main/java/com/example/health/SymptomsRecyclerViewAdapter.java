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
import java.util.List;

/**
 * @author Luis M. Claramunt
 * Adapter for the RecyclerView that shows Symptoms
 */
public class SymptomsRecyclerViewAdapter extends RecyclerView.Adapter<SymptomsRecyclerViewAdapter.SymptomsViewHolder> implements Filterable {
    private ArrayList<Symptom> symptomsList;
    private ArrayList<Symptom> symptomsFullList;             //List used for search
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
        public TextView symptomName, symptomRating;

        public SymptomsViewHolder(@NonNull View itemView, OnItemClickListener clickListener) {
            super(itemView);
            symptomName = itemView.findViewById(R.id.tv_symptom_row_name);
            symptomRating = itemView.findViewById(R.id.tv_symptom_row_rating);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(clickListener != null && position != RecyclerView.NO_POSITION){
                    clickListener.onItemClick(position);
                }
            });
        }
    }

    public SymptomsRecyclerViewAdapter(ArrayList<Symptom> symptomArrayList){
        symptomsList = symptomArrayList;
        symptomsFullList = new ArrayList<>(symptomsList);
    }

    /**
     * Pass the layout of a row to the adapter
     */
    @NonNull
    @Override
    public SymptomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_symptom_row, parent, false);
        return new SymptomsViewHolder(view, listener);
    }

    /**
     * Pass Symptom's values to TextViews displayed in the RecyclerView rows
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SymptomsViewHolder holder, int position) {
        Symptom symptom = symptomsList.get(position);
        holder.symptomName.setText(symptom.getName());
        holder.symptomRating.setText(Integer.toString(symptom.getRating()));
    }

    /**
     * An symptom has been created. Update the arrayList that is use for search and the GUI
     * @param symptom - New symptom
     */
    public void symptomCreated(Symptom symptom){
        symptomsFullList.add(symptom);
        notifyItemInserted(symptomsList.size()-1);
    }

    /**
     * An symptom has been edited. Update the arrayList that is use for search and the GUI
     * @param position - position in the arrayList
     * @param symptom - New symptom
     */
    public void symptomEdited(int position, Symptom symptom){
        symptomsFullList.set(position, symptom);
        notifyItemChanged(position);
    }

    /**
     * An app has been removed. Update the arrayList that is use for search and the GUI
     * @param position - position in the arrayList
     */
    public void symptomRemoved(int position){
        symptomsFullList.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * The number of items in the RecyclerView
     * @return - ArrayList size
     */
    @Override
    public int getItemCount() {
        return symptomsList.size();
    }

    @Override
    public Filter getFilter(){
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Symptom> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(symptomsFullList);
            }else{
                String pattern = constraint.toString().toLowerCase().trim();

                for(Symptom symptom: symptomsFullList){
                    String name = symptom.getName();
                    String rating = Integer.toString(symptom.getRating());
                    if(name.toLowerCase().contains(pattern) ||
                            rating.toLowerCase().contains(pattern)){
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
            symptomsList.clear();
            symptomsList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
