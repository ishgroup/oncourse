
# Android keystore

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
# google_secret.json
1. select or create project in google cloud platform: https://console.cloud.google.com/projectcreate
2. go to menu > APIs & Services > OAuth consent screen and proceed through the form (require folowed scopes openid .../auth/userinfo.email .../auth/userinfo.profile  .../auth/calendar.events .../auth/drive.file .../auth/drive.metadata)
3. Create 3 credentials: menu > APIs & Services > Credentials, hit [+ CREATE CREDANTIALS] > OAuth client ID
 - type: Web Application
 - type: Android
 - 





