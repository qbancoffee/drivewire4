Please consult the documentation:

https://sourceforge.net/apps/mediawiki/drivewireserver/index.php?title=Main_Page


To start DriveWire 4 from a GUI, double click on the DW4UI file most likely to work on your system...

Windows users can double click on DW4UI.exe or DW4UI.jar

Mac OS X users can double click on DW4UI.command

Linux and *BSD users can double click DW4UI.sh

There are many different factors that can determine whether any of these files will work for you.  

If you are unable to start DriveWire 4 using any of the above files, you can probably still start it
using the command line.

On all systems except Mac OSX, change to the directory where you unzipped the DW4 package, and enter:

java -jar DW4UI.jar

On Mac OS X (think different, I guess), instead enter:

java -XstartOnFirstThread -jar DW4UI.jar


For additional debugging info that may be helpful, try:

java -jar DW4UI.jar --debug

or 

java -jar DW4UI.jar --logviewer

or

java -jar DW4UI.jar --help

for more options...


Please consult the documentation:

https://sourceforge.net/apps/mediawiki/drivewireserver/index.php?title=Main_Page




