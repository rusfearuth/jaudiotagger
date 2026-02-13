@echo off
setlocal
set "SCRIPT_DIR=%~dp0"
set "REPO_ROOT=%SCRIPT_DIR%.."
set "CLASSPATH=%CLASSPATH%;%REPO_ROOT%\classes;%REPO_ROOT%\dist\jaudiotagger.jar"
java org.jaudiotagger.test.ExtractID3TagFromFile %1 %2
