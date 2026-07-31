-dontwarn
-dontshrink
-dontoptimize

# Keep the complete customer-facing binary API stable. Private implementation
# details can still be renamed by ProGuard.
-keepnames public class com.zqnt.sdk.edge.**
-keepclassmembers public class com.zqnt.sdk.edge.** {
    public protected *;
}
-keep public interface com.zqnt.sdk.edge.** { *; }
-keep public enum com.zqnt.sdk.edge.** { *; }

# Preserve metadata used by generic signatures, annotations, Lombok-generated
# nested builders and framework/runtime inspection.
-keepattributes Signature,*Annotation*,InnerClasses,EnclosingMethod,MethodParameters,Exceptions

# Do not expose local source paths in stack traces.
-renamesourcefileattribute SourceFile
