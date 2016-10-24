package com.punchlag.wigt.maps;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.punchlag.wigt.R;
import com.punchlag.wigt.activity.BaseActivity;

public class MapsActivity extends BaseActivity {

    private boolean isMapModeEnabled = true;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_maps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableNavigationUp();
        if (savedInstanceState == null) {
            parseArguments(getIntent().getExtras());
        } else {
            parseArguments(savedInstanceState);
        }

        switchDisplayMode(isMapModeEnabled);
    }

    private void parseArguments(Bundle args) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_switch_view:
                isMapModeEnabled = !isMapModeEnabled;
                switchDisplayMode(isMapModeEnabled);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchDisplayMode(boolean isMapModeEnabled) {
        if (isMapModeEnabled) {
            MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(MapsFragment.FRAGMENT_TAG);
            if (fragment == null) {
                replaceActivityContentFragmentWithTag(MapsFragment.newInstance(), MapsFragment.FRAGMENT_TAG);
            }
        } else {
            GeofencesListFragment fragment = (GeofencesListFragment) getSupportFragmentManager().findFragmentByTag(GeofencesListFragment.FRAGMENT_TAG);
            if (fragment == null) {
                replaceActivityContentFragmentWithTag(GeofencesListFragment.newInstance(), GeofencesListFragment.FRAGMENT_TAG);
            }
        }
    }
}
