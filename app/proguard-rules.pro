# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\AndroidStuidoSdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-dontwarn com.ikoding.app.biz.dataobject.**
-dontwarn com.alibaba.fastjson.**
-dontwarn com.letv.pp.**
-dontwarn com,iflytek.**
-dontwarn com.tencent.stat.**
-dontwarn com.nostra13.universal.**
-dontwarn de.hdodenhof.circleimageview.**
-dontwarn de.greenrobot.event.**
-dontwarn com.android.volley.**
-dontwarn com.google.gson.**
-dontwarn com.sina.**

#-libraryjars libs/SocialSDK_QQZone_2.jar

-keep public class com.alibaba.fastjson.* {*;}
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep enum com.facebook.**
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep public class com.chaojishipin.sarrs.R$*{
   public static final int *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepattributes Exceptions,InnerClasses,Signature
# 不混淆注解
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep class com.sina.** {*;}
-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
 #js 截流
-keep public class android.webkit.**
-keep public class de.hdodenhof.circleimageview.** {*;}

-keep class com.facebook.**
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.activeandroid.** { *; }
-keep public class com.ikoding.app.biz.dataobject.** { *;}

-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}

-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

-keep class com.letv.pp.** {*;}
-keep class com.iflytek.** {*;}
-keep class com.tencent.stat.** {*;}
-keep class com.nostra13.universalimageloader.** {*;}
-keep class com.android.support.g.** { *;}
-keep class com.android.support.v4.** { *;}
-keepclassmembers class ** {
    public void on*Event(...);
}
-keepclasseswithmembernames class * {
  native <methods>;
}
-keep class de.greenrobot.event.** {*;}
-keep class de.greenrobot.event {*;}
#-keep com.android.volley.** {*;}
#-keep com.google.gson.** {*;}

-keep class **.R$*{*; }
-keep class **.R {*;}
-keepclasseswithmembers class com.media.ffmpeg.FFMpegPlayer {
    <fields>;
    <methods>;
}
-keepclasseswithmembers class com.media.NativePlayer {
    <fields>;
    <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keepattributes JavascriptInterface
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}
-keep class com.alibaba.fastjson.** { *; }
# keep 所有的 javabean
-keep class com.letv.http.bean.** {*;}
-keep class com.chaojishipin.sarrs.bean.**{*;}

# keep 泛型
-keepattributes Signature

-keep public class * implements java.io.Serializable {
    public *;
}

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
#keep 自定义View
-keep public class * extends android.view.View{
   public <init>(android.content.Context);
   public <init>(android.content.Context,android.util.AttributeSet);
   public <init>(android.content.Context,android.util.AttributeSet,int);
}

#keep Executors 等thread类
-keep class com.chaojishipin.sarrs.thread.**{*;}
-keep class java.util.concurrent.**{*;}
#keep js截流代码
-keep class **.VideoPlayerController$*{*;}
-keep class **.DownloadJob*{*;}
-keep class * implements java.lang.Runnable {
   pulblic *;
}


#混淆前后的映射
-printmapping mapping.txt


