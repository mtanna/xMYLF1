package com.xmylf.mums.you_d.like.to.find;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.kochava.base.AttributionUpdateListener;
import com.kochava.base.Tracker;
import com.onesignal.OneSignal;
import com.rudderstack.android.sdk.core.RudderClient;
import com.rudderstack.android.sdk.core.RudderConfig;
import com.rudderstack.android.sdk.core.RudderIntegration;
import com.xmylf.mums.you_d.like.to.find.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Gaynder extends Application {

    private static final String ONESIGNAL_APP_ID = "eee119bc-c431-4a36-a82e-c0bfbaeffb12";
    String TAG = "Gaynder";
    SPUtils spUtils;
    Tracker tracker;

    RudderClient rudderClient;
    KochavaIntegrationFactory kochavaIntegrationFactory;
    String GUID ;

    private static final String KOCHAVA_KEY = "Kochava";
    @Override
    public void onCreate() {
        super.onCreate();

        String appToken = "mzz38dw4lngg";
        String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
        AdjustConfig config = new AdjustConfig(this, appToken, environment);
        Adjust.onCreate(config);
        Log.e(TAG,"environment: " + environment);

        Log.e(TAG,"call notification");
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        spUtils = new SPUtils(this);


        spUtils.setStringValue(AppConstants.PUSH_TOKEN,"eee119bc-c431-4a36-a82e-c0bfbaeffb12");

        GUID = getGUID();
        Log.e(TAG,"GUID: " + GUID);



        if (spUtils.getStringValue(AppConstants.GAID).length() == 0){

            spUtils.setStringValue(AppConstants.GAID, GUID );
            Log.e(TAG, "GUID " + GUID);

        }else {
            spUtils.setStringValue(AppConstants.GAID,GUID);
            Log.e(TAG,"GUID2.0: " + GUID);
        }



        if (spUtils.getStringValue(AppConstants.DEVICE_ID).length() == 0) {
            String deviceID = getDeviceId(this);
            spUtils.setStringValue(AppConstants.DEVICE_ID, deviceID);
            Log.e(TAG, "device id is  " + getDeviceId(this));
            //redirectActivity();
        } else {
            //redirectActivity();
        }


        String deviceID = getDeviceId(this);
        spUtils.setStringValue(AppConstants.DEVICE_ID, deviceID);
        Log.e(TAG, "device id is  " + getDeviceId(this));



        /*Tracker.configure(new Tracker.Configuration(getApplicationContext())
                .setAppGuid(GUID)
                .setLogLevel(Tracker.LOG_LEVEL_ERROR)
        );*/



         RudderIntegration.Factory FACTORY = new RudderIntegration.Factory() {
            @Override
            public RudderIntegration<?> create(Object settings, RudderClient client, RudderConfig rudderConfig) {
                return new KochavaIntegrationFactory(settings, rudderConfig);
            }

            @Override
            public String key() {
                return KOCHAVA_KEY;
            }
        };


        Tracker.configure(new Tracker.Configuration(getApplicationContext())
                .setAppGuid("kogaynder-2-qqx1wrphq")
                .setLogLevel(Tracker.LOG_LEVEL_ERROR)
                .setAttributionUpdateListener(new AttributionUpdateListener() {
                    @Override
                    public void onAttributionUpdated(@NonNull  String attribution) {
                        Log.e(TAG,"call inside onAttributionUpdated");
                        // got the attribution results, now we need to parse it
                        try {
                            JSONObject attributionObject = new JSONObject(attribution);
                            if("false".equals(attributionObject.optString("attribution", ""))) {
                                Log.e(TAG,"installation is not done");
                                // Install is not attributed.
                            } else {
                                Log.e(TAG,"installation is done");
                                // Install is attributed. Retrieve the values we care about.
                                final String attributedNetworkId = attributionObject.optString("network_id");
                                Log.e(TAG,"attributedNetworkId "+ attributedNetworkId);
                            }
                        } catch (JSONException exception) {
                            Log.e(TAG, "exception is " + exception.toString());
                        }
                    }
                })
        );


        Tracker.sendEvent(new Tracker.Event(Tracker.EVENT_TYPE_VIEW)
                .setName("App Started")
                .setSuccess(getGUID())
        );


        String kochava_id = Tracker.getDeviceId();
        Log.e(TAG, "kochava_id is " + kochava_id);

        /*String attribution = Tracker.getAttribution();
        Log.e(TAG, "attribution " + attribution);
        if(!attribution.isEmpty()) {
            // got the attribution results, now we need to parse it
            try {
                JSONObject attributionObject = new JSONObject(attribution);
                if("false".equals(attributionObject.optString("attribution", ""))) {
                    Log.e(TAG, "inside if");
                    // Install is not attributed.
                } else {
                    // Install is attributed. Retrieve the values we care about.
                    final String attributedNetworkId = attributionObject.optString("network_id");
                    Log.e(TAG,"attributedNetworkId "+ attributedNetworkId);

                }
            } catch (JSONException exception) {
                Log.e(TAG, "exception is "+ exception.toString());
            }
        }else{
            Log.e(TAG,"attribution empty " );

        }*/




        if (spUtils.getStringValue(AppConstants.KAID).length() == 0){
            spUtils.setStringValue(AppConstants.KAID,kochava_id);
        }

        /*JSONObject mainobj = new JSONObject();
        try {

            Log.e(TAG, "kochava_app_id is " + getGUID());


            mainobj.put("action", "start");
            mainobj.put("kochava_app_id", "kogaynder-2-qqx1wrphq");
            mainobj.put("kochava_device_id", "5d687840-d3a1-4ff3-afff-67a128ba7e98");

            JSONObject dataobj = new JSONObject();
            dataobj.put("usertime", "1634653247357");
            dataobj.put("app_version", "1.0.0");
            dataobj.put("device_ver", "1.0.0");
            dataobj.put("device_ua", "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
            dataobj.put("event_name", "SubscriptionTest");
            dataobj.put("origination_ip", "104.219.46.66");
            dataobj.put("currency", "USD");


            JSONObject deviceIDsObj = new JSONObject();
            deviceIDsObj.put("idfa", "KA31011634037261t2aa1d0214aab4a9aa3199d41f8018d26" ); //kochava_id);
            dataobj.put("device_ids", deviceIDsObj);

            mainobj.put("data", dataobj);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.e(TAG, "mainobj is " + mainobj);


        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... params) {
                try {
                    String response = makePostRequest("http://control.kochava.com/track/json",
                            mainobj.toString(), getApplicationContext());

                    Log.e(TAG, "response is " + response.toString());
                    return "Success";
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return "";
                }
            }

        }.execute("");*/

    }



    public String getGUID (){
        String  uniqueID = "koxmilfys-ndqzqq8";
        return uniqueID;
    }

    public static String getDeviceId(Context context) {
        String deviceId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }


    public static String makePostRequest(String stringUrl, String payload,
                                         Context context) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        String line;
        StringBuffer jsonString = new StringBuffer();

        uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        uc.setRequestMethod("POST");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();
        OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while((line = br.readLine()) != null){
                jsonString.append(line);
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        uc.disconnect();
        return jsonString.toString();
    }
}
