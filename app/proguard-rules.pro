# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
-allowaccessmodification
-dontusemixedcaseclassnames
-verbose

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*

# Okio
-dontwarn okio.**

# Kotlin
-dontwarn kotlin.**
# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Kotlin Serialization
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class com.illiarb.peek.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor
-dontwarn org.slf4j.**

# Compose
-keep class androidx.compose.** { *; }
-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Circuit
-dontwarn androidx.compose.animation.AnimatedContentScope
