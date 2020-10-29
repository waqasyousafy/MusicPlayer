package com.example.musicplayer.geners.singleGenres;

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
import com.example.musicplayer.albums.singleAlbum.SingleAlbumFragment;
import com.example.musicplayer.geners.GenresAdapter;
import com.example.musicplayer.tracks.TrackInfoModel;
import com.example.musicplayer.tracks.TracksAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SingleGenresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleGenresFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GENRES_ID = "genresId";
    private static final String GENRES_NAME = "genresName";

    // TODO: Rename and change types of parameters
    private int genresId;
    private String genreName;
    private TracksAdapter tracksAdapter;
    private RecyclerView tracksRecyclerView;
    private ArrayList<TrackInfoModel> trackInfoModelArrayList;
    private TextView titleActionBar;

    public SingleGenresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param genresId  Parameter 1.
     * @param genreName Parameter 2.
     * @return A new instance of fragment SingleGenresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleGenresFragment newInstance(int genresId, String genreName) {
        SingleGenresFragment fragment = new SingleGenresFragment();
        Bundle args = new Bundle();
        args.putInt(GENRES_ID, genresId);
        args.putString(GENRES_NAME, genreName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genresId = getArguments().getInt(GENRES_ID);
            genreName = getArguments().getString(GENRES_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_single_genres, container, false);
        Log.d("genressongs", "" + Utils.getAllSongsOfGenres(getContext(), genresId));
        trackInfoModelArrayList = Utils.getAllSongsOfGenres(getContext(), genresId);
        titleActionBar = root.findViewById(R.id.titleActionBar);

        titleActionBar.setText(genreName);

        tracksAdapter = new TracksAdapter(trackInfoModelArrayList);
        tracksRecyclerView = root.findViewById(R.id.tracksRecyclerView);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tracksRecyclerView.setItemAnimator(new DefaultItemAnimator());
        tracksRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        tracksRecyclerView.setAdapter(tracksAdapter);
        return root;
    }

}