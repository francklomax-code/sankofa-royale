#!/usr/bin/env bash
# Compile l'APK en local. Prérequis : Node 20+, JDK 21, Android SDK (ANDROID_HOME défini).
set -euo pipefail
cd "$(dirname "$0")"

if [ -z "${ANDROID_HOME:-}${ANDROID_SDK_ROOT:-}" ]; then
  echo "ANDROID_HOME n'est pas défini. Installez Android Studio ou les command-line tools." >&2
  exit 1
fi

MODE="${1:-debug}"
echo "▸ Installation des dépendances"
npm install --no-audit --no-fund

echo "▸ Copie des fichiers web dans le projet natif"
npx cap sync android

chmod +x android/gradlew
cd android

if [ "$MODE" = "release" ]; then
  : "${KEYSTORE_PATH:?Définissez KEYSTORE_PATH, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD}"
  echo "▸ Compilation signée (APK + AAB)"
  ./gradlew assembleRelease bundleRelease
  echo "APK : android/app/build/outputs/apk/release/app-release.apk"
  echo "AAB : android/app/build/outputs/bundle/release/app-release.aab"
else
  echo "▸ Compilation de test"
  ./gradlew assembleDebug
  echo "APK : android/app/build/outputs/apk/debug/app-debug.apk"
fi
