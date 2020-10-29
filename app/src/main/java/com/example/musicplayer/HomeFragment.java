package com.example.musicplayer;

import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.audioFoldersManagment.AudioBucketsFolder;
import com.example.musicplayer.models.AudioBuckets;
import com.example.musicplayer.playlistManagment.PlaylistAdaper;
import com.example.musicplayer.playlistManagment.PlaylistInfoModel;
import com.example.musicplayer.playlistManagment.singlePlaylistManagement.SinglePlaylistFragment;
import com.example.musicplayer.playlistManagment.trackSelectionManagement.SongSelectorForPlaylistFragment;
import com.example.musicplayer.recentAdded.RecentAddedFragment;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int HOME_FRAGMENT_CALL = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ConstraintLayout liberaryButton, foldersButton, recentlyAddedButton;
    private TextView numberOfSongs;
    private ImageView createPlaylistButton;
    private String playlistname;
    private RecyclerView playlistRecyclerView;
    private PlaylistAdaper playlistAdaper;
    private ArrayList<PlaylistInfoModel> playlistList;
    private ImageView menu_drawer;
    private TextView playlistsText, numberOfFolders, recentlyAddedTracksCount;
    private int UNLOCK_DRAWER = 1;
    private int LOCK_DRAWER = 0;
    private int BUTTON_CALL = 2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        numberOfSongs = root.findViewById(R.id.numberOfSongs);
        Permissions.check(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE, null, new PermissionHandler() {
            @Override
            public void onGranted() {

            }
        });

        numberOfSongs.setText("" + Utils.getAllAudioTracks(getContext()).size());
        playlistRecyclerView = root.findViewById(R.id.playlistRecyclerView);
        playlistList = Utils.getAllPlaylists(getContext());
        playlistAdaper = new PlaylistAdaper(playlistList, HOME_FRAGMENT_CALL);
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        playlistRecyclerView.setLayoutManager(linearLayoutManager);
        playlistRecyclerView.setItemAnimator(new DefaultItemAnimator());
        playlistRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
        playlistRecyclerView.setAdapter(playlistAdaper);
        playlistsText = root.findViewById(R.id.playlistsText);
        playlistsText.setText("PLAYLISTS (" + Utils.getAllPlaylists(getContext()).size() + ")");
        menu_drawer = root.findViewById(R.id.menu_drawer);
        liberaryButton = root.findViewById(R.id.liberaryButton);
        foldersButton = root.findViewById(R.id.folderButton);
        numberOfFolders = root.findViewById(R.id.numberOfFolders);
        recentlyAddedTracksCount = root.findViewById(R.id.recentlyAddedTracksCountText);
        recentlyAddedButton = root.findViewById(R.id.recentlyAddedButton);
        recentlyAddedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.rootfragment, new RecentAddedFragment(), (getActivity()).getSupportFragmentManager().getClass().getName());
                ft.addToBackStack(null);
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
            }
        });
//        numberOfFolders.setText("" + Utils.getAllAudioBuckets(getContext()).size());
        recentlyAddedTracksCount.setText("" + Utils.recentlyAddedTracks(getContext()).size());
        ArrayList<AudioBuckets> bucketlist = new ArrayList<>();
//        bucketlist = Utils.getAllAudioBuckets(getContext());
        for (int i = 0; i < bucketlist.size(); i++)
            Utils.getAllSongsFromBucket(getContext(), bucketlist.get(i).getBucketId());
        foldersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.rootfragment, new AudioBucketsFolder(), (getActivity()).getSupportFragmentManager().getClass().getName());
                ft.addToBackStack(null);
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
            }
        });
        createPlaylistButton = root.findViewById(R.id.createPlaylistButton);
        createPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog alertDialog = new Dialog(getContext(), R.style.CustomDialog);
                alertDialog.setContentView(R.layout.creatplaylistdialog);
                final EditText playlistText = alertDialog.findViewById(R.id.playlistText);
                final TextView okButton = alertDialog.findViewById(R.id.okButton);
                TextView cancelButton = alertDialog.findViewById(R.id.cancelButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.editTextValidation(playlistText)) {
                            if (Utils.createPlaylist(getContext(), playlistText.getText().toString())) {
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                // Replace the contents of the container with the new fragment
                                SongSelectorForPlaylistFragment songSelectorForPlaylistFragment1 = new SongSelectorForPlaylistFragment().newInstance(Utils.getLastPlaylistid(getContext()));
                                ft.replace(R.id.rootfragment, songSelectorForPlaylistFragment1, (getActivity()).getSupportFragmentManager().getClass().getName());
                                ft.addToBackStack(null);
                                // or ft.add(R.id.your_placeholder, new FooFragment());
                                // Complete the changes added above
                                ft.commit();
                                Toast.makeText(getContext(), "playlist created", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "playlist already created", Toast.LENGTH_SHORT).show();
                            }

                        }
                        Utils.getAllPlaylists(getContext());
                        playlistAdaper.updateAdapter(Utils.getAllPlaylists(getContext()));
                        alertDialog.dismiss();

                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }

        });
        liberaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.rootfragment, new LiberaryFragment(), (getActivity()).getSupportFragmentManager().getClass().getName());
                ft.addToBackStack(null);

                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
            }
        });
        menu_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent(BUTTON_CALL));

//
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new MessageEvent(UNLOCK_DRAWER));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SongSelectorForPlaylistFragment.MessageEvent event) {
        playlistAdaper.updateAdapter(Utils.getAllPlaylists(getContext()));
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().post(new MessageEvent(LOCK_DRAWER));
    }

    public static class MessageEvent {
        int MESSAGE_FLAG;

        public MessageEvent(int message) {
            this.MESSAGE_FLAG = message;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PlaylistAdaper.MessageEvent event) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        SinglePlaylistFragment singlePlaylistFragment = new SinglePlaylistFragment().newInstance(event.playlistId, event.playlistName);
        ft.replace(R.id.rootfragment, singlePlaylistFragment, (getActivity()).getSupportFragmentManager().getClass().getName());
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
