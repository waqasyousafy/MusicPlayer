package com.example.musicplayer.playlistManagment.singlePlaylistManagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.tracks.TrackInfoModel;
import com.example.musicplayer.tracks.TracksAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SinglePlaylistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SinglePlaylistFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<TrackInfoModel> trackInfoModelArrayList;
    private RecyclerView playlistSongsRecyclerView;
    private TracksAdapter tracksAdapter;
    private TextView titleActionBar;

    // TODO: Rename and change types of parameters
    private int playlistId;
    private String playlistName;

    public SinglePlaylistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment singlePlaylistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SinglePlaylistFragment newInstance(int param1, String param2) {
        SinglePlaylistFragment fragment = new SinglePlaylistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistId = getArguments().getInt(ARG_PARAM1);
            playlistName = getArguments().getString(ARG_PARAM2);
        }
        trackInfoModelArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_single_playlist, container, false);
        playlistSongsRecyclerView = root.findViewById(R.id.playlistSongsRecyclerView);
        trackInfoModelArrayList = Utils.getAllSongsFromPlaylist(getContext(), playlistId);
        titleActionBar = root.findViewById(R.id.titleActionBar);
        tracksAdapter = new TracksAdapter(trackInfoModelArrayList);
        titleActionBar.setText(playlistName);
        playlistSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistSongsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        playlistSongsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        playlistSongsRecyclerView.setAdapter(tracksAdapter);
        return root;
    }
}