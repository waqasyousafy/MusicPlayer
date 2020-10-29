package com.example.musicplayer.tracks;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.UpdateAdapterOnTrackChangeListener;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.artists.ArtistAdapter;
import com.example.musicplayer.artists.Artists_Fragment;
import com.example.musicplayer.permissionHandler.PermissionManager;
import com.example.musicplayer.playlistManagment.PlaylistSelectionFragment;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;


public class TracksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EDIT_CALL = 1;
    private static final int PLAYLIST_CALL = 0;
    private static int TRACK_TYPE = 1;
    private ArrayList<TrackInfoModel> list = new ArrayList<>();
    private Context context;
    private int result;

    public TracksAdapter(ArrayList<TrackInfoModel> list) {
        super();
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TRACK_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleitemview, parent, false);
            return new TrackViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleitemview, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
//        ((TrackViewHolder) holder).iconImage.setImageResource(R.drawable.musicicon);
//        Uri uri=Utils.getArtUriFromMusicFile(holder.itemView.getContext(), new File(list.get(position).getPath()));
        context = ((TrackViewHolder) holder).itemView.getContext();
        Bitmap bitmap;
        bitmap = Utils.getArtworkFromFile(holder.itemView.getContext(), (list.get(position).getTrackId()), list.get(position).getAlbumId());
        if (bitmap != null) {
            ((TrackViewHolder) holder).iconImage.setImageBitmap(bitmap);
        } else {
            ((TrackViewHolder) holder).iconImage.setImageResource(R.drawable.musicicon);
        }
        ((TrackViewHolder) holder).trackName.setText(list.get(position).getName());
        ((TrackViewHolder) holder).artistName.setText(list.get(position).getArtist());
        ((TrackViewHolder) holder).optionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context wrapper = new ContextThemeWrapper(((TrackViewHolder) holder).itemView.getContext(), R.style.MyPopupMenu);
                PopupMenu popup = new PopupMenu(wrapper, v);
                popup.inflate(R.menu.trackmenu);
                popup.setGravity(Gravity.END);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                //handle menu1 click
                                Toast.makeText(context, "edit clicked", Toast.LENGTH_LONG).show();
                                final Dialog alertDialog = new Dialog(context, R.style.CustomDialog);
                                alertDialog.setContentView(R.layout.trackinformationdialog);
                                TextView trackPath = alertDialog.findViewById(R.id.trackPath);
                                TextView sizeText = alertDialog.findViewById(R.id.sizeText);
                                final EditText titleEditText = alertDialog.findViewById(R.id.titleEditText);
                                final EditText albumEditText = alertDialog.findViewById(R.id.albumEditText);
                                final EditText artistEditText = alertDialog.findViewById(R.id.artistEditText);
                                artistEditText.setText(list.get(position).getArtist());
                                albumEditText.setText(list.get(position).getAlbum());
                                titleEditText.setText(list.get(position).getName());
                                titleEditText.setSelection(titleEditText.getText().length());
                                if (list.get(position).getSize() / (1024 * 1024 * 1024) > 0)
                                    sizeText.setText(list.get(position).getSize() / (1024 * 1024 * 1024) + "GB");
                                else if (list.get(position).getSize() / (1024 * 1024) > 0)
                                    sizeText.setText(list.get(position).getSize() / (1024 * 1024) + "MB");
                                else
                                    sizeText.setText(list.get(position).getSize() / (1024) + "KB");
                                TextView timeText = alertDialog.findViewById(R.id.timeText);
                                timeText.setText(Utils.getInTimeFormate(list.get(position).getDuration()));
                                trackPath.setText(list.get(position).getPath());
                                TextView okButton = alertDialog.findViewById(R.id.okButton);
                                okButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Utils.editTextValidation(titleEditText) && Utils.editTextValidation(artistEditText) && Utils.editTextValidation(albumEditText)) {
                                            if (!(titleEditText.getText().toString().equals(list.get(position).getName())) || !(artistEditText.getText().toString().equals(list.get(position).getArtist())) || !(albumEditText.getText().toString().equals(list.get(position).getAlbum()))) {
                                                result = Utils.updateAudioSongsInformation(context, list.get(position).getTrackId(), titleEditText.getText().toString(), artistEditText.getText().toString(), albumEditText.getText().toString());
                                                if (result == 1) {
                                                    updateAdapter(titleEditText.getText().toString(), artistEditText.getText().toString(), albumEditText.getText().toString(), position);
                                                    EventBus.getDefault().post(new MessageEvent(list.get(position).getTrackId(), EDIT_CALL));
                                                }
                                            }
                                            alertDialog.dismiss();
                                        }
                                    }
                                });
                                TextView cancelButton = alertDialog.findViewById(R.id.cancelButton);
                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                                alertDialog.show();
                                return true;
                            case R.id.ringingTone:
                                Utils.setRingtone(context, list.get(position).getTrackId());
                                return true;
                            case R.id.addToPlaylist:
                                AppCompatActivity activity = (AppCompatActivity) ((TrackViewHolder) holder).itemView.getContext();
                                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                                // Replace the contents of the container with the new fragment
                                PlaylistSelectionFragment playlistSelectionFragment = new PlaylistSelectionFragment().newInstance(list.get(position).getTrackId());
                                ft.replace(R.id.rootfragment, playlistSelectionFragment, (activity).getSupportFragmentManager().getClass().getName());
                                ft.addToBackStack(null);
                                // or ft.add(R.id.your_placeholder, new FooFragment());
                                // Complete the changes added above
                                ft.commit();
                                EventBus.getDefault().post(new MessageEvent(list.get(position).getTrackId(), PLAYLIST_CALL));
                                return true;
                            case R.id.share:
                                Utils.shareAudio(list.get(position).getPath(), ((TrackViewHolder) holder).itemView.getContext());
                                return true;
                            case R.id.deleteSong:
                                Utils.deleteSong(((TrackViewHolder) holder).itemView.getContext(), list.get(position).getTrackId());
                                list.remove(position);
                                notifyItemRemoved(position);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
        Log.d("songimage", "" + Utils.getArtworkFromFile(holder.itemView.getContext(), (list.get(position).getTrackId()), list.get(position).getAlbumId()));
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) != null)
            return TRACK_TYPE;
        return TRACK_TYPE;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateAdapter(String songTitle, String artistName, String albumTitle, int position) {
        list.get(position).setName(songTitle);
        list.get(position).setArtist(artistName);
        list.get(position).setAlbum(albumTitle);
        notifyItemChanged(position);
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconImage, optionIcon;
        private TextView trackName, artistName;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.trackImage);
            optionIcon = itemView.findViewById(R.id.sideOptionIcon);
            trackName = itemView.findViewById(R.id.trackTitle);
            artistName = itemView.findViewById(R.id.trackArtist);
        }
    }

    public static class MessageEvent {
        public int id, CALL;

        public MessageEvent(int songId, int CALL) {
            this.id = songId;
            this.CALL = CALL;
        }
    }
}
