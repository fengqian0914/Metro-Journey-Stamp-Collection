plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt") // 添加 Kotlin Kapt 插件

}

android {
    namespace = "com.example.MRTAPP"
    compileSdk = 33
    sourceSets {
        getByName("main").res.srcDirs(
            "src/main/res/layout/activity",
            "src/main/res/layout",
            "src/main/res"
        )
    }
    defaultConfig {
        applicationId = "com.example.MRTAPP"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        // 使用 project.properties 獲取 clientId 和 clientSecret
        buildConfigField ("String", "CLIENT_ID", "\"${project.property("CLIENT_ID")}\"")
        buildConfigField ("String", "CLIENT_SECRET", "\"${project.property("CLIENT_SECRET")}\"")
        buildConfigField ("String", "MRT_USERNAME", "\"${project.property("MRT_USERNAME")}\"")
        buildConfigField ("String", "MRT_PASSWORD", "\"${project.property("MRT_PASSWORD")}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
//    implementation ("com.github.MikeOrtiz:TouchImageView:1.4.1") // Android X
//
//    implementation("com.github.getActivity:ToastUtils:10.3")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.davemorrissey.labs:subsampling-scale-image-view:3.10.0")
    implementation ("com.github.iwgang:countdownview:2.1.6")
    implementation ("com.google.android.gms:play-services-vision:19.0.0")
//    implementation ("com.theartofdev.edmodo:android-image-cropper:2.8.0")
    implementation("com.vanniktech:android-image-cropper:4.5.0")

//    implementation ("com.theartofdev.edmodo:android-image-cropper:2.8.+")
    implementation("com.google.android.material:material:1.3.0-alpha03")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-alpha01")


//    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
//    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation ("com.rmtheis:tess-two:9.1.0")
    implementation ("androidx.activity:activity-ktx:1.2.2")
    implementation ("androidx.fragment:fragment-ktx:1.3.2")

    //qrcode
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.4.1")

    implementation ("androidx.appcompat:appcompat:1.3.0")

    //map

    implementation ("org.osmdroid:osmdroid-android:6.1.10")
    implementation ("org.slf4j:slf4j-api:2.0.7")


    //image_radio_btn
//    implementation ("com.github.Gavras:MultiLineRadioGroup:v1.0.0.6")

    //Firebase Libraries
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation ("com.google.firebase:firebase-database:20.3.1")
    implementation ("com.google.firebase:firebase-storage:20.3.0")

//    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    //Circular Image Library
    implementation ("de.hdodenhof:circleimageview:3.1.0")

//    implementation ("com.google.firebase:firebase-database-ktx")
//    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
//    implementation("com.google.firebase:firebase-database")


    //station object
    implementation ("com.google.code.gson:gson:2.10.1")


    // 圖片放大
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
//    implementation ("com.github.yalantis:ucrop:2.2.5")


    implementation ("androidx.viewpager:viewpager:1.0.0")

    //ListOf
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")

    //API
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")

    // bottom_sheet
    implementation ("com.github.travijuu:numberpicker:1.0.7")

    //google firebase login
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation ("com.google.firebase:firebase-firestore:24.7.1")
    //文字大小
    implementation ("com.intuit.ssp:ssp-android:1.0.4")

    //註冊圓形圖片裁切
    implementation ("com.github.yalantis:ucrop:2.2.8")
    implementation ("com.squareup.picasso:picasso:2.8")
    //開頭動畫
    implementation ("com.airbnb.android:lottie:5.2.0")
    //圖片載入
    implementation("com.github.bumptech.glide:glide:4.15.0")
    kapt("com.github.bumptech.glide:compiler:4.15.0") // 使用 kapt
}