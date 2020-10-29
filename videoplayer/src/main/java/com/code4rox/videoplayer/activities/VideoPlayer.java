package com.code4rox.videoplayer.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bullhead.equalizer.EqualizerFragment;
import com.code4rox.adsmanager.AdmobUtils;
import com.code4rox.adsmanager.InterAdsIdType;
import com.code4rox.videoplayer.R;
import com.code4rox.videoplayer.adapters.ScrollingWidgetsAdapter;
import com.code4rox.videoplayer.database.daos.Dao;
import com.code4rox.videoplayer.database.songdatabase;
import com.code4rox.videoplayer.database.tables.song_detail_table;
import com.code4rox.videoplayer.database.tinydb.TinyDB;
import com.code4rox.videoplayer.helperClasses.RecyclerViewDecorationClass;
import com.code4rox.videoplayer.permissionHandler.PermissionManager;
import com.code4rox.videoplayer.services.BackgroundPlayService;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdView;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import static android.media.AudioManager.STREAM_MUSIC;
import static android.os.Environment.DIRECTORY_PICTURES;
import static com.code4rox.videoplayer.services.AudioPlayerService.MEDIA_SESSION_TAG;
import static com.code4rox.videoplayer.utils.AnimationUtils.Type.SCALE_AND_ALPHA;
import static com.code4rox.videoplayer.utils.AnimationUtils.animateView;

public class VideoPlayer extends AppCompatActivity implements AdmobUtils.AdmobInterstitialListener {
    public static final int DEFAULT_CONTROLS_DURATION = 300;
    private static final int ORIENTATION_LANDSCAPE = 2;
    //Player Veriables
    private PlayerView mExoPlayerView;
    public MediaSource mVideoSource;
    private ConcatenatingMediaSource concatenatingMediaSource;
    private PlayerControlView controlView;
    //Gesture detector
    private final float MAX_GESTURE_LENGTH = 0.75f;
    public GestureDetector gestureDetector;
    //Context
    public Context context;
    //TextViews
    public TextView swapduration, swapupdatevalue, overLayToturialTitle, volumepercentText, brightnesspercentText, overlaydissmisDialog, titleprogress, playbackSpeedText, playasaudioText, mutePlayerText, repeatPlayerText, titleSong;
    //Image Views
    private ImageView backarrow, menuIcon, playasaudioImage, mutePlayerImage, repeatPlayerImage, iconAnimations, lockicon, controlAnimationView;
    //Framelayout Aspect Ratio
    public FrameLayout aspectratiolayout;
    //Seekbar
    private SeekBar seekbar1, seekbar;
    //Shared Preferences
    public SharedPreferences preferences;
    //AudioManager
    private AudioManager audioManager;
    //Progress bar Loading Bar
    private ProgressBar loading;
    //Image Buttons
    private ImageButton aspectratiobutton1;
    private ImageButton repeatcontroll;
    private ImageButton equalizer;
    private ImageButton exo_screenlock, exo_next, exo_prev;
    //Constraint Layout
    private ConstraintLayout featuresmenu, muteplayerbutton, lockLayout, repeatmenubutton, speed, loading_parent_layout, swaptimerrootview, overLayToturial, screenshortButton, backgroundplaybutton, vbview, popupbutton, exo_equalizer;
    // Animation
    private ValueAnimator controlViewAnimator;
    //General Veriables
    private String url;
    private String name;
    private String songname;
    public long position;
    public long totalduration;
    public long currentserviceposition;
    public int servicerepeatmode, maxGestureLength, isfull = 0, isempty = 0, count = 0, currentPlayerVolume;
    public ArrayList<String> playlist = new ArrayList<>();
    private boolean muted;
    private boolean serviceplayerisplaying = false;
    private boolean servicebackgroundplaystate;
    private boolean firsttime = true;
    private boolean mExoPlayerFullscreen;
    private boolean activityplayerisplaying = false;
    private boolean ispopupenable;
    private boolean local;
    private int urlindex;
    private boolean playlistmode;
    private int servicesongindex;
    private int sessionId;
    private boolean firstsessionid = true;
    private ImageButton exo_shuffle, sharevideo;
    Boolean isstream;
    private ImageButton nightmode;
    private boolean isnightmode = false;
    private float previousBrightness;
    private ImageButton sleepplayer;
    private int selectedItems;
    private TextView timer;
    private boolean timerSelectDuration;
    private CountDownTimer timers;
    private boolean toturialvisiablity;
    private SimpleExoPlayer player;
    private String path;
    private AudioFocusRequest focusRequest;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private MediaSession.Token mediaSessiontoken;
    private LockScreenReceiver lockScreenReceiver;
    private Dao dao;
    public static final int ACTIVITY_REQUEST_CODE = 1;
    private int file_size;
    private List<song_detail_table> data;
    private song_detail_table songdetail;

    private ImageButton expander;
    private HorizontalScrollView horizontalScrollView;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    private LinearLayoutCompat scrollgrouplayout;
    public RecyclerView scrollingwidgetview;
    private ArrayList<Integer> widgetslist;
    private WindowManager.LayoutParams layoutParams;
    Bundle savedInstanceState;
    private ScrollingWidgetsAdapter scrollingWidgetsAdapter;
    private int itemPosition;
    private int marginright;
    private int devicewidth;
    private TinyDB tinyDB;
    private DefaultTimeBar defaultTimeBar;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceslistener;
    private boolean isServiceReturnedIndex;
    private boolean playersourceerror;
    private int indexofwindow;
    private int previousindex = -1;
    private AlertDialog.Builder builder;
    private int songindex;
    private EqualizerFragment equalizerFragment;
    private Equalizer mEqualizer;
    private ArrayList<Integer> activatedicons;
    private int widgetid;
    private boolean singleactivitycall;
    private boolean isInterAdShow = false;
    private AdmobUtils admobUtils;
    private FrameLayout addview;
    private boolean songisindatabase = false;
    private AdView adView;
    private Dialog adDialog;
    private boolean isFirst = false;
    private boolean playerlocked;

    public VideoPlayer() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
        Log.d("lifecycle", "oncreate");

        adDialog = new Dialog(VideoPlayer.this);
        admobUtils = new AdmobUtils(this, this, InterAdsIdType.INTER_AM);

        initExitDialog();
        tinyDB = new TinyDB(this);
        servicebackgroundplaystate = getIntent().getBooleanExtra("BackgroundPlayService", false);
        servicerepeatmode = getIntent().getIntExtra("repeatmode", 0);
        widgetslist = new ArrayList<>();
        widgetslist.add(R.drawable.ic_mixer);
//        widgetslist.add(R.drawable.ic_time_ic);
        widgetslist.add(R.drawable.ic_share);
        widgetslist.add(R.drawable.ic_shuffle_ic);
        widgetslist.add(R.drawable.ic_night_ic);
        if (servicerepeatmode == 0) {
            widgetslist.add(R.drawable.ic_repeat_ic);
        } else {
            widgetslist.add(R.drawable.ic_repeat_selected);
        }
        widgetslist.add(R.drawable.ic_rotate_ic);
        widgetslist.add(R.drawable.ic_arrow_ic);
        exo_next = findViewById(R.id.exo_next);
        exo_prev = findViewById(R.id.exo_prev);


//        addview = findViewById(R.id.addPlayer);
        defaultTimeBar = findViewById(R.id.exo_progress);
        defaultTimeBar.setScrubberColor(getResources().getColor(R.color.colorPrimary));
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceheight = displayMetrics.heightPixels;
        devicewidth = displayMetrics.widthPixels;
        path = getIntent().getStringExtra("path");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d("lifecycle", "create pressed" + VideoPlayer.this.isInPictureInPictureMode());
        }
        //AUDIO MANAGER SERVICE
        layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 1f;
        getWindow().setAttributes(layoutParams);

        Log.d("brightnessplayer", "" + previousBrightness);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //PLAYER STARTED
        initializeElements();
        //Intent getting
        String imageurl = getIntent().getStringExtra("thumbnail");
        ConstraintLayout controlslayout = findViewById(R.id.controlerbottomlayout);
        //getting url every time the player started
        //For now we pass the demo path you could pass through intent
        preferences = getSharedPreferences("player", MODE_PRIVATE);
        name = getIntent().getStringExtra("title");
        currentserviceposition = getIntent().getLongExtra("currentposition", 0);
//        servicebackgroundplaystate = getIntent().getBooleanExtra("BackgroundPlayService", false);
//        servicerepeatmode = getIntent().getIntExtra("repeatmode", 0);
        isstream = getIntent().getBooleanExtra("isstream", false);
        toturialvisiablity = getIntent().getBooleanExtra("toturialvisiablity", true);
        //Over layer tutorial
        overLayToturialTitle.setText(getResources().getString(R.string.videoplayerTipsText));
        overLayToturialTitle.setVisibility(View.VISIBLE);
        overlaydissmisDialog = findViewById(R.id.dismissToturialDialog);
        overlaydissmisDialog.setText("Got it,\nDismiss this");
        Intent intent = getIntent();
        overLayToturial.setVisibility(View.VISIBLE);
        singleactivitycall = intent.getBooleanExtra("singleplaylistmodecall", false);
        //Seekbar
        seekbar.setRotation(-90);
        seekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.orangeColor), PorterDuff.Mode.SRC_ATOP);
        seekbar1.setRotation(-90);
        seekbar1.getProgressDrawable().setColorFilter(getResources().getColor(R.color.orangeColor), PorterDuff.Mode.SRC_ATOP);

        //Getting information for player initialization
        Uri uri;
        if (toturialvisiablity) {
            if (getIntent().getDataString() != null) {
                url = getIntent().getDataString();
                uri = Uri.parse(url);
                if (Objects.requireNonNull(uri.getScheme()).equals("content")) {
                    @SuppressLint("Recycle") Cursor returnCursor =
                            getContentResolver().query(uri, null, null, null, null);
                    int nameIndex = 0;
                    if (returnCursor != null) {
                        nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    }
//                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    if (returnCursor != null) {
                        returnCursor.moveToFirst();
                    }
                    if (returnCursor != null) {
                        songname = returnCursor.getString(nameIndex);
                    }
                    if (songname != null)
                        titleSong.setText(songname);
                    local = true;
                } else {
                    local = true;
                    songname = getFileNameFromUrl(url);
                    titleSong.setText(songname);
                }
            } else if (isstream) {
                url = getIntent().getStringExtra("url");
                songname = getFileNameFromUrl(url);
                titleSong.setText(songname);
            } else {
                urlindex = getIntent().getIntExtra("urlindex", 0);
                playlist = getIntent().getStringArrayListExtra("playlist");
                songname = getFileNameFromUrl(playlist.get(urlindex));
                playlistmode = true;
                titleSong.setText(songname);
                isFirst = true;
            }
        } else {
            local = getIntent().getBooleanExtra("local", false);
            if (local) {
                url = getIntent().getStringExtra("url");
                uri = Uri.parse(url);
                if (Objects.requireNonNull(uri.getScheme()).equals("content")) {
                    @SuppressLint("Recycle") Cursor returnCursor =
                            getContentResolver().query(uri, null, null, null, null);
                    int nameIndex = 0;
                    if (returnCursor != null) {
                        nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    }
                    if (returnCursor != null) {
                        returnCursor.moveToFirst();
                    }
                    if (returnCursor != null) {
                        songname = returnCursor.getString(nameIndex);
                    }
                    if (songname != null)
                        titleSong.setText(songname);
                    else
                        titleSong.setText("null");
                    local = true;
                } else {
                    local = true;
                    songname = getFileNameFromUrl(url);
                    titleSong.setText(songname);
                }
            } else if (isstream) {
                url = getIntent().getStringExtra("url");
                if (url != null) {
                    songname = getFileNameFromUrl(url);
                }
                titleSong.setText(songname);
            } else {

                playlist = getIntent().getStringArrayListExtra("playlist");
                urlindex = getIntent().getIntExtra("PlayerCurrentSongIndex", 0);
                songname = getFileNameFromUrl(playlist.get(urlindex));
                playlistmode = true;
                titleSong.setText(songname);
            }

        }

        //Building the media source both for playlist and local
        requestvideo(url);
        //initialize player
//        initExitDialog();
        initExoPlayer();
        if (getIntent().getBooleanExtra("shufflemode", false)) {
            mExoPlayerView.getPlayer().setShuffleModeEnabled(true);
        }
        //Rotation screen feature
        //For pop-up getting the current position
        if (getIntent().hasExtra("currentposition")) {
            overLayToturial.setVisibility(View.GONE);
            mExoPlayerView.getPlayer().seekTo(getIntent().getLongExtra("currentposition", 0));
        }

        //Player Feauters at bottom of player
        //For Screen lock
        exo_screenlock.setOnClickListener(v -> {
            lockLayout.setVisibility(View.VISIBLE);
            mExoPlayerView.hideController();
            mExoPlayerView.setEnabled(false);
            mExoPlayerView.setUseController(false);
            controlView.setEnabled(false);
            playerlocked = true;
        });
//For unloack screen
        lockicon.setOnClickListener(v -> {
            lockLayout.setVisibility(View.GONE);
            mExoPlayerView.showController();
            mExoPlayerView.setEnabled(true);
            controlView.setEnabled(true);
            mExoPlayerView.setUseController(true);
            playerlocked = false;
        });
        //For Aspect Ratio
        aspectratiobutton1.setOnClickListener(v -> {

            switch (mExoPlayerView.getResizeMode()) {
                case AspectRatioFrameLayout.RESIZE_MODE_FIT:
                    mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

                    aspectratiobutton1.setBackgroundResource(R.drawable.ic_crop);

                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_ZOOM:
                    mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

                    aspectratiobutton1.setBackgroundResource(R.drawable.ic_zoom_ic);

                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_FILL:
                    mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    aspectratiobutton1.setBackgroundResource(R.drawable.ic_100);

                    break;
                default:
                    mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT:
                case AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH:
                    break;
            }
        });
        //Playback speed
        speed.setOnClickListener(v -> {
            featuresmenu.setVisibility(View.GONE);
            menuIcon.setImageResource(R.drawable.ic_side_bar);
            if (mExoPlayerView.getPlayer() != null) {
                if (mExoPlayerView.getPlayer().getPlaybackParameters().speed == 1f) {
                    PlaybackParameters param = new PlaybackParameters(2f);
                    mExoPlayerView.getPlayer().setPlaybackParameters(param);
                    showAndAnimateControl(R.drawable.speed2x, true);


                } else if (mExoPlayerView.getPlayer().getPlaybackParameters().speed == 2f) {
                    PlaybackParameters param = new PlaybackParameters(4f);
                    showAndAnimateControl(R.drawable.ic_4x, true);
                    mExoPlayerView.getPlayer().setPlaybackParameters(param);
                } else {
                    PlaybackParameters param = new PlaybackParameters(1f);
                    showAndAnimateControl(R.drawable.ic_1x, true);
                    mExoPlayerView.getPlayer().setPlaybackParameters(param);
                }
            }
        });
        if (!local && !isstream) {
            songdetail = songdatabase.getDatabase(getApplicationContext()).songdao().get(playlist.get(mExoPlayerView.getPlayer().getCurrentWindowIndex()));

            builder = new AlertDialog.Builder(VideoPlayer.this);
            builder.setMessage(R.string.dialog_position_getter)
                    .setPositiveButton("RESUME", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            if (songdetail != null) {
                                mExoPlayerView.getPlayer().seekTo(songdetail.getSong_played_duration());
                                mExoPlayerView.getPlayer().setPlayWhenReady(true);
                            } else {
                                mExoPlayerView.getPlayer().setPlayWhenReady(true);
                            }
                            isFirst = false;
                        }
                    })
                    .setNegativeButton("START OVER", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mExoPlayerView.getPlayer().seekTo(0);
                            mExoPlayerView.getPlayer().setPlayWhenReady(true);
                            // User cancelled the dialog
                            isFirst = false;
                        }
                    });
        }
        exo_next.setOnClickListener((v) -> {
            songcheck();
            mExoPlayerView.getPlayer().seekTo(mExoPlayerView.getPlayer().getDuration());
            mExoPlayerView.getPlayer().next();
        });
        exo_prev.setOnClickListener((v) -> {
            songcheck();
            mExoPlayerView.getPlayer().previous();
        });
        //Player listener
        if (mExoPlayerView.getPlayer() != null) {
            mExoPlayerView.getPlayer().addListener(new ExoPlayer.EventListener() {
                @NonNullApi
                @Override
                public void onTimelineChanged(Timeline timeline, int reason) {

                }

                @NonNullApi
                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    Log.d("timersstatus", "track" + timerSelectDuration);
//                    if (timerSelectDuration) {
//                        Log.d("timersstatus", "off");
//                        finish();
//                    }
//                    if (!tinyDB.getBoolean("AutoPlayNextSetting")) {
//                        finish();
//                    }


                    if (!local && !isstream && !singleactivitycall) {
                        indexofwindow = mExoPlayerView.getPlayer().getCurrentWindowIndex();
                        if (indexofwindow != previousindex) {
                            songdetail = songdatabase.getDatabase(getApplicationContext()).songdao().get(playlist.get(mExoPlayerView.getPlayer().getCurrentWindowIndex()));
                            if (songdetail != null) {
                                if (songdetail.getSong_id() > -1) {
                                    songisindatabase = true;
                                }
                            }
                            if ((getIntent().getBooleanExtra("ispopup", false))
                                    || (mExoPlayerView.getPlayer().getRepeatMode() == mExoPlayerView.getPlayer().REPEAT_MODE_ONE)
                                    || servicebackgroundplaystate) {
                            } else {
                                if (songisindatabase) {
                                    if (tinyDB.getBoolean("ResumeSetting")) {
                                        Log.d("playerstatus", "insideondiscontinuity");
                                        if (getIntent().getBooleanExtra("actiivitysongcallintent", false)) {
                                            overLayToturial.setVisibility(View.GONE);
                                            mExoPlayerView.getPlayer().setPlayWhenReady(false);
                                            songdetail = songdatabase.getDatabase(getApplicationContext()).songdao().get(playlist.get(mExoPlayerView.getPlayer().getCurrentWindowIndex()));
                                            builder.show();
                                            isFirst = true;
                                        } else {
                                            if (!serviceplayerisplaying && songdetail != null) {
                                                overLayToturial.setVisibility(View.GONE);
                                                mExoPlayerView.getPlayer().setPlayWhenReady(false);
                                                songdetail = songdatabase.getDatabase(getApplicationContext()).songdao().get(playlist.get(mExoPlayerView.getPlayer().getCurrentWindowIndex()));
                                                builder.show();
                                                isFirst = true;
                                            }
                                        }

                                    }
                                }
                            }
                            previousindex = indexofwindow;
                        }
                    }

                }

                @NonNullApi
                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @NonNullApi
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    mExoPlayerView.setKeepScreenOn(true);
                    try {
                        if (playbackState == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT) {
                        }
                        if (playbackState == mExoPlayerView.getPlayer().STATE_ENDED) {
                            if (mExoPlayerView.getPlayer().getRepeatMode() == mExoPlayerView.getPlayer().REPEAT_MODE_OFF) {

                                loading_parent_layout.setVisibility(View.GONE);

                            } else {
                                mExoPlayerView.getPlayer().seekTo(0);

                            }
                            finish();
                        } else if (mExoPlayerView.getPlayer().getPlaybackState() == mExoPlayerView.getPlayer().STATE_READY) {
                            loading_parent_layout.setVisibility(View.GONE);

                        } else if (mExoPlayerView.getPlayer().getPlaybackState() == mExoPlayerView.getPlayer().STATE_BUFFERING) {
                            loading_parent_layout.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.VISIBLE);
                        }
                        if (playWhenReady && playbackState == mExoPlayerView.getPlayer().STATE_READY) {
                            adDialog.dismiss();
                            if (timerSelectDuration) {
                                long durationfortimer = (mExoPlayerView.getPlayer().getDuration() - mExoPlayerView.getPlayer().getCurrentPosition());
                                timerstart(durationfortimer);
                            }
                            loading_parent_layout.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                                    .setUsage(C.USAGE_MEDIA)
                                    .setContentType(C.CONTENT_TYPE_MOVIE)
                                    .build();
                            Objects.requireNonNull(mExoPlayerView.getPlayer().getAudioComponent()).setAudioAttributes(audioAttributes, true);
                            activityplayerisplaying = true;
                        } else if (playWhenReady) {
                            // might be idle (plays after prepare()),
                            // buffering (plays when data available)
                            // or ended (plays when seek away from end)
                        } else {
                            // player paused in any state\

                            if (!isFirst) {
                                if (isConnected()) {
                                    adDialog.show();
                                } else {
                                }

                            } else {

                            }

                          /*  admobUtils = new AdmobUtils(VideoPlayer.this);
                            admobUtils.loadBannerAd(adView);*/
                            if (timerSelectDuration) {
                                cancelTimer();
                                long durationfortimer = (mExoPlayerView.getPlayer().getDuration() - mExoPlayerView.getPlayer().getCurrentPosition());
                                scrollingWidgetsAdapter.changeTimerView(itemPosition, durationfortimer, true);
                            }
                            activityplayerisplaying = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                    //Handeling the error for none playable video
                    String TAG = "PLAYER_ERROR";
                    switch (error.type) {
                        case ExoPlaybackException.TYPE_SOURCE:
                            overLayToturial.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Cannot Play This Video", Toast.LENGTH_LONG).show();
                            playersourceerror = true;
                            finish();
                            break;

                        case ExoPlaybackException.TYPE_RENDERER:
                            break;

                        case ExoPlaybackException.TYPE_UNEXPECTED:
                            break;
                        case ExoPlaybackException.TYPE_OUT_OF_MEMORY:
                        case ExoPlaybackException.TYPE_REMOTE:
                            break;
                    }
                }

                @NonNullApi
                @Override
                public void onPositionDiscontinuity(int reason) {
                    if (mExoPlayerView.getPlayer() != null) {
                        int latestWindowIndex = mExoPlayerView.getPlayer().getCurrentWindowIndex();
                        if (latestWindowIndex != urlindex) {
                            // item selected in playlist has changed, handle here
                            urlindex = latestWindowIndex;
                            songname = getFileNameFromUrl(playlist.get(urlindex));
                            titleSong.setText(songname);
                        }
                    }
                }

                @NonNullApi
                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }


                @NonNullApi
                @Override
                public void onSeekProcessed() {
//                    if (timerSelectDuration) {
//                        if (activityplayerisplaying) {
//
//                        } else {
//                            cancelTimer();
//                            long durationfortimer = (mExoPlayerView.getPlayer().getDuration() - mExoPlayerView.getPlayer().getCurrentPosition());
//                            int positionoftimer = widgetslist.indexOf(R.drawable.ic_time_ic);
//                            scrollingWidgetsAdapter.changeTimerView(positionoftimer, durationfortimer, true);
//
//                        }
//
//                    } else {
//
//                    }


//                    timer();
//                    if (timerSelectDuration) {
//                        long durationfortimer = (mExoPlayerView.getPlayer().getDuration() - mExoPlayerView.getPlayer().getCurrentPosition());
////                        timerstart(durationfortimer);
//                        if (mExoPlayerView.getPlayer().getPlayWhenReady()) {
//                            timerstart(durationfortimer);
//                        } else {
//                            cancelTimer();
////                            timer.setText("" + String.format("%d:%d",
////                                    TimeUnit.MILLISECONDS.toMinutes(durationfortimer),
////                                    TimeUnit.MILLISECONDS.toSeconds(durationfortimer) -
////                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationfortimer))));
////                            timerstart(durationfortimer);
//                             durationfortimer = (mExoPlayerView.getPlayer().getDuration() - mExoPlayerView.getPlayer().getCurrentPosition());
//                            scrollingWidgetsAdapter.changeTimerView(4, durationfortimer, true);
//                        }
//                    }
//                    Bitmap bitmap = null;
//                    if (mExoPlayerView.getVideoSurfaceView() != null) {
//                        bitmap = ((TextureView) mExoPlayerView.getVideoSurfaceView()).getBitmap();
//                        Glide.with(getApplicationContext()).asBitmap().load(bitmap).into(frame);
//                    }

//                    loadPreview(mExoPlayerView.getPlayer().getCurrentPosition(),mExoPlayerView.getPlayer().getDuration());


//                    loadPreview(mExoPlayerView.getPlayer().getCurrentPosition(),mExoPlayerView.getPlayer().getDuration());
//                    Log.d("seekbaraction", "" + bitmap);

//                    frame.setImageBitmap(bitmap);
//                        loadPreview(mExoPlayerView.getPlayer().getCurrentPosition(), mExoPlayerView.getPlayer().getDuration())
//                    Log.d("bitmapcaptured", "" + bitmap);
                }
            });
        }
        if (player != null) {
            player.addAnalyticsListener(new AnalyticsListener() {
                @Override
                public void onAudioSessionId(@NotNull EventTime eventTime, int audioSessionId) {
                    try {
                        mEqualizer = new Equalizer(1000, audioSessionId);
                        mEqualizer.setEnabled(true);
                        sessionId = audioSessionId;

                    } catch (Exception e) {

                    }

                }

            });
        }
        //Repeat button for side menu
        repeatmenubutton.setOnClickListener(v ->

        {
            featuresmenu.setVisibility(View.GONE);
            menuIcon.setImageResource(R.drawable.ic_side_bar);
            if (mExoPlayerView.getPlayer().getRepeatMode() == mExoPlayerView.getPlayer().REPEAT_MODE_ONE) {
                mExoPlayerView.getPlayer().setRepeatMode(mExoPlayerView.getPlayer().REPEAT_MODE_OFF);
                repeatPlayerText.setTextColor(getResources().getColor(R.color.white));
                repeatPlayerImage.setImageResource(R.drawable.repeat);
            } else if (mExoPlayerView.getPlayer().getRepeatMode() == mExoPlayerView.getPlayer().REPEAT_MODE_OFF) {
                repeatPlayerText.setTextColor(getResources().getColor(R.color.orangeColor));
                repeatPlayerImage.setImageResource(R.drawable.ic_repeat_selected);
                mExoPlayerView.getPlayer().setRepeatMode(mExoPlayerView.getPlayer().REPEAT_MODE_ONE);
            }

        });
        //Getting the total area of touch screen
        mExoPlayerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->

        {
            if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                // Use smaller value to be consistent between screen orientations
                // (and to make usage easier)
                int width = right - left, height = bottom - top;
                maxGestureLength = (int) (Math.min(width, height) * MAX_GESTURE_LENGTH);
//                    setInitialGestureValues();
                seekbar.setMax(maxGestureLength);
                seekbar1.setMax(maxGestureLength);
                isfull = maxGestureLength;
                float currentVolume = audioManager.getStreamVolume(STREAM_MUSIC),
                        maxVolume = audioManager.getStreamMaxVolume(STREAM_MUSIC),
                        res = currentVolume / maxVolume;
                int progress = (int) (res * maxGestureLength);
                seekbar.setProgress(progress);
                if (tinyDB.getFloat("playerbrightness") > -1 && !isnightmode) {
                    WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                    layoutParams.screenBrightness = tinyDB.getFloat("playerbrightness");
                    getWindow().setAttributes(layoutParams);
                    seekbar1.setMax(maxGestureLength);
                    Log.d("brightnessseekbar", "oncreate-total-" + maxGestureLength);
                    seekbar1.setProgress(tinyDB.getInt("playerBrightnessSeekbarValue"));
                    Log.d("brightnessseekbar", "oncreate--" + tinyDB.getInt("playerBrightnessSeekbarValue"));
                    brightnesspercentText.setText(tinyDB.getString("textofbrightness"));
// vbview.setVisibility(View.VISIBLE);
// seekbar1.setVisibility(View.VISIBLE);
// brightnesspercentText.setVisibility(View.VISIBLE);
// titleprogress.setText("Brightness");
                }
                if (tinyDB.getInt("playervolume") > -1 && !muted) {
                    audioManager.setStreamVolume(STREAM_MUSIC, tinyDB.getInt("playervolume"), 0);
                }
            }
        });
        //To get the volume changed from the volume key pressed
        //Setting Content Observer
        SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(new Handler());
        this.
                getApplicationContext().
                getContentResolver().
                registerContentObserver(
                        Settings.System.CONTENT_URI, false,
                        mSettingsContentObserver);

        final MyGestureListener listener = new MyGestureListener();
        gestureDetector = new

                GestureDetector(this, listener);
        mExoPlayerView.setOnTouchListener(listener);
        //screen short button Action
        screenshortButton.setOnClickListener(v ->

        {
            featuresmenu.setVisibility(View.GONE);
            menuIcon.setImageResource(R.drawable.ic_side_bar);
            Permissions.check(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, null, new PermissionHandler() {

                @Override
                public void onGranted() {
                    takeSnapshot();
                }
            });
        });
//        mScaleGestureDetector = new ScaleGestureDetector(VideoPlayer.this, new ScaleListener());
        //Background play Feature
        backgroundplaybutton.setOnClickListener(v ->

        {
            featuresmenu.setVisibility(View.GONE);
            menuIcon.setImageResource(R.drawable.ic_side_bar);
            if (servicebackgroundplaystate) {
                playasaudioImage.setImageResource(R.drawable.ic_audio_ic);
                playasaudioText.setTextColor(getResources().getColor(R.color.white));
                servicebackgroundplaystate = false;
            } else {
                showAndAnimateControl(R.drawable.ic_audio_ic, true);
                playasaudioImage.setImageResource(R.drawable.ic_play_as_audio_selected);
                playasaudioText.setTextColor(getResources().getColor(R.color.orangeColor));
                servicebackgroundplaystate = true;
            }
        });
//        if (servicebackgroundplaystate) {
//            playasaudioImage.setImageResource(R.drawable.ic_play_as_audio_selected);
//            playasaudioText.setTextColor(getResources().getColor(R.color.orangeColor));
//            overLayToturial.setVisibility(View.GONE);
//            Log.d("servicerepeatmode", "" + servicerepeatmode);
//            if (servicerepeatmode == 1) {
//                mExoPlayerView.getPlayer().setRepeatMode(servicerepeatmode);
////                    repeatcontroll.setImageResource(R.drawable.ic_repeat_selected);
//                repeatPlayerText.setTextColor(getResources().getColor(R.color.orangeColor));
//                repeatPlayerImage.setImageResource(R.drawable.ic_repeat_selected);
//            } else {
////                    repeatcontroll.setImageResource(R.drawable.repeat_off);
//                repeatPlayerText.setTextColor(getResources().getColor(R.color.white));
//                repeatPlayerImage.setImageResource(R.drawable.ic_repeat_ic);
//            }
//        }
        //Current Volume
        currentPlayerVolume = audioManager.getStreamVolume(STREAM_MUSIC);
        Log.d("volumeofplayer", "" + currentPlayerVolume);
        //Mute Feature
        muteplayerbutton.setOnClickListener(v ->
        {
            featuresmenu.setVisibility(View.GONE);
            menuIcon.setImageResource(R.drawable.ic_side_bar);
            menuIcon.setImageResource(R.drawable.ic_side_bar);
            if (muted) {
                audioManager.setStreamVolume(STREAM_MUSIC, tinyDB.getInt("playervolume"), 0);
                mutePlayerImage.setImageResource(R.drawable.ic_mute_ic);
                mutePlayerText.setTextColor(getResources().getColor(R.color.white));
                muted = false;
            } else {
                iconAnimations.setImageResource(R.drawable.ic_mute_ic);
                showAndAnimateControl(R.drawable.ic_mute_player_popup, true);
                audioManager.setStreamVolume(STREAM_MUSIC, 0, 0);
                mutePlayerImage.setImageResource(R.drawable.ic_mute_player_selected);
                mutePlayerText.setTextColor(getResources().getColor(R.color.orangeColor));
                muted = true;
            }

        });

        preferenceslistener = (prefs, s) -> {
//            mExoPlayerView.getPlayer().setRepeatMode(prefs.getInt("repeatmode", 0));
//                if(servicebackgroundplaystate) {
//                    serviceplayerisplaying = prefs.getBoolean("serviceplayerisplaying", true);
//                }
            if (servicerepeatmode == 0)
                mExoPlayerView.getPlayer().setRepeatMode(prefs.getInt("servicerepeatmodehomebuttonCall", 0));
            if (getIntent().getFloatExtra("playerspeed", 1) == 1f) {
                float speed = prefs.getFloat("serviceplayerspeedhomebuttonCall", 1f);
                PlaybackParameters param = new PlaybackParameters(speed);
                if (mExoPlayerView.getPlayer() != null) {
                    mExoPlayerView.getPlayer().setPlaybackParameters(param);
                }
            }

            Log.d("servicerepeatmode", "repeatmode");
            overLayToturial.setVisibility(View.GONE);
            local = prefs.getBoolean("local", false);
            isServiceReturnedIndex = prefs.getBoolean("servicereturnedindex", false);
            boolean activitycalltosong = prefs.getBoolean("activitycalltosong", false);
            servicesongindex = prefs.getInt("PlayerCurrentSongIndex", 0);
            Boolean ispopup = getIntent().getBooleanExtra("ispopup", false);
            if (servicebackgroundplaystate || ispopup) {
                if (!local && !isstream) {
                    if (!activitycalltosong) {
                        mExoPlayerView.getPlayer().seekTo(servicesongindex, prefs.getLong("PlayerCurrentPosition", 0));
                        songname = getFileNameFromUrl(playlist.get(servicesongindex));
                    }
                    playlistmode = true;
                    titleSong.setText(songname);
                    mExoPlayerView.getPlayer().setPlayWhenReady(true);
                } else if (isstream) {
                    mExoPlayerView.getPlayer().seekTo(servicesongindex, prefs.getLong("PlayerCurrentPosition", 0));
                    songname = getFileNameFromUrl(url);
                    isstream = true;
                    titleSong.setText(songname);
                    mExoPlayerView.getPlayer().setPlayWhenReady(true);
                }

            } else {
                mExoPlayerView.getPlayer().setPlayWhenReady(false);
            }
            if (!activitycalltosong) {
                if (prefs.getLong("PlayerCurrentPosition", 0) != mExoPlayerView.getPlayer().getDuration()) {
                    mExoPlayerView.getPlayer().seekTo(prefs.getLong("PlayerCurrentPosition", 0));

                }
            } else {
                mExoPlayerView.getPlayer().seekTo(0);
            }
            if (!local) {
                mExoPlayerView.getPlayer().seekTo(prefs.getLong("PlayerCurrentPosition", 0));
            }
        };
        //overlay visibility on audio service
        if (toturialvisiablity && !servicebackgroundplaystate) {
            overLayToturial.setOnClickListener(v -> {

                overLayToturial.setVisibility(View.GONE);
                isFirst = false;
                mExoPlayerView.showController();
            });
        }
        if (servicebackgroundplaystate) {
            overLayToturial.setVisibility(View.GONE);
            if (mExoPlayerView.getPlayer() != null) {
                mExoPlayerView.getPlayer().setPlayWhenReady(true);
            }
        }


        //Menu settings
        menuIcon.setOnClickListener(v ->

        {
            if (featuresmenu.getVisibility() == View.VISIBLE) {
                featuresmenu.setVisibility(View.GONE);
                menuIcon.setImageResource(R.drawable.ic_side_bar);

            } else {
                featuresmenu.setVisibility(View.VISIBLE);
                menuIcon.setImageResource(R.drawable.ic_side_bar_selected);
            }
        });
        controlslayout.setOnClickListener(v ->

        {
            mExoPlayerView.setUseController(true);
            if (mExoPlayerView.isControllerVisible()) {
                mExoPlayerView.showController();
            }
        });
        //Back arrow pressed
        backarrow.setOnClickListener(v -> {
            onBackPressed();
        });


        //Pop-up feature
        popupbutton.setOnClickListener(v ->
        {
            featuresmenu.setVisibility(View.GONE);
            menuIcon.setImageResource(R.drawable.ic_side_bar);
            enterpipmode();
        });
        lockScreenReceiver = new
                LockScreenReceiver();
        IntentFilter lockFilter = new IntentFilter();
        lockFilter.addAction(Intent.ACTION_SCREEN_ON);
        lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
        lockFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(lockScreenReceiver, lockFilter);
//        com.code4rox.videoplayer.database.viewmodel.songviewmodel songviewmodel = new
//                ViewModelProvider(this).
//                get(com.code4rox.videoplayer.database.viewmodel.songviewmodel.class);
//        songviewmodel.getallsongs().
//                observe(this, new Observer<List<song_detail_table>>() {
//                    @Override
//                    public void onChanged(List<song_detail_table> song_detail_tables) {
//                        data = song_detail_tables;
//                    }
//                });
        scrollingwidgetview.addItemDecoration(new
                RecyclerViewDecorationClass());
        scrollingWidgetsAdapter = new
                ScrollingWidgetsAdapter(widgetslist);
        scrollingwidgetview.setAdapter(scrollingWidgetsAdapter);
        scrollingwidgetview.scrollToPosition(widgetslist.size() - 3);
        RecyclerView.EdgeEffectFactory edgeEffectFactory = new RecyclerView.EdgeEffectFactory() {
            @NonNull
            @Override
            protected EdgeEffect createEdgeEffect(@NonNull RecyclerView view, int direction) {
                EdgeEffect edgeEffect = new EdgeEffect(view.getContext());
                edgeEffect.setColor(ContextCompat.getColor(view.getContext(), R.color.grey_trans));
                return edgeEffect;
            }
        };
        scrollingwidgetview.setEdgeEffectFactory(edgeEffectFactory);
        // if (tinyDB.getFloat("playerbrightness") > -1) {
        //     WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //     layoutParams.screenBrightness = tinyDB.getFloat("playerbrightness");
        //     getWindow().setAttributes(layoutParams);
        //     seekbar1.setProgress(tinyDB.getInt("playerBrightnessSeekbarValue"));
        //     brightnesspercentText.setText(tinyDB.getString("textofbrightness"));
        // }
        if (tinyDB.getInt("playervolume") > -1) {
            audioManager.setStreamVolume(STREAM_MUSIC, tinyDB.getInt("playervolume"), 0);

        }
//        admobUtils = new AdmobUtils(VideoPlayer.this);
//        admobUtils.loadNativeAd (addview , R.layout.ad_unified, NativeAdsIdType.MM_NATIVE_AM);


        /////////////
        Log.d("playerspeed", "" + getIntent().getFloatExtra("playerspeed", 1f));
        if (getIntent().getFloatExtra("playerspeed", 1f) > 0) {
            float speed = getIntent().getFloatExtra("playerspeed", 1f);
            PlaybackParameters param = new PlaybackParameters(speed);
            if (mExoPlayerView.getPlayer() != null) {
                mExoPlayerView.getPlayer().setPlaybackParameters(param);
            }
        }
//        if(getIntent().getIntExtra("repeatmode",0)>=0)
//        {
//            int repeatmode=getIntent().getIntExtra("repeatmode",0);
//            if (mExoPlayerView.getPlayer() != null) {
//                mExoPlayerView.getPlayer().setRepeatMode(repeatmode);
//            }
//        }

    }

    void changediconorder(int position, int widget) {
        widgetslist.remove(position);
        widgetslist.remove(widgetslist.size() - 1);
        widgetslist.add(widget);
        widgetslist.add(R.drawable.ic_arrow_ic);
        scrollingWidgetsAdapter.notifyDataSetChanged();
    }

    void changedfordeactivation(int position, int widget) {
        if (widget == R.drawable.ic_rotate_ic) {
            int x = 0;
            for (int i = 0; i < widgetslist.size(); i++) {
                if (widgetslist.get(i) == R.drawable.ic_repeat_selected || widgetslist.get(i) == R.drawable.nightactive || widgetslist.get(i) == R.drawable.ic_shuffle_active) {
                    x++;
                }
            }
            if (x == 3) {
                widgetslist.remove(position);
                int indexofequalizer = widgetslist.indexOf(R.drawable.ic_mixer);
                if (indexofequalizer == 0) {
                    widgetslist.add(indexofequalizer + 2, widget);
                }
            } else if (x == 2) {
                widgetslist.remove(position);
                int indexofequalizer = widgetslist.indexOf(R.drawable.ic_mixer);
                if (indexofequalizer == 0) {
                    widgetslist.add(indexofequalizer + 3, widget);
                }
            } else if (x == 1) {
                widgetslist.remove(position);
                int indexofequalizer = widgetslist.indexOf(R.drawable.ic_mixer);
                if (indexofequalizer == 0) {
                    widgetslist.add(indexofequalizer + 4, widget);
                }
            } else {
                widgetslist.remove(position);
                int indexofarrow = widgetslist.indexOf(R.drawable.ic_arrow_ic);
                if (indexofarrow > 0) {
                    widgetslist.add(indexofarrow, widget);
                }
            }

        } else {
            boolean rotateicon = false;
            int x = 0;
            for (int i = 0; i < widgetslist.size(); i++) {
                if (widgetslist.get(i) == R.drawable.ic_rotat_active) {
                    rotateicon = true;
                }
                if (widgetslist.get(i) == R.drawable.ic_repeat_selected || widgetslist.get(i) == R.drawable.nightactive || widgetslist.get(i) == R.drawable.ic_shuffle_active) {
                    x++;
                }
            }
            if (rotateicon) {
                if (x == 3) {
                    widgetslist.remove(position);
                    int indexofequalizer = widgetslist.indexOf(R.drawable.ic_mixer);
                    if (indexofequalizer == 0) {
                        widgetslist.add(indexofequalizer + 2, widget);
                    }
                } else if (x == 2) {
                    widgetslist.remove(position);
                    int indexofequalizer = widgetslist.indexOf(R.drawable.ic_mixer);
                    if (indexofequalizer == 0) {
                        widgetslist.add(indexofequalizer + 3, widget);
                    }
                } else if (x == 1) {
                    widgetslist.remove(position);
                    int indexofequalizer = widgetslist.indexOf(R.drawable.ic_mixer);
                    if (indexofequalizer == 0) {
                        widgetslist.add(indexofequalizer + 4, widget);
                    }
                } else {
                    widgetslist.remove(position);
                    int indexofarrow = widgetslist.indexOf(R.drawable.ic_arrow_ic);
                    if (indexofarrow > 0) {
                        widgetslist.add(indexofarrow, widget);
                    }
                }
            } else {
                widgetslist.remove(position);
                int indexofrotation = widgetslist.indexOf(R.drawable.ic_rotate_ic);
                int indexofrotationactive = widgetslist.indexOf(R.drawable.ic_rotat_active);
                if (indexofrotation > 0) {
                    widgetslist.add(indexofrotation - 1, widget);
                }
            }
        }
        scrollingWidgetsAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScrollingWidgetsAdapter.MessageEvent event) {
        widgetid = event.imageReference;
        itemPosition = event.position;
        if (widgetid == R.drawable.ic_rotate_ic || widgetid == R.drawable.ic_rotat_active) {
            if (mExoPlayerFullscreen) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                changedfordeactivation(itemPosition, R.drawable.ic_rotate_ic);
                mExoPlayerFullscreen = false;
                hideSystemUI();
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                changediconorder(itemPosition, R.drawable.ic_rotat_active);
                showSystemUi();
                mExoPlayerFullscreen = true;
            }
        } else if (widgetid == R.drawable.ic_repeat_ic || widgetid == R.drawable.ic_repeat_selected) {
            if (mExoPlayerView.getPlayer() != null) {
                if (mExoPlayerView.getPlayer().getRepeatMode() == mExoPlayerView.getPlayer().REPEAT_MODE_ONE) {
                    mExoPlayerView.getPlayer().setRepeatMode(mExoPlayerView.getPlayer().REPEAT_MODE_OFF);
                    repeatPlayerText.setTextColor(getResources().getColor(R.color.white));
                    repeatPlayerImage.setImageResource(R.drawable.ic_repeat_ic);
                    changedfordeactivation(itemPosition, R.drawable.ic_repeat_ic);
                } else if (mExoPlayerView.getPlayer().getRepeatMode() == mExoPlayerView.getPlayer().REPEAT_MODE_OFF) {
                    mExoPlayerView.getPlayer().setRepeatMode(mExoPlayerView.getPlayer().REPEAT_MODE_ONE);
                    repeatPlayerText.setTextColor(getResources().getColor(R.color.orangeColor));
                    repeatPlayerImage.setImageResource(R.drawable.ic_repeat_selected);
                    changediconorder(itemPosition, R.drawable.ic_repeat_selected);
                }
            }
        } else if (widgetid == R.drawable.ic_mixer) {
            if (mExoPlayerView.getPlayer() != null && mExoPlayerView.getPlayer().getAudioComponent() != null) {
                equalizerFragment = EqualizerFragment.newBuilder()
                        .setAccentColor(Color.parseColor("#4caf50"))
                        .setAudioSessionId(sessionId)
                        .build();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.eqFrame, equalizerFragment)
                        .addToBackStack("equalizer")
                        .commit();
            }
        } else if (widgetid == R.drawable.ic_shuffle_ic || widgetid == R.drawable.ic_shuffle_active) {
            if (mExoPlayerView.getPlayer() != null) {
                if (mExoPlayerView.getPlayer().getShuffleModeEnabled()) {
                    mExoPlayerView.getPlayer().setShuffleModeEnabled(false);
                    changedfordeactivation(itemPosition, R.drawable.ic_shuffle_ic);
                } else {
                    mExoPlayerView.getPlayer().setShuffleModeEnabled(true);
                    changediconorder(itemPosition, R.drawable.ic_shuffle_active);

                }
            }
        } else if (widgetid == R.drawable.ic_share) {
            if (local || isstream) {
                shareVideo(url);
            } else {
                if (mExoPlayerView.getPlayer() != null) {
                    shareVideo(playlist.get(mExoPlayerView.getPlayer().getCurrentWindowIndex()));
                }
            }
        } else if (widgetid == R.drawable.ic_night_ic || widgetid == R.drawable.nightactive) {
            if (!isnightmode) {
                layoutParams.screenBrightness = 0.1f;
                getWindow().setAttributes(layoutParams);
                isnightmode = true;
                Toast.makeText(getApplicationContext(), "Night Mode On", Toast.LENGTH_LONG).show();
                changediconorder(itemPosition, R.drawable.nightactive);
            } else {
                layoutParams.screenBrightness = tinyDB.getFloat("playerbrightness");
                getWindow().setAttributes(layoutParams);
                isnightmode = false;
                Toast.makeText(getApplicationContext(), "Night Mode Off", Toast.LENGTH_LONG).show();
                changedfordeactivation(itemPosition, R.drawable.ic_night_ic);
            }
        } else if (widgetid == R.drawable.ic_arrow_ic) {
            scrollingwidgetview.scrollToPosition(scrollingWidgetsAdapter.getItemCount() - 7);
        }
    }

    public void timerstart(long selectedItem) {
        cancelTimer();
        long timeinmili;
        if (selectedItems != 5) {
            timeinmili = selectedItem * 60000;
        } else {
            timeinmili = selectedItem;
        }
        timers = new CountDownTimer(timeinmili, 1000) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished != 0) {
                    int positionoftimer = 0;
                    if (widgetid == R.drawable.ic_time_ic)
                        positionoftimer = widgetslist.indexOf(R.drawable.ic_time_ic);
                    scrollingWidgetsAdapter.changeTimerView(positionoftimer, millisUntilFinished, true);
                    Log.d("timerstatus", "insidetimer");
                }
            }

            public void onFinish() {
                finish();
            }
        }.start();
    }

    private void cancelTimer() {
        if (timers != null) {
            if (widgetid == R.drawable.ic_time_ic) {
                timers.cancel();
                int positionoftimer = widgetslist.indexOf(R.drawable.ic_time_ic);
                scrollingWidgetsAdapter.changeTimerView(positionoftimer, 0, false);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("pipmode", "onnewintent");
        if (mExoPlayerView.getPlayer() != null) {
            mExoPlayerView.getPlayer().release();
        }
        Uri uri;
        if (getIntent().getDataString() != null) {
            url = getIntent().getDataString();
            uri = Uri.parse(url);
            if (Objects.requireNonNull(uri.getScheme()).equals("content")) {
                @SuppressLint("Recycle") Cursor returnCursor =
                        getContentResolver().query(uri, null, null, null, null);
                int nameIndex = 0;
                if (returnCursor != null) {
                    nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                }
                if (returnCursor != null) {
                    returnCursor.moveToFirst();
                }
                if (returnCursor != null) {
                    songname = returnCursor.getString(nameIndex);
                }
                titleSong.setText(songname);
                local = true;
            }
        } else if (isstream) {
            url = intent.getStringExtra("url");
            songname = getFileNameFromUrl(url);
            titleSong.setText(songname);
        } else {
            urlindex = intent.getIntExtra("urlindex", 0);
            Log.d("urlindex", "" + urlindex);
            playlist = intent.getStringArrayListExtra("playlist");
            songname = getFileNameFromUrl(playlist.get(urlindex));
            playlistmode = true;
            titleSong.setText(songname);

        }

        requestvideo(url);
        initExoPlayer();
    }

    public void enterpipmode() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (servicebackgroundplaystate) {
                    VideoPlayer.this.getMaxNumPictureInPictureActions();
                    PictureInPictureParams.Builder pictureInPictureParamsbuilder = new PictureInPictureParams.Builder();
                    Rational aspectratio = new Rational(2, 1);
                    pictureInPictureParamsbuilder.setAspectRatio(aspectratio);
                    VideoPlayer.this.enterPictureInPictureMode(pictureInPictureParamsbuilder.build());
                    playasaudioImage.setImageResource(R.drawable.ic_audio_ic);
                    playasaudioText.setTextColor(getResources().getColor(R.color.white));
                    servicebackgroundplaystate = false;
                    ispopupenable = true;
                } else {
                    PictureInPictureParams.Builder pictureInPictureParamsbuilder = new PictureInPictureParams.Builder();
                    Rational aspectratio = new Rational(2, 1);
                    pictureInPictureParamsbuilder.setAspectRatio(aspectratio);
                    VideoPlayer.this.enterPictureInPictureMode(pictureInPictureParamsbuilder.build());
                    ispopupenable = true;
                }
            }

        } else {
            if (PermissionManager.isPopupEnabled(VideoPlayer.this)) {
                if (servicebackgroundplaystate) {
                    ispopupenable = true;
                    servicebackgroundplaystate = false;
                    startVideoBackgroundService();
                    finish();
                } else {
                    stopService(new Intent(VideoPlayer.this, BackgroundPlayService.class));
                    tinyDB.putBoolean("BackgroundPlaySetting", false);
                    ispopupenable = true;
                    startVideoBackgroundService();
                    finish();
                }

            } else {
                Toast.makeText(VideoPlayer.this, "Pop Up Permission Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selectedItems = 0;  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayer.this);
        // Set the dialog title
        builder.setTitle("Select Timer")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(R.array.toppings, 0,
                        (dialog, which) -> {
                            // If the user checked the item, add it to the selected items
                            selectedItems = which;
                            switch (selectedItems) {
                                case 0:
                                    cancelTimer();
                                    timerSelectDuration = false;
                                    break;
                                case 1:

                                    timerstart(15);
                                    timerSelectDuration = false;
                                    Log.d("timerlog", "15");

                                    break;
                                case 2:

                                    timerstart(30);
                                    timerSelectDuration = false;

                                    break;
                                case 3:

                                    timerstart(45);
                                    timerSelectDuration = false;

                                    break;
                                case 4:

                                    timerstart(60);
                                    timerSelectDuration = false;

                                    break;
                                case 5:
                                    timerSelectDuration = true;

                                    long durationfortimer = (mExoPlayerView.getPlayer().getDuration() - mExoPlayerView.getPlayer().getCurrentPosition());
                                    timerstart(durationfortimer);

                                    if (activityplayerisplaying) {
                                    } else {
                                        cancelTimer();

                                    }
                                    break;
                                default:


                            }
                            dialog.dismiss();

                        });
        return builder.create();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // If the event was not handled then see if the player view can handle it.
        return super.dispatchKeyEvent(event) || mExoPlayerView.dispatchKeyEvent(event);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration
            newConfig) {
        if (isInPictureInPictureMode) {

            mExoPlayerView.setUseController(false);
            mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            newConfig.orientation = ORIENTATION_LANDSCAPE;
            // Hide the full-screen UI (controls, etc.) while in picture-in-picture mode.
        } else {
            // Restore the full-screen UI.
            mExoPlayerView.setUseController(true);
            mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

        }
    }

    public String getFileNameFromUrl(String path) {
        String[] pathArray = path.split("/");
        return pathArray[pathArray.length - 1];
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void startVideoBackgroundService() {
        tinyDB.putBoolean("BackgroundPlaySetting", true);
        Intent datasending = new Intent(VideoPlayer.this, BackgroundPlayService.class);
        datasending.putExtra("name", songname);
        datasending.putExtra("Activityplyerstate", activityplayerisplaying);

        if (mExoPlayerView.getPlayer() != null) {
            datasending.putExtra("currentActvityposition", mExoPlayerView.getPlayer().getCurrentPosition());
            datasending.putExtra("sufflemode", mExoPlayerView.getPlayer().getShuffleModeEnabled());
            datasending.putExtra("repeatemode", mExoPlayerView.getPlayer().getRepeatMode());
        }
        datasending.putExtra("toturialoverlay", false);
        datasending.putExtra("playerspeed", mExoPlayerView.getPlayer().getPlaybackParameters().speed);
        if (local) {
            datasending.putExtra("localvideo", true);
            datasending.putExtra("url", url);
        } else if (isstream) {
            datasending.putExtra("isstream", true);
            datasending.putExtra("url", url);
        } else {
            datasending.putStringArrayListExtra("playlist", playlist);
            datasending.putExtra("windowindex", mExoPlayerView.getPlayer().getCurrentWindowIndex());
        }
        if (servicebackgroundplaystate && mExoPlayerView.getPlayer().getPlaybackState() != mExoPlayerView.getPlayer().STATE_ENDED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(datasending);
            } else {
                startService(datasending);
            }
            mExoPlayerView.getPlayer().setPlayWhenReady(false);

        } else if (ispopupenable && mExoPlayerView.getPlayer().getPlaybackState() != mExoPlayerView.getPlayer().STATE_ENDED) {
            datasending.putExtra("localvideo", local);
            if (ispopupenable) {
                datasending.putExtra("ispopenable", true);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(datasending);
            } else {
                startService(datasending);
            }
            mExoPlayerView.getPlayer().setPlayWhenReady(false);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            overlaydissmisDialog.setText("Got it,\nDismiss this");
            mExoPlayerFullscreen = false;
            scrollingwidgetview.setAdapter(scrollingWidgetsAdapter);
            scrollingwidgetview.scrollToPosition(widgetslist.size() - 3);
            showSystemUi();

        } else {
            overlaydissmisDialog.setText(getResources().getString(R.string.dissmiss_Text));
            scrollingwidgetview.setAdapter(scrollingWidgetsAdapter);
            scrollingwidgetview.scrollToPosition(widgetslist.size() - 2);
            mExoPlayerFullscreen = true;
            hideSystemUI();

        }

    }

    void takeSnapshot() {

        Bitmap bitmap = null;
        if (mExoPlayerView.getVideoSurfaceView() != null) {
            bitmap = ((TextureView) mExoPlayerView.getVideoSurfaceView()).getBitmap();
        }
        try {
            String folder_main = "Media Player";
            String folder_absolute_path = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + "/" + folder_main;

            File f2 = new File(folder_absolute_path);
            boolean isDirectoryCreated = f2.exists();
            if (!isDirectoryCreated) {
                isDirectoryCreated = f2.mkdirs();
            }
            if (isDirectoryCreated) {
                // do something
            }

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd_HH-mm-ss");
            Date whatismytime = Calendar.getInstance().getTime();
            String strDate = sdf.format(whatismytime);

            File file = new File(folder_absolute_path, strDate + ".jpg");

            OutputStream fOut;
            fOut = new FileOutputStream(file);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            }
            fOut.flush();
            fOut.close();
            Runnable runnable = () -> Toast.makeText(VideoPlayer.this, "Snapshot saved !!", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(runnable, 150);
            refreshAndroidGallery(Uri.fromFile(file));
        } catch (Exception ex) {
            Toast.makeText(VideoPlayer.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void refreshAndroidGallery(Uri fileUri) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(fileUri);
        VideoPlayer.this.sendBroadcast(mediaScanIntent);
    }

    private void showSystemUi() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {

            if (mExoPlayerView.getPlayer() != null) {
                if (mExoPlayerView.getPlayer().getPlaybackState() == mExoPlayerView.getPlayer().STATE_READY) {
                    mExoPlayerView.getPlayer().setPlayWhenReady(false);

                } else {
                    mExoPlayerView.getPlayer().setPlayWhenReady(true);
                }
            }

        }

        return super.onKeyDown(keyCode, event);
    }

    private void initExoPlayer() {
        if (PermissionManager.checkReadStoragePermissions(VideoPlayer.this, 0)) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            if (playlistmode) {
                player.prepare(concatenatingMediaSource);
            } else {
                player.prepare(mVideoSource);
            }
            mExoPlayerView.setPlayer(player);
            MediaSessionCompat mediaSession = new MediaSessionCompat(VideoPlayer.this, MEDIA_SESSION_TAG);
            MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
            if (playlistmode) {
                player.seekTo(urlindex, C.TIME_UNSET);
            }
            mediaSessionConnector.setPlayer(player);
            mediaSession.setActive(true);
            mExoPlayerView.hideController();

            mExoPlayerView.setControllerHideOnTouch(true);
            if (mExoPlayerView.getPlayer() != null) {
                mExoPlayerView.getPlayer().setShuffleModeEnabled(false);
            }
            if (getIntent().getBooleanExtra("shufflemode", false)) {
                mExoPlayerView.getPlayer().setShuffleModeEnabled(true);
            }
            if (servicebackgroundplaystate) {
                player.setPlayWhenReady(false);
            } else {
                if (tinyDB.getBoolean("ResumeSetting") && !singleactivitycall && songisindatabase) {
                    player.setPlayWhenReady(false);

                } else
                    player.setPlayWhenReady(true);

            }

        } else {
            Toast.makeText(getApplicationContext(), "Storage Permission Required", Toast.LENGTH_SHORT).show();
        }
        if (tinyDB.getBoolean("AspectRatioSetting")) {
            mExoPlayerView.setResizeMode(tinyDB.getInt("AspectRatioMode"));
            switch (mExoPlayerView.getResizeMode()) {
                case AspectRatioFrameLayout.RESIZE_MODE_FIT:
                    aspectratiobutton1.setBackgroundResource(R.drawable.ic_zoom_ic);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_ZOOM:
                    aspectratiobutton1.setBackgroundResource(R.drawable.ic_100);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_FILL:
                    aspectratiobutton1.setBackgroundResource(R.drawable.ic_crop);
                    break;
                default:
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT:
                case AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH:
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Enable the Aspect Ratio in Setting ", Toast.LENGTH_LONG).show();
        }
    }

    public void shareVideo(String path) {

        if (!isstream) {
            File videoFile = new File(path);
            Uri videoURI = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    ? FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    this.getPackageName() + ".provider", videoFile)
                    : Uri.fromFile(videoFile);
            ShareCompat.IntentBuilder.from(VideoPlayer.this)
                    .setStream(videoURI)
                    .setType("video/mp4")
                    .setChooserTitle("Share video...")
                    .startChooser();
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_TEXT, url);
            startActivity(Intent.createChooser(intent, "Share"));

        }
    }

    public void requestvideo(String url) {

        if (playlistmode) {
            //For Playlist running
            concatenatingMediaSource = new ConcatenatingMediaSource();
            for (int i = 0; i < playlist.size(); i++) {
                mVideoSource = buildMediaSource(Uri.parse(playlist.get(i)));
                concatenatingMediaSource.addMediaSource(mVideoSource);

            }
        } else {
            if (url != null) {
                Uri daUri = Uri.parse(url);
                mVideoSource = buildMediaSource(daUri);
            }
        }
    }

    private void initializeElements() {
        mExoPlayerView = findViewById(R.id.exoplayer);
        controlView = mExoPlayerView.findViewById(R.id.exo_controller);
        loading_parent_layout = findViewById(R.id.loading_parent_layout);
        brightnesspercentText = findViewById(R.id.brightnesspercentagetext);
        volumepercentText = findViewById(R.id.volumepercentagetext);
        seekbar = findViewById(R.id.circularSeekBar1);
        seekbar1 = findViewById(R.id.circularSeekBar2);
        loading = findViewById(R.id.loading);
        vbview = findViewById(R.id.volumeBrightnessContainer);
        menuIcon = findViewById(R.id.menuIcon);

        screenshortButton = findViewById(R.id.exo_screenshort);
        controlAnimationView = findViewById(R.id.controlAnimationView);
        aspectratiobutton1 = findViewById(R.id.aspectratiobutton1);
        aspectratiolayout = findViewById(R.id.main_media_frame);
        swapduration = findViewById(R.id.durationtime);
        swapupdatevalue = findViewById(R.id.swappoints);
        speed = findViewById(R.id.speedbutton);
        swaptimerrootview = findViewById(R.id.timerUpdaterootview);
        backgroundplaybutton = findViewById(R.id.backgroundplay);
        overLayToturial = findViewById(R.id.overlayToturial);
        overLayToturialTitle = findViewById(R.id.titleToturial);
        titleprogress = findViewById(R.id.titleprogress);
        //menu icons
        iconAnimations = findViewById(R.id.iconAnimations);
        featuresmenu = findViewById(R.id.featuresmenu);
        featuresmenu.setZ(1);
        playbackSpeedText = findViewById(R.id.playbackSpeedText);
        exo_equalizer = findViewById(R.id.exo_equalizer);
        playasaudioImage = findViewById(R.id.playasaudioImage);
        playasaudioText = findViewById(R.id.playasaudioText);
        muteplayerbutton = findViewById(R.id.muteplayerbutton);
        mutePlayerImage = findViewById(R.id.mutePlayerImage);
        mutePlayerText = findViewById(R.id.mutePlayerText);
        repeatPlayerImage = findViewById(R.id.repeatPlayerImage);
        repeatPlayerText = findViewById(R.id.repeatPlayerText);
        lockLayout = findViewById(R.id.lockLayout);
        lockicon = findViewById(R.id.lockicon);
        exo_screenlock = findViewById(R.id.exo_screenlock);
        repeatmenubutton = findViewById(R.id.repeatmenubutton);
        popupbutton = findViewById(R.id.popupbutton);
        //toolbar
        backarrow = findViewById(R.id.backarrow);
        titleSong = findViewById(R.id.titleSong);
        scrollingwidgetview = findViewById(R.id.floatingviewscontainer);


    }


    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(this, getApplicationContext().getApplicationInfo().packageName);
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(VideoPlayer.this, null, httpDataSourceFactory);
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

    public void showAndAnimateControl(final int drawableId, final boolean goneOnEnd) {
        if (controlViewAnimator != null && controlViewAnimator.isRunning()) {
            controlViewAnimator.end();
        }

        if (drawableId == -1) {
            if (controlAnimationView.getVisibility() == View.VISIBLE) {
                controlViewAnimator = ObjectAnimator.ofPropertyValuesHolder(controlAnimationView,
                        PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f),
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1.4f, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.4f, 1f)
                ).setDuration(DEFAULT_CONTROLS_DURATION);
                controlViewAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        controlAnimationView.setVisibility(View.GONE);
                    }
                });
                controlViewAnimator.start();
            }
            return;
        }

        float scaleFrom = goneOnEnd ? 1f : 1f, scaleTo = goneOnEnd ? 1.8f : 1.4f;
        float alphaFrom = goneOnEnd ? 1f : 0f, alphaTo = goneOnEnd ? 0f : 1f;


        controlViewAnimator = ObjectAnimator.ofPropertyValuesHolder(controlAnimationView,
                PropertyValuesHolder.ofFloat(View.ALPHA, alphaFrom, alphaTo),
                PropertyValuesHolder.ofFloat(View.SCALE_X, scaleFrom, scaleTo),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleFrom, scaleTo)
        );
        controlViewAnimator.setDuration(goneOnEnd ? 1000 : 500);
        controlViewAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (goneOnEnd) controlAnimationView.setVisibility(View.GONE);
                else controlAnimationView.setVisibility(View.VISIBLE);
            }
        });


        controlAnimationView.setVisibility(View.VISIBLE);
        controlAnimationView.setImageDrawable(ContextCompat.getDrawable(VideoPlayer.this, drawableId));
        controlViewAnimator.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences.registerOnSharedPreferenceChangeListener(preferenceslistener);
        servicerepeatmode = getIntent().getIntExtra("repeatmode", 0);
        Log.d("lifecycle", "Resume pressed");
        if (checkServiceRunning(BackgroundPlayService.class)) {
            stopService(new Intent(VideoPlayer.this, BackgroundPlayService.class));
            tinyDB.putBoolean("BackgroundPlaySetting", false);
        }
        if (servicebackgroundplaystate) {
            if (mExoPlayerView.getPlayer() != null) {
                mExoPlayerView.getPlayer().setPlayWhenReady(true);
            }
        } else {
            if (activityplayerisplaying)
                mExoPlayerView.getPlayer().setPlayWhenReady(true);
        }
        if (servicebackgroundplaystate || getIntent().getBooleanExtra("ispopup", false)) {
            if (!getIntent().getBooleanExtra("ispopup", false)) {
                playasaudioImage.setImageResource(R.drawable.ic_play_as_audio_selected);
                playasaudioText.setTextColor(getResources().getColor(R.color.orangeColor));
            }
            overLayToturial.setVisibility(View.GONE);
            Log.d("servicerepeatmode", "" + servicerepeatmode);
            if (servicerepeatmode == 1) {
                mExoPlayerView.getPlayer().setRepeatMode(servicerepeatmode);
//                    repeatcontroll.setImageResource(R.drawable.ic_repeat_selected);
                repeatPlayerText.setTextColor(getResources().getColor(R.color.orangeColor));
                repeatPlayerImage.setImageResource(R.drawable.ic_repeat_selected);
            } else {
//                    repeatcontroll.setImageResource(R.drawable.repeat_off);
                repeatPlayerText.setTextColor(getResources().getColor(R.color.white));
                repeatPlayerImage.setImageResource(R.drawable.ic_repeat_ic);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("lifecycle", "pause pressed");
        if (!ispopupenable && servicebackgroundplaystate) {
            if (!checkServiceRunning(BackgroundPlayService.class)) {
                startVideoBackgroundService();
            }
        }
        if (mExoPlayerView.getPlayer() != null) {
            if (!ispopupenable) {
                if (activityplayerisplaying) {
                    mExoPlayerView.getPlayer().setPlayWhenReady(false);
                    activityplayerisplaying = true;
                } else {
                    activityplayerisplaying = false;
                }
            }

        }
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn = false;
        if (powerManager != null) {
            isScreenOn = powerManager.isInteractive();
        }
        if (!isScreenOn) {
            mExoPlayerView.getPlayer().setPlayWhenReady(false);
            // The screen has been locked
            // do stuff...
        }
        if (featuresmenu.getVisibility() == View.VISIBLE) {
            featuresmenu.setVisibility(View.GONE);
        }
        if (tinyDB.getBoolean("AspectRatioSetting")) {
            tinyDB.putInt("AspectRatioMode", mExoPlayerView.getResizeMode());

        }
    }


    public boolean checkServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isInPictureInPictureMode()) {
                if (activityplayerisplaying)
                    if (mExoPlayerView.getPlayer() != null) {
                        mExoPlayerView.getPlayer().setPlayWhenReady(true);
                    }
            } else {
                if (ispopupenable) {
                    if (mExoPlayerView.getPlayer() != null) {
                        mExoPlayerView.getPlayer().setPlayWhenReady(false);
                    }
                    ispopupenable = false;
                }
            }
        }
    }

    public int getsizeoffile(String url) {
        int file_size = Integer.parseInt(String.valueOf(new File(url).length() / 1024));
        return file_size;
    }

    public void songcheck() {

        if (playlistmode) {
            songdetail = new song_detail_table();
            if (mExoPlayerView.getPlayer() != null) {
                songdetail = songdatabase.getDatabase(getApplicationContext()).songdao().get(playlist.get(mExoPlayerView.getPlayer().getCurrentWindowIndex()));
                if (songdetail == null) {
                    song_detail_table song_detail = new song_detail_table();
                    song_detail.setSong_url(playlist.get(mExoPlayerView.getPlayer().getCurrentWindowIndex()));
                    song_detail.setSong_duration(mExoPlayerView.getPlayer().getDuration());
                    song_detail.setSong_played_duration(mExoPlayerView.getPlayer().getCurrentPosition());
                    song_detail.setSong_size(getsizeoffile(playlist.get(mExoPlayerView.getPlayer().getCurrentWindowIndex())));
                    songdatabase.getDatabase(getApplicationContext()).songdao().insert(song_detail);
                } else {
                    songdatabase.getDatabase(getApplicationContext()).songdao().update(songdetail.getSong_id(), mExoPlayerView.getPlayer().getCurrentPosition());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        songcheck();
        if (mExoPlayerView.getPlayer() != null) {
            mExoPlayerView.getPlayer().release();
            mExoPlayerView.setPlayer(null);

        }
        preferences.edit().clear().apply();
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceslistener);
        unregisterReceiver(lockScreenReceiver);
        tinyDB.putFloat("playerspeed", 1f);
        cancelTimer();

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onInterstitialAdClose() {
        super.onBackPressed();
    }

    @Override
    public void onInterstitialAdLoaded() {

    }

    @Override
    public void onInterstitialAdFailed() {

    }

    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        private static final int MOVEMENT_THRESHOLD = 40;
        boolean isMoving = false, ishoriMoving = false;
        boolean isVolumeGestureEnabled = true;

        @Override
        public boolean onDown(MotionEvent event) {
            if (mExoPlayerView.getPlayer() != null) {
                position = mExoPlayerView.getPlayer().getCurrentPosition();
                if (mExoPlayerFullscreen) {
                    hideSystemUI();
                } else {
                    showSystemUi();
                }
            }
            featuresmenu.setVisibility(View.GONE);
            menuIcon.setImageResource(R.drawable.ic_side_bar);
            return super.onDown(event);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            vbview.setVisibility(View.GONE);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            vbview.setVisibility(View.GONE);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (tinyDB.getBoolean("DoubleTapSetting")) {
                if (e.getRawX() > mExoPlayerView.getWidth() / 2) {
                    if (mExoPlayerView.getPlayer() != null) {
                        if (mExoPlayerView.getPlayer().getCurrentPosition() < (mExoPlayerView.getPlayer().getDuration() - 10000)) {
                            mExoPlayerView.getPlayer().seekTo(mExoPlayerView.getPlayer().getCurrentPosition() + 10000);
                            onFastForward();
                        } else {
                            mExoPlayerView.getPlayer().seekTo(mExoPlayerView.getPlayer().getDuration());
                        }
                    }
                } else {
                    if (mExoPlayerView.getPlayer() != null) {
                        if (mExoPlayerView.getPlayer().getCurrentPosition() > 10000) {
                            mExoPlayerView.getPlayer().seekTo(mExoPlayerView.getPlayer().getCurrentPosition() - 10000);
                            onFastRewind();
                        } else {
                            mExoPlayerView.getPlayer().seekTo(0);
                        }
                    }
                }
            }
            mExoPlayerView.hideController();
            return true;
        }

        void onFastRewind() {
            showAndAnimateControl(R.drawable.ic_rewind, true);
        }

        void onFastForward() {
            showAndAnimateControl(R.drawable.ic_forward, true);
        }

        String timeConverter(long duration) {
            String hms;
            if (duration > 3599999) {
                hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                        TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                        TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                return hms;
            } else {
                hms = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
            }
            return hms;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public boolean onScroll(MotionEvent initialEvent, MotionEvent movingEvent, float distanceX, float distanceY) {

            Log.d("distanceCalculation:", "distanceX:" + Math.abs(distanceX) + "distanceY" + Math.abs(distanceY) + "" + isMoving);
            final boolean insideThreshold = Math.abs(movingEvent.getY() - initialEvent.getY()) <= MOVEMENT_THRESHOLD;
            Log.d("count", "" + count);
            if (count == 0 && firsttime) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    count++;
                } else {
                    count = 0;
                }
                firsttime = false;

            }
            if (count > 0) {
                if (mExoPlayerView.getPlayer() != null) {
                    totalduration = mExoPlayerView.getPlayer().getDuration();
                }
                ishoriMoving = true;
                if (distanceX > 0) {
                    if (mExoPlayerView.getPlayer() != null) {
                        if (mExoPlayerView.getPlayer().getCurrentPosition() < 1000) {
                            if (activityplayerisplaying) {
                                mExoPlayerView.getPlayer().setPlayWhenReady(true);
                            } else {
                                mExoPlayerView.getPlayer().setPlayWhenReady(false);
                            }

                            swapduration.setText(timeConverter(0));
                            mExoPlayerView.getPlayer().seekTo(0);

                        } else {
                            mExoPlayerView.getPlayer().seekTo(mExoPlayerView.getPlayer().getCurrentPosition() - 400);
                            swapduration.setText(timeConverter(mExoPlayerView.getPlayer().getCurrentPosition()));
                        }
                    }

                } else {
                    if (mExoPlayerView.getPlayer() != null) {
                        if (mExoPlayerView.getPlayer().getCurrentPosition() >= totalduration) {
                            swapduration.setText(timeConverter(totalduration));

                        } else {
                            mExoPlayerView.getPlayer().seekTo(mExoPlayerView.getPlayer().getCurrentPosition() + 400);
                            swapduration.setText(timeConverter(mExoPlayerView.getPlayer().getCurrentPosition()));
                        }
                    }
                }
                long updatingpoints;
                if (mExoPlayerView.getPlayer() != null) {
                    if (mExoPlayerView.getPlayer().getCurrentPosition() < position) {
                        updatingpoints = position - mExoPlayerView.getPlayer().getCurrentPosition();
                        swapupdatevalue.setText("[" + "-" + timeConverter(updatingpoints) + "]");
                    } else {
                        updatingpoints = mExoPlayerView.getPlayer().getCurrentPosition() - position;
                        swapupdatevalue.setText("[" + "+" + timeConverter(updatingpoints) + "]");
                    }
                }
                if (swaptimerrootview.getVisibility() != View.VISIBLE) {
                    animateView(swaptimerrootview, SCALE_AND_ALPHA, true, 200);
                }
                swaptimerrootview.setVisibility(View.VISIBLE);
                mExoPlayerView.showController();
            } else if (insideThreshold) {
                return false;
            } else {
                mExoPlayerView.hideController();
                swaptimerrootview.setVisibility(View.INVISIBLE);
                titleprogress.setText("Volume");
                isMoving = true;
                boolean acceptVolumeArea = initialEvent.getX() > mExoPlayerView.getWidth() / 2;
                boolean acceptBrightnessArea = !acceptVolumeArea;
                if (isVolumeGestureEnabled && acceptVolumeArea) {
                    if (distanceY > 0) {
                        if (seekbar.getProgress() >= maxGestureLength) {
                            seekbar.setProgress(isfull);

                        } else {
                            seekbar.setProgress(seekbar.getProgress() + (int) distanceY);
                        }
                    } else {
                        if (seekbar.getProgress() <= 1) {
                            seekbar.setProgress(isempty);
                        } else {
                            seekbar.setProgress(seekbar.getProgress() + (int) distanceY);

                        }
                    }
                    float currentProgressPercent =
                            (float) seekbar.getProgress() / maxGestureLength;//second part is get max gesture length
                    volumepercentText.setText("" + (int) (Math.abs(currentProgressPercent) * audioManager.getStreamMaxVolume(STREAM_MUSIC)));

                    int currentVolume = (int) (audioManager.getStreamMaxVolume(STREAM_MUSIC) * currentProgressPercent);
                    if (currentVolume <= 0) {
                        mutePlayerImage.setImageResource(R.drawable.ic_mute_player_selected);
                        mutePlayerText.setTextColor(getResources().getColor(R.color.orangeColor));
                        muted = true;
                    } else {
                        mutePlayerImage.setImageResource(R.drawable.ic_mute_player);
                        mutePlayerText.setTextColor(getResources().getColor(R.color.white));
                        muted = false;
                    }
                    audioManager.setStreamVolume(STREAM_MUSIC, currentVolume, 0);
                    if (vbview.getVisibility() != View.VISIBLE) {
                        animateView(vbview, SCALE_AND_ALPHA, true, 200);
                    }
                    if (seekbar1.getVisibility() == View.VISIBLE) {
                        seekbar1.setVisibility(View.GONE);
                        brightnesspercentText.setVisibility(View.GONE);
                    }
                    seekbar.setVisibility(View.VISIBLE);
                    volumepercentText.setVisibility(View.VISIBLE);
                    tinyDB.putInt("playervolume", currentVolume);
                } else if (acceptBrightnessArea) {
                    titleprogress.setText("Brightness");
                    if ((int) distanceY > 0) {
                        if (seekbar1.getProgress() >= maxGestureLength) {
                            seekbar1.setProgress(isfull);
                        } else {
                            seekbar1.setProgress(seekbar1.getProgress() + (int) distanceY);
                        }
                    } else {
                        if (seekbar1.getProgress() <= 1) {
                            seekbar1.setProgress(0);
                        } else {
                            seekbar1.setProgress(seekbar1.getProgress() + (int) distanceY);
                        }
                    }
                    float currentProgressPercent =
                            (float) seekbar1.getProgress() / maxGestureLength;//second part is get max gesture length
                    brightnesspercentText.setText("" + (int) (Math.abs(currentProgressPercent) * 15));
                    WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                    if (currentProgressPercent <= 0.1f) {
                        currentProgressPercent = 0.1f;
                    }
                    layoutParams.screenBrightness = currentProgressPercent;
                    getWindow().setAttributes(layoutParams);
                    tinyDB.putFloat("playerbrightness", currentProgressPercent);
                    tinyDB.putInt("playerBrightnessSeekbarValue", seekbar1.getProgress());
                    tinyDB.putString("textofbrightness", "" + (int) (Math.abs(currentProgressPercent) * 15));
                    if (vbview.getVisibility() != View.VISIBLE) {
                        animateView(vbview, SCALE_AND_ALPHA, true, 200);
                    }
                    if (seekbar.getVisibility() == View.VISIBLE) {
                        seekbar.setVisibility(View.GONE);
                        volumepercentText.setVisibility(View.GONE);
                    }
                    seekbar1.setVisibility(View.VISIBLE);
                    brightnesspercentText.setVisibility(View.VISIBLE);
                }
            }
            return true;

        }

        void onScrollEnd() {

            if (vbview.getVisibility() == View.VISIBLE) {
                animateView(vbview, SCALE_AND_ALPHA, false, 200, 200);
            }


        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            return true;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP && isMoving) {
                isMoving = false;
                onScrollEnd();
                count = 0;
                firsttime = true;
                return true;

            } else if (event.getAction() == MotionEvent.ACTION_UP && ishoriMoving) {
                ishoriMoving = false;
                if (swaptimerrootview.getVisibility() == View.VISIBLE) {
                    animateView(swaptimerrootview, SCALE_AND_ALPHA, false, 200, 200);
                }
                count = 0;
                firsttime = true;
                return true;
            }
            return false;
        }
    }

    public class SettingsContentObserver extends ContentObserver {

        SettingsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            updateStuff();
        }

        private void updateStuff() {
            float currentVolume = audioManager.getStreamVolume(STREAM_MUSIC),
                    maxVolume = audioManager.getStreamMaxVolume(STREAM_MUSIC),
                    res = currentVolume / maxVolume;
            int progress = (int) (res * maxGestureLength);
            seekbar.setProgress(progress);
            seekbar.setVisibility(View.VISIBLE);
            if (vbview.getVisibility() != View.VISIBLE) {
                animateView(vbview, SCALE_AND_ALPHA, true, 200);
            }

        }
    }

    public class LockScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    // Screen is on but not unlocked (if any locking mechanism present)
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    mExoPlayerView.getPlayer().setPlayWhenReady(false);
                    // Screen is locked
                } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    // Screen is unlocked
                }
            }
        }
    }

//    private class backgroundwork extends AsyncTask<Void, Void, List> {
//
//
//        @Override
//        protected List doInBackground(Void... voids) {
//
//            return data;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(List list) {
//            super.onPostExecute(list);
//            for (int i = 0; i < data.size(); i++)
//                Log.d("database_table_data", "" + data.get(i));
//
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//        }
//
//        @Override
//        protected void onCancelled(List list) {
//            super.onCancelled(list);
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//        }
//    }

    private class OnSwipeTouchListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }

    @Override
    public void onBackPressed() {
        isFirst = true;
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();

        } else {
            if (playerlocked) {

            } else if (!admobUtils.showInterstitialAd()) {
                super.onBackPressed();
            }
        }

    }

    private void initExitDialog() {
        adDialog = new Dialog(this, R.style.customDialog);
        adDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(adDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        adDialog.setContentView(R.layout.add_dialog);
        adDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        adDialog.getWindow().setDimAmount(0.5f);
        adDialog.setCancelable(true);


        adDialog.setOnKeyListener((dialog, keyCode, event) -> {

            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                //onBackPressed();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                return true;
            }
            return false;
        });
        //exitDialog.onBackPressed();
        ImageView cross = adDialog.findViewById(R.id.cross_image);
        adView = adDialog.findViewById(R.id.banner_ad_view);
        cross.setOnClickListener(v -> {
            adDialog.dismiss();
        });
        admobUtils.loadBannerAd(adView);


    }

    private void showAdDialog() {


        if (adDialog != null) {
            if (!adDialog.isShowing()) {
                if (!VideoPlayer.this.isFinishing()) {
                    try {
                        adDialog.show();
                    } catch (WindowManager.BadTokenException e) {

                    }
                }

            }
        }
    }

    private void hideExitDialog() {
        if (adDialog != null) {
            if (adDialog.isShowing()) {
                adDialog.dismiss();
            }
        }
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}