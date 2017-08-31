##--------------- Default Settings  ----------
-optimizationpasses 10
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-dontnote
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.app.Activity{
    public void *(android.view.View);
}
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keep class * extends android.webkit.WebViewClient
-keepclassmembers class * extends android.webkit.WebViewClient {
    <methods>;
}

-keepattributes Signature

-keep public class **.R

-dontwarn java.**
-dontnote java.**
-keep class java.** { *; }

-dontwarn javax.**
-dontnote javax.**
-keep class javax.** { *; }

-dontwarn org.**
-dontnote org.**
-keep class org.** { *; }

-dontwarn android.**
-dontnote android.**
-keep class android.** { *; }

-dontwarn com.android.**
-dontnote com.android.**
-keep class com.android.** { *; }

-dontwarn com.google.**
-dontnote com.google.**
-keep class com.google.** { *; }

-dontwarn org.w3c.**
-dontnote org.w3c.**
-keep class org.w3c.** { *; }

-dontwarn com.facebook.**
-dontnote com.facebook.**
-keep class com.facebook.** { *; }

##---------------End: Default Settings  ----------

##--------------- Gson  ----------
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }

##--------------- Jackson  ----------
-keep class com.fasterxml.** { *; }
-keep interface com.fasterxml.** {*;}
-dontwarn com.fasterxml.**

##--------------- Butterknife  ----------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

##--------------- square Product ----------
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn com.squareup.**
-keep class com.squareup.** { *; }

#-------------- Log ----------------
-assumenosideeffects class android.util.Log {
    <methods>;
}
-assumenosideeffects class jp.promin.android.blackhistory.utils.BHLogger {
   <methods>;
}
-keepattributes Signature

#------------- RxJava ------------
-dontwarn rx.**
-dontnote rx.**
-keep class rx.**  { *; }

-dontwarn sun.misc.Unsafe
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}

#------------- Twitter4j -------------
-dontwarn twitter4j.**
-dontnote twitter4j.**
-keep class twitter4j.**  { *; }

#------------ Realm ----------
-dontwarn io.realm.**
