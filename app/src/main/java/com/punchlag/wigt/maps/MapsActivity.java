package com.punchlag.wigt.maps;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.punchlag.wigt.R;
import com.punchlag.wigt.activity.BaseActivity;

public class MapsActivity extends BaseActivity {

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

        MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(MapsFragment.FRAGMENT_TAG);
        if(fragment == null) {
            addActivityContentFragmentWithTag(MapsFragment.newInstance(), MapsFragment.FRAGMENT_TAG);
        }
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
