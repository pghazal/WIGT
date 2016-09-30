package com.punchlag.wigt.activity;

import android.content.Intent;
import android.os.Bundle;
import com.punchlag.wigt.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
