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

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firebase Realtime Database
-keep class com.google.firebase.database.** { *; }
-keep class com.google.firebase.database.ktx.** { *; }

# Firebase Auth
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.auth.ktx.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Navigation Compose
-keep class androidx.navigation.** { *; }
-keep class androidx.navigation.compose.** { *; }

# OpenCSV
-keep class com.opencsv.** { *; }
-dontwarn com.opencsv.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Data models
-keep class com.example.e_posyandu.data.model.** { *; }
-keepclassmembers class com.example.e_posyandu.data.model.** {
    <fields>;
}

# ViewModels
-keep class com.example.e_posyandu.ui.viewmodel.** { *; }
-keepclassmembers class com.example.e_posyandu.ui.viewmodel.** {
    <fields>;
}

# Repository
-keep class com.example.e_posyandu.data.repository.** { *; }
-keepclassmembers class com.example.e_posyandu.data.repository.** {
    <fields>;
}

# UI Components
-keep class com.example.e_posyandu.ui.component.** { *; }
-keep class com.example.e_posyandu.ui.screen.** { *; }

# Utilities
-keep class com.example.e_posyandu.util.** { *; }

# Keep serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep R classes
-keep class **.R$* {
    public static <fields>;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep generic signatures
-keepattributes Signature

# Keep exceptions
-keepattributes Exceptions

# Keep inner classes
-keepattributes InnerClasses

# Keep annotations
-keepattributes *Annotation*

# Keep source file names for debugging
-keepattributes SourceFile,LineNumberTable

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# Keep JSON classes for Firebase
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep Retrofit classes (if using)
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Keep OkHttp classes (if using)
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Keep Jackson classes (if using)
-dontwarn com.fasterxml.jackson.**
-keep class com.fasterxml.jackson.** { *; }

# Keep Moshi classes (if using)
-dontwarn com.squareup.moshi.**
-keep class com.squareup.moshi.** { *; }

# Keep Room classes (if using)
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep WorkManager classes (if using)
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker

# Keep Hilt classes (if using)
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager { *; }

# Keep Kotlin reflection
-keep class kotlin.reflect.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Kotlin serialization (if using)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep Kotlin time (if using)
-keep class kotlin.time.** { *; }

# Keep Kotlin collections
-keep class kotlin.collections.** { *; }

# Keep Kotlin sequences
-keep class kotlin.sequences.** { *; }

# Keep Kotlin ranges
-keep class kotlin.ranges.** { *; }

# Keep Kotlin functions
-keep class kotlin.Function { *; }
-keep class kotlin.Function0 { *; }
-keep class kotlin.Function1 { *; }
-keep class kotlin.Function2 { *; }

# Keep Kotlin properties
-keep class kotlin.properties.** { *; }

# Keep Kotlin reflection
-keep class kotlin.reflect.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Kotlin serialization (if using)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep Kotlin time (if using)
-keep class kotlin.time.** { *; }

# Keep Kotlin collections
-keep class kotlin.collections.** { *; }

# Keep Kotlin sequences
-keep class kotlin.sequences.** { *; }

# Keep Kotlin ranges
-keep class kotlin.ranges.** { *; }

# Keep Kotlin functions
-keep class kotlin.Function { *; }
-keep class kotlin.Function0 { *; }
-keep class kotlin.Function1 { *; }
-keep class kotlin.Function2 { *; }

# Keep Kotlin properties
-keep class kotlin.properties.** { *; }