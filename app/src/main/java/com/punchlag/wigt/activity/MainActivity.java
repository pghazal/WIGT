package com.punchlag.wigt.activity;

import android.os.Bundle;

import com.punchlag.wigt.R;
import com.punchlag.wigt.fragment.MainFragment;

public class MainActivity extends BaseActivity {

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.FRAGMENT_TAG);
        if (fragment == null) {
            addActivityContentFragmentWithTag(MainFragment.newInstance(), MainFragment.FRAGMENT_TAG);
        }
    }
}
