# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\StaticProgram\Android\sdk/tools/proguard/proguard-android.txt
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

-ignorewarnings

##############################
# rule of jpush
-dontwarn cn.jpush.**
-keep class cn.jpush.**{*;}

##############################
# rule of library projects
-keep class com.nineoldandroids.**{*;}

##############################
# rule of android support
-keep class android.support.**{*;}

##############################
# rule of umeng analytics
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class cn.wanther.toolkit.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}