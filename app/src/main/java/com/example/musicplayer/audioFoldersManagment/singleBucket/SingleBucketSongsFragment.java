package com.example.musicplayer.audioFoldersManagment.singleBucket;

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
 * Use the {@link SingleBucketSongsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleBucketSongsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView songsRecyclerView;
    private TracksAdapter tracksAdapter;
    private ArrayList<TrackInfoModel> trackInfoModelArrayList;
    private TextView actionBarTitle;

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;

    public SingleBucketSongsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleBucketSongsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleBucketSongsFragment newInstance(int param1, String param2) {
        SingleBucketSongsFragment fragment = new SingleBucketSongsFragment();
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
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        trackInfoModelArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_single_bucket_songs, container, false);
        actionBarTitle = root.findViewById(R.id.titleActionBar);
        actionBarTitle.setText(mParam2);
        songsRecyclerView = root.findViewById(R.id.songsRecyclerView);
        trackInfoModelArrayList = Utils.getAllSongsFromBucket(getContext(), mParam1);
        tracksAdapter = new TracksAdapter(trackInfoModelArrayList);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        songsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        songsRecyclerView.setAdapter(tracksAdapter);
        return root;
    }
}