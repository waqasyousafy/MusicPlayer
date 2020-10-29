package com.example.musicplayer.artists.singleArtist;

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
import com.example.musicplayer.albums.AlbumModel;
import com.example.musicplayer.albums.AlbumsAdapter;
import com.example.musicplayer.albums.singleAlbum.SingleAlbumFragment;
import com.example.musicplayer.tracks.TrackInfoModel;
import com.example.musicplayer.tracks.TracksAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleArtistFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARTIST_ID = "artistId";
    private static final String ARTIST_NAME = "artistName";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;
    private RecyclerView songsRecyclerView, albumRecyclerview;
    private TracksAdapter tracksAdapter;
    private ArrayList<TrackInfoModel> tracksList;
    private ArrayList<AlbumModel> albumsList;
    private AlbumsAdapter albumsAdapter;
    private TextView titleActionBar;
    private int SINGLE_ARTIST_FRAGMENT_CALL = 1;

    public SingleArtistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SingleArtistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleArtistFragment newInstance(int param1, String artistName) {
        SingleArtistFragment fragment = new SingleArtistFragment();
        Bundle args = new Bundle();
        args.putInt(ARTIST_ID, param1);
        args.putString(ARTIST_NAME, artistName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARTIST_ID, 0);
            mParam2 = getArguments().getString(ARTIST_NAME);
        }
        tracksList = new ArrayList<>();
        albumsList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_single_artist, container, false);
        titleActionBar = root.findViewById(R.id.titleActionBar);
        songsRecyclerView = root.findViewById(R.id.songsRecyclerView);
        tracksList = Utils.getAllSongsOfArtist(getContext(), mParam1);
        tracksAdapter = new TracksAdapter(tracksList);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        songsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        songsRecyclerView.setAdapter(tracksAdapter);


        albumsList = Utils.getallAlbumsOfArtist(getContext(), mParam1);
        albumsAdapter = new AlbumsAdapter(albumsList, SINGLE_ARTIST_FRAGMENT_CALL);
        albumRecyclerview = root.findViewById(R.id.albumRecyclerview);
        titleActionBar.setText(mParam2);
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        albumRecyclerview.setLayoutManager(linearLayoutManager);
        albumRecyclerview.setItemAnimator(new DefaultItemAnimator());
        albumRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
        albumRecyclerview.setAdapter(albumsAdapter);


//        Log.d("artistsongs", "" + Utils.getAllSongsOfArtist(getContext(), mParam1).size());
//        Log.d("artistsongs", "" + Utils.getallAlbumsOfArtist(getContext(), mParam1).get(2).getNumberOfTracks());
        return root;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AlbumsAdapter.MessageEvent event) {
        Log.d("messageeventcall", "artistCall");
        /* Do something */
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        SingleAlbumFragment singleAlbumFragment = SingleAlbumFragment.newInstance(event.albumId, event.albumName);
        ft.replace(R.id.rootfragment, singleAlbumFragment);
        ft.addToBackStack(null);
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();
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