package com.example.musicplayer.albums;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.artists.ArtistAdapter;
import com.example.musicplayer.playlistManagment.PlaylistSelectionFragment;
import com.example.musicplayer.tracks.TrackInfoModel;
import com.example.musicplayer.tracks.TracksAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class AlbumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<AlbumModel> albumlist = new ArrayList<>();
    private static int ItemType = 1;
    private int callFromFragment = 0;
    private int ALBUM_FRAGMENT_CALL = 0;
    private int SINGLE_ARTIST_FRAGMENT_CALL = 1;
    private ArrayList<TrackInfoModel> albumTrackList;

    public AlbumsAdapter(ArrayList<AlbumModel> list, int callFrom) {
        this.albumlist = list;
        this.callFromFragment = callFrom;
    }

    @NonNull

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ItemType) {
            if (callFromFragment == ALBUM_FRAGMENT_CALL) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlealbumlayout, parent, false);
                return new AlbumsAdapter.ItemViewHolder(view);
            } else if (callFromFragment == SINGLE_ARTIST_FRAGMENT_CALL) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleartistalbumlayout, parent, false);
                return new AlbumsAdapter.ItemViewHolder(view);
            }
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleartistalbumlayout, parent, false);
        return new AlbumsAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((AlbumsAdapter.ItemViewHolder) holder).iconImage.setImageResource(R.drawable.ic_aa);
        if (albumlist.get(position).getNumberOfTracks() > 1) {
            ((AlbumsAdapter.ItemViewHolder) holder).artistWork.setText(albumlist.get(position).getNumberOfTracks() + " songs");
        } else if (albumlist.get(position).getNumberOfTracks() == 1) {
            ((AlbumsAdapter.ItemViewHolder) holder).artistWork.setText(albumlist.get(position).getNumberOfTracks() + " song");
        }
        ((AlbumsAdapter.ItemViewHolder) holder).artistName.setText(albumlist.get(position).getAlbumName());
        ((ItemViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent(albumlist.get(position).getAlbumId(), albumlist.get(position).getAlbumName()));
            }
        });
        ((AlbumsAdapter.ItemViewHolder) holder).optionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context wrapper = new ContextThemeWrapper(((AlbumsAdapter.ItemViewHolder) holder).itemView.getContext(), R.style.MyPopupMenu);
                PopupMenu popup = new PopupMenu(wrapper, v);
                popup.inflate(R.menu.albummenu);
                popup.setGravity(Gravity.END);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.play:
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                ((ItemViewHolder) holder).itemView.getContext().startActivity(cameraIntent);
                                return true;
                            case R.id.addToPlaylist:
                                ArrayList<Integer> songsList = new ArrayList<>();
                                albumTrackList = new ArrayList<>();
                                albumTrackList = Utils.getAllSongsOfAlbum(((AlbumsAdapter.ItemViewHolder) holder).itemView.getContext(), albumlist.get(position).getAlbumId());
                                for (int i = 0; i < albumTrackList.size(); i++)
                                    songsList.add(albumTrackList.get(i).getTrackId());

                                //////////////////// Fragment loading //////////////////////////////
                                AppCompatActivity activity = (AppCompatActivity) (((AlbumsAdapter.ItemViewHolder) holder).itemView.getContext());
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
                                albumTrackList = new ArrayList<>();
                                albumTrackList = Utils.getAllSongsOfAlbum(((AlbumsAdapter.ItemViewHolder) holder).itemView.getContext(), albumlist.get(position).getAlbumId());
                                Utils.shareAudios(albumTrackList, ((AlbumsAdapter.ItemViewHolder) holder).itemView.getContext());
                                return true;
                            case R.id.delete:
                                Utils.deleteAlbum(((AlbumsAdapter.ItemViewHolder) holder).itemView.getContext(), albumlist.get(position).getAlbumId());
                                albumlist.remove(position);
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
    public int getItemCount() {
        return albumlist.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (albumlist.get(position) != null)
            return ItemType;
        return ItemType;
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
        }
    }

    public static class MessageEvent {
        public int albumId;
        public String albumName;

        public MessageEvent(int albumId, String albumName) {
            this.albumId = albumId;
            this.albumName = albumName;
        }
    }
}
