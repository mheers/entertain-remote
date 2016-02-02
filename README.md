# entertain-remote
Remote control your Telekom / T-Systems Entertain Media Receiver 303 with your PC, Raspberry Pi, FHEM, OpenHAB ...

#Howto
  1. root your Android-device
  2. install Entertain RC on Android-device and connect the app with the receiver
  3. get apk-file /data/app/com.t_systems.ctv.android-1.apk from Android-device
  4. unpack the apk and extract the classes.dex with dex2jar from https://github.com/pxb1988/dex2jar
  4. read companionId and key from /data/data/com.t_systems.ctv.android/shared_prefs/ctv_prefs.xml from Android-device and put them here
  5. compile this file with "javac MediaReceiverRemote.java -cp classes-dex2jar.jar:."
  6. run it with "java -cp classes-dex2jar.jar:./ MediaReceiverRemote command poweron" and your receiver should start

#TODO:
  * Pairing
  * Play stationId
