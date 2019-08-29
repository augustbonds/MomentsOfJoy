load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_android_library")
load("@rules_jvm_external//:defs.bzl", "artifact")

PACKAGE = "com.augustbonds.momentsofjoy"
MANIFEST = "android/app/src/main/AndroidManifest.xml"

android_binary(
    name = "momentsofjoy-app",
    custom_package = PACKAGE,
    manifest = MANIFEST,
    deps = [
        ":momentsofjoy",
    ],
)

kt_android_library(
    name = "momentsofjoy",
    srcs = glob(["android/app/src/main/java/**/*.kt"]),
    custom_package = PACKAGE,
    manifest = MANIFEST,
    resource_files = glob(["android/app/src/main/res/**/*"]),
    deps = [
        artifact("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.50"),
        artifact("com.android.support.constraint:constraint-layout:1.1.3"),
        artifact("androidx.work:work-runtime-ktx:2.2.0"),
    ],
)
