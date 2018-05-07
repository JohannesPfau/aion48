@ECHO off
TITLE InsaneAion 4.8 - LoginServer Panel
:START
CLS
IF "%MODE%" == "" (
CALL PanelLS.bat
)
ECHO Starting InsaneAion Login in %MODE% mode.
JAVA %JAVA_OPTS% -cp ./libs/* com.aionemu.loginserver.LoginServer
IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO Login Server has terminated abnormally!
ECHO.
PAUSE
EXIT
:END
ECHO.
ECHO Login Server is terminated!
ECHO.
PAUSE
EXIT