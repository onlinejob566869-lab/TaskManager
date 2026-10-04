# ─── Room Database ───
-keep class com.taskmanager.app.data.local.entities.** { *; }
-keep class com.taskmanager.app.data.model.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-dontwarn androidx.room.paging.**

# ─── Kotlin Metadata (required for reflection) ───
-keep class kotlin.Metadata { *; }
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions, EnclosingMethod

# ─── Compose ───
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# ─── Coroutines ───
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler { *; }

# ─── DataStore ───
-keep class androidx.datastore.** { *; }

# ─── Navigation ───
-keep class androidx.navigation.** { *; }

# ─── App's own model classes (used in Room/JSON) ───
-keep class com.taskmanager.app.data.model.Task { *; }
-keep class com.taskmanager.app.data.model.Category { *; }
-keep class com.taskmanager.app.data.model.TaskStatus { *; }
-keep class com.taskmanager.app.data.model.Priority { *; }
-keep class com.taskmanager.app.data.model.DarkModePref { *; }

# ─── Enum classes ───
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
