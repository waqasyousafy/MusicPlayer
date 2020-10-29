package com.example.musicplayer.artists;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicplayer.LiberaryFragment;
import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.playlistManagment.PlaylistSelectionFragment;
import com.example.musicplayer.tracks.TrackInfoModel;
import com.example.musicplayer.tracks.TracksAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

public class ArtistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int ARTIST_TYPE = 1;
    private ArrayList<ArtistInfoModel> list = new ArrayList<>();
    private ArrayList<TrackInfoModel> artistTrackList;

    public ArtistAdapter(ArrayList<ArtistInfoModel> list) {
        this.list = list;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ARTIST_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleitemview, parent, false);
            return new ArtistAdapter.ArtistViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleitemview, parent, false);
        return new ArtistAdapter.ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((ArtistAdapter.ArtistViewHolder) holder).iconImage.setImageResource(R.drawable.ic_person);
        ((ArtistViewHolder) holder).artistWork.setText(list.get(position).getNumberofTracks() + " tracks" + " | " + list.get(position).getNumberOfAlbums() + " albums");
        ((ArtistAdapter.ArtistViewHolder) holder).artistName.setText(list.get(position).getArtistName());
        ((ArtistViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent(list.get(position).getArtistId(), list.get(position).getArtistName(), null));
            }
        });
        ((ArtistViewHolder) holder).optionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context wrapper = new ContextThemeWrapper(((ArtistViewHolder) holder).itemView.getContext(), R.style.MyPopupMenu);
                PopupMenu popup = new PopupMenu(wrapper, v);
                popup.inflate(R.menu.albummenu);
                popup.setGravity(Gravity.END);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addToPlaylist:
                                ArrayList<Integer> songsList = new ArrayList<>();
                                artistTrackList = new ArrayList<>();
                                artistTrackList = Utils.getAllSongsOfArtist(((ArtistViewHolder) holder).itemView.getContext(), list.get(position).getArtistId());
                                for (int i = 0; i < artistTrackList.size(); i++)
                                    songsList.add(artistTrackList.get(i).getTrackId());

                                //////////////////// Fragment loading //////////////////////////////
                                AppCompatActivity activity = (AppCompatActivity) ((ArtistViewHolder) holder).itemView.getContext();
                                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                // Replace the contents of the container with the new fragment
                                PlaylistSelectionFragment playlistSelectionFragment = new PlaylistSelectionFragment().newInstance1(songsList);
                                ft.replace(R.id.rootfragment, playlistSelectionFragment, (activity).getSupportFragmentManager().getClass().getName());
                                ft.addToBackStack(null);
                                // or ft.add(R.id.your_placeholder, new FooFragment());
                                // Complete the changes added above
                                ft.commit();
                                return true;
                            case R.id.share:
                                artistTrackList = new ArrayList<>();
                                artistTrackList = Utils.getAllSongsOfArtist(((ArtistViewHolder) holder).itemView.getContext(), list.get(position).getArtistId());
//                                    Utils.shareAudio(trackInfoModel.getPath(),((ArtistAdapter.ArtistViewHolder) holder).itemView.getContext());
                                    Utils.shareAudios(artistTrackList,((ArtistAdapter.ArtistViewHolder) holder).itemView.getContext());
                                return true;
                            case R.id.delete:
                                Utils.deleteArtist(((ArtistAdapter.ArtistViewHolder) holder).itemView.getContext(), list.get(position).getArtistId());
                                list.remove(position);
                                notifyItemRemoved(position);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }

        });
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) != null)
            return ARTIST_TYPE;
        return ARTIST_TYPE;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconImage, optionIcon;
        private TextView artistName, artistWork;

        public ArtistViewHolder(@NonNull final View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.trackImage);
            optionIcon = itemView.findViewById(R.id.sideOptionIcon);
            artistName = itemView.findViewById(R.id.trackTitle);
            artistWork = itemView.findViewById(R.id.trackArtist);

        }

    }

    public void updateAdapter(ArrayList<ArtistInfoModel> artistInfoModelArraylist) {
        this.list = artistInfoModelArraylist;
        notifyDataSetChanged();
    }

    public static class MessageEvent {
        public int artistId;
        public String artistName;
        public ArrayList<Integer> songlist;

        public MessageEvent(int artistId, String artistName, ArrayList<Integer> songlist) {
            this.artistId = artistId;
            this.songlist = songlist;
            this.artistName = artistName;
        }
    }
}

