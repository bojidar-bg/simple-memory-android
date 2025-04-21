#!/bin/sh

set -e

./gradlew assembleRelease

zipalign -c -P 16 -v 4  ./build/outputs/apk/release/simple-memory-android-release-unsigned.apk

apksigner sign --ks simple-memory-android.keystore --ks-key-alias memory --out simple-memory-android.apk ./build/outputs/apk/release/memory-grandpa-release-unsigned.apk
