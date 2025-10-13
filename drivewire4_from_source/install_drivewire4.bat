@echo off
setlocal enabledelayedexpansion

:: ---------------------------------------
:: CONFIGURATION
:: ---------------------------------------
set "APP_NAME=DriveWire4"
set "SOURCE_DIR=%CD%"
set "TARGET_DIR=%USERPROFILE%\%APP_NAME%"
set "START_MENU_BASE=%APPDATA%\Microsoft\Windows\Start Menu\Programs"
set "START_MENU_DIR=%START_MENU_BASE%\%APP_NAME%"
set "SHORTCUT=%START_MENU_DIR%\%APP_NAME%.lnk"
set "ICON_FILE=%TARGET_DIR%\dw4_icon.ico"
set "MAIN_EXE=%TARGET_DIR%\drivewire4_windows_x86_64.bat"
set "UNINSTALLER=%START_MENU_DIR%\Uninstall %APP_NAME%.bat"
set "XML_FILE=%TARGET_DIR%\drivewireUI.xml"

:: ---------------------------------------
:: COPY FILES
:: ---------------------------------------
echo Installing %APP_NAME%...
if not exist "%SOURCE_DIR%" (
    echo ERROR: Source folder "%SOURCE_DIR%" not found.
    pause
    exit /b 1
)







echo Copying program files to "%TARGET_DIR%"...
xcopy "%SOURCE_DIR%" "%TARGET_DIR%" /E /I /Y >nul




:: ---------------------------------------
:: MODIFY drivewireUI.xml (fixed; PSv2 compatible)
:: ---------------------------------------
if exist "%XML_FILE%" (
    echo Editing drivewireUI.xml configuration...

    powershell -NoProfile -Command ^
      "$appName = '%APP_NAME%';" ^
      "$targetDir = Join-Path $env:USERPROFILE $appName;" ^
      "$xmlPath = '%XML_FILE%';" ^
      "$url = 'file://' + ($targetDir.Replace('\\','/')) + '/hdb-dos/index.html';" ^
      "$xml = [System.IO.File]::ReadAllText($xmlPath);" ^
      "$insertFolder = '<Folder title=\"HDB-DOS\"><URL title=\"Load HDB-DOS\">' + $url + '</URL></Folder>';" ^
      "if ($xml.IndexOf($insertFolder) -lt 0) {" ^
      "  $loc = $xml.IndexOf('</Local>');" ^
      "  if ($loc -ge 0) { $xml = $xml.Substring(0,$loc) + $insertFolder + '</Local>' + $xml.Substring($loc + 8); }" ^
      "}" ^
      "$tabTag = '<LibraryTab_0>Local|HDB-DOS|Load HDB-DOS</LibraryTab_0>';" ^
      "if ($xml.IndexOf($tabTag) -lt 0) {" ^
      "  $loc2 = $xml.IndexOf('</configuration>');" ^
      "  if ($loc2 -ge 0) { $xml = $xml.Substring(0,$loc2) + $tabTag + '</configuration>' + $xml.Substring($loc2 + 16); }" ^
      "}" ^
      "[System.IO.File]::WriteAllText($xmlPath, $xml, [System.Text.Encoding]::UTF8);"
) else (
    echo WARNING: "%XML_FILE%" not found, skipping XML modification.
)




:: ---------------------------------------
:: DETERMINE TARGET LAUNCH COMMAND
:: ---------------------------------------
:: If it’s a JAR file, use javaw.exe; otherwise, just run the EXE directly
set "TARGET_CMD=%MAIN_EXE%"
if /I "%MAIN_EXE:~-4%"==".jar" (
    set "TARGET_CMD=javaw.exe -jar \"%MAIN_EXE%\""
)


:: ---------------------------------------
:: CREATE START MENU SUBDIRECTORY
:: ---------------------------------------
if not exist "%START_MENU_DIR%" (
    echo Creating Start Menu folder "%START_MENU_DIR%"...
    mkdir "%START_MENU_DIR%"
)



:: ---------------------------------------
:: CREATE SHORTCUT IN START MENU WITH ICON
:: ---------------------------------------
echo Creating Start Menu shortcut...

:: Check for icon file
if not exist "%ICON_FILE%" (
    echo NOTE: No icon.ico found in "%TARGET_DIR%". Using default icon.
    set "ICON_FILE=%SystemRoot%\System32\shell32.dll"
)

powershell -NoProfile -Command ^
 "$s=(New-Object -COM WScript.Shell).CreateShortcut('%SHORTCUT%');" ^
 "$s.TargetPath='C:\Windows\System32\cmd.exe';" ^
 "$s.Arguments='/c "\" %TARGET_CMD%';" ^
 "$s.WorkingDirectory='%TARGET_DIR%';" ^
 "$s.IconLocation='%ICON_FILE%';" ^
 "$s.Description='Launch %APP_NAME%';" ^
 "$s.WindowStyle=1;" ^
 "$s.Save()"

:: ---------------------------------------
:: CREATE UNINSTALLER SCRIPT
:: ---------------------------------------
echo Creating uninstaller "%UNINSTALLER%"...

(
    echo @echo off
    echo title Uninstall %APP_NAME%
    echo echo This will completely remove %APP_NAME%.
    echo set /p confirm=Are you sure you want to uninstall %APP_NAME%^? [Y/N]^:
    echo if /I "%%confirm%%" NEQ "Y" exit /b 0
    echo echo Uninstalling...
    echo rmdir /s /q "%TARGET_DIR%"
    echo rmdir /s /q "%START_MENU_DIR%"
    echo echo %APP_NAME% has been uninstalled.
    echo pause
) > "%UNINSTALLER%"

attrib +r "%UNINSTALLER%" 2>nul

:: ---------------------------------------
:: DONE
:: ---------------------------------------
echo Installation complete!
echo.
echo %APP_NAME% installed successfully.
echo You can launch it from:
echo   Start Menu ? %APP_NAME% ? %APP_NAME%
echo.
echo To uninstall, use:
echo   Start Menu ? %APP_NAME% ? Uninstall %APP_NAME%
echo.
pause
endlocal
exit /b 0

echo Installation complete!
echo %APP_NAME% has been added to your Start Menu.
pause
endlocal
exit /b 0