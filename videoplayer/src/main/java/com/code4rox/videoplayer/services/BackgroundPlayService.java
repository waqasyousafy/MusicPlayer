package com.code4rox.videoplayer.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.code4rox.videoplayer.R;
import com.code4rox.videoplayer.activities.NonNullApi;
import com.code4rox.videoplayer.activities.VideoPlayer;
import com.code4rox.videoplayer.database.tinydb.TinyDB;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.Nullable;

import static android.provider.MediaStore.Images.Thumbnails.MINI_KIND;

public class BackgroundPlayService extends Service {
    private static final String PLAYBACK_CHANNEL_ID = "backgroundPlay";
    private static final int PLAYBACK_NOTIFICATION_ID = 101;
    final Context context = this;
    //     Bitmap _bitmap;
    public String url;
    public boolean activityplayerisplaying = false, serviceplayerisplaying;
    public int repeatmode;
    String title;
    long currentPosition;
    MediaSource mediaSource;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private SimpleExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;
    private ArrayList<String> playlist = new ArrayList<>();
    private boolean toturialvisiablity;
    private ImageView activitylauncher;
    private ImageButton play, pause;
    private WindowManager mWindowManager;
    private View mFloatingView;
    private PlayerView floatExoplayerView;
    private boolean ispopenable;
    private boolean iscontrolsvisible = false;
    private boolean local;
    private int songindex;
    private ImageButton next, previous;
    private boolean isstream;
    private AudioManager audioManager;
    private AudioAttributes playbackAttributes;
    private AudioFocusRequest focusRequest;
    private ConcatenatingMediaSource concatenatingMediaSource;
    private TinyDB tinyDB;
    private boolean sufflemeode;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("lifecycle", "Service oncreate");
        tinyDB = new TinyDB(getApplicationContext());
    }

    @SuppressLint("InflateParams")
    public void floatingWidget(Intent intent, ArrayList<String> playlist) {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (mWindowManager != null) {
            mWindowManager.addView(mFloatingView, params);
        }

        //The root element of the expanded view layout
//        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);
        //Set the close button
        play = mFloatingView.findViewById(R.id.playbutton);
        pause = mFloatingView.findViewById(R.id.pausebutton);
        ImageView closeButton = mFloatingView.findViewById(R.id.close_button);
        next = mFloatingView.findViewById(R.id.exo_next);
        previous = mFloatingView.findViewById(R.id.exo_prev);
        closeButton.setOnClickListener(
                (view) -> {
                    stopSelf();
                });
        floatExoplayerView = mFloatingView.findViewById(R.id.floatingexoplayer);
        activitylauncher = mFloatingView.findViewById(R.id.activitylauncher_button);
        activitylauncher.setOnClickListener(view -> {
            //Open the application  click.
            stopBackgroundVideoService();
            Intent intent1 = new Intent(BackgroundPlayService.this, VideoPlayer.class);
            intent1.putExtra("toturialvisiablity", false);

            if (floatExoplayerView.getPlayer() != null) {
                intent1.putExtra("currentposition", floatExoplayerView.getPlayer().getCurrentPosition());
            }
            if (local) {
                intent1.putExtra("local", local);
                intent1.putExtra("url", url);
            } else if (isstream) {
                intent1.putExtra("isstream", isstream);
                intent1.putExtra("url", url);
            } else {
                intent1.putExtra("playlist", playlist);
                intent1.putExtra("PlayerCurrentSongIndex", songindex);
            }
            intent1.putExtra("ispopup", true);
            Log.d("serviceurl", "" + url);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("repeatmode", player.getRepeatMode());
            intent1.putExtra("playerspeed",player.getPlaybackParameters().speed);
            startActivity(intent1);
            //close the service and remove view from the view hierarchy
        });

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.frameLayout2).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;
                        if (iscontrolsvisible) {
                            pause.setVisibility(View.GONE);
                            play.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                            previous.setVisibility(View.GONE);
                            activitylauncher.setVisibility(View.GONE);
                            closeButton.setVisibility(View.GONE);
                            iscontrolsvisible = false;
                        } else {
                            if (serviceplayerisplaying) {
                                pause.setVisibility(View.VISIBLE);
                                play.setVisibility(View.GONE);
                            } else {

                                pause.setVisibility(View.GONE);
                                play.setVisibility(View.VISIBLE);

                            }
                            closeButton.setVisibility(View.VISIBLE);
                            activitylauncher.setVisibility(View.VISIBLE);
                            next.setVisibility(View.VISIBLE);
                            previous.setVisibility(View.VISIBLE);
                            iscontrolsvisible = true;
                        }
                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {

                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            activitylauncher.setVisibility(View.VISIBLE);

                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
        floatExoplayerView.setControllerHideOnTouch(true);
        floatExoplayerView.setControllerShowTimeoutMs(3000);
        floatExoplayerView.setUseController(false);
    }


    @SuppressLint("CommitPrefEdits")
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent != null) {
            title = intent.getStringExtra("name");
            /*playlist = intent.getStringArrayListExtra("playlist");
            index=intent.getIntExtra("index",0);*/
//            SongIndex = String.valueOf(index);
            currentPosition = intent.getLongExtra("currentActvityposition", 0);
            activityplayerisplaying = intent.getBooleanExtra("Activityplyerstate", true);
            repeatmode = intent.getIntExtra("repeatemode", 0);
            toturialvisiablity = intent.getBooleanExtra("toturialvisiablity", false);
            ispopenable = intent.getBooleanExtra("ispopenable", false);
            local = intent.getBooleanExtra("localvideo", false);
            isstream = intent.getBooleanExtra("isstream", false);
            sufflemeode=intent.getBooleanExtra("sufflemode",false);
            Log.d("local", "service=" + local);
//        imageurl=intent.getStringExtra("thumbnail");
            if (local || isstream) {
                url = intent.getStringExtra("url");
                Log.d("activityinforUrl", " serviceurl:" + url);
            }
            player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
            if (local || isstream) {
                mediaSource = buildMediaSource(Uri.parse(url));
                player.prepare(mediaSource);
                player.seekTo(currentPosition);
            } else {
                Log.d("cursorstatus", "insideof-concatenating");
                playlist = intent.getStringArrayListExtra("playlist");
                Log.d("urlindex", "service:" + songindex);
                songindex = intent.getIntExtra("windowindex", 0);
                concatenatingMediaSource = new ConcatenatingMediaSource();
//        songlist = getListFiles(new File(VideoPlayerVideos));
                Log.d("cursorstatus", "" + playlist);
                for (int i = 0; i < playlist.size(); i++) {
                    mediaSource = buildMediaSource(Uri.parse(playlist.get(i)));
                    Log.d("checkstatus", "inside-check-else");
                    concatenatingMediaSource.addMediaSource(mediaSource);
                }
                player.prepare(concatenatingMediaSource);
                player.seekTo(songindex, currentPosition);
                if (intent.getFloatExtra("playerspeed", 1f) != 0) {
                    PlaybackParameters param = new PlaybackParameters(intent.getFloatExtra("playerspeed", 1f));
                    player.setPlaybackParameters(param);
                }
                player.setShuffleModeEnabled(sufflemeode);
                Log.d("servicewindowindex", "" + player.getCurrentWindowIndex());
            }
            player.setRepeatMode(repeatmode);
            if (activityplayerisplaying) {
                player.setPlayWhenReady(true);
                Log.d("activityplayerisplaying", "service" + activityplayerisplaying);
            } else {
                player.setPlayWhenReady(false);
            }
            sharedPreferences = getSharedPreferences("player", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            Log.d("curent postion", "" + player.getCurrentPosition());
            player.addListener(new Player.EventListener() {

                @NonNullApi
                @Override
                public void onTimelineChanged(Timeline timeline, int reason) {

                }

                @NonNullApi
                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @NonNullApi
                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @NonNullApi
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (player.getPlaybackState() == player.STATE_ENDED) {
                        if (repeatmode == 0) {
                            stopBackgroundVideoService();
                        }

                    }
                    if (playWhenReady && player.getPlaybackState() == player.STATE_READY) {
                        // media actually playing
                        Log.d("stateserviceplaying", "playing");
                        serviceplayerisplaying = true;
                        if (ispopenable) {
                            pause.setVisibility(View.VISIBLE);
                            play.setVisibility(View.GONE);
                            com.google.android.exoplayer2.audio.AudioAttributes audioAttributes = new com.google.android.exoplayer2.audio.AudioAttributes.Builder()
                                    .setUsage(C.USAGE_MEDIA)
                                    .setContentType(C.CONTENT_TYPE_MOVIE)
                                    .build();
                            player.getAudioComponent().setAudioAttributes(audioAttributes, true);
                        }
                    } else if (!playWhenReady) {
                        // player paused in any state
                        Log.d("stateserviceplaying", "pause");
                        serviceplayerisplaying = false;
                        if (ispopenable) {
                            pause.setVisibility(View.GONE);
                            play.setVisibility(View.VISIBLE);
                        }
                    }  // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)


                }

                @NonNullApi
                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @NonNullApi
                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @NonNullApi
                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @NonNullApi
                @Override
                public void onPositionDiscontinuity(int reason) {
                    int latestWindowIndex = player.getCurrentWindowIndex();
                    if (latestWindowIndex != songindex) {
                        // item selected in playlist has changed, handle here
                        songindex = latestWindowIndex;
                        title = getFileNameFromUrl(playlist.get(songindex));
//                url=playlist.get(urlindex);
                        Log.d("urlnew", "" + songindex);

                        // ...
                    }
                }

                @NonNullApi
                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @NonNullApi
                @Override
                public void onSeekProcessed() {

                }
            });
            if (ispopenable) {
                floatingWidget(intent, playlist);
                floatExoplayerView.setPlayer(player);
                floatExoplayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                player.setPlayWhenReady(true);
                play.setOnClickListener(v -> {

                    if (floatExoplayerView.getPlayer() != null) {
                        floatExoplayerView.getPlayer().setPlayWhenReady(true);
                    }
                    pause.setVisibility(View.VISIBLE);
                    play.setVisibility(View.GONE);


                });
                pause.setOnClickListener(v -> {
                    if (floatExoplayerView.getPlayer() != null) {
                        floatExoplayerView.getPlayer().setPlayWhenReady(false);
                    }
                    pause.setVisibility(View.GONE);
                    play.setVisibility(View.VISIBLE);
                });

            }

            playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                    context,
                    PLAYBACK_CHANNEL_ID,
                    R.string.playback_channel_name,
                    PLAYBACK_NOTIFICATION_ID,
                    new PlayerNotificationManager.MediaDescriptionAdapter() {

                        @Override
                        public String getCurrentContentTitle(Player player) {
                            if (local) {
                                Uri uri = Uri.parse(url);
                                if (Objects.requireNonNull(uri.getScheme()).equals("content")) {
                                    @SuppressLint("Recycle") Cursor returnCursor =
                                            getContentResolver().query(uri, null, null, null, null);
                                    int nameIndex = 0;
                                    if (returnCursor != null) {
                                        nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    }
//                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                                    returnCursor.moveToFirst();
                                    title = returnCursor.getString(nameIndex);
                                }
                            } else if (isstream) {
                                title = getFileNameFromUrl(url);
                            } else {
                                title = getFileNameFromUrl(playlist.get(songindex));
                            }
                            return title;
                        }

                        @Nullable
                        @Override
                        public PendingIntent createCurrentContentIntent(Player player) {

                            return PendingIntent.getActivity(context, 0, gettingintent(), PendingIntent.FLAG_UPDATE_CURRENT);
                        }

                        @Nullable
                        @Override
                        public String getCurrentContentText(Player player) {
                            return null;
                        }

                        @Nullable
                        @Override
                        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                            Bitmap bitmap;
                            if (local) {
                                bitmap = ThumbnailUtils.createVideoThumbnail(url, MINI_KIND);
                            } else if (isstream) {
                                bitmap = ThumbnailUtils.createVideoThumbnail(url, MINI_KIND);
                            } else {
                                bitmap = ThumbnailUtils.createVideoThumbnail(playlist.get(player.getCurrentPeriodIndex()), MINI_KIND);
                            }
                            return bitmap;
                        }
                    }

            );
            playerNotificationManager.setUsePlayPauseActions(true);


            playerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
                @Override
                public void onNotificationStarted(int notificationId, Notification notification) {

                    startForeground(notificationId, notification);

                }

                @Override
                public void onNotificationCancelled(int notificationId) {
                    Log.d("lifecycle", "Service onnotificationcancell");
                    stopBackgroundVideoService();
                }

            });

            playerNotificationManager.setPlayer(player);
            playerNotificationManager.setUseNavigationActions(true);
            playerNotificationManager.setUseStopAction(true);
            playerNotificationManager.setFastForwardIncrementMs(0);
            playerNotificationManager.setRewindIncrementMs(0);

//        Toast.makeText(getApplicationContext(),"postion"+player.getCurrentPosition(),Toast.LENGTH_SHORT).show();
            Log.d("lifecycle", "Service  onstart");
        }

        return START_STICKY;
    }

    public String getFileNameFromUrl(String path) {
        String[] pathArray = path.split("/");
        return pathArray[pathArray.length - 1];
    }

    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(this, getApplicationContext().getApplicationInfo().packageName);
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, null, httpDataSourceFactory);
        TextUtils.isEmpty(null);
        int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    public Intent gettingintent() {
        Intent intent1 = new Intent(context, VideoPlayer.class);
        if (local) {
            intent1.putExtra("title", title);
        }

        intent1.putExtra("isstream", isstream);
        intent1.putExtra("local", local);
        if (ispopenable) {
            intent1.putExtra("BackgroundPlayService", false);
            intent1.putExtra("ispopup",true);
            Log.d("serviceisplaying", "pop");
        } else {
            intent1.putExtra("BackgroundPlayService", true);
            Log.d("serviceisplaying", "no-popup");
        }
        intent1.putExtra("url", url);
        intent1.putExtra("repeatmode", player.getRepeatMode());
        Log.d("servicerepeatmode","service"+player.getRepeatMode());
        intent1.putExtra("playerspeed",player.getPlaybackParameters().speed);
        Log.d("playerspeed","service"+player.getPlaybackParameters().speed);
        intent1.putExtra("toturialvisiablity", toturialvisiablity);
        intent1.putExtra("serviceRunning", serviceplayerisplaying);
        intent1.putStringArrayListExtra("playlist", playlist);
        return intent1;
    }

    public void Onclose() {
        if (playerNotificationManager != null) {
            playerNotificationManager.setPlayer(null);
            player.release();
        }
    }


    public void stopBackgroundVideoService() {
        tinyDB.putBoolean("BackgroundPlaySetting", false);
        if (player.getPlaybackState() != player.STATE_ENDED) {
            editor.putLong("PlayerCurrentPosition", player.getCurrentPosition());
            Log.d("Service", "" + player.getCurrentPosition());
            Log.d("servicewindowindex", "second=" + player.getCurrentWindowIndex());
            editor.putInt("PlayerCurrentSongIndex", player.getCurrentWindowIndex());
        } else {
            editor.putLong("PlayerCurrentPosition", 0);
        }
//        editor.putInt("playlistSognNumber",index);
        editor.putBoolean("serviceplayerisplaying", serviceplayerisplaying);
        editor.putBoolean("servicereturnedindex", true);
        editor.putBoolean("local", local);
        editor.putInt("servicerepeatmodehomebuttonCall",player.getRepeatMode());
        editor.putFloat("serviceplayerspeedhomebuttonCall",player.getPlaybackParameters().speed);
        Log.d("servicestateplaying", "service-" + serviceplayerisplaying);
        editor.commit();
        stopSelf();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle", "serviceondestroycalled");
        if (ispopenable) {
            if (mFloatingView != null) {
                mWindowManager.removeView(mFloatingView);

            }
        }
        Onclose();
        /*     stopSelf();*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("lifecycle", "binded");
        return null;
    }
}