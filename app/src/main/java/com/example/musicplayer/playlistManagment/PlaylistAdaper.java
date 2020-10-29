package com.example.musicplayer.playlistManagment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.albums.AlbumModel;
import com.example.musicplayer.albums.AlbumsAdapter;
import com.example.musicplayer.playlistManagment.singlePlaylistManagement.SinglePlaylistFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PlaylistAdaper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<PlaylistInfoModel> playlistList = new ArrayList<>();
    private static int ItemType = 1, SELECTION_CALL = 1;
    private ArrayList<PlaylistInfoModel> list;

    public PlaylistAdaper(ArrayList<PlaylistInfoModel> playlistList, int SELECTION_CALL) {
        super();
        Collections.reverse(playlistList);
        this.list = playlistList;
        this.SELECTION_CALL = SELECTION_CALL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (SELECTION_CALL == 1) {
            if (viewType == ItemType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleitemview, parent, false);
                return new PlaylistAdaper.ItemViewHolder(view);
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleitemview, parent, false);
            return new PlaylistAdaper.ItemViewHolder(view);
        } else if (SELECTION_CALL == 0) {
            if (viewType == ItemType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleartistalbumlayout, parent, false);
                return new PlaylistAdaper.ItemViewHolder(view);
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleartistalbumlayout, parent, false);
            return new PlaylistAdaper.ItemViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleartistalbumlayout, parent, false);
        return new PlaylistAdaper.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((PlaylistAdaper.ItemViewHolder) holder).iconImage.setImageResource(R.drawable.ic_aa);
        ((PlaylistAdaper.ItemViewHolder) holder).artistName.setText(list.get(position).getPlaylistName());
        ((PlaylistAdaper.ItemViewHolder) holder).artistWork.setText("" + Utils.getSongCountForPlaylist(((ItemViewHolder) holder).itemView.getContext(), list.get(position).getPlaylistId()) + " Songs");
        ((PlaylistAdaper.ItemViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent(list.get(position).getPlaylistId(), list.get(position).getPlaylistName()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) != null)
            return ItemType;
        return ItemType;
    }

    public void updateAdapter(ArrayList<PlaylistInfoModel> allPlaylists) {
        this.list = allPlaylists;
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconImage, optionIcon;
        private TextView artistName, artistWork;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.trackImage);
            optionIcon = itemView.findViewById(R.id.sideOptionIcon);
            artistName = itemView.findViewById(R.id.trackTitle);
            artistWork = itemView.findViewById(R.id.trackArtist);
            optionIcon.setVisibility(View.INVISIBLE);
        }
    }

    public static class MessageEvent {
        public int playlistId;
        public String playlistName;


        public MessageEvent(int id, String name) {
            this.playlistId = id;
            this.playlistName = name;
        }
    }
}
