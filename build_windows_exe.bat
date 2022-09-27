@echo off
setlocal ENABLEDELAYEDEXPANSION

set version=
FOR /F %%i IN (target/classes/version.txt) DO set version=%%i

set jar_name=target\click-less-version_final.jar
set jar=%jar_name:version=!version!%
@echo on
jpackage --input %~dp0target^
 --name ClikLess^
 --app-version %version%^
 --main-jar %~dp0%jar%^
 --main-class co.uk.bittwisted.ClickLessApp^
 --icon %~dp0misc\clik_lezz_icon.ico^
 --dest %~dp0builds\windows\production^
 --type exe^
 --java-options '--enable-preview'^
 --win-menu^
 --win-dir-chooser