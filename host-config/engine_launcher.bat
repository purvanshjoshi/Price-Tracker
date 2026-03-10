@echo off
:: This script is called by Google Chrome to start the Java Engine
:: Make sure the absolute path to your target JAR is correct

:: Set the current working directory to the project backend root
cd /d "d:\Price Tracker\backend"

:: Find the Java executable we downloaded to D:\Tools
FOR /D %%G IN ("D:\Tools\Java\jdk*") DO (
    SET JAVA_EXE="%%G\bin\java.exe"
)

:: Execute the built Java JAR
%JAVA_EXE% -jar target/PriceTrackerEngine.jar
