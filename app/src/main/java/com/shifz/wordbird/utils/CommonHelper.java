package com.shifz.wordbird.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import com.shifz.wordbird.InActivity;

/**
 * Created by Shifar Shifz on 10/23/2015.
 */
public class CommonHelper {

    private final Context context;

    public CommonHelper(Context context) {
        this.context = context;
    }

    public static boolean parseBoolean(int anInt) {
        return anInt == 1;
    }

    public static String parseTinyIntString(boolean success) {
        return success ? "1" : "0";
    }

    /**
     *
     * @param context current context
     * @return boolean true if network else false
     */
    public static boolean isNetwork(final Context context) {

        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public  boolean isNetwork() {
        return isNetwork(this.context);
    }

    public String getOwnerName() {

        if(isSupport(14)){
            final Cursor profileCursor = this.context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
            if(profileCursor!=null && profileCursor.moveToFirst()){
                final String ownerName = profileCursor.getString(profileCursor.getColumnIndex("display_name"));
                profileCursor.close();
                return ownerName;
            }
        }

        //Name can't be collected, so the email
        return null;
    }

    public String getOwnerEmail(){
        final AccountManager am = (AccountManager) this.context.getSystemService(Context.ACCOUNT_SERVICE);
        final Account[] accounts = am.getAccounts();
        for(final Account account:accounts){
            final String name = account.name;
            if(Patterns.EMAIL_ADDRESS.matcher(name).matches()){
                return name;
            }
        }
        return null;
    }

    private static boolean isSupport(final int apiLevel){
        return Build.VERSION.SDK_INT >= apiLevel;
    }

    public String getDeviceImei() {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static String firstCharUp(String word) {
        return word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
