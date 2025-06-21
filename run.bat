@echo off
set CLASSPATH=lib\flatlaf-3.5.1.jar;lib\flatlaf-intellij-themes-3.5.1.jar;lib\gson-2.10.1.jar
mkdir out\production
javac -d out\production src\main\TryTasks.java src\models\Task.java src\services\TaskService.java src\services\AuthService.java src\views\LoginFrame.java src\views\MainFrame.java
java -cp "out\production;%CLASSPATH%" main.TryTasks
pause 