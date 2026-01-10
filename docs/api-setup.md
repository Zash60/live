# API Setup Guide

This guide provides step-by-step instructions for setting up the required APIs for LiveApp: YouTube Data API v3 and Google OAuth 2.0.

## Prerequisites

- Google Cloud Console account
- A Google Cloud Project
- Android Studio with the project loaded

## YouTube Data API v3 Setup

### 1. Enable the YouTube Data API v3

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Select your project (or create a new one)
3. Navigate to "APIs & Services" > "Library"
4. Search for "YouTube Data API v3"
5. Click on it and enable the API

### 2. Create API Credentials

1. In the Google Cloud Console, go to "APIs & Services" > "Credentials"
2. Click "Create Credentials" > "API key"
3. Restrict the API key:
   - Select "Restrict key"
   - Under "API restrictions", choose "Restrict key"
   - Select "YouTube Data API v3"
   - Save the changes
4. Note down the API key - you'll need it later

### 3. Configure OAuth 2.0 Client ID

1. In "Credentials", click "Create Credentials" > "OAuth 2.0 Client ID"
2. Choose "Android" as the application type
3. Fill in the required fields:
   - **Name**: LiveApp Android Client
   - **Package name**: `com.example.liveapp`
   - **SHA-1 certificate fingerprint**: Get this from your debug keystore
4. Click "Create"
5. Download the JSON file containing your client configuration

## Google OAuth Setup

### 1. Enable Google Sign-In API

1. In Google Cloud Console, go to "APIs & Services" > "Library"
2. Search for "Google Sign-In API"
3. Enable the API

### 2. Configure OAuth Consent Screen

1. Go to "APIs & Services" > "OAuth consent screen"
2. Choose "External" user type
3. Fill in the app information:
   - **App name**: LiveApp
   - **User support email**: Your email
   - **Developer contact information**: Your email
4. Add scopes:
   - `https://www.googleapis.com/auth/youtube`
   - `https://www.googleapis.com/auth/youtube.readonly`
   - `https://www.googleapis.com/auth/youtube.force-ssl`
5. Add test users if needed
6. Save and continue

## Android Project Configuration

### 1. Add API Keys to Local Properties

Create or update `local.properties` in the project root:

```properties
# YouTube Data API v3
YOUTUBE_API_KEY=your_youtube_api_key_here

# Google OAuth
GOOGLE_CLIENT_ID=your_google_client_id_here
GOOGLE_CLIENT_SECRET=your_google_client_secret_here
```

**Security Note**: Never commit `local.properties` to version control. Add it to `.gitignore`.

### 2. Configure Build Variants

For different environments (debug/release), you can use build config fields:

In `app/build.gradle.kts`, add:

```kotlin
android {
    // ... existing config

    buildTypes {
        debug {
            buildConfigField("String", "YOUTUBE_API_KEY", "\"${properties.getProperty("YOUTUBE_API_KEY")}\"")
            buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${properties.getProperty("GOOGLE_CLIENT_ID")}\"")
        }
        release {
            buildConfigField("String", "YOUTUBE_API_KEY", "\"${properties.getProperty("YOUTUBE_API_KEY")}\"")
            buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${properties.getProperty("GOOGLE_CLIENT_ID")}\"")
        }
    }
}
```

### 3. Update Android Manifest

Add the following permissions to `app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- For Google Sign-In -->
<uses-permission android:name="android.permission.GET_ACCOUNTS" />
<uses-permission android:name="android.permission.USE_CREDENTIALS" />
```

Add metadata for Google Sign-In:

```xml
<application>
    <!-- ... existing config -->

    <meta-data
        android:name="com.google.android.gms.version"
        android:value="@integer/google_play_services_version" />

    <!-- Replace with your actual client ID -->
    <meta-data
        android:name="com.google.android.gms.auth.api.signin.CLIENT_ID"
        android:value="your_google_client_id_here" />
</application>
```

## Testing API Integration

### 1. Verify YouTube API Access

Test the YouTube Data API by making a simple request:

```kotlin
// In your data source or repository
val youtube = YouTube.Builder(transport, jsonFactory, credential)
    .setApplicationName("LiveApp")
    .build()

val search = youtube.search().list("id,snippet")
search.key = BuildConfig.YOUTUBE_API_KEY
search.q = "test query"
search.type = "video"
search.maxResults = 10L

val response = search.execute()
```

### 2. Test Google OAuth

Implement Google Sign-In in your auth feature:

```kotlin
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
    .requestEmail()
    .build()

val googleSignInClient = GoogleSignIn.getClient(context, gso)
```

## Troubleshooting

### Common Issues

1. **API Key Restrictions**: Ensure your API key is not restricted too narrowly
2. **OAuth Consent Screen**: Make sure your app is published or you have test users added
3. **SHA-1 Fingerprint**: Use the correct fingerprint for your signing config
4. **Permissions**: Verify all required permissions are granted

### Error Codes

- `access_denied`: Check OAuth consent screen configuration
- `invalid_client`: Verify client ID and secret
- `quota_exceeded`: Monitor your API usage in Google Cloud Console

## Security Best Practices

1. **Never commit API keys**: Use local.properties or environment variables
2. **Use restricted API keys**: Limit API keys to specific APIs and referrers
3. **Implement token refresh**: Handle expired tokens gracefully
4. **Certificate pinning**: Consider implementing SSL pinning for production

## Next Steps

Once APIs are configured, you can proceed with:
- Implementing authentication flows
- Setting up streaming data sources
- Configuring chat integration

For more details, refer to the [YouTube Data API documentation](https://developers.google.com/youtube/v3) and [Google Sign-In documentation](https://developers.google.com/identity/sign-in/android/start).