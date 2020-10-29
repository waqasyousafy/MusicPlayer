package com.example.musicplayer.geners;

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

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.geners.singleGenres.SingleGenresFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Geners_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Geners_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<GenresModel> genresList;
    private RecyclerView genresRecyclerView;
    private GenresAdapter genresAdapter;

    public Geners_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment geners_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Geners_Fragment newInstance(String param1, String param2) {
        Geners_Fragment fragment = new Geners_Fragment();
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
        genresList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_geners, container, false);
        genresRecyclerView = root.findViewById(R.id.generRecycler);
        genresList = Utils.getAllAudioGenres(getContext());
        genresAdapter = new GenresAdapter(genresList);
        genresRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        genresRecyclerView.setItemAnimator(new DefaultItemAnimator());
        genresRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        genresRecyclerView.setAdapter(genresAdapter);


        return root;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GenresAdapter.MessageEvent event) {
        Log.d("messageeventcall", "albumFragmentCall");
        /* Do something */
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        SingleGenresFragment singleGenresFragment = SingleGenresFragment.newInstance(event.genresId, event.genresName);
        ft.replace(R.id.rootfragment, singleGenresFragment);
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
