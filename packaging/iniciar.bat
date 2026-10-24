@echo off
cd /d "%~dp0"
java -jar UniRide.jar
if errorlevel 1 pause
