# DriveWire 4
DriveWire 4 Java by Aaron Wolfe

Latest version is 4.3.4p

## Download compiled versions 
- [DriveWire4 4.3.4p for Windows x64 with Java included](https://github.com/qbancoffee/drivewire4/releases/tag/4.3.4p_Windows_x64)
- [DriveWire4 4.3.4p for All  Java not included](https://github.com/qbancoffee/drivewire4/releases/tag/4.3.4p)
- [DriveWire4 4.3.4p for linux x86_64 with Java included](https://github.com/qbancoffee/drivewire4/releases/tag/4.3.4p_linux_x86_64)
- [DriveWire4 4.3.4p for linux arm_64 with Java included](https://github.com/qbancoffee/drivewire4/releases/tag/4.3.4p_linux_aarch64)



## Quality of Life Improvements

### SWT Library Update

The SWT library has been updated from version **4.2.4 (2012)** to **4.37 (September 2025)**.  
This update brings a wide range of improvements to SWT, most notably within the embedded browser component.

As a result, DriveWire 4 can now load more modern web pages and support current web technologies.  
Outdated or broken URLs have been removed from the bookmark menu, and new, relevant links have been added.

---

### Updated Bookmark Menu

The bookmark menu now includes:

- **NitrOS9**  
    - [NitrOS 9 Documentation Wiki](https://sourceforge.net/projects/nitros9/)  
- **Software Websites**  
    - [Curtis Boyle’s Games Site](http://www.lcurtisboyle.com/nitros9/coco_game_list.html)
    - [Mike Snyder’s COCO Quest](https://cocoquest.com/#DOWNLOAD)
- **DriveWire Websites**
    - [DriveWire 4 Wiki *(in development)*](https://github.com/qbancoffee/drivewire4/wiki/DriveWire-4)
    - [DriveWire 4 GitHub Repository](https://github.com/qbancoffee/drivewire4)
    - [DriveWire4 SourceForge Repository *(discontinued)*](https://sourceforge.net/projects/drivewireserver/)
    - [Official DriveWire Repository *(Boisy Pitre)*](https://github.com/boisy/DriveWire)
    - [pyDriveWire](https://github.com/n6il/pyDriveWire)
- **HDB-DOS**  
    - [Load HDB-DOS](https://github.com/qbancoffee/drivewire4/wiki/HDB%E2%80%90DOS)

---

### Enhanced Browser Capabilities

Leveraging the new SWT library, DriveWire 4 can now render HTML `<audio>` controls within its built-in browser.  
The **Load HDB-DOS** page is a local web page that includes an HTML audio player for three HDB-DOS WAV files:

- CoCo 1  
- CoCo 2  
- CoCo 3  

These WAV files can be played directly within the DriveWire 4 browser, allowing for convenient and integrated HDB-DOS loading across CoCo systems.

---

### Prepackaged Releases

Prepackaged releases are available for:

- **Windows**  
- **Linux x86_64**  
- **Linux aarch_64**

Each release is bundled with its own **Java Runtime Environment (JRE)** in a self-contained directory.  
Installation scripts are provided that will:

- Copy DriveWire 4 and its bundled JRE to your system  
- Create a desktop launcher  
- Create an uninstall script  

These installers do **not** install Java system-wide and will **not interfere with existing Java installations**, allowing DriveWire 4 to run as a fully standalone application.

---

### Linux Installation Note

Before running DriveWire 4 for the first time on Linux, ensure that the package  
`libswt-webkit-gtk-4-jni` is installed.  

If this library is missing, DriveWire 4 may behave abnormally and alter its installation files.  
In such cases, reinstalling DriveWire 4 **after** installing `libswt-webkit-gtk-4-jni` will be necessary.

---

### Platform Compatibility

This version of DriveWire 4 has been tested and confirmed to work on:

- **Ubuntu (x86_64)**  
- **Raspbian aarch64 (Raspberry Pi)**  
- **Windows 10**  
- **Windows 11**

Although startup scripts are included for **Intel** and **ARM Macs**, functionality has not been verified due to lack of hardware for testing.

---










This repo holds two netbeans projects for two versions of DriveWire 4 by Aaron Wolfe that have been slightly modified so that they can be compiled and run with newer versions of java.

- drivewire4_from_source is DriveWire4 version 4.3.3p assembled from the source files available on sourceforge.<br>
Get the original source files from sourceforge.
https://sourceforge.net/projects/drivewireserver/<br>



### Compiling
To compile and run, make sure you have:
- Java JDK 17 or greater installed.
- ant
- NetBeans 16 or higher.(you don't stricly need this if you just want to compile)
  
The following was tested under linux but it should work the same under Windows.
With ant installed, navigate to the project directory "drivewire4/drivewire4_from_source" and type the following to compile:

```bash
ant
```
Depending on the version of ant you have this step might produce an error. you'll see something like the following.

```bash
BUILD FAILED
/home/rockyhill/Downloads/src/drivewire4/drivewire4_from_source/nbproject/build-impl.xml:1560: /home/rockyhill/Downloads/src/drivewire4/drivewire4_from_source/build/test/classes does not exist.
```
to solve this all you need to do is create test/classes directory inside of the newly created build directory. on linux you can issue the following command.
```bash
mkdir -p build/test/classes
```

Run the ant command again.
```bash
ant
```
For use on ARM64 you'll need to compile a small shared library so you'll need.
- g++
- X11 development headers
<br>
If you are going to run this on a raspberry pi that uses the X11 windowing system, then you'll need a small library that calls XInitThreads(). In order to use SWT, XInitThreads() needs to be called beforehand. For whatver reason, SWT does not take care of this so we must. 
I found and modified a small library "xdll.cpp" that someone made to solve this issue and it can be found in drivewire4/drivewire4_from_source

Make sure you are compiling this on a raspberry pi and and that you have the X11 develeopment headers and g++ installed.
to compile issue the following command. You can also cross compile this for arm64 from your dev computer but I haven't tried that yet.



```bash
g++ -o libx.so -shared -fPIC -Wl,-soname,libx.so -L/usr/lib/X11 -I/usr/include/X11 xdll.cpp -lX11
```

Copy the newly created shared library "libx.so" to "drivewire4/drivewire4_from_source/lib"

You can now start DriveWire 4 using the included script for arm64.
```bash
./drivewire4_linux_arm_64
```


For the drivewire4_from_source project use the script for your Operating system after compiling.

- drivewire4_linux_x86_64
- drivewire4_linux_arm_64
- drivewire4_windows_x86_64.bat
- drivewire4_macosx.x86_64
- drivewire4_macosx.aarch64

  
Ant uses "nbproject/private/private.properties" to get the path to the JDK from NetBeans. In my case "user.properties.file=/home/rockyhill/snap/netbeans/74/build.properties".
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



