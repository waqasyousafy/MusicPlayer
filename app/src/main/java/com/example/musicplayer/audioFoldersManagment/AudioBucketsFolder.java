package com.example.musicplayer.audioFoldersManagment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.audioFoldersManagment.singleBucket.SingleBucketSongsFragment;
import com.example.musicplayer.models.AudioBuckets;
import com.example.musicplayer.playlistManagment.PlaylistAdaper;
import com.example.musicplayer.playlistManagment.singlePlaylistManagement.SinglePlaylistFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudioBucketsFolder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioBucketsFolder extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView bucketsRecyclerView;
    private ArrayList<AudioBuckets> folderList;
    private AudioFolderAdapter audioFolderAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AudioBucketsFolder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AudioBucketsFolder.
     */
    // TODO: Rename and change types and number of parameters
    public static AudioBucketsFolder newInstance(String param1, String param2) {
        AudioBucketsFolder fragment = new AudioBucketsFolder();
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
        folderList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_audio_buckets_folder, container, false);
        bucketsRecyclerView = root.findViewById(R.id.bucketRecyclerView);
//        folderList = Utils.getAllAudioBuckets(getContext());
        audioFolderAdapter = new AudioFolderAdapter(folderList);
        bucketsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bucketsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bucketsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        bucketsRecyclerView.setAdapter(audioFolderAdapter);

        return root;

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AudioFolderAdapter.MessageEvent event) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        SingleBucketSongsFragment singleBucketSongsFragment = new SingleBucketSongsFragment().newInstance(event.bucketId, event.bucketName);
        ft.replace(R.id.rootfragment, singleBucketSongsFragment, (getActivity()).getSupportFragmentManager().getClass().getName());
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