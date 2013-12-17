#!/bin/bash

version=1.0.2

path=target/verba-android-
alias=verba-android
apk=${path}${version}.apk
unalignedAPK=${path}${version}-unaligned.apk
keystore=verba-android-release-key.keystore

zipalign=`echo $ANDROID_HOME/tools/zipalign`

echo "signing $apk"

jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore ${keystore} ${apk} ${alias}

echo "verifying signature of ${apk}"
#jarsigner -verify ${apk}
jarsigner -verify -verbose ${apk}

echo "ready to zipalign"
mv ${apk} ${unalignedAPK}
${zipalign} -v 4 ${unalignedAPK} ${apk}
