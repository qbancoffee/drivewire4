@echo off

java -Djava.library.path=/rxtx/windows -cp ./swt/linux/swt.jar;./dist/lib/;dist/dw4nb.jar com.groupunix.drivewireui.MainWin %*
