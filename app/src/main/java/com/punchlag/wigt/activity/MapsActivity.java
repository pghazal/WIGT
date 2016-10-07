package com.punchlag.wigt.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.punchlag.wigt.R;
import com.punchlag.wigt.fragment.MapsFragment;
import com.punchlag.wigt.utils.Arguments;

public class MapsActivity extends BaseActivity {

    private boolean mAlarmActivated;

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
        addActivityContentFragmentWithTag(MapsFragment.newInstance(), MapsFragment.FRAGMENT_TAG);
    }

    private void parseArguments(Bundle args) {
        if (args != null) {
            mAlarmActivated = args.getBoolean(Arguments.ARG_ALARM_ACTIVATED);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
