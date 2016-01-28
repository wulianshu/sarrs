package com.chaojishipin.sarrs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author wangyemin
 *         公共SharedPreferences 类
 */
public class SharePreferenceManager {

    private final static String COMMON_SP_NAME = "commonsp";

    public final static String SHARE_GUIDE_FLAG = "shareguideflag";
    public final static String SUBCHANNEL_POST_FLAG = "shareguideflag";

    public static void saveVaule(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(COMMON_SP_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void saveVaule(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(COMMON_SP_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private static boolean getVaule(Context context, String key, boolean defValue) {
        boolean flag = false;
        try {
            SharedPreferences sp =
                    context.getSharedPreferences(COMMON_SP_NAME, Context.MODE_PRIVATE);
            flag = sp.getBoolean(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 获取boolean类型数据 （默认返回false）
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getVaule(Context context, String key) {
        return getVaule(context, key, false);
    }

    /**
     * 获取String类型数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getVaule(Context context, String key, String defValue) {
        String flag = defValue;
        try {
            SharedPreferences sp =
                    context.getSharedPreferences(COMMON_SP_NAME, Context.MODE_PRIVATE);
            flag = sp.getString(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

}
