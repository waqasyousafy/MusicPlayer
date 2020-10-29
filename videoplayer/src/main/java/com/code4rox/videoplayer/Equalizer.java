package com.code4rox.videoplayer;

import android.os.Bundle;

import com.bullhead.equalizer.EqualizerFragment;

import androidx.appcompat.app.AppCompatActivity;

public class Equalizer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);
       int sessionid= getIntent().getIntExtra("sessionid",0);
        EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(R.color.colorAccent)
                .setAudioSessionId(sessionid)
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.eqFrame, equalizerFragment)
                .commit();
    }
}
