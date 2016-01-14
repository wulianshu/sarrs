package com.chaojishipin.sarrs.thirdparty;



import android.app.Activity;
import android.content.Context;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;
import com.letv.component.utils.NetWorkTypeUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class JrspConfiguration {

    private static boolean debug;
    private static boolean apply_auto_play = PreferencesManager.getInstance().getApplyAutoPlay();
    private static boolean apply_push = PreferencesManager.getInstance().isPush();
    static {

        try {
            Properties properties = new Properties();

            InputStream in =
                    JrspConfiguration.class.getClassLoader().getResourceAsStream("jrsp.properties");
            properties.load(in);

            setDebug(Boolean.parseBoolean(properties.getProperty("jrsp.debug")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        JrspConfiguration.debug = debug;
    }

    public static boolean isApplyAutoplay() {
        return apply_auto_play;
    }

    public static void setApplyAutoplay(boolean auto_play) {
        JrspConfiguration.apply_auto_play = auto_play;
    }

    public static boolean isApplyPush() {
        return apply_push;
    }

    public static void setApplyPush(boolean apply_push) {
        JrspConfiguration.apply_push = apply_push;
    }
}
