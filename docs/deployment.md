# Deployment Guide

This guide covers the complete process for deploying LiveApp to the Google Play Store, including app bundle generation, release management, and post-deployment monitoring.

## Prerequisites

- Google Play Console account with app created
- Release keystore and signing configuration
- App bundle (.aab) file generated
- Release notes and screenshots prepared
- Privacy policy and terms of service URLs

## App Bundle Generation

### 1. Generate Release Bundle

Use the following Gradle command to generate a release app bundle:

```bash
./gradlew bundleRelease
```

The bundle will be generated at:
```
app/build/outputs/bundle/release/app-release.aab
```

### 2. Verify Bundle

Before uploading, verify the bundle contents:

```bash
# Check bundle info
bundletool dump manifest --bundle app-release.aab

# Validate bundle
bundletool validate --bundle app-release.aab
```

### 3. Generate APKs for Testing (Optional)

If you need to test the release build locally:

```bash
# Generate universal APK from bundle
bundletool build-apks --bundle=app-release.aab --output=app-release.apks --mode=universal

# Extract APK
unzip app-release.apks
# APK will be in: universal.apk
```

## Google Play Console Setup

### 1. Create App in Play Console

1. Go to [Google Play Console](https://play.google.com/console/)
2. Click "Create app"
3. Fill in app details:
   - **App name**: LiveApp
   - **Default language**: English
   - **App type**: App (not game)
   - **Free or paid**: Free

### 2. Store Listing

Configure your store listing:

1. **App details**:
   - Short description (80 characters max)
   - Full description (4000 characters max)
   - Screenshots (at least 2, max 8 per type)
   - Feature graphic (1024x500)
   - Phone, tablet, and TV banners

2. **Contact details**:
   - Website
   - Email
   - Privacy policy URL
   - Terms of service URL

3. **Categorization**:
   - Category: Video Players & Editors
   - Tags: streaming, live, YouTube, chat
   - Content rating: Everyone or Teen

### 3. Content Rating

Complete the content rating questionnaire in the Play Console to get your app rated.

### 4. Pricing & Distribution

1. **Countries**: Select all countries or specific ones
2. **Pricing**: Free app
3. **Device catalog**: Select supported devices
4. **Contact information**: Developer contact details

## Release Management

### 1. Internal Testing Track

Before production release:

1. Create an internal testing track
2. Upload the app bundle
3. Add internal testers (email addresses)
4. Publish to internal testing

### 2. Closed Testing Track (Beta)

1. Create a closed testing track
2. Upload app bundle
3. Set up testing program:
   - Add testers via Google Groups or email lists
   - Set feedback URL
4. Publish to closed testing

### 3. Open Testing Track (Beta)

1. Create an open testing track
2. Upload app bundle
3. Configure:
   - Testing period
   - Opt-in URL
4. Publish to open testing

### 4. Production Release

1. Create a production release
2. Upload the final app bundle
3. Configure release:
   - **Release name**: e.g., "Version 1.0.0"
   - **Release notes**: See below for format
   - **Rollout percentage**: Start with 20% for gradual rollout

## Release Notes Format

Use the following format for release notes:

### Version 1.0.0
**New Features:**
- Live streaming with YouTube integration
- Real-time chat with moderation tools
- User authentication via Google OAuth
- Stream scheduling and presets

**Improvements:**
- Enhanced performance and stability
- Improved user interface
- Better error handling

**Bug Fixes:**
- Fixed streaming connection issues
- Resolved chat message display problems
- Corrected authentication flow

## App Signing

### 1. Play App Signing

Google recommends using Play App Signing:

1. In Play Console, go to "App integrity" > "App signing"
2. Choose "Use Google-generated key" or "Upload your own key"
3. If uploading your own key:
   - Export your upload key from Android Studio
   - Upload the certificate (.pem file)

### 2. Keystore Management

Keep your keystore secure:

- Store in a password manager or secure vault
- Backup the keystore file
- Never commit keystore to version control
- Use environment variables for CI/CD

## Permissions and Capabilities

### Required Permissions

Ensure your `AndroidManifest.xml` includes:

```xml
<!-- Internet access -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Camera and microphone for streaming -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

<!-- Storage for caching -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

<!-- Foreground service for streaming -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION" />
```

### Feature Declarations

```xml
<!-- Camera feature -->
<uses-feature android:name="android.hardware.camera" android:required="true" />
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />

<!-- Microphone feature -->
<uses-feature android:name="android.hardware.microphone" android:required="true" />
```

## Pre-Launch Checklist

Before submitting for production:

- [ ] App bundle generated and signed
- [ ] All screenshots and graphics uploaded
- [ ] Privacy policy and terms linked
- [ ] Content rating completed
- [ ] Internal and beta testing completed
- [ ] Crash reports reviewed and fixed
- [ ] Performance tested on various devices
- [ ] Accessibility compliance checked
- [ ] Translation strings complete

## Submission Process

### 1. Upload Bundle

1. In Play Console, go to "Release" > "Production"
2. Click "Create new release"
3. Upload your `.aab` file
4. Fill in release details

### 2. Review and Submit

1. Review all information
2. Check for any policy violations
3. Submit for review

### 3. Review Timeline

- **Initial review**: 1-3 days
- **Updates**: 1-7 days depending on changes needed
- **Appeals**: Additional time if appealing rejections

## Post-Deployment

### 1. Monitor Release

1. Check rollout percentage
2. Monitor crash reports in Play Console
3. Review user feedback and ratings
4. Track download and engagement metrics

### 2. Handle Issues

- **Crashes**: Use Firebase Crashlytics or Play Console crash reports
- **ANRs**: Analyze Application Not Responding errors
- **User feedback**: Respond to reviews and support requests

### 3. Update Process

For future updates:

1. Increment version code and name
2. Generate new bundle
3. Create new release in Play Console
4. Test thoroughly before full rollout
5. Start with staged rollout (20% → 50% → 100%)

## Rollback Strategy

If issues arise after deployment:

1. **Immediate rollback**: Use Play Console to rollback to previous version
2. **Gradual rollback**: Reduce rollout percentage
3. **Hotfix**: Prepare and submit emergency update

## Advanced Features

### In-App Updates

Implement in-app updates for seamless user experience:

```kotlin
val appUpdateManager = AppUpdateManagerFactory.create(context)
val appUpdateInfoTask = appUpdateManager.appUpdateInfo

appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
        // Request update
    }
}
```

### Staged Rollouts

Use staged rollouts to minimize risk:

1. Start with 20% of users
2. Monitor for 24-48 hours
3. Increase to 50% if stable
4. Go to 100% after full confidence

## Compliance and Policies

### Data Safety

Complete the Data Safety form in Play Console:

- **Data collection**: Specify what data you collect
- **Data sharing**: Declare if data is shared with third parties
- **Security practices**: Describe security measures

### Target API Level

Ensure your app targets recent API levels:

- **targetSdk**: 34 (Android 14)
- **minSdk**: 21 (Android 5.0)

### 64-bit Support

Ensure your app supports 64-bit architectures by including:

```kotlin
android {
    defaultConfig {
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }
}
```

## Troubleshooting Deployment Issues

### Common Issues

1. **Bundle rejected**: Check for policy violations
2. **Signing issues**: Verify keystore and certificates
3. **Missing translations**: Ensure all strings are translated
4. **Large bundle size**: Optimize assets and code

### Support Resources

- [Google Play Console Help](https://support.google.com/googleplay/android-developer)
- [Android Developer Documentation](https://developer.android.com/)
- [Firebase Documentation](https://firebase.google.com/docs)

## Release Automation

For automated releases, consider using:

- **Fastlane**: Automate Play Store deployments
- **GitHub Actions**: Custom workflows for releases
- **Gradle Play Publisher**: Plugin for automated publishing

Example Fastlane configuration:

```ruby
lane :deploy do
  gradle(task: "bundleRelease")
  upload_to_play_store(
    track: 'internal',
    aab: 'app/build/outputs/bundle/release/app-release.aab'
  )
end
```

This comprehensive deployment process ensures your app reaches users safely and maintains high quality standards.