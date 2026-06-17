$ErrorActionPreference = "Stop"

$toolsDir = "D:\Tools"
if (!(Test-Path $toolsDir)) { New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null }

Write-Host "Downloading OpenJDK 17 to D:\Tools..."
$jdkZip = "$toolsDir\jdk-17.zip"
if (!(Test-Path $jdkZip)) {
    Invoke-WebRequest -Uri "https://aka.ms/download-jdk/microsoft-jdk-17.0.10-windows-x64.zip" -OutFile $jdkZip
}
Write-Host "Extracting JDK 17..."
if (!(Test-Path "$toolsDir\Java")) {
    Expand-Archive -Path $jdkZip -DestinationPath "$toolsDir\Java" -Force
}

Write-Host "Downloading Apache Maven to D:\Tools..."
$mvnZip = "$toolsDir\maven.zip"
if (!(Test-Path $mvnZip)) {
    Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip" -OutFile $mvnZip
}
Write-Host "Extracting Maven..."
if (!(Test-Path "$toolsDir\Maven")) {
    Expand-Archive -Path $mvnZip -DestinationPath "$toolsDir\Maven" -Force
}

$jdkExtracted = Get-ChildItem -Path "$toolsDir\Java" -Directory | Select-Object -First 1
$mvnExtracted = Get-ChildItem -Path "$toolsDir\Maven" -Directory -Filter "apache-maven*" | Select-Object -First 1

$env:JAVA_HOME = $jdkExtracted.FullName
$env:Path = "$env:JAVA_HOME\bin;" + $mvnExtracted.FullName + "\bin;" + $env:Path

Write-Host "--- Environment Setup Complete ---"
java -version
mvn -version

Write-Host "--- Starting Maven Build ---"
Set-Location "d:\Price Tracker\backend"
mvn clean package

Write-Host "--- DONE ---"
