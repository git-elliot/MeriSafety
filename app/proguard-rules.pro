# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/paras/Android/Sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#// Basic proguard rules

-keepattributes InnerClasses
-keepattributes EnclosingMethod

#Removing logging code
-assumenosideeffects class android.util.Log {
public static *** d(...);
public static *** v(...);
public static *** i(...);
public static *** w(...);
public static *** e(...);
}

#The -dontwarn option tells ProGuard not to complain about some artefacts in the Scala runtime

-dontwarn android.support.**
-dontwarn android.app.Notification
-dontwarn org.apache.**
-dontwarn com.google.common.**
-dontwarn com.sun.mail.imap.**
-dontwarn org.apache.harmony.awt.**
-dontwarn javax.security.**
-dontwarn java.awt.**
-dontwarn javax.activation.**
-dontwarn java.lang.invoke**
-dontwarn org.apache.httpcomponents.**
-dontwarn org.apache.http.**

-ignorewarnings

-keep class * {
    public private *;
}

