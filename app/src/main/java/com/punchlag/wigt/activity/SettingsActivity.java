package com.punchlag.wigt.activity;

import android.os.Bundle;

import com.punchlag.wigt.R;
import com.punchlag.wigt.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.FRAGMENT_TAG);
        if (fragment == null) {
            replaceActivityContentFragmentWithTag(SettingsFragment.newInstance(), SettingsFragment.FRAGMENT_TAG);
        }
    }
}
