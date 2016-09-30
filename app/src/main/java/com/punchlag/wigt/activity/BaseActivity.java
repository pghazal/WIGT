package com.punchlag.wigt.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.punchlag.wigt.R;

public abstract class BaseActivity extends AppCompatActivity {

    @BindView(R.id.activity_content)
    public FrameLayout activityContent;

    public abstract int getLayoutResourceId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        ButterKnife.bind(this);
    }

    protected boolean addActivityContentFragmentWithTag(Fragment fragment, String tag) {
        if (fragment == null) {
            return false;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(activityContent.getId(), fragment, tag)
                .commit();
        return true;
    }
}
