package com.adityarathi.muo.ui.activities;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import com.adityarathi.muo.R;
import com.adityarathi.muo.ui.customViews.SmartViewPager;
import com.adityarathi.muo.ui.fragments.TracksListFragment;
import com.adityarathi.muo.utils.Common;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.viewpager) SmartViewPager viewPager;
    @Bind(R.id.type_tabs) SmartTabLayout tabs;
    @Bind(R.id.collapse_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;

    public Context mContext;
    private Common mApp;
    private Drawer drawer;
    private CustomTabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
                        new PrimaryDrawerItem().withName("Music").withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName("Social Feed").withIcon(FontAwesome.Icon.faw_bell),
                        new PrimaryDrawerItem().withName("Settings").withIcon(FontAwesome.Icon.faw_coffee)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {

                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            String name = ((Nameable) drawerItem).getName().getText(MainActivity.this);

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

        adapter = new CustomTabAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(15);
        viewPager.setAdapter(adapter);
        tabs.setCustomTabView(R.layout.custom_smart_tab_view1,R.id.tab_text);
        tabs.setViewPager(viewPager);
        viewPager.setCurrentItem(0);


    }

    public class CustomTabAdapter extends FragmentStatePagerAdapter {
        private final String[] tab_names = {"RECENT", "SONGS", "ARTISTS", "ALBUMS","PLAYLISTS"};

        private CustomTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab_names[position];
        }

        @Override
        public int getCount() {
            return tab_names.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                    return new TracksListFragment();
            }
        }
    }




}
