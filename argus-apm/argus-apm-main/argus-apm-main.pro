# 编译对外输出用的jar包
# proguard-project_common.txt里面包含需要keep的代码
-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontpreverify

-dontwarn
-dontoptimize
-dontnote **

-verbose

-repackageclasses 'com.argusapm.android'
-renamesourcefileattribute apmsdk

-allowaccessmodification

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,InnerClasses,LineNumberTable
-keepattributes EnclosingMethod


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider


-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep public class * extends android.webkit.WebView


-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keep public class * extends android.view.ViewGroup {
    public boolean shouldDelayChildPressedState();
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
  public static <fields>;
}
-keep public interface com.android.vending.licensing.ILicensingService

-dontnote com.android.vending.licensing.ILicensingService

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class com.argusapm.android.network.cloudrule.** {*;}
-keep class com.argusapm.android.network.IUpload{*;}
-keep class com.argusapm.android.network.upload.CollectDataSyncUpload{*;}

# sdk接口相关
-keep class com.argusapm.android.api.* {
    *;
}
-keep class com.argusapm.android.core.Config{
    *;
}
-keep class com.argusapm.android.core.Config$ConfigBuilder{
    *;
}

-keep class com.argusapm.android.core.job.monitor.MonitorHelper{
    *;
}
-keep class com.argusapm.android.core.job.activity.ActivityHelper{
    *;
}

-keep class com.argusapm.android.core.job.func.AopFuncTrace{
    *;
}

-keep class com.argusapm.android.core.job.io.AopIOTrace{
    *;
}

-keep class com.argusapm.android.core.job.monitor.PowerMonitorHelper{
    *;
}
-keep class com.argusapm.android.core.job.activity.AH{
    *;
}
-keep class com.argusapm.android.core.job.net.i.QHC{
    *;
}
-keep class com.argusapm.android.core.job.net.i.QURL{
    *;
}
-keep class com.argusapm.android.core.job.func.FuncTrace{
    *;
}
-keep class com.argusapm.android.core.job.io.IOFactory{
    *;
}
-keep class com.argusapm.android.core.job.net.i.QOKHttp {
    *;
}
-keep class com.argusapm.android.core.job.webview.AopWebTrace {
    *;
}

-keep class com.argusapm.android.Env {
    *;
}

-keep class com.argusapm.android.core.storage.DbSwitch {
    *;
}

-keep class com.argusapm.android.aop.* {
    *;
}

#apm-okhttp性能采集
-keep class com.argusapm.android.okhttp3.NetWorkInterceptor { *;}
#keep OkHttpClient及OkHttpClient的Builder内部类
-keepattributes Exceptions,InnerClasses,...
-keep class okhttp3.OkHttpClient{
    *;
}
-keep class okhttp3.OkHttpClient$Builder {
    *;
}
