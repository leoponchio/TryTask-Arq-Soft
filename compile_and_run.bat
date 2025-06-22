@echo off
mkdir bin 2>nul
mkdir lib 2>nul

echo Baixando dependencias...
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar' -OutFile 'lib/gson-2.10.1.jar'}"
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/formdev/flatlaf/3.0/flatlaf-3.0.jar' -OutFile 'lib/flatlaf-3.0.jar'}"
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/formdev/flatlaf-extras/3.0/flatlaf-extras-3.0.jar' -OutFile 'lib/flatlaf-extras-3.0.jar'}"
powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/formdev/flatlaf-intellij-themes/3.0/flatlaf-intellij-themes-3.0.jar' -OutFile 'lib/flatlaf-intellij-themes-3.0.jar'}"

echo Compilando...
javac -cp "lib/*;src" -d bin src/Main.java src/models/*.java src/views/*.java src/controllers/*.java src/services/*.java src/models/tasks/*.java

if %errorlevel% equ 0 (
    echo Compilacao concluida com sucesso!
    echo Executando o programa...
    java -cp "bin;lib/*" Main
) else (
    echo Erro na compilacao!
    pause
) 