@ECHO off
TITLE InsaneAion 4.8 Login Panel
:MENU
CLS
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                    InsaneAion 4.8 LoginServer Panel                   ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Development                                       3 - Quit        ^|
ECHO   ^|    2 - Production                                                        ^|
ECHO   ^|                                                                          ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
SET /P OPTION=Type your option and press ENTER: 
IF %OPTION% == 1 (
SET MODE=DEVELOPMENT
SET JAVA_OPTS=-Xms32m -Xmx512m -Xdebug -Xrunjdwp:transport=dt_socket,address=8999,server=y,suspend=n -ea
CALL StartLS.bat
)
IF %OPTION% == 2 (
SET MODE=PRODUCTION
SET JAVA_OPTS=-Xms64m -Xmx64m -server
CALL StartLS.bat
)
IF %OPTION% == 3 (
EXIT
)
IF %OPTION% GEQ 4 (
GOTO :MENU
)