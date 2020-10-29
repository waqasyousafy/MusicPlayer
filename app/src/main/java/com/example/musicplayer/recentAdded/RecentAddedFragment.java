package com.example.musicplayer.recentAdded;

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
 * Use the {@link RecentAddedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentAddedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recentAddedRecycler;
    private TracksAdapter tracksAdapter;
    private ArrayList<TrackInfoModel> trackInfoModelArrayList;
    private TextView actionBarTitle;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecentAddedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentAddedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentAddedFragment newInstance(String param1, String param2) {
        RecentAddedFragment fragment = new RecentAddedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        trackInfoModelArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_recent_added, container, false);
        actionBarTitle = root.findViewById(R.id.titleActionBar);
        actionBarTitle.setText("Recently Added");
        recentAddedRecycler = root.findViewById(R.id.recentAddedRecycler);
        trackInfoModelArrayList = Utils.recentlyAddedTracks(getContext());
        tracksAdapter = new TracksAdapter(trackInfoModelArrayList);
        recentAddedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recentAddedRecycler.setItemAnimator(new DefaultItemAnimator());
        recentAddedRecycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recentAddedRecycler.setAdapter(tracksAdapter);
        return root;
    }
}