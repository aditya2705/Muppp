package com.adityarathi.muo.ui.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adityarathi.muo.R;
import com.adityarathi.muo.asyncTasks.AsyncBuildLibraryTask;
import com.adityarathi.muo.dbHelper.DBAccessHelper;
import com.adityarathi.muo.dbHelper.MediaStoreAccessHelper;
import com.adityarathi.muo.ui.adapters.SongsAdapter;
import com.adityarathi.muo.ui.fragments.BlankFragment;
import com.adityarathi.muo.ui.fragments.TracksListFragment;
import com.adityarathi.muo.ui.objects.Song;
import com.adityarathi.muo.utils.Common;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    public Context mContext;
    private Common mApp;
    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mContext = this;
        mApp = (Common) mContext.getApplicationContext();

        //Create the drawer
        drawer = new DrawerBuilder(this)
                .withFullscreen(true)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerWidthRes(R.dimen._240sdp)
                .withActionBarDrawerToggleAnimated(true)
                .withTranslucentStatusBar(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Tracks").withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName("Playlists").withIcon(FontAwesome.Icon.faw_bell),
                        new PrimaryDrawerItem().withName("Artists").withIcon(FontAwesome.Icon.faw_coffee),
                        new PrimaryDrawerItem().withName("Albums").withIcon(FontAwesome.Icon.faw_group)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {

                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            String name = ((Nameable) drawerItem).getName().getText(MainActivity.this);
                            getSupportActionBar().setTitle(name);
                            Fragment fragment = null;
                            switch (i) {
                                case 0:
                                    fragment = new TracksListFragment();
                                    break;
                                default:
                                    fragment = new BlankFragment();
                                    break;

                            }
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        }

                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }
                })
                .withFireOnInitialOnClick(false)
                .withSavedInstance(savedInstanceState)
                .build();

        drawer.setSelectionAtPosition(0, true);


    }




}
