package com.adityarathi.muo.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.adityarathi.muo.R;
import com.adityarathi.muo.services.BuildMusicLibraryService;
import com.adityarathi.muo.ui.fragments.BuildingLibraryProgressFragment;
import com.adityarathi.muo.utils.Common;

import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {

    public static BuildingLibraryProgressFragment mBuildingLibraryProgressFragment;

    private boolean loaded;
    private Common mApp;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        mContext = this;
        mApp = (Common) mContext.getApplicationContext();

        loaded = mApp.getSharedPreferences().getBoolean("LOADED_DATA",false);

        if(!loaded) {
            mBuildingLibraryProgressFragment = new BuildingLibraryProgressFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mBuildingLibraryProgressFragment).commit();
            Intent intent = new Intent(mContext, BuildMusicLibraryService.class);
            startService(intent);

        }else{
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
