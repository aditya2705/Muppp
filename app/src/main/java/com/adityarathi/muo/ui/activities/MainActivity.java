package com.adityarathi.muo.ui.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.adityarathi.muo.R;
import com.adityarathi.muo.asyncTasks.AsyncBuildLibraryTask;
import com.adityarathi.muo.dbHelper.DBAccessHelper;
import com.adityarathi.muo.dbHelper.MediaStoreAccessHelper;
import com.adityarathi.muo.ui.adapters.SongsAdapter;
import com.adityarathi.muo.ui.objects.Song;
import com.adityarathi.muo.utils.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    DBAccessHelper dbAccessHelper;

    public Context mContext;
    private Common mApp;

    private ProgressDialog progressDialog;

    private ArrayList<Song> songList = new ArrayList<>();

    private boolean loaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = this;

        mApp = (Common) mContext.getApplicationContext();
        loaded = mApp.getSharedPreferences().getBoolean("LOADED_DATA",false);


        dbAccessHelper = new DBAccessHelper(this);

        //setup Recycler View
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        if(!loaded) {

            AsyncBuildLibraryTask task = new AsyncBuildLibraryTask(mContext.getApplicationContext());

            AsyncBuildLibraryTask.OnBuildLibraryProgressUpdate buildLibraryProgressUpdate = new AsyncBuildLibraryTask.OnBuildLibraryProgressUpdate() {
                @Override
                public void onStartBuildingLibrary() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Loading...");
                    progressDialog.show();
                }

                @Override
                public void onProgressUpdate(AsyncBuildLibraryTask task, String mCurrentTask, int overallProgress, int maxProgress, boolean mediaStoreTransferDone) {

                    //This fragment only shows the MediaStore transfer progress.
                    if (mediaStoreTransferDone)
                        onFinishBuildingLibrary(task);

                }

                @Override
                public void onFinishBuildingLibrary(AsyncBuildLibraryTask task) {

                    mApp.getSharedPreferences().edit().putBoolean("LOADED_DATA", true).commit();
                    progressDialog.dismiss();
                    getSongList();

                }
            };

            task.setOnBuildLibraryProgressUpdate(buildLibraryProgressUpdate);
            task.execute();

        }else{

            getSongList();

        }


    }

    private void getSongList() {

        Cursor musicCursor = dbAccessHelper.getAllSongs();


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


        recyclerView.setAdapter(new SongsAdapter(this,songList));

        //saveSongsToDB(musicCursor);


    }

    private void saveSongsToDB(Cursor mediaStoreCursor) {

        //Prefetch each column's index.
        final int titleColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        final int artistColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        final int albumColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        final int albumIdColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        final int durationColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        final int trackColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
        final int yearColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media.YEAR);
        final int dateAddedColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
        final int dateModifiedColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);
        final int filePathColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        final int idColIndex = mediaStoreCursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int albumArtistColIndex = mediaStoreCursor.getColumnIndex(MediaStoreAccessHelper.ALBUM_ARTIST);

    		/* The album artist field is hidden by default and we've explictly exposed it.
    		 * The field may cease to exist at any time and if it does, use the artists
    		 * field instead.
    		 */
        if (albumArtistColIndex==-1) {
            albumArtistColIndex = artistColIndex;
        }

        //Iterate through MediaStore's cursor and save the fields to Jams' DB.
        for (int i=0; i < mediaStoreCursor.getCount(); i++) {

            mediaStoreCursor.moveToPosition(i);

            String songTitle = mediaStoreCursor.getString(titleColIndex);
            String songArtist = mediaStoreCursor.getString(artistColIndex);
            String songAlbum = mediaStoreCursor.getString(albumColIndex);
            String songAlbumId = mediaStoreCursor.getString(albumIdColIndex);
            String songAlbumArtist = mediaStoreCursor.getString(albumArtistColIndex);
            String songFilePath = mediaStoreCursor.getString(filePathColIndex);
            String songGenre = "";//
            String songDuration = mediaStoreCursor.getString(durationColIndex);
            String songTrackNumber = mediaStoreCursor.getString(trackColIndex);
            String songYear = mediaStoreCursor.getString(yearColIndex);
            String songDateAdded = mediaStoreCursor.getString(dateAddedColIndex);
            String songDateModified = mediaStoreCursor.getString(dateModifiedColIndex);
            String songId = mediaStoreCursor.getString(idColIndex);
            String numberOfAlbums = "0";
            String numberOfTracks = "0";
            String numberOfSongsInGenre = "General";
            String songSource = DBAccessHelper.LOCAL;
            String songSavedPosition = "-1";

            String songAlbumArtPath = "";

            //Filter out track numbers and remove any bogus values.
            if (songTrackNumber!=null) {
                if (songTrackNumber.contains("/")) {
                    int index = songTrackNumber.lastIndexOf("/");
                    songTrackNumber = songTrackNumber.substring(0, index);
                }

                try {
                    if (Integer.parseInt(songTrackNumber) <= 0) {
                        songTrackNumber = "";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    songTrackNumber = "";
                }

            }

            long durationLong = 0;
            try {
                durationLong = Long.parseLong(songDuration);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ContentValues values = new ContentValues();
            values.put(DBAccessHelper.SONG_TITLE, songTitle);
            values.put(DBAccessHelper.SONG_ARTIST, songArtist);
            values.put(DBAccessHelper.SONG_ALBUM, songAlbum);
            values.put(DBAccessHelper.SONG_ALBUM_ARTIST, songAlbumArtist);
            values.put(DBAccessHelper.SONG_DURATION, convertMillisToMinsSecs(durationLong));
            values.put(DBAccessHelper.SONG_FILE_PATH, songFilePath);
            values.put(DBAccessHelper.SONG_TRACK_NUMBER, songTrackNumber);
            values.put(DBAccessHelper.SONG_GENRE, songGenre);
            values.put(DBAccessHelper.SONG_YEAR, songYear);
            values.put(DBAccessHelper.SONG_ALBUM_ART_PATH, songAlbumArtPath);
            values.put(DBAccessHelper.SONG_LAST_MODIFIED, songDateModified);
            values.put(DBAccessHelper.SONG_ALBUM_ART_PATH, songAlbumArtPath);
            values.put(DBAccessHelper.BLACKLIST_STATUS, false);
            values.put(DBAccessHelper.ADDED_TIMESTAMP, "");
            values.put(DBAccessHelper.RATING, 0);
            values.put(DBAccessHelper.LAST_PLAYED_TIMESTAMP, songDateModified);
            values.put(DBAccessHelper.SONG_SOURCE, songSource);
            values.put(DBAccessHelper.SONG_ID, songId);
            values.put(DBAccessHelper.SAVED_POSITION, songSavedPosition);
            values.put(DBAccessHelper.ALBUMS_COUNT, numberOfAlbums);
            values.put(DBAccessHelper.SONGS_COUNT, numberOfTracks);
            values.put(DBAccessHelper.GENRE_SONG_COUNT, numberOfSongsInGenre);

            //Add all the entries to the database to build the songs library.
            dbAccessHelper.getWritableDatabase().insert(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                    null,
                    values);


        }


    }

    /**
     * Convert millisseconds to hh:mm:ss format.
     *
     * @param milliseconds The input time in milliseconds to format.
     * @return The formatted time string.
     */
    private String convertMillisToMinsSecs(long milliseconds) {

        int secondsValue = (int) (milliseconds / 1000) % 60;
        int minutesValue = (int) ((milliseconds / (1000*60)) % 60);
        int hoursValue  = (int) ((milliseconds / (1000*60*60)) % 24);

        String seconds = "";
        String minutes = "";
        String hours = "";

        if (secondsValue < 10) {
            seconds = "0" + secondsValue;
        } else {
            seconds = "" + secondsValue;
        }

        minutes = "" + minutesValue;
        hours = "" + hoursValue;

        String output = "";
        if (hoursValue!=0) {
            minutes = "0" + minutesValue;
            hours = "" + hoursValue;
            output = hours + ":" + minutes + ":" + seconds;
        } else {
            minutes = "" + minutesValue;
            hours = "" + hoursValue;
            output = minutes + ":" + seconds;
        }

        return output;
    }


}
