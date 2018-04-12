# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# As mentioned in play billing library documentation.
-keep class com.android.vending.billing.**

# Proguard warns about okhttp being used in Picasso. As the github page of picasso suggests, we've
# added this to proguard rules to suppress the warnings.
-dontwarn com.squareup.okhttp.**

# As per crashlytics documentation
-keepattributes *Annotation*
# Use this if our app is easier to reverse engineer.
#-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
# The following 2 provide faster proguard builds.
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**