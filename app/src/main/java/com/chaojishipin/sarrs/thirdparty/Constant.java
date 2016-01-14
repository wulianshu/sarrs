package com.chaojishipin.sarrs.thirdparty;

import com.chaojishipin.sarrs.ChaoJiShiPinApplication;

public class Constant {
    /**
     * 每次请求视频列表的数量
     */

    public static final boolean DEBUG;

    public static final int REQUEST_VIDEO_SIZE = 10;
    public static final String TIME_TEMPLATE = "yyyy-MM-dd hh:mm:ss";
    public static final int CONFIRM_PLAY_DELAYED = 15000;
    public static final String VERSION_NAME;
    public static final String DEVICE_ID;
    public static final String UUID ;
    public static final String CHANNEL;
    public static final String SUBCHANNEL = Utils.getSubchannel(ChaoJiShiPinApplication.getInstatnce());
    public static final int CATEGORY_REC = 0;
    public static final String APP_KEY = "2938694341";
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    public static final String UPDATE_APP_KEY = "01008020101006800010";
    public static final String PCODE = "";
    public static final String PACKAGE_NAME;
    public static final String SIGNATURE;
    public static final float SIZE_21 = 21;
    public static final float SIZE_14 = 14;
    public static final String IR_UAID = "UA-letv-140001";
    public static final String BRAND_NAME = Utils.getBrandName();
    public static final String SERIAL_NUMBER = Utils.getSerialNumber();

    static {
        DEBUG = JrspConfiguration.isDebug();
        VERSION_NAME = Utils.getVersionName();
        DEVICE_ID = Utils.getDeviceId();
        CHANNEL = Utils.getChannel();
        java.util.UUID uuid = new DeviceUuidFactory(ChaoJiShiPinApplication.getInstatnce()).getDeviceUuid();
        UUID = uuid == null ? "" : uuid.toString();
        PACKAGE_NAME = ChaoJiShiPinApplication.getInstatnce().getPackageName();
        SIGNATURE = Utils.getSignInfo();
    }

    public interface CACHE {
        static final String VERSION = "2";
        static final String CHANNEL_LIST = "channel_list";
        static final String CHANNEL = "channel";
        static final String LOGGED_USER_INFO = "login_user_info";
        static final String HISTORY = "history";
    }
    public interface MAIN_FRAGMENT_PARAMS {
        static final String CID = "cid";
        static final String POSITION = "position";
    }
    public interface SECRET {
        /**
         * 最后少一位7，在使用时加上，防止被反编译破解
         */
        static final String SPREAD_SECRET = "video7~8!5g@9e#6today$7%0^3*le_123app";
        static final String COMMENT_SECRET = "1FCZoxLCzq3QL@9e#SLY4BLjOnDfGkHN7q9BhncHF0U9O5wA";
        static final byte[] AES_KEY = {49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52,
            53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50};
    }
}
