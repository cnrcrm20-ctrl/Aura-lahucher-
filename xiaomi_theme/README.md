# Aura Celestial Boot Animation for Xiaomi & Redmi Note 11 Pro

This directory contains the non-root boot animation deliverables for Xiaomi MIUI (13 / 14) and Xiaomi HyperOS devices, specifically configured for the **Redmi Note 11 Pro** (1080x2400 AMOLED display, 20:9 aspect ratio, 30 fps).

---

## Deliverables in this Package

1. **`Aura_Redmi_Note_11_Pro.mtz`**
   - The complete Xiaomi Theme package formatted for the official MIUI/HyperOS Theme Engine.
   - Contains:
     - `description.xml` (Theme metadata: Title, Author, UI version)
     - `bootanimation` (Store-compressed archive containing `desc.txt`, `part0/` intro sequence, and `part1/` loop)
     - `preview/preview_animation_0.png` (Theme preview image)

2. **`bootanimation.zip`**
   - Standard Android uncompressed (0% compression / `ZIP_STORED`) boot animation archive with UNIX newline `desc.txt` (`1080 2400 30`).
   - Usable directly on AOSP / Custom ROMs / rooted systems placing it into `/system/media/` or `/product/media/`.

---

## Technical Specifications

- **Target Resolution:** `1080 x 2400` pixels (matches Redmi Note 11 Pro display natively)
- **Target Frame Rate:** `30` frames per second
- **`desc.txt` Specification:**
  ```text
  1080 2400 30
  p 1 0 part0
  p 0 0 part1
  ```
  - `part0/` (30 frames): Intro sequence — celestial rotating glowing Aura ring fading in on an obsidian background.
  - `part1/` (15 frames): Ambient pulse loop — continuous smooth breathing pulse until Android finishes system startup.

---

## How to Import and Apply (Non-Root on Redmi Note 11 Pro)

### Method 1: Official Xiaomi Themes App (Global / EU / China ROM)
1. Transfer `Aura_Redmi_Note_11_Pro.mtz` to your phone's internal storage (e.g. `Download/` or `MIUI/theme/`).
2. Open the built-in **Themes** application on your device.
3. Tap on **Profile** (bottom-right tab) > **Themes**.
4. Scroll down and tap **Import** (if visible on your regional ROM version).
5. Navigate to the `.mtz` file and select it.
6. Once imported, navigate to **Profile** > **Customize theme** > **Boot animation**.
7. Select **Aura Celestial Boot** and tap **Apply**.

### Method 2: Third-Party MTZ Theme Importer (If "Import" button is hidden in your region)
*Note: Some Global/EEA MIUI ROMs hide the offline import button by default.*
1. Install an official theme tool such as **MIUI Theme App** or **ChaoMe Theme Creator** from Google Play Store.
2. Select "Pick Theme (.mtz)" and choose `Aura_Redmi_Note_11_Pro.mtz`.
3. Follow the tool's prompt to apply the boot animation module to your current theme.

---

## Why Root Is Not Required
Modifying `/system/media/bootanimation.zip` directly requires unlocking the bootloader and root access because Android Verified Boot (AVB 2.0 / dm-verity) locks the system partition as read-only.
In contrast, Xiaomi's Theme Engine applies boot animations from the user partition (`/data/system/theme/bootanimation`), which is completely user-accessible through the official Theme Manager.
