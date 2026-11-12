# Photo Viewer — Native Android TV Application

A complete, production-quality, native Android TV gallery and photo viewer built with Kotlin and Jetpack Compose. It is a 1:1 visual and behavioral recreation of the dark grayscale interface from `index__6_.html`, optimized specifically for Android TV with remote control (D-pad) navigation, real local storage and USB drive access, and smooth high-performance image rendering.

---

## Features

- **Android TV First:** Built for Leanback TV launchers, landscape orientation, and 100% remote control navigation (D-pad, OK, Back, Menu, Info, Color keys, Channel buttons, Media keys, Digits).
- **Storage & Volume Management:**
  - Real Internal Storage (`Local Storage`) stats via `StatFs` and MediaStore.
  - Removable USB Drive (`Pendrive`) auto-detection via `StorageManager` and live `BroadcastReceiver` (`ACTION_MEDIA_MOUNTED` / `UNMOUNTED` / `EJECT` / `UsbManager`).
  - Safely eject option for USB drives and Scan for devices.
- **Collapsible Panels:** Storage panel and Folders panel with 18×56 px tab handles, smooth collapse animations, and real-time usage bars.
- **Top Bar Controls:** Breadcrumbs navigation, search with live auto-suggestions, sort popover (6 sorting orders: Name A–Z, Name Z–A, Newest, Oldest, Largest, Smallest), and S/M/L tile size segmented switch.
- **Viewer Overlay:**
  - Full-screen photo viewing on deep black (`#0F0F0F`).
  - Smooth pan and zoom (0.25× to 10×) with boundary clamping.
  - 90° clockwise rotation with aspect ratio recalculation.
  - Floating badge indicator for zoom level, Fit, Rotated, and Slideshow status.
  - Details card overlay with file name, type, dimensions, file size, date, and storage location.
  - Collapsible thumbnail strip with auto-centering on the active photo.
  - Automated 3.5-second Slideshow mode with screen wake lock.
  - Favorites toggle and direct download to device `Downloads/` directory.
- **Menu Drawer:** Right-side slide-over sheet containing Photo actions, Browse shortcuts, and Help (Shortcuts modal and TV remote mode toggle).
- **TV Remote Hint Bar:** Context-aware bottom bar displaying available D-pad shortcuts and colored key actions.
- **Pixel-Perfect Theme:** Strict dark grayscale palette (`#141414`, `#1A1A1A`, `#202020`, `#2A2A2A`, `#343434`, `#ECECEC`, `#A8A8A8`, `#6F6F6F`, `#D6D6D6`), 3 px double focus ring, and local Inter font bundle.

---

## Building the APK

### Debug Build
To build the debug APK, run:
```bash
gradle :app:assembleDebug
```
The output APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Release Build
To build the release APK, run:
```bash
gradle :app:assembleRelease
```
The output APK will be located at:
```
app/build/outputs/apk/release/app-release.apk
```

---

## Installing on Android TV

### Method 1: ADB over Network (Recommended)
1. On your Android TV, go to **Settings → Device Preferences → About**, and click **Build** 7 times to enable Developer Options.
2. In **Settings → Developer Options**, turn on **Network Debugging** (ADB over Wi-Fi).
3. Find your TV's IP address in **Settings → Network & Internet**.
4. On your computer:
   ```bash
   adb connect <TV-IP-ADDRESS>:5555
   adb install -r app/build/outputs/apk/release/app-release.apk
   ```

### Method 2: USB Pendrive
1. Copy `app-release.apk` to a USB flash drive.
2. Plug the USB drive into your Android TV.
3. Use a TV file manager app (e.g., File Commander or FX File Explorer) to install the APK directly.

---

## Design Parity Notes

- **Typography & Font Scaling:** In TV mode, the app uses `Dimens` where `1 rem = 28.8 px` scaled relative to the 1920×1080 reference canvas. Inter font is bundled locally (`res/font/inter.ttf`) without downloadable font dependencies, keeping the application completely offline.
- **Backdrop Blur:** CSS `backdrop-filter: blur(...)` is approximated using exact semi-transparent dark alpha backgrounds (`rgba(32, 32, 32, 0.88)` for circle buttons, `rgba(0, 0, 0, 0.60)` for dialog scrims) to guarantee high performance and stutter-free 60fps animations on low-power TV chipsets.
- **Focus Ring:** The TV D-pad focus highlight implements the exact CSS double ring (`0 0 0 3px #141414, 0 0 0 6px #F4F4F4`) following element outlines.
- **Real Storage + Fallback Demonstration:** When user photos exist on internal storage or connected USB drives, they are scanned and displayed. If a storage device is empty, procedural landscape artworks matching the HTML generator (`Alpine Ridge`, `Dune Light`, `Moonrise`, `Orbit`, `Skyline`, `Tide`) are rendered on-demand.
