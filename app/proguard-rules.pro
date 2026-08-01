# ============================================
# 🌿 SANA - Reglas de ProGuard/R8
# Ofuscación y optimización para release
# ============================================

# === REGLAS GENERALES ===
# Mantener anotaciones
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions

# === KOTLIN ===
# Mantener clases de Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# === CORRUTINAS ===
# Mantener corrutinas de Kotlin
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# === COMPOSE ===
# Mantener funciones Composable
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# === ROOM DATABASE ===
# Mantener entidades y DAOs de Room
-keep class com.sana.app.core.database.entities.** { *; }
-keep class com.sana.app.core.database.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# === HILT ===
# Mantener módulos de Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-dontwarn dagger.hilt.**

# === RETROFIT / OKHTTP ===
# Mantener clases de red
-keep class com.sana.app.core.network.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# === GSON ===
# Mantener clases serializables con Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.sana.app.core.network.models.** { *; }

# === LOTTIE ===
# Mantener animaciones Lottie
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# === DATASTORE ===
# Mantener clases de DataStore
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# === MANTENER MODELOS DE DATOS ===
# Mantener todas las clases de datos (data classes)
-keep class com.sana.app.**.model.** { *; }
-keep class com.sana.app.**.dto.** { *; }

# === MANTENER ENUMS ===
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# === MANTENER PARCELABLES ===
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# === QUITAR LOGS EN RELEASE ===
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}