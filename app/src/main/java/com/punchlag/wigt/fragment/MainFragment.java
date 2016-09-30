package com.punchlag.wigt.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.widget.TimePicker;

import com.punchlag.wigt.R;
import com.punchlag.wigt.activity.MapsActivity;
import com.punchlag.wigt.model.Alarm;
import com.punchlag.wigt.utils.Arguments;
import com.punchlag.wigt.utils.SystemUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

public class MainFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = MainFragment.class.getSimpleName();

    @BindView(R.id.timePicker)
    TimePicker timePicker;

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
        initTimePicker();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initTimePicker() {
        Calendar calendar = Calendar.getInstance();
        timePicker.setIs24HourView(true);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        if (SystemUtils.isAboveMarshmallow()) {
            timePicker.setHour(hours);
            timePicker.setMinute(minutes);
        } else {
            timePicker.setCurrentHour(hours);
            timePicker.setCurrentMinute(minutes);
        }
    }

    @OnClick(R.id.validationButton)
    @TargetApi(Build.VERSION_CODES.M)
    public void validateAlarm() {
        Alarm alarm;
        if (SystemUtils.isAboveMarshmallow()) {
            alarm = new Alarm(timePicker.getHour(), timePicker.getMinute());
        } else {
            alarm = new Alarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        }

        Intent intent = new Intent(getContext(), MapsActivity.class);
        intent.putExtra(Arguments.ARG_ALARM, alarm);
        startActivity(intent);
    }
}
