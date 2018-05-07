@ECHO off
TITLE Aion-Core - GameServer Console
:START
CLS
IF "%MODE%" == "" (
CALL PanelGS.bat
)

ECHO Starting Aion-Core GameServer in %MODE% mode.
JAVA %JAVA_OPTS% -ea -javaagent:./libs/ac-commons-1.3.jar -cp ./libs/*;AC-Game.jar com.aionemu.gameserver.GameServer
SET CLASSPATH=%OLDCLASSPATH%
IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO GameServer has terminated abnormally!
ECHO.
PAUSE
EXIT
:END
ECHO.
ECHO GameServer is terminated!
ECHO.
PAUSE
EXIT