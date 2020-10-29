package com.example.musicplayer.audioFoldersManagment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.models.AudioBuckets;
import com.example.musicplayer.tracks.TracksAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class AudioFolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AudioBuckets> audioBucketsList;
    private int ITEM_TYPE = 1;

    public AudioFolderAdapter(ArrayList<AudioBuckets> list) {
        super();
        this.audioBucketsList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlefolderlayout, parent, false);
            return new AudioFolderAdapter.ItemViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlefolderlayout, parent, false);
        return new AudioFolderAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((ItemViewHolder) holder).folderTitle.setText(audioBucketsList.get(position).getBucketName());
        ((ItemViewHolder) holder).folderPath.setText(audioBucketsList.get(position).getPath());
        ((ItemViewHolder) holder).numberofSongsText.setText("" + Utils.getAllSongsFromBucket(((ItemViewHolder) holder).itemView.getContext(), audioBucketsList.get(position).getBucketId()).size());
        ((ItemViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent(audioBucketsList.get(position).getBucketId(), audioBucketsList.get(position).getBucketName()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return audioBucketsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (audioBucketsList.get(position) != null)
            return ITEM_TYPE;
        return ITEM_TYPE;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView folderTitle, folderPath, numberofSongsText;

        public ItemViewHolder(View view) {
            super(view);
            folderTitle = itemView.findViewById(R.id.folderTitle);
            folderPath = itemView.findViewById(R.id.folderPath);
            numberofSongsText = itemView.findViewById(R.id.numberofSongsText);

        }
    }

    public static class MessageEvent {
        public int bucketId;
        public String bucketName;

        public MessageEvent(int folderId, String folderName) {
            this.bucketId = folderId;
            this.bucketName = folderName;
        }
    }
}
