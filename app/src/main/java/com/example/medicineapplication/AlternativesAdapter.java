package com.example.medicineapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AlternativesAdapter extends RecyclerView.Adapter<AlternativesAdapter.AlternativeViewHolder> {
    private final List<String> alternatives;

    public AlternativesAdapter(List<String> alternatives) {
        this.alternatives = alternatives;
    }

    @NonNull
    @Override
    public AlternativeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alternative, parent, false);
        return new AlternativeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlternativeViewHolder holder, int position) {
        holder.alternativeText.setText(alternatives.get(position));
    }

    @Override
    public int getItemCount() {
        return alternatives.size();
    }

    public static class AlternativeViewHolder extends RecyclerView.ViewHolder {
        public TextView alternativeText;

        public AlternativeViewHolder(View view) {
            super(view);
            alternativeText = view.findViewById(R.id.alternativeText);
        }
    }
}