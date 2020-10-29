package com.example.musicplayer.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Random;

public class MyService extends Service {

    // Binder given to clients (notice class declaration below)
    private final IBinder mBinder = new MyBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("service-bounding", "bind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service-bounding", "create-service");
    }

    /**
     * Class used for the client Binder. The Binder object is responsible for returning an instance
     * of "MyService" to the client.
     */
    public class MyBinder extends Binder {
        public MyService getService() {
            // Return this instance of MyService so clients can call public methods
            return MyService.this;
        }
    }

    /**
     * This is how the client gets the IBinder object from the service. It's retrieve by the "ServiceConnection"
     * which you'll see later.
     **/

    /**
     * method for clients to get a random number from 0 - 100
     */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service-bounding", "start-service");
//        getRandomNumber();
        // Keep this global to the scope of the class. You only need one
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("service-bounding", "unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d("service-bounding", "rebind");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("service-bounding", "destroy-service");
    }
}