package com.code4rox.adsmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Objects;

public class AdmobUtils {

    NativeAdListener nativeAdListener;
    private Context context;
    private InterstitialAd mInterstitialAd;
    private AdmobInterstitialListener admobListener;
    private boolean bannerSize = false;
    private boolean check = false;
    private UnifiedNativeAd nativeAd;
    private FrameLayout frameLayout;
    private int nativeAdLayout;
    private InterAdsIdType interAdsIdType;
    private NativeAdsIdType nativeAdsIdType;
    private AdmobBannerListener admobBannerListener;


    public AdmobUtils(Context context, AdmobInterstitialListener admobListener, InterAdsIdType interAdsIdType) {
        this.context = context;
        this.interAdsIdType = interAdsIdType;
        this.admobListener = admobListener;
        mInterstitialAd = newInterstitialAd();

    }

    public AdmobUtils(Context context, AdmobInterstitialListener admobListener, int i) {
        this.context = context;
        this.admobListener = admobListener;
        mInterstitialAd = newMainInterstitialAd();
    }

    // Just
    public AdmobUtils(Context context) {
        this.context = context;
    }




    public void loadInterstitial(AdmobInterstitialListener admobListener) {
        this.admobListener = admobListener;
        mInterstitialAd = newInterstitialAd();
    }

    public void loadNativeAd(FrameLayout frameLayout, int nativeAdLayout, NativeAdsIdType nativeAdsIdType) {
        this.nativeAdLayout = nativeAdLayout;
        this.frameLayout = frameLayout;
        this.nativeAdsIdType = nativeAdsIdType;
        this.nativeAd = loadNativeAd();
    }

    public void setNativeAdListener(NativeAdListener nativeAdListener) {
        this.nativeAdListener = nativeAdListener;
    }

    public void loadBannerAd(final AdView adView) {
        if (!TinyDB.getInstance(context).getBoolean(Constants.IS_PREMIUM)) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            //Set the Banner Id
//           adView.setAdSize(AdSize.LARGE_BANNER);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    if (adView.getVisibility() == View.GONE) {
                        adView.setVisibility(View.VISIBLE);
                    }
                    if(admobBannerListener != null){
                        admobBannerListener.onBannerAdLoaded();
                    }
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    if(admobBannerListener != null){
                        admobBannerListener.onBannerAdFailedLoaded();
                    }
                }
            });
            adView.loadAd(adRequest);
        }
    }

    public void setAdmobBannerListener(AdmobBannerListener admobBannerListener){
        this.admobBannerListener = admobBannerListener;
    }

    public void loadRectBannerAd(final View adView) {
        if (!TinyDB.getInstance(context).getBoolean(Constants.IS_PREMIUM) && FirebaseRemoteConfig.getInstance().getBoolean(Constants.FIRE_IS_RECT_BANNER_SHOW)) {

            AdView mAdView = new AdView(context);
            if (!FirebaseRemoteConfig.getInstance().getBoolean(Constants.FIRE_IS_SMALL_RECT_BANNER)) {
                mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            } else {
                mAdView.setAdSize(AdSize.LARGE_BANNER);
            }
            mAdView.setAdUnitId(context.getResources().getString(R.string.adjust_native_fb));
            ((RelativeLayout) adView).addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();

            //Set the Banner Id
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    if (adView.getVisibility() == View.GONE) {
                        adView.setVisibility(View.VISIBLE);
                    }
                }
            });
            mAdView.loadAd(adRequest);

        }
    }

    public InterstitialAd getInterstitialAd() {
        return mInterstitialAd;
    }

    public boolean showInterstitialAd() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Log.d("finish3", "onFinish: ");

            return true;
        } else {
            return false;
        }
    }

    private InterstitialAd newInterstitialAd() {
        if (!TinyDB.getInstance(context).getBoolean(Constants.IS_PREMIUM)) {

            InterstitialAd interstitialAd = new InterstitialAd(context);
            String adId = "";
            if (interAdsIdType == InterAdsIdType.SPLASH_INTER_AM) {
                adId = context.getString(R.string.splash_inter_am);
            } else if (interAdsIdType == InterAdsIdType.INTER_AM) {
                adId = context.getString(R.string.inter_am);
            }
            interstitialAd.setAdUnitId(adId);
            interstitialAd.loadAd(new AdRequest.Builder().build());
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    FirebaseAnalytics.getInstance(context).logEvent("am_click_inter", new Bundle());

                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    FirebaseAnalytics.getInstance(context).logEvent("am_show_inter", new Bundle());
                    Log.d("Interstitial", "onAdOpen: ");
                    TinyDB.getInstance(context).putBoolean(Constants.CHECK_INTER_AD_SHOW, true);

                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    if (admobListener != null) {
                        admobListener.onInterstitialAdLoaded();
                    }
                }

                @Override
                public void onAdClosed() {

                    mInterstitialAd = newInterstitialAd();
                    Log.d("Timer", "Admob +");

                    if (admobListener != null) {
                        admobListener.onInterstitialAdClose();
                    }
                    TinyDB.getInstance(context).putBoolean(Constants.CHECK_INTER_AD_SHOW, false);
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    if (admobListener != null) {
                        admobListener.onInterstitialAdFailed();

                    }
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.d("Interstitial", "onAdImpression: ");
                }
            });
            return interstitialAd;
        } else {
            return null;
        }
    }

    private InterstitialAd newMainInterstitialAd() {
        if (!TinyDB.getInstance(context).getBoolean(Constants.IS_PREMIUM)) {
            InterstitialAd interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(context.getString(R.string.inter_am));
            interstitialAd.loadAd(new AdRequest.Builder().build());
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    mInterstitialAd = newMainInterstitialAd();
                    admobListener.onInterstitialAdClose();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.d("Interstitial", "onAdImpression: ");
                }
            });
            return interstitialAd;
        } else {
            return null;
        }
    }

    private UnifiedNativeAd loadNativeAd() {

        if ((nativeAdLayout == -1 && frameLayout == null) || TinyDB.getInstance(context).getBoolean(Constants.IS_PREMIUM)) {
            return null;
        }

        final UnifiedNativeAd[] nativeAd = {null};

        AdLoader.Builder builder;
        String nativeAdId = "";

        if (nativeAdsIdType == NativeAdsIdType.SPLASH_NATIVE_AM) {
            nativeAdId = context.getResources().getString(R.string.splash_native_am);
        } else if (nativeAdsIdType == NativeAdsIdType.MM_NATIVE_AM) {
            nativeAdId = context.getResources().getString(R.string.mm_native_am);
        } else if (nativeAdsIdType == NativeAdsIdType.ADJUST_NATIVE_AM) {
            nativeAdId = context.getResources().getString(R.string.adjust_native_am);
        } else if (nativeAdsIdType == NativeAdsIdType.EXIT_NATIVE_AM) {
            nativeAdId = context.getResources().getString(R.string.exit_native_am);
        }

        builder = new AdLoader.Builder(context, nativeAdId);

        // OnUnifiedNativeAdLoadedListener implementation.
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (nativeAd[0] != null) {
                nativeAd[0].destroy();
            }
            nativeAd[0] = unifiedNativeAd;
           /* FrameLayout frameLayout =
                    findViewById(R.id.fl_adplaceholder);*/
            UnifiedNativeAdView adView = (UnifiedNativeAdView) ((Activity) context).getLayoutInflater()
                    .inflate(nativeAdLayout, null);
            populateUnifiedNativeAdView(unifiedNativeAd, adView);
            frameLayout.removeAllViews();
            frameLayout.addView(adView);

            if (nativeAdListener != null) {
                nativeAdListener.onNativeAdLoaded();
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                if (nativeAdListener != null) {
                    nativeAdListener.onNativeAdError();
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                FirebaseAnalytics.getInstance(context).logEvent("am_show_native", new Bundle());
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                FirebaseAnalytics.getInstance(context).logEvent("am_click_native", new Bundle());
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

//        videoStatus.setText("");

        return nativeAd[0];

    }
    private  Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        mediaView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (child instanceof ImageView) {
                    ImageView imageView = (ImageView) child;
                    imageView.setAdjustViewBounds(true);

                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {}
        });

        adView.setMediaView(mediaView);


        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
//        adView.setPriceView(adView.findViewById(R.id.ad_price));
//        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
//        adView.setStoreView(adView.findViewById(R.id.ad_store));
//        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

       /* if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
*/
        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
      /*      videoStatus.setText(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));*/

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                   /* refresh.setEnabled(true);
                    videoStatus.setText("Video status: Video playback has ended.");*/
                    super.onVideoEnd();
                }
            });
        } else {
         /*   videoStatus.setText("Video status: Ad does not contain a video asset.");
            refresh.setEnabled(true);*/
        }


    }
 /*   private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);

        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        ImageView blurImageView = adView.findViewById(R.id.iv_media_blur);
//        adView.setPriceView(adView.findViewById(R.id.ad_price));
//        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
//        adView.setStoreView(adView.findViewById(R.id.ad_store));
//        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getMediaContent() == null) {
            blurImageView.setVisibility(View.GONE);
        } else {
            try {

                blurImageView.setVisibility(View.VISIBLE);

                Drawable blurDrawable = nativeAd.getMediaContent().getMainImage();

                Bitmap drawableBitmap = drawableToBitmap(blurDrawable);

                Bitmap bluredBitmap = fastBlur(drawableBitmap, 1f, 16);

                blurImageView.setImageBitmap(bluredBitmap);


            } catch (Exception e) {
                e.printStackTrace();
  //              blurImageView.setVisibility(View.GONE);
            }
        }

        adView.setNativeAd(nativeAd);

       *//* if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
*//*
        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
//        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {

            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        }



//        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
*//*        if (vc.hasVideoContent()) {
      *//**//*      videoStatus.setText(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));*//**//*

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                   *//**//* refresh.setEnabled(true);
                    videoStatus.setText("Video status: Video playback has ended.");*//**//*
                    super.onVideoEnd();
                }
            });
        } else {
         *//**//*   videoStatus.setText("Video status: Ad does not contain a video asset.");
            refresh.setEnabled(true);*//**//*
        }*//*


    }*/

    public Dialog initDialogNative(int nativeDialogLayout, int customStyle) {

        Dialog dialogNative;
        dialogNative = new Dialog(context, customStyle);
        dialogNative.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialogNative.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialogNative.setContentView(nativeDialogLayout);
        dialogNative.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogNative.getWindow().setDimAmount(0.5f);
        dialogNative.setCancelable(false);
        return dialogNative;
    }
    private Bitmap fastBlur(Bitmap sentBitmap, float scale, int radius) {
        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];

        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vMin = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vMin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vMin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vMin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vMin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }


        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }


    public interface AdmobInterstitialListener {
        void onCreate();

        void onInterstitialAdClose();
        void onInterstitialAdLoaded();
        void onInterstitialAdFailed();
    }

    public interface NativeAdListener {
        void onNativeAdLoaded();
        void onNativeAdError();
    }

    public interface AdmobBannerListener {
        void onBannerAdLoaded();
        void onBannerAdFailedLoaded();
    }


    public void destroyNativeAd(){
        if(nativeAd != null){
            nativeAd.destroy();
        }
    }



   /*     <LinearLayout
    android:id="@+id/ad_layout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:visibility="visible"
    >

        <com.google.android.gms.ads.AdView xmlns:ads="http://schemas.android.com/apk/res-auto"
    android:id="@+id/banner_ad_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:visibility="gone"
    ads:adSize="BANNER"
    ads:adUnitId="@string/banner_ad_unit_id" />



    </LinearLayout>*/


    //native ad xml

   /*  <FrameLayout
    android:id="@+id/fl_adplaceholder"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
  */
}

