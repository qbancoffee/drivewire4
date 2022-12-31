# DriveWire 4
DriveWire 4 Java version

Get the original source files from sourceforge.
https://sourceforge.net/projects/drivewireserver/

This repo holds a version of DriveWire 4 java that has been slightly modified so that it can be compiled and run with newer versions of Java.
Watch this video to see how it was done.<br>
- [Compiling and running DriveWire 4 with OpenJDK 17](https://youtu.be/7fjNQZ2uRJI)

Make sure you have a JDK installed. I used OpenJDK 17 but any other JDK should work as well.
Install ant and at least NetBeans 16

With ant installed, navigate to the project directory and type the following to compile:
```bas
ant
```
To run the program type the following:
```bash
ant run
```

Unfortunately this NetBeans project and ant are closely tied together and I can't figure out how to decouple them enough to where ant works correctly without first having to open the project and building at least once with NetBeans. The following should work but it doesn't at first:
```bash
java -jar dist/dw4nb.jar
Error: Unable to initialize main class com.groupunix.drivewireui.MainWin
Caused by: java.lang.NoClassDefFoundError: org/apache/log4j/Layout
```

This is because the lib folder with all of the required libraries was not created in the dist folder. Ant uses "nbproject/private/private.properties" to get the path to the JDK from NetBeans. In my case "user.properties.file=/home/rockyhill/snap/netbeans/74/build.properties".

This is quickly solved by either updating that path manually or opening the project in NetBeans and building once. This is a minor annoyance but it's solvable.

After running it, you'll notice that the UI is very differnt from the UI that one sees when running the latest version of DriveWire 4.
I suspect this might be a version isssue so hopefully someone will have the source for that and one day this repo can be updated.
See screenshots of the UI below.
## Screenshots

### Main UI
<img src="https://github.com/qbancoffee/drivewire4/blob/main/images/dw4_mainwin.png" width="300">

### Lite UI

<img src="https://github.com/qbancoffee/drivewire4/blob/main/images/dw4_liteui.png" width="300">


<br>



