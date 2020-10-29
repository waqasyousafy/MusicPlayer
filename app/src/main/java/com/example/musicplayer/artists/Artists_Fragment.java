package com.example.musicplayer.artists;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.HomeFragment;
import com.example.musicplayer.R;
import com.example.musicplayer.UpdateAdapterOnTrackChangeListener;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.artists.singleArtist.SingleArtistFragment;
import com.example.musicplayer.playlistManagment.PlaylistSelectionFragment;
import com.example.musicplayer.tracks.TracksAdapter;
import com.nabinbhandari.android.permissions.PermissionHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Artists_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Artists_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<ArtistInfoModel> artistList;
    private ArtistAdapter artistAdapter;
    private RecyclerView artistRecyclerView;

    public Artists_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static Artists_Fragment newInstance(String param1, String param2) {
        Artists_Fragment fragment = new Artists_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_artists, container, false);
        // Inflate the layout for this fragment
        com.nabinbhandari.android.permissions.Permissions.check(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE, null, new PermissionHandler() {

            @Override
            public void onGranted() {
                artistList = Utils.getAllArtistsFromAudio(getContext());
            }
        });
        artistAdapter = new ArtistAdapter(artistList);
        artistRecyclerView = root.findViewById(R.id.artistRecyclerView);
        artistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        artistRecyclerView.setItemAnimator(new DefaultItemAnimator());
        artistRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        artistRecyclerView.setAdapter(artistAdapter);
        return root;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArtistAdapter.MessageEvent event) {
        /* Do something */
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        SingleArtistFragment singleArtistFragment = SingleArtistFragment.newInstance(event.artistId, event.artistName);
        ft.replace(R.id.rootfragment, singleArtistFragment);
        ft.addToBackStack(null);
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TracksAdapter.MessageEvent event) {
        if (event.CALL == 1)
            artistAdapter.updateAdapter(Utils.getAllArtistsFromAudio(getContext()));
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

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
