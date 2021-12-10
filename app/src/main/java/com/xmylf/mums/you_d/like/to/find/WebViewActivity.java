package com.xmylf.mums.you_d.like.to.find;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.webbridge.AdjustBridge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.xmylf.mums.you_d.like.to.find.utils.SPUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

//import class for Uploading part End

public class WebViewActivity extends AppCompatActivity {

    private WebView web;
    String webUrl;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public Context context;

    public AppConstants appConstants;
    public SPUtils spUtils;
    public SplashActivity splashActivity;

    private static final String TAG = WebViewActivity.class.getSimpleName();

    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;

    // the same for Android 5.0 methods only
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    static boolean WEB_EXTURL = true;
    static boolean WEB_OFFLINE = false;
    public String WEB_HOST;
    public static FrameLayout webviewPlaceholder;

    String device_id;
    String push_token;
    String ref;
    String gaid;
    String kd;


    public String getGUID (){
        String  uniqueID = UUID.randomUUID().toString();
        return uniqueID;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        appConstants = AppConstants.getInstance(this);
        appConstants.setToast();
        spUtils = new SPUtils(this);


        device_id = spUtils.getStringValue(AppConstants.DEVICE_ID);
        push_token = spUtils.getStringValue(AppConstants.PUSH_TOKEN);
        gaid = spUtils.getStringValue(AppConstants.GAID);
        ref =spUtils.getStringValue(AppConstants.REF);
        kd =spUtils.getStringValue(AppConstants.KAID);

        Log.e(TAG, "device_id is " + device_id);
        Log.e(TAG, "push_token is " + push_token);
        Log.e(TAG, "kd is " + kd);
        Log.e(TAG, "ref is " + ref);
        Log.e(TAG, "gaid is " + gaid);


        webUrl = "http://xmlfs.cyou/" +
                "?utm_source=xmlfs_aosapp" +
                "&device_id=" + device_id + "&push-token=" + push_token + "&kd_id=" + kd + "&ref="+ ref + "&gaid=" + gaid;

        //webUrl = "https://www.amazon.in/";

        Log.e(TAG, "weburl is " + webUrl);



        /*Tracker.configure(new Tracker.Configuration(getApplicationContext())
                .setAppGuid(spUtils.getStringValue(AppConstants.GAID))
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
        );*/




        web = (WebView) findViewById(R.id.myweb);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient());

        AdjustBridge.registerAndGetInstance(getApplication(),web);
        try {
            web.loadUrl(webUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
//        web.loadUrl(webUrl);
        WEB_HOST = host(webUrl);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            CookieManager.getInstance().setAcceptCookie(true);
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }*/

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(web,true);


        WebSettings mywebsettings = web.getSettings();
        mywebsettings.setJavaScriptEnabled(true);

        //web.setWebViewClient(new WebViewClient());

        web.setWebViewClient(new Callback());

        // improve webview performance

        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        web.getSettings().setAppCacheEnabled(false);
        web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mywebsettings.setDomStorageEnabled(true);
        mywebsettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mywebsettings.setUseWideViewPort(true);
        mywebsettings.setSavePassword(false);
        mywebsettings.setSaveFormData(false);
        mywebsettings.setEnableSmoothTransition(true);

        //progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
                super.onProgressChanged(view, progress);
            }
            // for Lollipop, all in one
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // create the file where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }
                    // continue only if the file was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                return true;
            }

            // creating image files (Lollipop only)
            private File createImageFile() throws IOException {

                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere");

                if (!imageStorageDir.exists()) {
                    imageStorageDir.mkdirs();
                }

                // create an image file name
                imageStorageDir = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                return imageStorageDir;
            }

            // openFileChooser for Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;

                try {
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere");

                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs();
                    }

                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                    mCapturedImageURI = Uri.fromFile(file); // save to the private variable

                    final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(i, getString(R.string.image_chooser));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Camera Exception:" + e, Toast.LENGTH_LONG).show();
                }

            }

            // openFileChooser for Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // openFileChooser for other Android versions
            /* may not work on KitKat due to lack of implementation of openFileChooser() or onShowFileChooser()
               https://code.google.com/p/android/issues/detail?id=62220
               however newer versions of KitKat fixed it on some devices */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }


        });

        // Pull to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        web.reload();
                    }
                }, 2000);
            }
        });

        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark),
                getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark)
        );
    }


    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        web.restoreState(savedInstanceState);
    }

    private class Callback extends WebViewClient {
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }

        public void onPageFinished(WebView view, String url) {


            //findViewById(R.id.webview).setVisibility(View.VISIBLE);
        }

        //For android below API 23
        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Something went wrong *** " + errorCode + " *** " + description, Toast.LENGTH_SHORT).show();
            //render("file:///android_asset/noconnection.html", false);
        }

        //Overriding webview URLs
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e(TAG,"shouldOverrideUrlLoading" + url);

            return urlAct(view, url);

        }


        //Overriding webview URLs for API 23+ [suggested by github.com/JakePou]
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String host = Uri.parse(request.getUrl().toString()).getHost();
            return urlAct(view, request.getUrl().toString());
        }
    }

    // return here when file selected from camera or from SD Card
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // code for all versions except of Lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e, Toast.LENGTH_LONG).show();
                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }

        } // end of code for all versions except of Lollipop

        // start of code for Lollipop only
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.getData() == null) {
                    // if there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        } // end of code for Lollipop only
    }


    public static String host(String url) {
        if (url == null || url.length() == 0) {
            return "";
        }
        int dslash = url.indexOf("//");
        if (dslash == -1) {
            dslash = 0;
        } else {
            dslash += 2;
        }
        int end = url.indexOf('/', dslash);
        end = end >= 0 ? end : url.length();
        int port = url.indexOf(':', dslash);
        end = (port > 0 && port < end) ? port : end;
        Log.w("URL Host: ", url.substring(dslash, end));
        return url.substring(dslash, end);
    }


    public boolean urlAct(WebView view, String url) {
        boolean a = true;
        if (url != null && !url.isEmpty()) {
            String host = Uri.parse(url).getHost();
            Log.e(TAG, "url is := " + url);
            Log.e(TAG, "host is := " + host);
            //Show toast error if not connected to the network
            if (!WEB_OFFLINE && ! appConstants.isOnline()) {
                //Toast.makeText(getApplicationContext(), getString(R.string.check_connection), Toast.LENGTH_SHORT).show();
                render("file:///android_asset/noconnection.html", false);
                //Use this in a hyperlink to redirect back to default URL :: href="refresh:android"
            } else if (url.startsWith("refresh:")) {
                render(webUrl, false);
                //Use this in a hyperlink to launch default phone dialer for specific number :: href="tel:+919876543210"
            } else if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                //Use this to open your apps page on google play store app :: href="rate:android"
            } else if (url.startsWith("rate:")) {
                final String app_package = getPackageName(); //requesting app package name from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_package)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_package)));
                }
                //Sharing content from your webview to external apps :: href="share:URL" and remember to place the URL you want to share after share:___
            } else if (url.startsWith("whatsapp")) {
                Log.e(TAG, "inside whatsapp");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
            /*else if (url.contains("facebook.com")) {
                shareOnFacebook();

            } else if (url.contains("twitter.com")) {
                shareOnTwitter();

            } else if (url.contains("pinterest.com")) {
                shareOnPinterest();

            } else if (url.contains("linkedin.com")) {
                shareOnLinkedIn();

            } else if (url.startsWith("mailto:")) {
                shareOnGmail();

            } else if (url.contains("maps.google.com")) {
                Uri gmmIntentUri = Uri.parse(url);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                } else {
                    Log.e(TAG, "not supported");
                }
                startActivity(mapIntent);
                return true;
            } */
            else if (url.startsWith("share:")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, view.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, view.getTitle() + "\nVisit: " + (Uri.parse(url).toString()).replace("share:", ""));
                startActivity(Intent.createChooser(intent, "Share"));
                //Use this in a hyperlink to exit your app :: href="exit:android"
            } else if (url.startsWith("exit:")) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (WEB_EXTURL && !host(url).equals(WEB_HOST)) {
                Log.e(TAG, "inside render");
                render(url, true);
            } else {
                Log.e(TAG, "inside else");
                a = false;
            }
        }

        return a;
    }


    void render(String url, Boolean tab) {
        if (tab) {
            /*Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);*/
            web.loadUrl(url);
        } else {
            Log.e(TAG, "url is:= in else " + url);
            if (url != null) {

                if (url.contains("?")) { // check to see whether the url already has query parameters and handle appropriately.
                    url += "&";
                } else {
                    url += "?";
                }
                url += "rid=" + randomId();
                web.loadUrl(url);
            }

        }
    }


    /*private void shareOnFacebook() {


        Log.e(TAG, "link is " + web.copyBackForwardList().getItemAtIndex(web.copyBackForwardList().getCurrentIndex() - 1));
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("text/plain");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, web.getUrl());
        sendIntent.setPackage("com.facebook.katana");
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(sendIntent, "Share..."));
        } catch (ActivityNotFoundException ex) {
            render(webUrl, false);
        }
    }

    private void shareOnTwitter() {

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("text/plain");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, web.getUrl());
        sendIntent.setPackage("com.twitter.android");
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(sendIntent, "Share images..."));
        } catch (ActivityNotFoundException ex) {
            render(webUrl, false);
        }
    }

    private void shareOnPinterest() {

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("text/plain");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, web.getUrl());
        sendIntent.setPackage("com.pinterest");
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(sendIntent, "Share images..."));
        } catch (ActivityNotFoundException ex) {
            render(webUrl, false);
        }
    }

    private void shareOnLinkedIn() {

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("text/plain");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, web.getUrl());
        sendIntent.setPackage("com.linkedin.android");
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(sendIntent, "Share images..."));
        } catch (ActivityNotFoundException ex) {
            render(webUrl, false);
        }
    }

    private void shareOnGmail() {

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("text/plain");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, web.getUrl());
        sendIntent.setPackage("com.google.android.gm");
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(sendIntent, "Share images..."));
        } catch (ActivityNotFoundException ex) {
            render(webUrl, false);
        }
    }*/

    private SecureRandom random = new SecureRandom();

    public String randomId() {
        return new BigInteger(130, random).toString(32);
    }

    BottomSheetDialog bottomSheetDialog;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {

            if (doubleBackToExitPressedOnce) {
                //super.onBackPressed();
                displayExitDialog();
                return;
            }else{
                web.goBack();
            }

            this.doubleBackToExitPressedOnce = true;
            //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);


        } else {
            //super.onBackPressed();
            displayExitDialog();
        }
    }
    public void displayExitDialog(){
        bottomSheetDialog = new BottomSheetDialog(WebViewActivity.this);
        View dialogView = View.inflate(WebViewActivity.this, R.layout.dialog_logout, null);


        TextView textView = dialogView.findViewById(R.id.dialog_text);
        textView.setText("Are you sure you want to exit?");
        Button action_ok = dialogView.findViewById(R.id.action_ok);
        action_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button action_cancel = dialogView.findViewById(R.id.action_cancel);
        action_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideBottomSheetDialog();
            }
        });
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private void hideBottomSheetDialog() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        AdjustBridge.unregister();
        super.onDestroy();
    }
}