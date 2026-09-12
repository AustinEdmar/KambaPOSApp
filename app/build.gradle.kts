plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

hilt{
    enableAggregatingTask = false //gera o codigo hilt mais rapido
}

android {
    namespace = "com.austin.kambapos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.austin.px"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)


    //implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.datastore:datastore-preferences:1.2.0")

    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")



    // Pusher (protocolo usado pelo Reverb)
    implementation("com.pusher:pusher-java-client:2.4.4")

    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    // build.gradle
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.ui)
    implementation(libs.transport.runtime)

    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // WorkManager
    //implementation(libs.androidx.work.runtime)

// Hilt Worker
    implementation(libs.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    //ROOM
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)


    //Sunmi SDK
    implementation(libs.printerlibrary)




    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.compose.foundation.layout)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
