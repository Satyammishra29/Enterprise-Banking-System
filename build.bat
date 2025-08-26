@echo off
echo Building Enterprise Banking System...
echo.

REM Set JavaFX path (update this to your actual JavaFX SDK path)
set JAVAFX_PATH=C:\Users\Infer\Downloads\openjfx-24.0.1_windows-x64_bin-sdk\javafx-sdk-24.0.1\lib

REM Clean previous builds
if exist "dist" rmdir /s /q "dist"
if exist "out" rmdir /s /q "out"

REM Create directories
mkdir out
mkdir out\view\auth
mkdir out\view\dashboard
mkdir out\view\banking
mkdir out\view\reports
mkdir out\view\settings
mkdir dist

echo Compiling Java source files...
javac -cp "lib/*" --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -sourcepath src -d out src/Main.java src/model/*.java src/model/auth/*.java src/model/banking/*.java src/dao/*.java src/util/*.java src/controller/auth/*.java src/controller/banking/*.java src/controller/dashboard/*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Copying FXML files...
copy src\view\auth\*.fxml out\view\auth\
copy src\view\dashboard\*.fxml out\view\dashboard\
copy src\view\banking\*.fxml out\view\banking\
copy src\view\reports\*.fxml out\view\reports\
copy src\view\settings\*.fxml out\view\settings\

echo Creating executable JAR...
jar cfm dist/EnterpriseBankingSystem.jar manifest.txt -C out .

echo Copying dependencies...
copy lib\*.jar dist\

echo Creating run script...
echo @echo off > dist\run.bat
echo java --module-path "javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml -cp "EnterpriseBankingSystem.jar;*.jar" Main >> dist\run.bat

echo Creating JavaFX runtime directory...
mkdir dist\javafx-sdk
mkdir dist\javafx-sdk\lib
copy "%JAVAFX_PATH%\*" dist\javafx-sdk\lib\

echo.
echo Build completed successfully!
echo.
echo Files created in 'dist' directory:
echo - EnterpriseBankingSystem.jar (main application)
echo - run.bat (Windows launcher script)
echo - javafx-sdk\ (JavaFX runtime)
echo - *.jar (dependencies)
echo.
echo To run the application:
echo 1. Navigate to the 'dist' directory
echo 2. Double-click 'run.bat'
echo.
pause
