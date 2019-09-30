# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\pingfan\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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
-keepattributes Signature

-ignorewarnings

-dontwarn android.support.v7.**
-keep class android.support.v7.** { *;}

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *;}

-dontwarn android.support.annotation.**
-keep class android.support.annotation.** { *;}

-dontwarn com.android.volley.**
-keep class com.android.volley.** { *;}

-dontwarn com.pgyersdk.**
-keep class com.pgyersdk.** { *;}

-dontwarn com.hp.hpl.sparta.**
-keep class com.hp.hpl.sparta.** { *;}

-dontwarn net.sourceforge.pinyin4j.**
-keep class net.sourceforge.pinyin4j.** { *;}

-dontwarn demo.**
-keep class demo.** { *;}

-dontwarn de.greenrobot.event.**
-keep class de.greenrobot.event.** { *;}

-dontwarn com.google.i18n.phonenumbers.**
-keep class com.google.i18n.phonenumbers.** { *;}

-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.** { *;}

-dontwarn com.umeng.**
-keep class com.umeng.** { *;}

-dontwarn org.apache.http.legacy.**
-keep class org.apache.http.legacy.** { *;}

-dontwarn com.amap.api.**
-keep class com.amap.api.** { *;}

-dontwarn com.tencent.mm.**
-keep class com.tencent.mm.** { *;}

-dontwarn com.baidu.**
-keep class com.baidu.** { *;}

-dontwarn com.lidroid.xutils.**
-keep class com.lidroid.xutils.** { *;}

-dontwarn io.agora.rtc.**
-keep class io.agora.rtc.** { *;}

-dontwarn io.agora.**
-keep class io.agora.** { *;}

-dontwarn io.rong.**
-keep class io.rong.** { *;}

-dontwarn com.facebook.**
-keep class com.facebook.** { *;}

-dontwarn org.greenrobot.**
-keep class org.greenrobot.** { *;}

-dontwarn org.xwalk.core.**
-keep class org.xwalk.core.** { *;}
