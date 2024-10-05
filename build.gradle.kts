// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
}
group = "com.github.shanggeshijidefeng" // 替换为你的 GitHub 用户名或公司名
version = "0.0.3" // 版本号