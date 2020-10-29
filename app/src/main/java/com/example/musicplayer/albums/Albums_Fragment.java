package com.example.musicplayer.albums;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.Utils;
import com.example.musicplayer.albums.singleAlbum.SingleAlbumFragment;
import com.example.musicplayer.artists.ArtistAdapter;
import com.example.musicplayer.artists.ArtistInfoModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Albums_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Albums_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int ALBUM_FRAGMENT_CALL = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<AlbumModel> albumList;
    private int numberOfSongs;
    private AlbumsAdapter albumsAdapter;
    private RecyclerView albumRecyclerView;

    public Albums_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment3.
     */
    // TODO: Rename and change types and number of parameters
    public static Albums_Fragment newInstance(String param1, String param2) {
        Albums_Fragment fragment = new Albums_Fragment();
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
        albumList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_albums, container, false);
        albumList = Utils.getAllAudioAlbum(getContext());
        albumsAdapter = new AlbumsAdapter(albumList, ALBUM_FRAGMENT_CALL);
        albumRecyclerView = root.findViewById(R.id.albumRcycler);
        albumRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        albumRecyclerView.setItemAnimator(new DefaultItemAnimator());
        albumRecyclerView.setAdapter(albumsAdapter);
        return root;
//        Log.d("albums", "" + albumList.get(1).getNumberOfTracks());

    }

    private void getAudioBuckets(Context mContext) {
        List<String> buckets = new ArrayList<>();
        List<String> uris = new ArrayList<>();
        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Albums._ID
                , MediaStore.Audio.Albums.ALBUM
                , MediaStore.Audio.Albums.ARTIST
                , MediaStore.Audio.Albums.NUMBER_OF_SONGS
        };
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String nameAlbum = cursor.getString(1);
                String artist = cursor.getString(2);
                int numberOfSongs = cursor.getInt(3);

            }
            cursor.close();
        }
    }

    private String getRootDirNameFromUrl(String path) {
        String[] pathArray = path.split("/");
        return pathArray[pathArray.length - (pathArray.length - 2)];
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AlbumsAdapter.MessageEvent event) {
        Log.d("messageeventcall", "albumFragmentCall");
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
