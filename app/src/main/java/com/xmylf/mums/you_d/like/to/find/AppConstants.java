package com.xmylf.mums.you_d.like.to.find;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AppConstants {
    public static final String SHARED_PREF = "GrowTreesSP";
    public static final String DEVICE_ID = "DeviceID";
    public static final String GAID = "gaid";
    public static final String KAID = "kaid";
    public static final String PUSH_TOKEN = "push_token";
    public static final String REF = "ref";


    private static AppConstants instance = new AppConstants();
    static Context context;
    ConnectivityManager connectivityManager;
    boolean connected = false;

    public static AppConstants getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
        }
        return connected;
    }

    public void displayToast(String msg) {
        try {
            lbl_toast.setText(msg);
            toast.show();
        } catch (Exception e) {

        }
    }

    public void displayToastError(String msg) {
        try {
            lbl_toast_error.setText(msg);
            toast_error.show();
        } catch (Exception e) {

        }
    }

    Toast toast, toast_error;
    View toast_layout, toast_layout_error;
    TextView lbl_toast, lbl_toast_error;

    public void setToast() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            toast_layout = inflater.inflate(R.layout.toast_layout,
                    null);

            lbl_toast = (TextView) toast_layout.findViewById(R.id.lbl_toast);
            toast = new Toast(context.getApplicationContext());
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toast_layout);
            setErrorToast();
        }
    }

    public void setErrorToast() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        toast_layout_error = inflater.inflate(R.layout.toast_layout_error,
                null);
        lbl_toast_error = (TextView) toast_layout_error.findViewById(R.id.lbl_toast);
        toast_error = new Toast(context.getApplicationContext());
        toast_error.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast_error.setDuration(Toast.LENGTH_SHORT);
        toast_error.setView(toast_layout_error);
    }

}
