package com.hamza.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hamza.newsapp.activities.DetailsActivity;
import com.hamza.newsapp.model.Source;
import com.squareup.picasso.Picasso;
import java.util.List;

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.SourceViewHolder> {
    private final Context context;
    private final LayoutInflater inflater;
    private List<Source> sources;

    public SourceAdapter(Context context, List<Source> sources) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.sources = sources;
    }

    @NonNull
    @Override
    public SourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.news_list_item, parent, false);
        return new SourceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SourceViewHolder holder, int position) {
        final Source source = sources.get(position);
        holder.nameTextView.setText(source.getName());
        holder.descriptionTextView.setText(source.getDescription());

        if (source.getUrl() != null && !source.getUrl().isEmpty()) {
            Picasso.get().load(source.getUrl()).error(R.drawable.not_available).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.not_available);
        }

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (position == 0) {
            layoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, holder.itemView.getResources().getDisplayMetrics());
        } else {
            layoutParams.topMargin = 0;
        }
        holder.itemView.setLayoutParams(layoutParams);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("source", new Gson().toJson(source));
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return sources.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSources(List<Source> newSources) {
        this.sources = newSources;
        notifyDataSetChanged();
    }

    public static class SourceViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView descriptionTextView;
        public ImageView imageView;


        public SourceViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_title);
            descriptionTextView = itemView.findViewById(R.id.text_source);
            imageView = itemView.findViewById(R.id.image_headline);

        }
    }
}
