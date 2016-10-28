package com.punchlag.wigt.maps;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.punchlag.wigt.R;
import com.punchlag.wigt.activity.BaseActivity;

import butterknife.BindView;

public class MapsActivity extends BaseActivity {

    @BindView(R.id.switchRadioGroup)
    RadioGroup switchRadioGroup;

    private boolean isMapModeEnabled = true;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_maps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            parseArguments(getIntent().getExtras());
        } else {
            parseArguments(savedInstanceState);
        }

        switchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isMapModeEnabled = !isMapModeEnabled;
                switchDisplayMode(isMapModeEnabled);
            }
        });

        switchDisplayMode(isMapModeEnabled);
    }

    private void parseArguments(Bundle args) {

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
