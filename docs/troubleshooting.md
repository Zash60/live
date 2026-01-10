# Troubleshooting Guide

This guide provides solutions to common issues encountered during development, building, and deployment of LiveApp.

## Development Issues

### Gradle Build Failures

#### Issue: `Plugin [id: 'com.android.application', version: '8.1.4'] was not found`

**Solution:**
- Ensure you're using the correct Gradle version
- Check your `gradle/wrapper/gradle-wrapper.properties`
- Update the Android Gradle Plugin version in `build.gradle.kts`

#### Issue: `Could not resolve all dependencies`

**Solution:**
- Clear Gradle cache: `./gradlew clean build --refresh-dependencies`
- Check network connectivity
- Verify repository URLs in `settings.gradle.kts`
- Update proxy settings if behind a corporate firewall

#### Issue: `Manifest merger failed`

**Solution:**
- Check for conflicting permissions in `AndroidManifest.xml` files
- Ensure unique application IDs for different modules
- Review manifest declarations in feature modules

### API Configuration Issues

#### Issue: `YouTube API key not found`

**Solution:**
- Verify `local.properties` contains the correct API keys
- Check environment variables for CI/CD builds
- Ensure keys are not committed to version control

#### Issue: `Google Sign-In fails`

**Error:** `DEVELOPER_ERROR`

**Solutions:**
- Verify SHA-1 certificate fingerprint matches Google Cloud Console
- Check OAuth client ID configuration
- Ensure correct package name and signing config

#### Issue: `API quota exceeded`

**Solution:**
- Monitor API usage in Google Cloud Console
- Implement exponential backoff for retries
- Cache API responses where possible
- Request quota increase if needed

### Authentication Issues

#### Issue: `Login flow gets stuck`

**Solution:**
- Check Google Play Services version
- Verify internet connectivity
- Clear app data and cache
- Check OAuth consent screen configuration

#### Issue: `Token refresh fails`

**Solution:**
- Implement proper token refresh logic
- Handle token expiration gracefully
- Store tokens securely using Android Keystore

## Runtime Issues

### Performance Issues

#### Issue: `App is slow to start`

**Solutions:**
- Analyze with Android Profiler
- Reduce initial data loading
- Implement lazy loading for heavy components
- Check for memory leaks

#### Issue: `UI freezes during operations`

**Solutions:**
- Move heavy operations to background threads
- Use `viewModelScope` for coroutines
- Implement pagination for large data sets
- Profile with Android Studio Profiler

### Streaming Issues

#### Issue: `Stream won't start`

**Solutions:**
- Check camera/microphone permissions
- Verify network connectivity
- Check YouTube API status
- Validate RTMP/WebRTC configuration

#### Issue: `Poor stream quality`

**Solutions:**
- Adjust bitrate settings
- Check network bandwidth
- Monitor device performance
- Implement adaptive bitrate streaming

### Chat Issues

#### Issue: `Messages not sending`

**Solutions:**
- Check internet connectivity
- Verify WebRTC connection
- Check moderation settings
- Monitor server logs

#### Issue: `Chat overlay not appearing`

**Solutions:**
- Verify overlay permissions
- Check system alert window settings
- Ensure proper z-index layering

## Testing Issues

### Unit Testing

#### Issue: `MockK injection fails`

**Solution:**
- Use `@MockK` annotation correctly
- Initialize MockK with `MockKAnnotations.init(this)`
- Check dependency injection setup

#### Issue: `Coroutine tests timeout`

**Solution:**
- Use `runTest` instead of `runBlockingTest`
- Set appropriate test timeouts
- Use `TestCoroutineDispatcher`

### Instrumentation Testing

#### Issue: `Tests fail on CI but pass locally`

**Solutions:**
- Use appropriate emulator configurations
- Handle asynchronous operations properly
- Check for device-specific issues
- Review test data and mock setups

## Deployment Issues

### Build Issues

#### Issue: `Bundle generation fails`

**Solutions:**
- Check bundletool version
- Verify signing configuration
- Ensure all dependencies are compatible
- Check for native library issues

#### Issue: `Play Store rejects bundle`

**Solutions:**
- Review Play Store policies
- Check target API level
- Verify app size limits
- Ensure proper manifest declarations

### Signing Issues

#### Issue: `Keystore not found`

**Solutions:**
- Verify keystore path in CI/CD
- Check environment variables
- Ensure keystore is not corrupted
- Backup and restore keystore properly

## Common Error Messages

### `INSTALL_FAILED_INSUFFICIENT_STORAGE`

**Solution:**
- Clear device storage
- Use smaller APK/bundle
- Test on device with more storage

### `INSTALL_FAILED_UPDATE_INCOMPATIBLE`

**Solution:**
- Check package name consistency
- Verify signing key is the same
- Clear app data before installation

### `java.lang.OutOfMemoryError`

**Solutions:**
- Increase heap size in `gradle.properties`
- Optimize image assets
- Implement memory-efficient data structures
- Use ProGuard/R8 for code shrinking

### `NetworkOnMainThreadException`

**Solution:**
- Move network calls to background threads
- Use coroutines with `Dispatchers.IO`
- Implement proper async patterns

## Debugging Tools

### Android Studio Tools

- **Logcat**: Filter by package name and log level
- **Profiler**: Monitor CPU, memory, and network usage
- **Layout Inspector**: Debug UI hierarchy issues
- **Device File Explorer**: Check app data and cache

### Third-party Tools

- **Firebase Crashlytics**: Monitor crashes and ANRs
- **LeakCanary**: Detect memory leaks
- **Stetho**: Debug network requests and database
- **Flipper**: Facebook's debugging platform

## Logging and Monitoring

### Implement Proper Logging

```kotlin
// Use Timber for consistent logging
Timber.d("Debug message")
Timber.e(exception, "Error occurred")

// Tag logs appropriately
Timber.tag("Auth").i("User logged in: ${user.id}")
```

### Monitor Key Metrics

- App startup time
- API response times
- Memory usage patterns
- Battery consumption
- Network usage

## Getting Help

### Internal Resources

- Check existing issues in the repository
- Review documentation in `docs/` folder
- Consult team knowledge base
- Ask in development channels

### External Resources

- [Android Developers Documentation](https://developer.android.com/)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/android)
- [Google Issue Tracker](https://issuetracker.google.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)

## Prevention Best Practices

1. **Code Reviews**: Catch issues before they reach production
2. **Automated Testing**: Unit and integration tests
3. **Static Analysis**: Use Lint and Detekt
4. **Performance Monitoring**: Regular profiling sessions
5. **Documentation**: Keep troubleshooting guides updated

## Emergency Procedures

### For Critical Production Issues

1. **Assess Impact**: Determine scope and severity
2. **Rollback Plan**: Prepare previous version deployment
3. **Communication**: Notify stakeholders
4. **Hotfix**: Implement and test fix quickly
5. **Monitor**: Watch for recurrence after fix

### Contact Information

- **Development Team**: dev-team@company.com
- **DevOps Team**: devops@company.com
- **Product Team**: product@company.com

Remember to document any new issues and solutions encountered during development to keep this guide current.