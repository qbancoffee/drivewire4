# DriveWire 4
DriveWire 4 Java by Aaron Wolfe

- [Download the Latest release](https://github.com/qbancoffee/drivewire4/releases/latest)
- [DriveWire4 for All  Java not included](https://github.com/qbancoffee/drivewire4/releases/tag/4.3.3p)
- [DriveWire4 for linux x86_64 with Java included](https://github.com/qbancoffee/drivewire4/releases/tag/4.3.3p_linux_x86_64)
- [DriveWire4 for linux arm_64 with Java included](https://github.com/qbancoffee/drivewire4/releases/tag/4.3.3p_linux_arm_64)
- [DriveWire4 for Windows x64 with Java included](https://github.com/qbancoffee/drivewire4/releases/tag/4.3.3p_Windowsx64)



This repo holds two netbeans projects for two versions of DriveWire 4 by Aaron Wolfe that have been slightly modified so that they can be compiled and run with newer versions of java.

- drivewire4_from_source is DriveWire4 version 4.3.3p assembled from the source files available on sourceforge.<br>
Get the original source files from sourceforge.
https://sourceforge.net/projects/drivewireserver/<br>

- drivewire4_decompiled is a version of the sources obtained from the decompilation of DW4UI.jar.
<br>
Watch this video to see how the sourceforge version was modified to compile and run with Java 17.

- [Compiling and running DriveWire 4 with OpenJDK 17](https://youtu.be/7fjNQZ2uRJI)


To compile and run, make sure you have:
- A JDK installed, I used OpenJDK 17 but any other JDK should work as well.
- ant
- NetBeans 16 or higher.
<br>
With ant installed, navigate to the project directory and type the following to compile:

```bash
ant
```
Unfortunately this NetBeans project and ant are closely tied together and I can't figure out how to decouple them enough to where ant works correctly without first having to open the project and building at least once with NetBeans. The following should work but it doesn't at first:

For the drivewire4_from_source project use the script for your Operating system after compiling.

- drivewire4_linux_x86_64
- drivewire4_linux_arm_64
- drivewire4_windows_x86_64.bat
- drivewire4_mac_x86_64


For the drivewire4_decompiled version.
```bash
java -jar dist/drivewire_decompiled.jar
Error: Unable to initialize main class com.groupunix.drivewireui.MainWin
Caused by: java.lang.NoClassDefFoundError: org/apache/log4j/Layout
```
<br>


This is because the lib folder with all of the required libraries was not created in the dist folder. Ant uses "nbproject/private/private.properties" to get the path to the JDK from NetBeans. In my case "user.properties.file=/home/rockyhill/snap/netbeans/74/build.properties".
<br>
This is quickly solved by either updating that path manually or opening the project in NetBeans and building once. This is a minor annoyance but it's solvable.
<br>
After running it, you'll notice that the UI is very differnt from the UI that one sees when running the latest version of DriveWire 4.
I suspect this might be a version isssue so hopefully someone will have the source for that and one day this repo can be updated.
See screenshots of the UI below.



## Screenshots
<br>

### Main UI on Windows 10 64 bit de-compiled version.
<img src="https://github.com/qbancoffee/drivewire4/blob/main/images/dw4_decompiled_win64.png" width="300">
<br>

### Main UI on Linux 64 bit de-compiled version.
<img src="https://github.com/qbancoffee/drivewire4/blob/main/images/dw4_decompiled_linux64.png" width="300">
<br>

### Main UI on 64 bit linux sourceforge version.
<img src="https://github.com/qbancoffee/drivewire4/blob/main/images/dw4_mainwin.png" width="300">
<br>

### Lite UI on 64 bit linux sourceforge version.
<br>
<img src="https://github.com/qbancoffee/drivewire4/blob/main/images/dw4_liteui.png" width="300">


<br>



