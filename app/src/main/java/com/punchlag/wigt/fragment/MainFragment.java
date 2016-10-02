package com.punchlag.wigt.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;

import com.punchlag.wigt.R;
import com.punchlag.wigt.activity.MapsActivity;
import com.punchlag.wigt.utils.Arguments;

import butterknife.BindView;
import butterknife.OnClick;

public class MainFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = MainFragment.class.getSimpleName();

    @BindView(R.id.alarmSwitch)
    SwitchCompat alarmSwitch;

    @BindView(R.id.validationButton)
    AppCompatButton validationButton;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_main;
    }

    @Override
    public void configureSubviews(Bundle savedInstanceState) {
    }

    @OnClick(R.id.validationButton)
    @TargetApi(Build.VERSION_CODES.M)
    public void validateAlarm() {
        boolean alarmActivated = alarmSwitch.isChecked();

        Intent intent = new Intent(getContext(), MapsActivity.class);
        intent.putExtra(Arguments.ARG_ALARM_ACTIVATED, alarmActivated);
        startActivity(intent);
    }
}
