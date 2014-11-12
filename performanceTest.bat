@echo off

rem get current parent directory
set CURRENT_DIR=%~dp0

if "%JAVA_HOME%" == "" goto noJavaHome
set _JAVA_CMD=%JAVA_HOME%\bin\java
goto run

:noJavaHome
set _JAVA_CMD=C:\Program Files\Java\jdk1.5.0_05\bin\java.exe

:run
rem set JAVA_OPTS = -Xms256m -Xmx256m 

"%_JAVA_CMD%"  %JAVA_OPTS% -Djava.ext.dirs="%CURRENT_DIR%\lib" -cp "%CURRENT_DIR%\bin;.;%CURRENT_DIR%\conf" com.baidu.jprotobuf.pbrpc.transport.EchoServicePerformanceMain

:end
set _JAVA_CMD=
set JAVA_OPTS =

@echo on