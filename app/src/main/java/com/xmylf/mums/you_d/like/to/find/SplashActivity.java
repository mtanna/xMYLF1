package com.xmylf.mums.you_d.like.to.find;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.xmylf.mums.you_d.like.to.find.utils.SPUtils;


public class SplashActivity extends AppCompatActivity  implements InstallReferrerStateListener {

    public static int Splash_time = 2000;
    public TelephonyManager telephonyManager;
    public String TAG = "SplashActivity";

    public AppConstants appConstants;
    public SPUtils spUtils;

    InstallReferrerClient mReferrerClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        getSupportActionBar().hide();


        setContentView(R.layout.activity_splash);

        appConstants = AppConstants.getInstance(this);
        appConstants.setToast();
        spUtils = new SPUtils(this);

        mReferrerClient = InstallReferrerClient.newBuilder(this).build();
        mReferrerClient.startConnection(this);


        redirectActivity();
    }
    @Override
    public void onInstallReferrerSetupFinished(int responseCode) {
        switch (responseCode) {
            case InstallReferrerClient.InstallReferrerResponse.OK:
                try {
                        Log.v(TAG, "InstallReferrer conneceted");
                    ReferrerDetails response = mReferrerClient.getInstallReferrer();
                    String installReferrer = response.getInstallReferrer();

                    handleReferrer(response);
                    Log.e(TAG,"response " + installReferrer);


                    if (spUtils.getStringValue(AppConstants.REF).length() == 0){
                        spUtils.setStringValue(AppConstants.REF, installReferrer);

                    }

                    mReferrerClient.endConnection();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                Log.e(TAG, "InstallReferrer not supported");
                break;
            case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                Log.e(TAG, "Unable to connect to the service");
                break;
            default:
                Log.e(TAG, "responseCode not found.");
        }
    }

    @Override
    public void onInstallReferrerServiceDisconnected() {

    }

    private void handleReferrer(ReferrerDetails response) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public void redirectActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent intent = new Intent(SplashActivity.this, WebViewActivity.class);
                startActivity(intent);
                finish();
            }
        }, Splash_time);
    }
}