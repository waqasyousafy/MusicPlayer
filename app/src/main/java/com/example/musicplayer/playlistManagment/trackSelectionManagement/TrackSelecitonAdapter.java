package com.example.musicplayer.playlistManagment.trackSelectionManagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.tracks.TrackInfoModel;

import java.util.ArrayList;
import java.util.List;

public class TrackSelecitonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<TrackInfoModel> tracksList;
    private static int ITEM_TYPE = 1;
    private SelectionListener selectionListener;
    private boolean selection;

    public TrackSelecitonAdapter(ArrayList<TrackInfoModel> list) {
        super();
        this.tracksList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trackselectionlayout, parent, false);
            return new TrackSelecitonAdapter.ItemViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trackselectionlayout, parent, false);
        return new TrackSelecitonAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((TrackSelecitonAdapter.ItemViewHolder) holder).trackName.setText(tracksList.get(position).getName());
        ((TrackSelecitonAdapter.ItemViewHolder) holder).artistName.setText(tracksList.get(position).getArtist());
        ((TrackSelecitonAdapter.ItemViewHolder) holder).selectionBox.setChecked(tracksList.get(position).isSelected());
        ((TrackSelecitonAdapter.ItemViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tracksList.get(position).isSelected()) {
                    tracksList.get(position).setSelected(false);
                    ((ItemViewHolder) holder).selectionBox.setChecked(false);
                    selectionListener.onItemClicked(position, false);
//                    itemView.setBackgroundColor(Color.WHITE);
                } else {
                    tracksList.get(position).setSelected(true);
                    ((ItemViewHolder) holder).selectionBox.setChecked(true);
                    selectionListener.onItemClicked(position, true);

//                    itemView.setBackgroundColor(Color.parseColor("#ebebeb"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracksList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (tracksList.get(position) != null)
            return ITEM_TYPE;
        return ITEM_TYPE;
    }

    public void setSelectionInterface(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    public void setCheckedAll(boolean selection) {
        for (TrackInfoModel object : getData())
            object.setSelected(selection);

        setSelection(selection);
    }

    private void setSelection(boolean selection) {
        this.selection = selection;
        if (selectionListener != null)
            selectionListener.onSelectionChange(selection);
        notifyDataSetChanged();
    }

    public List<TrackInfoModel> getData() {
        return tracksList;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconImage;
        private CheckBox selectionBox;
        private TextView trackName, artistName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.trackImage);
            selectionBox = itemView.findViewById(R.id.selectionBox);
            trackName = itemView.findViewById(R.id.trackTitle);
            artistName = itemView.findViewById(R.id.trackArtist);
        }
    }

    public interface SelectionListener {
        void onItemClicked(int position, boolean selection);

        void onSelectionChange(boolean selection);
    }
}
