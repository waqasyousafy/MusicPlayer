package com.code4rox.videoplayer.activities;

import android.graphics.Color;
import android.os.Bundle;

import com.bullhead.equalizer.EqualizerFragment;
import com.code4rox.videoplayer.R;

import androidx.appcompat.app.AppCompatActivity;

public class EqualizerActivity extends AppCompatActivity {
int sessionid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer2);
        sessionid=getIntent().getIntExtra("sessionid",0);
        EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(Color.parseColor("#ee3847"))
                .setAudioSessionId(sessionid)
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.eqFrame, equalizerFragment)
                .commit();
    }
}
