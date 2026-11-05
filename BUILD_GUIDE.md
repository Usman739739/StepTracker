# 📦 Build Guide — Generating the APK

This guide explains how to build the StepTracker APK on your local machine.

---

## Prerequisites

Before building, ensure you have:

1. **Android Studio** Ladybug (2024.2.1) or newer
   - Download: https://developer.android.com/studio
   
2. **JDK 17** or newer (included with Android Studio)

3. **Android SDK** with:
   - API 35 (compileSdk)
   - Build Tools 35.0.0+
   - Android SDK Platform-Tools

---

## Method 1: Android Studio (Easiest)

### Step 1: Open Project
1. Launch Android Studio
2. Select **"Open an Existing Project"**
3. Navigate to the `StepTracker` folder
4. Click **OK**

### Step 2: Sync Gradle
- Android Studio will automatically sync Gradle and download dependencies
- Wait for the sync to complete (progress bar at bottom)
- If prompted, install any missing SDK components

### Step 3: Build Debug APK
1. Go to **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Wait for build to complete
3. Click **"locate"** in the notification
4. APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Step 4: Install on Device
- Connect Android device via USB (enable USB debugging)
- Click **Run** (green play button) in Android Studio
- Or drag the APK file onto your device

---

## Method 2: Command Line

### Step 1: Set Up Environment

**macOS/Linux:**
```bash
# Set Android SDK path (adjust path as needed)
export ANDROID_HOME=$HOME/Library/Android/sdk  # macOS
# export ANDROID_HOME=$HOME/Android/Sdk        # Linux

# Add to PATH
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
```

**Windows (Command Prompt):**
```cmd
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
set PATH=%PATH%;%ANDROID_HOME%\platform-tools
```

### Step 2: Create local.properties

In the `StepTracker` folder, create `local.properties`:

```properties
sdk.dir=/path/to/your/android/sdk
```

Or use the template:
```bash
cp local.properties.example local.properties
# Edit local.properties with your SDK path
```

### Step 3: Build APK

**Debug APK (for testing):**
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

**Release APK (for distribution):**
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Step 4: Sign Release APK (Required for installation)

**Generate a keystore (one-time):**
```bash
keytool -genkey -v -keystore steptracker.keystore -alias steptracker -keyalg RSA -keysize 2048 -validity 10000
```

**Sign the APK:**
```bash
# Align the APK
$ANDROID_HOME/build-tools/35.0.0/zipalign -v -p 4 \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  app-release-aligned.apk

# Sign with apksigner
$ANDROID_HOME/build-tools/35.0.0/apksigner sign \
  --ks steptracker.keystore \
  --out app-release.apk \
  app-release-aligned.apk
```

### Step 5: Install APK

**Via ADB:**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Or drag-and-drop** the APK file onto your Android device.

---

## Method 3: Generate App Bundle (for Play Store)

If you want to publish to Google Play Store:

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

---

## Troubleshooting

### Build Fails: SDK Location Not Found
**Solution:** Create `local.properties` with correct SDK path:
```properties
sdk.dir=/Users/yourname/Library/Android/sdk
```

### Build Fails: Java Version Mismatch
**Solution:** Ensure JDK 17+ is being used:
```bash
java -version  # Should show 17 or higher

# If using wrong version, set JAVA_HOME:
export JAVA_HOME=/path/to/jdk17
```

### Build Fails: Out of Memory
**Solution:** Increase Gradle memory in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m
```

### APK Won't Install: "App Not Installed"
**Solutions:**
- Uninstall any existing version first
- For release APK, ensure it's properly signed
- Check device has enough storage

---

## APK File Sizes

- **Debug APK:** ~15-20 MB (includes debug symbols)
- **Release APK:** ~8-12 MB (optimized and minified)
- **App Bundle:** ~10-15 MB (split by device config)

---

## Testing the APK

After installation, test these features:

1. **Onboarding Flow**
   - Grant all permissions
   - Complete setup

2. **Step Counting**
   - Start tracking
   - Walk around and verify count updates
   - Check notification shows live count

3. **Background Tracking**
   - Close app (swipe away)
   - Walk for a few minutes
   - Reopen app and verify steps were counted

4. **Bike/Drive Detection**
   - Start biking or driving
   - Verify tracking auto-pauses
   - Stop and verify it resumes

5. **Schedule**
   - Set schedule (e.g., 1 minute from now to 2 minutes from now)
   - Verify service starts/stops automatically

6. **Widget**
   - Add widget to home screen
   - Verify it shows current steps
   - Tap to open app

---

## Next Steps

After building successfully:

1. **Test on multiple devices** (Android 8.0+)
2. **Check battery usage** in Settings → Battery
3. **Verify Health Connect sync** (if available on device)
4. **Test with screen off** for extended periods
5. **Review logs** with `adb logcat | grep StepTracker`

---

## Support

For build issues:
- Check Android Studio's **Build** output window
- Run `./gradlew build --stacktrace` for detailed errors
- Verify all prerequisites are installed
- Ensure SDK path is correct in `local.properties`
