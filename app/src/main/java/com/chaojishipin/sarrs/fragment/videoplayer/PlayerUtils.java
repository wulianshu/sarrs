package com.chaojishipin.sarrs.fragment.videoplayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import com.chaojishipin.sarrs.utils.ConstantUtils;
import com.chaojishipin.sarrs.utils.LogUtil;
import com.chaojishipin.sarrs.utils.Utils;
import com.letv.component.player.LetvVideoViewBuilder.Type;

@SuppressLint("NewApi")
public class PlayerUtils {
    /**
     * 影片播放正常的最小位置
     */
    public final static int PLAY_CORRECT_MIN_POSITION = 0;
    
    public final static int API_14 = 14;
    
    public final static int API_8 = 8;
    
    public final static String FIRST = "first";
    
    public final static String END = "end";

    public final static String LOCAL_M3U8_DOMAIN = "http://127.0.0.1:8084";

    public final static String DOWNLOAD_M3U8 = "m3u8";

    public final static String DOWNLOAD_MP4 = "mp4";

    private final static String TAG = "PlayerUtils";

    public static final String SUPERURL = "SuperUrl";

    public static final String HIGHURL = "HighUrl";

    public static final String STANDARDURL = "StandardUrl";

    public static final String SMOOTHURL = "SmoothUrl";
    
    public static final String PLAY = "play";


    public static final String Request_Api_OK="A00004";
    public static final String Request_Api_ERROR="A00001";

    /**
     * 网络变化意图
     */
    public static final String CONNECTIVTY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    /**
     * 默认播放器的解码类型
     */
    public static Type DEFAULT_PLAYER_TYPE = PlayDecodeMananger.getCurrPlayerType();

    public static String M300 = "M300";

    public static String M301 = "M301";

    public static String M400 = "M400";

    public static String M401 = "M401";

    public static String M310 = "M310";

    public static String M311 = "M311";

    public static String M312 = "M312";

    public static String M410 = "M410";

    public static String M411 = "M411";

    public static String M412 = "M412";

    public static String M500 = "M500";

    public static String SIET_LETV = "letv";

    public static String SITE_CLOUDDISK = "nets";
    /**
     * 流畅
     */
    public static final String PLS_MP4 = "252009";
    /**
     * 标清
     */
    public static final String PLS_MP4_350 = "252021";
    /**
     * 高清
     */
    public static final String PLS_MP4_720p_db = "252022";

    public static final String mp4_800 = "252013";
    public static final String PLS_TSS=PLS_MP4_720p_db+","+PLS_MP4_350+","+PLS_MP4+","+mp4_800;


    public static final String VIDEO_MP4 = "9";

    /**
     * 标清
     */
    public static final String VIDEO_MP4_350 = "21";
    /**
     * 高清
     */
    public static final String VIDEO_MP4_720_db = "22";

    public static final String VIDEONAME_DI = "第";

    public static final String VIDEONAME_JI = "集";

    public static final String SPACE = " ";

    public static final String SITE_YOUKU = "youku";

    public static final String SITE_TUDOU = "tudou";

    public static final String SITE_QQ = "qq";

    public static final String SITE_MANGGUO = "imgo";

    public static final String SITE_PPTV = "pptv";
    
    public static final String SITE_PPS = "pps";
    
    public static final String SITE_IQYI = "iqiyi";
    
    public static final String PPS_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0";

    public static final String SITE_IFENG = "ifeng";

    public static final String SITE_SOHU = "sohu";

    public static final String SITE_KU6 = "ku6";

    public static final String SITE_XUNLEI = "xunlei";
    
    public static final String XIAOMI_LOCAL_VERSION = "4.4.4";
    
    public static final String XIAOMI = "xiaomi";
    
    public static final String MI = "MI";

    public static final String MP4 = "mp4";

    public static final String M3U8 = "m3u8";

    public static final long GHZ_LOW = 1200000;

    public static final long GHZ_HIGH = 1500000;

    public static final int STANDARD_CPU_CORE = 4;

    public static final int YOUKU_ANALYSIS_TAG = 0;

    public static final int QQ_ANALYSIS_TAG = YOUKU_ANALYSIS_TAG + 1;

    public static final int IMGO_ANALYSIS_TAG = QQ_ANALYSIS_TAG + 1;

    public static final int KANKAN_ANALYSIS_TAG = IMGO_ANALYSIS_TAG + 1;

    public static final int PPTV_ANALYSIS_TAG = KANKAN_ANALYSIS_TAG + 1;

    public static final int IFENG_ANALYSIS_TAG = PPTV_ANALYSIS_TAG + 1;

    public static final int SOUHU_ANALYSIS_TAG = IFENG_ANALYSIS_TAG + 1;

    public static final int KU6_ANALYSIS_TAG = SOUHU_ANALYSIS_TAG + 1;

    public static final int KANKAN_SECOND_ANALYSIS_TAG = KU6_ANALYSIS_TAG + 1;
    
    public static final String MOBILEAGENT = "Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; NX503A Build/JDQ39) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 V1_AND_SQ_5.1.1_158_YYB_D QQ/5.1.1.2245 Chrome/18.0.1025.166";

    public static final String PCAGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0";

    public static final String SPLATID = "1041";
    
    public static final String PLATID = "10";
    
    public static void hideVirtualKey(Window window) {
//        View view = window.getDecorView();
//        window.clearFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        window.clearFlags(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

    public static void showVirtualKey(Window window) {
//        View view = window.getDecorView();
//        window.clearFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        window.clearFlags(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    /**
     * 返回00:00格式
     * 
     * @param time
     * @return zhangshuo 2014年4月28日 下午4:31:05
     */
    public static String toStringTime(int time) {
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
//        LogUtils.e(TAG, "!!!!!格式化前的时间!!!!!!!!"+time);
        try {
            if (time <= 0) {
                time = 0;
            }
            time -= TimeZone.getDefault().getRawOffset();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String currtime = simpleDateFormat.format(time);
//            int totalSeconds = time / 1000;
//            int seconds = totalSeconds % 60;
//            int totalminutes = totalSeconds / 60;
//            int minutes = totalminutes %60;
//            int hour =
//            formatBuilder.setLength(0);
//            String currtime = formatter.format("%02d:%02d", minutes, seconds).toString();
//            LogUtils.e(TAG, "!!!!!!格式化后的时间!!!!!!"+currtime);
            return currtime;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            formatter.close();
        }
        return null;
    }

    /**
     * 判断视频是否正在缓冲
     * 
     * @param preSecondPositon
     * @param nowPosition
     * @return zhangshuo 2014年5月11日 下午4:43:39
     */
    public static boolean judgeBuffering(int preSecondPositon, int nowPosition) {
        if (preSecondPositon > 0) {
            int isBuffer = nowPosition - preSecondPositon;
            LogUtil.e(TAG, "!!!!!!!!!当前播放位置间隔!!!!!!!" + isBuffer);
            return (isBuffer > -500 && isBuffer < 500);
        }
        return false;
    }

//    public static String getVideoName(String vt, String name, Episode episode) {
//        StringBuffer nameBuff = new StringBuffer();
//        if (null != episode) {
//            boolean isNameNull = TextUtils.isEmpty(name);
//            if (isNameNull) {
//                nameBuff.append("");
//            } else {
//                nameBuff.append(name);
//            }
//            if (MoviesConstant.VT_TV.equals(vt) || MoviesConstant.VT_CARTOON.equals(vt)) {
//                if (!isNameNull) {
//                    nameBuff.append(SPACE);
//                    nameBuff.append(SPACE);
//                }
//                String porder = episode.getPorder();
//                if (!TextUtils.isEmpty(porder)) {
//                    nameBuff.append(VIDEONAME_DI);
//                    nameBuff.append(SPACE);
//                    nameBuff.append(porder);
//                    nameBuff.append(SPACE);
//                    nameBuff.append(VIDEONAME_JI);
//                }
//            } else if (MoviesConstant.VT_ZONGYI.equals(vt)) {
//                if (!isNameNull) {
//                    nameBuff.append(SPACE);
//                }
//                String porder = episode.getPorder();
//                if (!TextUtils.isEmpty(porder)) {
//                    nameBuff.append(porder);
//                }
//                String subName = episode.getSubName();
//                if (!TextUtils.isEmpty(subName)) {
//                    nameBuff.append(SPACE);
//                    nameBuff.append(subName);
//                }
//            } else if (MoviesConstant.VT_MOVIE.equals(vt)) {
//                return nameBuff.toString();
//            } else {
//                if (!isNameNull) {
//                    nameBuff.append(SPACE);
//                }
//                String porder = episode.getPorder();
//                if (!TextUtils.isEmpty(porder)) {
//                    nameBuff.append(porder);
//                }
//                String subName = episode.getSubName();
//                if (!TextUtils.isEmpty(subName)) {
//                    nameBuff.append(SPACE);
//                    nameBuff.append(subName);
//                }
//            }
//        }
//        return nameBuff.toString();
//    }

    public static void sendPlayingBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.funshion.video.CUTDOWNLOADSPEED");
        intent.putExtra("player", true);
        context.sendBroadcast(intent);
    }

    public static void sendNotPlayingBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.funshion.video.CUTDOWNLOADSPEED");
        intent.putExtra("player", false);
        context.sendBroadcast(intent);
    }

//    public static ArrayList<UrlAnalysisParamter> ruleParser(JSONObject obj) {
//        try {
//            JSONArray rule = obj.optJSONArray("rule");
//            if (null != rule) {
//                int size = rule.length();
//                ArrayList<UrlAnalysisParamter> analysisParamters =
//                        new ArrayList<UrlAnalysisParamter>(size);
//                for (int m = 0; m < size; m++) {
//                    JSONObject ruleItem = rule.optJSONObject(m);
//                    UrlAnalysisParamter analysisParamter = new UrlAnalysisParamter();
//                    analysisParamter.setVid(ruleItem.optString("$VID"));
//                    analysisParamter.setType(ruleItem.optString("$TYPE"));
//                    analysisParamter.setFile(ruleItem.optString("$FILE"));
//                    analysisParamter.setCode(ruleItem.optString("$CODE"));
//                    analysisParamter.setKk(ruleItem.optString("$KK"));
//                    analysisParamter.setC1(ruleItem.optString("$C1"));
//                    analysisParamter.setC2(ruleItem.optString("$C2"));
//                    analysisParamter.setApi_key(ruleItem.optString("$API_KEY"));
//                    analysisParamter.setPlat(ruleItem.optString("$PLAT"));
//                    analysisParamter.setSver(ruleItem.optString("$SVER"));
//                    analysisParamter.setPartner(ruleItem.optString("$PARTNER"));
//                    analysisParamter.setPath(ruleItem.optString("$PATH"));
//                    analysisParamter.setGcid(ruleItem.optString("$GCID"));
//                    analysisParamters.add(analysisParamter);
//                }
//                return analysisParamters;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static boolean isOutSite(PlayData playData) {
//        String site = playData.getSite();
//        return (!SIET_LETV.equalsIgnoreCase(site) && !SITE_CLOUDDISK.equalsIgnoreCase(site));
//    }

    public static boolean isOutSite(String site) {
        return (!SIET_LETV.equalsIgnoreCase(site) && !SITE_CLOUDDISK.equalsIgnoreCase(site));
    }

    /**
     * 获取当前默认清晰度
     *
     * @return zhangshuo 2014年10月15日 下午3:01:49
     */
    public static String getPlayDefinition() {
        String definition = STANDARDURL;
        try {
            int numCores = Utils.getNumCores();
            LogUtil.e(TAG, "!!!!!!当前CPU核数!!!!!" + numCores);
            String cpuHz = Utils.getMaxCpuFreq();
            LogUtil.e(TAG, "!!!!!!!!!当前CPU赫兹数!!!!!!!" + cpuHz);
            long cpuHzLong = getLongCpuHz(cpuHz);
            // 首先获取CPU核数
            if (numCores < STANDARD_CPU_CORE) {
                if (!TextUtils.isEmpty(cpuHz) && !"N/A".equals(cpuHz)) {
                    if (cpuHzLong <= GHZ_LOW) {
                        definition = SMOOTHURL;
                    } else {
                        definition = STANDARDURL;
                    }
                }
            } else {
                definition = HIGHURL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return definition;
    }

    private static long getLongCpuHz(String cpuHz) {
        long cpuHzLong = 0;
        try {
            cpuHz = replaceBlank(cpuHz);
            cpuHzLong = Long.parseLong(cpuHz.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpuHzLong;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    public static void setPlayMapVaule(HashMap<String, String> playUrlMap, JSONObject obj) {
        String smoothUrl = obj.optString(SMOOTHURL);
        String standardUrl = obj.optString(STANDARDURL);
        String highUrl = obj.optString(HIGHURL);
        String superUrl = obj.optString(SUPERURL);
        if (!TextUtils.isEmpty(smoothUrl)) {
            playUrlMap.put(SMOOTHURL, smoothUrl);
        }
        if (!TextUtils.isEmpty(standardUrl)) {
            playUrlMap.put(STANDARDURL, standardUrl);
        }
        if (!TextUtils.isEmpty(highUrl)) {
            playUrlMap.put(HIGHURL, highUrl);
        }
        if (!TextUtils.isEmpty(superUrl)) {
            playUrlMap.put(SUPERURL, superUrl);
        }
    }

//    public static String getVideoName(PlayData playdata, String vt, String name, Episode episode) {
//        StringBuffer nameBuff = new StringBuffer();
//        try {
//            if (null != episode) {
//                boolean isNameNull = TextUtils.isEmpty(name);
//                if (isNameNull) {
//                    nameBuff.append("");
//                } else {
//                    nameBuff.append(name);
//                }
//                if (MoviesConstant.VT_TV.equals(vt) || MoviesConstant.VT_CARTOON.equals(vt)) {
//                    if (!isNameNull) {
//                        nameBuff.append(SPACE);
//                        nameBuff.append(SPACE);
//                    }
//
//                    String nameBuffStr = nameBuff.toString();
//                    if(TextUtils.isEmpty(nameBuffStr)){
//                        nameBuffStr = playdata.getmPlayRecord().getName();
//                    }
//                    if (-1 != nameBuffStr.indexOf(VIDEONAME_DI)
//                            && -1 != nameBuffStr.indexOf(VIDEONAME_JI)) {
//                        nameBuffStr = nameBuffStr.substring(0, nameBuffStr.indexOf(VIDEONAME_DI));
//                        nameBuff.delete(0, nameBuff.length());
//                        nameBuff.append(nameBuffStr);
//                    }
//                    String porder = episode.getPorder();
//                    if (!TextUtils.isEmpty(porder)) {
//                        nameBuff.append(VIDEONAME_DI);
//                        nameBuff.append(SPACE);
//                        nameBuff.append(porder);
//                        nameBuff.append(SPACE);
//                        nameBuff.append(VIDEONAME_JI);
//                    }
//                } else if (MoviesConstant.VT_ZONGYI.equals(vt)) {
//                    if (!isNameNull) {
//                        nameBuff.append(SPACE);
//                        if (null != playdata && null != playdata.getmPlayRecord()
//                                && !TextUtils.isEmpty(playdata.getmPlayRecord().getPorder())) {
//                            String porder = playdata.getmPlayRecord().getPorder();
//                            String nameBuffStr = nameBuff.toString();
//                            if (-1 != nameBuffStr.indexOf(porder)) {
//                                nameBuffStr = nameBuffStr.substring(0, nameBuffStr.indexOf(porder));
//                                playdata.setmViewName(nameBuffStr);
//                                nameBuff.delete(0, nameBuff.length());
//                                nameBuff.append(nameBuffStr);
//                            }
//                        }
//                    }
//                    String porder = episode.getPorder();
//                    if (!TextUtils.isEmpty(porder)) {
//                        nameBuff.append(porder);
//                    }
//                    String subName = episode.getName();
//                    if (!TextUtils.isEmpty(subName)) {
//                        nameBuff.append(SPACE);
//                        nameBuff.append(subName);
//                    }
//                } else if (MoviesConstant.VT_MOVIE.equals(vt)) {
//                    return nameBuff.toString();
//                } else {
//                    if (!isNameNull) {
//                        nameBuff.append(SPACE);
//                    }
//                    String porder = episode.getPorder();
//                    if (!TextUtils.isEmpty(porder)) {
//                        nameBuff.append(porder);
//                    }
//                    String subName = episode.getSubName();
//                    if (TextUtils.isEmpty(subName)) {
//                        subName = episode.getName();
//                    }
//                    if (!TextUtils.isEmpty(subName)) {
//                        nameBuff.append(SPACE);
//                        nameBuff.append(subName);
//                    }
//                }
//            }
//            return nameBuff.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
    
    /**
     * 传入src，返回相应站点的User-Agent值
     * @param src -- 源
     * @param videoType --m3u8/mp4（暂时没用）
     * @param requestType --播放或下载 play/download
     * @return User-Agent
     */
    public static String getUserAgent(String src,String requestType,String videoType) {
    	if(TextUtils.isEmpty(src))
    		return null;
    	
    	String userAgent = "";
    	if(SITE_IQYI.equals(src) || SITE_MANGGUO.equals(src) || SITE_YOUKU.equals(src)){
    		userAgent = MOBILEAGENT;
    	}
       return userAgent;
    }

    /**
     * 添加用于平台分析的p1\p2\p3的值
     * @param url
     * @return
     * zhangshuo
     * 2015年2月2日 下午5:09:09
     */
    public static String addPlatCode(String url) {
        StringBuffer sbff = new StringBuffer();
        sbff.append(url);
        sbff.append("&p1=0&p2=0l&p3=");
        return sbff.toString();
    }
}
