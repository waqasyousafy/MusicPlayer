package com.example.musicplayer.tracks;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.albums.AlbumModel;
import com.example.musicplayer.playlistManagment.PlaylistAdaper;
import com.example.musicplayer.playlistManagment.PlaylistSelectionFragment;
import com.example.musicplayer.playlistManagment.singlePlaylistManagement.SinglePlaylistFragment;
import com.nabinbhandari.android.permissions.PermissionHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Tracks_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tracks_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<TrackInfoModel> tracksList;
    private TracksAdapter tracksAdapter;
    private RecyclerView trackRecyclerView;

    public Tracks_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static Tracks_Fragment newInstance(String param1, String param2) {
        Tracks_Fragment fragment = new Tracks_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracksList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tracks, container, false);
        // Inflate the layout for this fragment
        com.nabinbhandari.android.permissions.Permissions.check(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE, null, new PermissionHandler() {

            @Override
            public void onGranted() {
                tracksList = Utils.getAllAudioTracks(Objects.requireNonNull(getContext()));
            }
        });
        tracksAdapter = new TracksAdapter(tracksList);
        trackRecyclerView = root.findViewById(R.id.tracksRecyclerView);
        trackRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trackRecyclerView.setItemAnimator(new DefaultItemAnimator());
        trackRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        trackRecyclerView.setAdapter(tracksAdapter);
        return root;
    }
}
