# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/jmbto/Android/Sdk/tools/proguard/proguard-android.txt
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

#-keepparameternames
-keepattributes Signature
-keepattributes InnerClasses

-dontwarn butterknife.internal.ButterKnifeProcessor
-dontskipnonpubliclibraryclasses
#-renamesourcefileattribute SourceFile
#-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
#-keepclassmembernames class * { java.lang.Class class$(java.lang.String); java.lang.Class class$(java.lang.String, boolean); }
-optimizations !code/allocation/variable

# Suppress warnings from javax.servlet
-dontwarn javax.servlet.**
-dontwarn javax.lang.model.**
-dontwarn javax.tools.**
-dontwarn javax.annotation.**

# Uncomment if you want to have more meaningful backtraces
# Useful for obfuscation debugging
# You absolutely must keep this commented out for production
# -keepattributes SourceFile,LineNumberTable

-adaptclassstrings
-adaptresourcefilecontents **.xml
#
-adaptresourcefilenames

# This option removes all package names.
# with -adaptresourcefilenames it may cause some resource files to clash.
# If that happens, try -flattenpackagehierarchy instead
-repackageclasses

-overloadaggressively
-allowaccessmodification
