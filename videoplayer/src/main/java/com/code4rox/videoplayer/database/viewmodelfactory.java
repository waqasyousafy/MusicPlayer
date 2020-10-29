package com.code4rox.videoplayer.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.code4rox.videoplayer.database.viewmodel.songviewmodel;

public class viewmodelfactory implements ViewModelProvider.Factory {

    private final Application mDataSource;

    public viewmodelfactory(Application dataSource) {
        mDataSource = dataSource;
    }

    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(songviewmodel.class)) {
            return (T) new songviewmodel(mDataSource);
        }
        //noinspection unchecked
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}