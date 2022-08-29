# Meshtastic-Android

![GitHub all releases](https://img.shields.io/github/downloads/meshtastic/meshtastic-android/total)
[![Android CI](https://github.com/meshtastic/Meshtastic-Android/actions/workflows/android.yml/badge.svg)](https://github.com/meshtastic/Meshtastic-Android/actions/workflows/android.yml)
[![CLA assistant](https://cla-assistant.io/readme/badge/meshtastic/Meshtastic-Android)](https://cla-assistant.io/meshtastic/Meshtastic-Android)
[![Fiscal Contributors](https://opencollective.com/meshtastic/tiers/badge.svg?label=Fiscal%20Contributors&color=deeppink)](https://opencollective.com/meshtastic/)
[![Vercel](https://img.shields.io/static/v1?label=Powered%20by&message=Vercel&style=flat&logo=vercel&color=000000)](https://vercel.com?utm_source=meshtastic&utm_campaign=oss)

This is a tool for using Android with open-source mesh radios. For more information see our
webpage: [meshtastic.org](https://www.meshtastic.org). If you are looking for the the device side
code, see [here](https://github.com/meshtastic/Meshtastic-esp32).

This project is currently beta testing, if you have questions or feedback
please [Join our discussion forum](https://meshtastic.discourse.group/). We would love to hear from
you!

The production version of our application is here:

[![Download at https://play.google.com/store/apps/details?id=com.geeksville.mesh](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.geeksville.mesh&referrer=utm_source%3Dgithub-android-readme)

To join the beta program for the app go to
this [URL](https://play.google.com/apps/testing/com.geeksville.mesh) to opt-in to the alpha/beta
test. If you encounter any problems or have
questions, [post in the forum](https://meshtastic.discourse.group/) and we'll help.

The app is also distributed via F-Droid
repo: [https://mesh.tastic.app/fdroid/repo](https://mesh.tastic.app/fdroid/repo)

However, if you must use 'raw' APKs you can get them from
our [github releases](https://github.com/meshtastic/Meshtastic-Android/releases). This is not
recommended because if you manually install an APK it will not automatically update.

## Build instructions

If you would like to develop this application we'd love your help! These build instructions are
brief and should be improved, please send a PR if you can.

- Use Android Studio to build/debug
- Use "git submodule update --init --recursive" to pull in the various submodules we depend on
- There are a few config files which you'll need to copy from templates included in the project. Run
  the following commands to do so:

```bash
rm ./app/google-services.json
cp ./app/google-services-example.json ./app/google-services.json
rm ./app/src/main/res/values/curfirmwareversion.xml
cp ./app/special/curfirmwareversion.xml ./app/src/main/res/values/
```

- Now you should be able to select "Run / Run" in the IDE and it will happily start running on your
  phone or the emulator. Note: The emulators don't support bluetooth, so some features can not be
  used in that environment.

## Analytics setup

- analytics are included but can be disabled by the user on the settings screen

- on dev devices

```bash
adb shell setprop debug.firebase.analytics.app com.geeksville.mesh
adb shell setprop log.tag.FirebaseCrashlytics DEBUG
```

for verbose logging:

```bash
adb shell setprop log.tag.FA VERBOSE
```

# Credits

This project is the work of volunteers:

- @artemisoftnian: Contributed Spanish translations.
- @CycloMies: Contributed Swedish, Finnish and German translations.
- @eriktheV-king: Contributed Dutch and French translations.
- @Lgoix: Contributed tooltip for radio mode
- @Eugene: Contributed Russian translation.
- @Jinx17: Contributed Slovenian translation.
- @Konradrundfunk: Contributed German fixes.
- @Mango-sauce: Contributed Chinese translation.
- @NEKLAN: Contributed Japanese translation.
- @Ohcdh: Contributed Irish and Italian translations.
- @Slavino: Contributed Slovak translation.
- @Zone: Contributed Turkish translation.

Copyright 2022, Geeksville Industries, LLC. GPL V3 license
