package com.example.musicplayer.playlistManagment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.artists.ArtistAdapter;
import com.example.musicplayer.playlistManagment.singlePlaylistManagement.SinglePlaylistFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistSelectionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SELECTION_CALL = 1;
    private static final String ARRAY_PARAM = "Integer Arraylist";
    private RecyclerView playlistRecyclerView;
    private PlaylistAdaper playlistAdaper;
    private ArrayList<PlaylistInfoModel> playlistInfoModelArrayList;

    // TODO: Rename and change types of parameters
    private int mParam1;
    private ArrayList<Integer> songIdsList=new ArrayList<>();

    public PlaylistSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PlaylistSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistSelectionFragment newInstance(int param1) {
        PlaylistSelectionFragment fragment = new PlaylistSelectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaylistSelectionFragment newInstance1(ArrayList<Integer> songsIdsList) {
        PlaylistSelectionFragment fragment = new PlaylistSelectionFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList(ARRAY_PARAM, songsIdsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            songIdsList = getArguments().getIntegerArrayList(ARRAY_PARAM);
        }

        playlistInfoModelArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_playlist_selection, container, false);
        playlistInfoModelArrayList = Utils.getAllPlaylists(getContext());
        playlistRecyclerView = root.findViewById(R.id.playlistRecyclerView);
        playlistAdaper = new PlaylistAdaper(playlistInfoModelArrayList, SELECTION_CALL);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistRecyclerView.setItemAnimator(new DefaultItemAnimator());
        playlistRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        playlistRecyclerView.setAdapter(playlistAdaper);

        return root;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PlaylistAdaper.MessageEvent event) {
        if (mParam1 > 0)
            Utils.addTOPlaylistByID(getContext(), event.playlistId, mParam1);
        else
            Utils.addTOPlaylistByID(getContext(), event.playlistId, songIdsList);
        getActivity().onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


}