@echo off
set JAVA_HOME="C:\Program Files\Java\jdk-24.0.1"
set FX_HOME="C:\Users\Andoryu\Downloads\javafx-sdk-24.0.1\lib"
set JAR_PATH="C:\Users\Andoryu\Desktop\game_project\out\artifacts\Projekt_Liska_jar\game_project.jar"

%JAVA_HOME%\bin\java ^
--module-path %FX_HOME% ^
--add-modules javafx.controls,javafx.fxml,javafx.media,javafx.graphics ^
--enable-native-access=javafx.graphics ^
--add-exports javafx.graphics/com.sun.glass.utils=ALL-UNNAMED ^
-jar %JAR_PATH%
pause