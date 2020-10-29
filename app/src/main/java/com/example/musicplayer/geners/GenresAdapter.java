package com.example.musicplayer.geners;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
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
import com.example.musicplayer.albums.AlbumsAdapter;
import com.example.musicplayer.playlistManagment.PlaylistSelectionFragment;
import com.example.musicplayer.tracks.TrackInfoModel;
import com.example.musicplayer.tracks.TracksAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class GenresAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int ItemType = 1;
    private ArrayList<GenresModel> genresList = new ArrayList<>();
    private Context context;
    private ArrayList<TrackInfoModel> genresTrackList;

    public GenresAdapter(ArrayList<GenresModel> list) {
        super();
        this.genresList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ItemType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleitemview, parent, false);
            return new GenresAdapter.ItemViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleitemview, parent, false);
        return new GenresAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        context = ((ItemViewHolder) holder).itemView.getContext();
        ((GenresAdapter.ItemViewHolder) holder).iconImage.setImageResource(R.drawable.ic_guitar);
        ((GenresAdapter.ItemViewHolder) holder).trackName.setText(genresList.get(position).getGenresName());
        ((GenresAdapter.ItemViewHolder) holder).artistName.setText(genresList.get(position).getNumberOfSongs() + " songs");
        ((ItemViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent(genresList.get(position).getGenresId(), genresList.get(position).getGenresName()));
            }
        });
        ((ItemViewHolder) holder).optionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);
                PopupMenu popup = new PopupMenu(wrapper, v);
                popup.inflate(R.menu.albummenu);
                popup.setGravity(Gravity.END);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.play:
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                context.startActivity(cameraIntent);
                                return true;
                            case R.id.addToPlaylist:
                                ArrayList<Integer> songsList = new ArrayList<>();
                                genresTrackList = new ArrayList<>();
                                genresTrackList = Utils.getAllSongsOfGenres(context, genresList.get(position).getGenresId());
                                for (int i = 0; i < genresTrackList.size(); i++)
                                    songsList.add(genresTrackList.get(i).getTrackId());

                                //////////////////// Fragment loading //////////////////////////////
                                AppCompatActivity activity = (AppCompatActivity) (context);
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
                                genresTrackList = new ArrayList<>();
                                genresTrackList = Utils.getAllSongsOfGenres(context, genresList.get(position).getGenresId());
                                Utils.shareAudios(genresTrackList, context);
                                return true;
                            case R.id.delete:
                                Utils.deleteGenres(context, genresList.get(position).getGenresId());
                                genresList.remove(position);
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
        return genresList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (genresList.get(position) != null)
            return ItemType;
        return ItemType;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconImage, optionIcon;
        private TextView trackName, artistName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.trackImage);
            optionIcon = itemView.findViewById(R.id.sideOptionIcon);
            trackName = itemView.findViewById(R.id.trackTitle);
            artistName = itemView.findViewById(R.id.trackArtist);
        }
    }

    public static class MessageEvent {
        public int genresId;
        public String genresName;

        public MessageEvent(int genresId, String genresName) {
            this.genresId = genresId;
            this.genresName = genresName;
        }
    }
}
