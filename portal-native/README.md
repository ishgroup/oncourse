Portal native app
===

Native portal application for iOS, android and web

## Setup

To run application locally:

1. Open app root directory in terminal
2. Run next command:  `yarn start`
3. To run in browser press "Run in web browser" in opened window
4. To run on iOS or Android devices install [Expo Client](https://expo.io/tools) application and scan QR code from terminal with Camera App (iOS) or with "Scan QR Code" on the "Projects" tab of the Expo client app on Android

## iOS

### App signing and building

To sign an Apple App, you will need:

* Apple Team ID
* iOS Distribution Certificate in p12 format
* iOS Distribution Privisioning profile

App will be signed on build stage by passing credential files paths and passwords as build script params

Before starting a build install following tools as cli:

[Fastlane](https://fastlane.tools/)

[Xcode](https://developer.apple.com/xcode/)

[Turtle](https://github.com/expo/turtle)

Now, we are ready. Here are build steps:

1. Run ```yarn expo export --dev --public-url http://127.0.0.1:8080/``` from root folder. This command will create dist folder with assets that will be used for build
2. Start live server in dist to be accessed by ```http://127.0.0.1:8080/``` link
3. Run build script:
   ```EXPO_IOS_DIST_P12_PASSWORD="your_p12_certificate_password" turtle build:ios --team-id your_apple_team_id --dist-p12-path path_to_p12_cert --provisioning-profile-path path_to_provisioning_profile --allow-non-https-public-url --public-url http://127.0.0.1:8080/ios-index.json```

were your_p12_certificate_password, your_apple_team_id, path_to_p12_cert, path_to_provisioning_profile changed with real values.

After build finish, result will be saved inside a folder called expo-apps inside your Home directory

[More info](https://www.robincussol.com/build-standalone-expo-apk-ipa-with-turtle-cli/)


## Android

### Keystore:

To generate keystore you could use [keytool cli](https://www.ibm.com/docs/en/sdk-java-technology/7?topic=keytool-key-certificate-management-tool)

Generate example (replace KEYSTORE_PASSWORD, KEY_PASSWORD, KEY_ALIAS, release.keystore, and com.expo.your.android.package with the values of your choice):
```
 keytool \\
 -genkey -v \\
 -storetype JKS \\
 -keyalg RSA \\
 -keysize 2048 \\
 -validity 10000 \\
 -storepass KEYSTORE_PASSWORD \\
 -keypass KEY_PASSWORD \\
 -alias KEY_ALIAS \\
 -keystore release.keystore \\
 -dname "CN=com.expo.your.android.package,OU=,O=,L=,S=,C=US"
 ```
Command to extract fingerprints:
```
keytool -keystore path-to-debug-or-production-keystore -list -v
```

Command to attach certificate:
```
keytool -keystore path-to-debug-or-production-keystore -importcert -alias alias.name -file certificate-file.cer
```
### Build:

App will be signed on build stage by passing path and passwords to keystore

Before starting a build make sure that your environment is using JAVA 8

Here are build steps:

1. Run ```yarn expo export --dev --public-url http://127.0.0.1:8080/``` from root folder. This command will create dist folder with assets that will be used for build
2. Start live server in dist to be accessed by ```http://127.0.0.1:8080/``` link
3. Run build script:
   ```EXPO_ANDROID_KEYSTORE_PASSWORD="your_keystore_password" EXPO_ANDROID_KEY_PASSWORD="your_keystore_key_password" turtle build:android --keystore-path your_keystore_path --keystore-alias your_keystore_alias --allow-non-https-public-url --public-url http://127.0.0.1:8080/android-index.json```

were your_keystore_password, your_keystore_key_password, your_keystore_path, your_keystore_alias changed with real values.

After build finish, result will be saved inside a folder called expo-apps inside your Home directory

[More info about build](https://www.robincussol.com/build-standalone-expo-apk-ipa-with-turtle-cli/)

[App signing](https://developer.android.com/studio/publish/app-signing)

[Android App Bundles](https://developer.android.com/guide/app-bundle)
