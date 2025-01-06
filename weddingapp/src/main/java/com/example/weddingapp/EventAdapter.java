package com.example.weddingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> eventList;
    private OnEventDeleteListener deleteListener;

    public interface OnEventDeleteListener {
        void onDeleteEvent(int position);
    }

    public EventAdapter(ArrayList<Event> eventList, OnEventDeleteListener deleteListener) {
        this.eventList = eventList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.tvEventName.setText(event.getName());
        holder.tvEventDate.setText(event.getDate());
        holder.tvEventTime.setText(event.getTime());
        holder.tvEventNotes.setText(event.getNotes());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteEvent(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventDate, tvEventTime, tvEventNotes;
        ImageButton btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
            tvEventNotes = itemView.findViewById(R.id.tvEventNotes);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
