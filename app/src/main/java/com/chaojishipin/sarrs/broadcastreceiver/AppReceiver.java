package com.chaojishipin.sarrs.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.File;

public class AppReceiver extends BroadcastReceiver {
    public AppReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
//        PackageManager packageManager = context.getPackageManager();
//        packageManager.getPac
//        String uninstpackagename = intent.getData().getSchemeSpecificPart();
//        String mypackagename = context.getPackageName();
//        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
//            if(uninstpackagename.equals(mypackagename)){
//                File file = new File();
//            }
//        }
    }
}
