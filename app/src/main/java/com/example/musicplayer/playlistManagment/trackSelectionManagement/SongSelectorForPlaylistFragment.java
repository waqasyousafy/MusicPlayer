package com.example.musicplayer.playlistManagment.trackSelectionManagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.tracks.TrackInfoModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongSelectorForPlaylistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongSelectorForPlaylistFragment extends Fragment implements TrackSelecitonAdapter.SelectionListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "playlistId";

    // TODO: Rename and change types of parameters
    private int playlistId;
    private ArrayList<TrackInfoModel> trackInfoModelArrayList;
    private RecyclerView trackRecyclerView;
    private TrackSelecitonAdapter trackSelecitonAdapter;
    ArrayList<Integer> selectionList = new ArrayList<>();
    private ImageView donebutton;
    private TextView titleactionbar;
    private CheckBox allSelectionBox;

    public SongSelectorForPlaylistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SongSelectorForPlaylistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SongSelectorForPlaylistFragment newInstance(int param1) {
        SongSelectorForPlaylistFragment fragment = new SongSelectorForPlaylistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("argss", "" + getArguments());
        if (getArguments() != null) {
            playlistId = getArguments().getInt(ARG_PARAM1);
        }
        trackInfoModelArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_song_selector_for_playlist, container, false);
        trackRecyclerView = rootView.findViewById(R.id.selectionTracksRecyclerview);
        trackInfoModelArrayList = Utils.getAllAudioTracks(getContext());
        trackSelecitonAdapter = new TrackSelecitonAdapter(trackInfoModelArrayList);
        trackSelecitonAdapter.setSelectionInterface(this);
        trackRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trackRecyclerView.setItemAnimator(new DefaultItemAnimator());
        trackRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        trackRecyclerView.setAdapter(trackSelecitonAdapter);


        ///actionbar
        donebutton = rootView.findViewById(R.id.doneSelection);
        titleactionbar = rootView.findViewById(R.id.titleActionBar);
        allSelectionBox = rootView.findViewById(R.id.allSelectionBox);
        allSelectionBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add the action of your checkbox
                if (allSelectionBox.isChecked()) {
                    allSelectionBox.setChecked(true);
                    trackSelecitonAdapter.setCheckedAll(true);
                    donebutton.setVisibility(View.VISIBLE);
                    for (TrackInfoModel trackObj : trackInfoModelArrayList) {
                        selectionList.add(trackObj.getTrackId());
                    }
                    titleactionbar.setText(selectionList.size() + " selected");
                } else {
                    allSelectionBox.setChecked(false);
                    trackSelecitonAdapter.setCheckedAll(false);
                    donebutton.setVisibility(View.INVISIBLE);
                    for (TrackInfoModel trackObj : trackInfoModelArrayList) {
                        selectionList.remove(selectionList.indexOf(trackObj.getTrackId()));
                    }
                    selectionList.clear();
                    titleactionbar.setText(selectionList.size() + " selected");
                }
            }
        });
        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("selectionlist", "" + selectionList);
                Utils.addTOPlaylistByID(getContext(), playlistId, selectionList);
                EventBus.getDefault().post(new MessageEvent());
                getActivity().onBackPressed();
            }
        });
        return rootView;
    }

    @Override
    public void onItemClicked(int position, boolean selection) {
        if (selection) {
            selectionList.add(trackInfoModelArrayList.get(position).getTrackId());
        } else {
            selectionList.remove(selectionList.indexOf(trackInfoModelArrayList.get(position).getTrackId()));
        }

        if (selectionList.size() == 0) {
            donebutton.setVisibility(View.INVISIBLE);
            titleactionbar.setText("0" + " selected");
        } else {
            donebutton.setVisibility(View.VISIBLE);
        }

        if (trackSelecitonAdapter.getItemCount() != selectionList.size()) {
            allSelectionBox.setChecked(false);
            allSelectionBox.setBackgroundColor(getResources().getColor(R.color.transparaent));
            titleactionbar.setText("" + selectionList.size() + " selected");
        } else {
            allSelectionBox.setChecked(true);
            allSelectionBox.setBackgroundColor(getResources().getColor(R.color.transparaent));
            titleactionbar.setText("" + selectionList.size() + " selected");
        }
    }

    @Override
    public void onSelectionChange(boolean selection) {

    }

    public static class MessageEvent {


        public MessageEvent() {

        }
    }
}