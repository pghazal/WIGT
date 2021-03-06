package com.punchlag.wigt.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.widget.SeekBar;

import com.punchlag.wigt.R;
import com.punchlag.wigt.maps.MapsActivity;
import com.punchlag.wigt.utils.Arguments;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingsFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = "SettingsFragment";

    @BindView(R.id.alarmSwitch)
    SwitchCompat alarmSwitch;

    @BindView(R.id.validationButton)
    AppCompatButton validationButton;

    @BindView(R.id.seekBarValueText)
    AppCompatTextView seekBarValueText;

    @BindView(R.id.seekBar)
    AppCompatSeekBar seekBar;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_settings;
    }

    @Override
    public void configureSubviews(Bundle savedInstanceState) {
        configureSeekBar();
    }

    private void configureSeekBar() {
        final int step = 100;
        int max = 1000;
        final int min = 100;

        seekBar.setMax((max - min) / step);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = min + (progress * step);

               // if (progress >= 0 && progress <= seekBar.getMax()) {
                    seekBarValueText.setText("" + value);
                //}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick(R.id.validationButton)
    public void goToMapScreen() {
        boolean alarmActivated = alarmSwitch.isChecked();

        Intent intent = new Intent(getContext(), MapsActivity.class);
        intent.putExtra(Arguments.ARG_ALARM_ACTIVATED, alarmActivated);
        startActivity(intent);
    }
}
