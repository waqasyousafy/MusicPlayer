package com.example.musicplayer.albums.singleAlbum;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.albums.AlbumsAdapter;
import com.example.musicplayer.artists.ArtistAdapter;
import com.example.musicplayer.artists.singleArtist.SingleArtistFragment;
import com.example.musicplayer.tracks.TrackInfoModel;
import com.example.musicplayer.tracks.TracksAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleAlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleAlbumFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ALBUM_ID = "albumId";
    private static final String ALBUM_NAME = "albumName";

    // TODO: Rename and change types of parameters
    private int albumId;
    private String albumName;
    private RecyclerView trackRecyclerView;
    private TracksAdapter tracksAdapter;
    private ArrayList<TrackInfoModel> trackslist;
    private TextView titleActionBar;

    public SingleAlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleAlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleAlbumFragment newInstance(int param1, String param2) {
        SingleAlbumFragment fragment = new SingleAlbumFragment();
        Bundle args = new Bundle();
        args.putInt(ALBUM_ID, param1);
        args.putString(ALBUM_NAME, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumId = getArguments().getInt(ALBUM_ID);
            albumName = getArguments().getString(ALBUM_NAME);
        }
        trackslist = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_single_album, container, false);
        titleActionBar = root.findViewById(R.id.titleActionBar);
        titleActionBar.setText(albumName);
        trackRecyclerView = root.findViewById(R.id.tracksRecyclerView);
        trackslist = Utils.getAllSongsOfAlbum(getContext(), albumId);
        tracksAdapter = new TracksAdapter(trackslist);
        trackRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trackRecyclerView.setItemAnimator(new DefaultItemAnimator());
        trackRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        trackRecyclerView.setAdapter(tracksAdapter);
        Log.d("albumssongs", "" + Utils.getAllSongsOfAlbum(getContext(), albumId));
        return root;
    }


}