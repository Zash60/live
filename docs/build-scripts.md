# Build Scripts Configuration

This document describes the Gradle build configuration for LiveApp, including build variants, signing configurations, and optimization settings.

## Project Structure

The project uses a multi-module architecture with the following modules:

- `app/`: Main application module
- `core/`: Shared utilities and common components
- `domain/`: Business logic layer
- `data/`: Data access layer
- `features/`: Feature-specific modules (auth, streaming, chat, settings)

## Root Build Configuration

### build.gradle.kts (Root)

```kotlin
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("com.android.library") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.5" apply false
}
```

## App Module Configuration

### build.gradle.kts (App)

#### Plugins
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}
```

#### Android Configuration
```kotlin
android {
    namespace = "com.example.liveapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.liveapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // API Keys from local.properties
        val properties = Properties().apply {
            val localProperties = rootProject.file("local.properties")
            if (localProperties.exists()) {
                load(localProperties.inputStream())
            }
        }

        buildConfigField("String", "YOUTUBE_API_KEY", "\"${properties.getProperty("YOUTUBE_API_KEY", "")}\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${properties.getProperty("GOOGLE_CLIENT_ID", "")}\"")
    }
}
```

#### Signing Configurations
```kotlin
signingConfigs {
    create("debug") {
        storeFile = rootProject.file("debug.keystore")
        storePassword = "android"
        keyAlias = "androiddebugkey"
        keyPassword = "android"
    }

    create("release") {
        storeFile = rootProject.file("release.keystore")
        storePassword = System.getenv("RELEASE_STORE_PASSWORD") ?: "default_password"
        keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: "default_alias"
        keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: "default_password"
    }
}
```

#### Build Types
```kotlin
buildTypes {
    debug {
        applicationIdSuffix = ".debug"
        signingConfig = signingConfigs.getByName("debug")
        isMinifyEnabled = false
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        buildConfigField("String", "BASE_URL", "\"https://api.example.com/debug\"")
    }

    release {
        signingConfig = signingConfigs.getByName("release")
        isMinifyEnabled = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        buildConfigField("String", "BASE_URL", "\"https://api.example.com\"")
        isShrinkResources = true
        isDebuggable = false
    }

    create("staging") {
        initWith(getByName("debug"))
        applicationIdSuffix = ".staging"
        matchingFallbacks.add("debug")
        buildConfigField("String", "BASE_URL", "\"https://api.example.com/staging\"")
    }
}
```

#### Product Flavors
```kotlin
flavorDimensions.add("environment")
productFlavors {
    create("development") {
        dimension = "environment"
        applicationIdSuffix = ".dev"
        versionNameSuffix = "-dev"
    }

    create("production") {
        dimension = "environment"
        versionNameSuffix = "-prod"
    }
}
```

#### Bundle Configuration
```kotlin
bundle {
    language {
        enableSplit = false
    }
    density {
        enableSplit = true
    }
    abi {
        enableSplit = true
    }
}
```

## Build Variants

The following build variants are available:

| Variant | Description | Application ID | Minify | Signing |
|---------|-------------|----------------|--------|---------|
| debug | Development build | com.example.liveapp.debug | No | Debug keystore |
| release | Production build | com.example.liveapp | Yes | Release keystore |
| staging | Staging build | com.example.liveapp.staging | No | Debug keystore |
| developmentDebug | Dev flavor debug | com.example.liveapp.dev.debug | No | Debug keystore |
| developmentRelease | Dev flavor release | com.example.liveapp.dev | Yes | Release keystore |
| productionDebug | Prod flavor debug | com.example.liveapp.debug | No | Debug keystore |
| productionRelease | Prod flavor release | com.example.liveapp | Yes | Release keystore |

## ProGuard Configuration

### proguard-rules.pro

The ProGuard rules are configured to preserve:

- Data classes and domain models
- Hilt-generated classes
- Compose and lifecycle classes
- Room database classes
- Retrofit and OkHttp classes
- Google API and YouTube classes
- Kotlin coroutines and serialization classes

## Dependencies

### Core Dependencies
```kotlin
dependencies {
    // Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")

    // Compose
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.material3:material3:1.1.1")
    // ... other Compose dependencies

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    // ... other core dependencies
}
```

### Testing Dependencies
```kotlin
dependencies {
    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Instrumentation Testing
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")
}
```

## Build Commands

### Common Gradle Commands

```bash
# Clean build
./gradlew clean

# Build all variants
./gradlew build

# Build specific variant
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew assembleStaging

# Install on device
./gradlew installDebug
./gradlew installRelease

# Run tests
./gradlew test
./gradlew connectedAndroidTest

# Generate bundle
./gradlew bundleRelease
```

### CI/CD Build Commands

For automated builds, use:

```bash
# Build and test
./gradlew clean build test

# Build release bundle
./gradlew clean bundleRelease

# Build APK for testing
./gradlew assembleDebug assembleRelease
```

## Environment Variables

For CI/CD pipelines, set the following environment variables for release builds:

- `RELEASE_STORE_PASSWORD`: Password for the release keystore
- `RELEASE_KEY_ALIAS`: Alias for the release key
- `RELEASE_KEY_PASSWORD`: Password for the release key

## local.properties

Create a `local.properties` file in the project root for local development:

```properties
# API Keys
YOUTUBE_API_KEY=your_youtube_api_key
GOOGLE_CLIENT_ID=your_google_client_id

# Keystore paths (optional, defaults will be used)
debug.keystore.path=/path/to/debug.keystore
release.keystore.path=/path/to/release.keystore
```

**Security Note**: Never commit `local.properties` to version control.

## Optimization Tips

1. **Enable R8**: Ensure `isMinifyEnabled = true` for release builds
2. **Resource shrinking**: Set `isShrinkResources = true` for release
3. **Bundle splitting**: Configure bundle splits for smaller APKs
4. **Build cache**: Use `--build-cache` for faster builds
5. **Parallel builds**: Use `--parallel` for multi-module projects

## Troubleshooting

### Common Build Issues

1. **API Key not found**: Ensure `local.properties` exists with correct keys
2. **Signing config errors**: Check keystore paths and passwords
3. **ProGuard warnings**: Review and update ProGuard rules as needed
4. **Dependency conflicts**: Use `./gradlew app:dependencies` to analyze conflicts

### Performance Optimization

- Use `org.gradle.parallel=true` in `gradle.properties`
- Enable Gradle daemon with `org.gradle.daemon=true`
- Configure build cache with `org.gradle.caching=true`