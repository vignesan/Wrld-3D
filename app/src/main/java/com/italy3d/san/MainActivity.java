package com.italy3d.san;

/**
 * Created by vignesan on 26/6/17.
 */
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.eegeo.indoors.IndoorMapView;
import com.eegeo.mapapi.EegeoApi;
import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.MapView;
import com.eegeo.mapapi.map.OnMapReadyCallback;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static final String AD_UNIT_ID = "enter your reward video id";
    private static final String APP_ID = "enter your app id";
    private static final long COUNTER_TIME = 130;
    private CountDownTimer mCountDownTimer;
    private boolean mAdOver;
    private boolean mAdPaused;
    private RewardedVideoAd mRewardedVideoAd;
    private long mTimeRemaining;
    private AdView mAdView;

    private MapView m_mapView;
    private IndoorMapView m_interiorView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, APP_ID);
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        startAd();

        EegeoApi.init(this, getString(R.string.eegeo_api_key));

        setLockedOrientation();

        setContentView(R.layout.activity_main);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        m_mapView = (MapView) findViewById(R.id.mapView);
        m_mapView.onCreate(savedInstanceState);

        m_mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final EegeoMap map) {
                RelativeLayout uiContainer = (RelativeLayout) findViewById(R.id.eegeo_ui_container);
                m_interiorView = new IndoorMapView(m_mapView, uiContainer, map);

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!mAdOver && mAdPaused) {
            resumeAd();
        }
        mRewardedVideoAd.resume(this);
        m_mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAd();
        mRewardedVideoAd.pause(this);
        m_mapView.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_mapView.onDestroy();
    }

    private void setLockedOrientation() {
        if (getResources().getBoolean(R.bool.is_large_device)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    private void pauseAd() {
        mCountDownTimer.cancel();
        mAdPaused = true;
    }

    private void resumeAd() {
        createTimer(COUNTER_TIME);
        mAdPaused = false;
    }

    private void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build());
        }
    }


    private void startAd() {

        loadRewardedVideoAd();
        createTimer(COUNTER_TIME);


    }

    private void createTimer(long time) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(time * 1000, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                mTimeRemaining = ((millisUnitFinished / 1000) + 1);
            }

            @Override
            public void onFinish() {
                if (mRewardedVideoAd.isLoaded()) {
                    showRewardedVideo();
                }
            }
        };
        mCountDownTimer.start();
    }

    private void showRewardedVideo() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewarded(RewardItem reward) {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

}
