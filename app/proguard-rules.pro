
-optimizationpasses 5

-dontusemixedcaseclassnames

-dontskipnonpubliclibraryclasses

-verbose

-dontskipnonpubliclibraryclassmembers

-dontpreverify

-keepattributes *Annotation*,InnerClasses

-keepattributes Signature

-keepattributes SourceFile,LineNumberTable

-optimizations !code/simplification/cast,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * implements androidx.versionedparcelable.VersionedParcelable

-keep class **.R$* {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

# AndroidX 防止混淆
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

-keepclassmembers class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}

-keep class android.net.**{*;}
-keep class com.android.internal.**{*;}
-keep class org.apache.**{*;}
-keep class * extends android.app.** { *; }

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

-keepclassmembers class ** {
    public void onEvent*(**);
}
-keepclassmembers class ** {
public void xxxxxx(**);
}