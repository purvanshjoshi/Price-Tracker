@echo off
:: This script is called by Google Chrome to start the Java Engine
:: Make sure the absolute path to your target JAR is correct

:: Set the current working directory to the project backend root
cd /d "d:\Price Tracker\backend"

:: Find the Java executable we downloaded to D:\Tools
FOR /D %%G IN ("D:\Tools\Java\jdk*") DO (
    SET JAVA_EXE="%%G\bin\java.exe"
)

:: Execute the built Java JAR and Log it
echo [Launcher] Started at %TIME% >> "d:\Price Tracker\host_test.log"
%JAVA_EXE% -jar "d:\Price Tracker\backend\target\PriceTrackerEngine.jar" 2>> "d:\Price Tracker\host_error.log"
