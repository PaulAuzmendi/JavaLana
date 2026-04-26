REM variables for Java programas: Java, Java DB, Squirrel-sql, Tomcat, and ucanaccess driver
REM current forlder
set CATALINA_BASE=%cd:~%
REM current drive
set THIS_DRIVE=%cd:~0,2%
set JAVA_STACK=%THIS_DRIVE%\Programs\JavaStack
set JAVA_HOME=%JAVA_STACK%\jdk1.8.0_131
set CATALINA_HOME=%JAVA_STACK%\apache-tomcat-9.0.89
SET DERBY_HOME=%JAVA_HOME%\db
SET SQUIRREL_HOME=%JAVA_STACK%\squirrelsql-4.0.0-standard
SET PATH=%JAVA_HOME%\bin;%DERBY_HOME%\bin;%SQUIRREL_HOME%;%CATALINA_HOME%\bin;%PATH%
SET CLASSPATH=%CLASSPATH%;.;%DERBY_HOME%\lib\derby.jar;%DERBY_HOME%\lib\derbynet.jar;%DERBY_HOME%\lib\derbyclient.jar;%DERBY_HOME%\lib\derbytools.jar;%DERBY_HOME%\lib\derbyoptionaltools.jar;%CATALINA_HOME%\lib\servlet-api.jar;%CATALINA_HOME%\lib\Jama-1.0.2.jar;%CATALINA_HOME%\lib\commons-lang3-3.8.1.jar;%CATALINA_HOME%\lib\commons-logging-1.2.jar;%CATALINA_HOME%\lib\hsqldb-2.5.0.jar;%CATALINA_HOME%\lib\jackcess-3.0.1.jar;%CATALINA_HOME%\lib\ucanaccess-5.0.0.jar;.
