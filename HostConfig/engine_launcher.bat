@echo off
:: This script is called by Google Chrome to start the Java Engine
:: Make sure the absolute path to your target JAR is correct

:: Set the current working directory to the project root
cd /d "d:\Price Tracker"

:: Execute the built Java JAR
java -jar target/PriceTrackerEngine.jar
