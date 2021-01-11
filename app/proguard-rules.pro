# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/apple/Library/Android/sdk/tools/proguard/proguard-android.txt
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
# This is added for okhttp 3.1.2 bug fix as shown at https://github.com/square/okhttp/issues/2323
#paypal
    -keepclassmembers class * implements javax.net.ssl.SSLSocketFactory {
         private javax.net.ssl.SSLSocketFactory delegate;
    }
#alipay
    -libraryjars libs/alipaySDK-(Date).jar -keep class com.alipay.android.app.IAlixPay{*;}
    -keep class com.alipay.android.app.IAlixPay$Stub{*;}
    -keep class com.alipay.android.app.IRemoteServiceCallback{*;}
    -keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
    -keep class com.alipay.sdk.app.PayTask{ public *;}
    -keep class com.alipay.sdk.app.AuthTask{ public *;}
