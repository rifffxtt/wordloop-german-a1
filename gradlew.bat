@echo off
set GRADLE_VERSION=8.5
set GRADLE_HOME=%USERPROFILE%\.gradle\wrapper\dists\gradle-%GRADLE_VERSION%-bin\*
if exist %GRADLE_HOME%\bin\gradle.bat (
    call %GRADLE_HOME%\bin\gradle.bat %*
) else (
    echo Gradle wrapper not found. Please run "gradle wrapper" first or download from https://gradle.org/releases/
)