package com.adityarathi.muo.ui.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adityarathi.muo.R;
import com.adityarathi.muo.ui.adapters.SongsAdapter;
import com.adityarathi.muo.ui.interfaces.RecyclerItemClickListener;
import com.adityarathi.muo.ui.objects.Song;
import com.adityarathi.muo.utils.Common;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TracksListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TracksListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View rootView;

    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    private ArrayList<Song> songList = new ArrayList<>();
    private Common mApp;

    private String mQuerySelection = "";



    public TracksListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TracksListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TracksListFragment newInstance(String param1, String param2) {
        TracksListFragment fragment = new TracksListFragment();
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
        rootView = inflater.inflate(R.layout.fragment_tracks_list, container, false);
        ButterKnife.bind(this,rootView);

        mApp = (Common) getActivity().getApplicationContext();

        //setup Recycler View
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        getSongList();

        return rootView;
    }

    private void getSongList() {

        Cursor musicCursor = mApp.getDBAccessHelper().getAllSongs();

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }


        recyclerView.setAdapter(new SongsAdapter(getActivity(),songList));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mApp.getPlaybackKickstarter()
                        .initPlayback(getActivity(),
                                mQuerySelection,
                                Common.PLAY_ALL_SONGS,
                                position,
                                true,
                                false);
            }
        }));

    }

}
