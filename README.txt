程序编译打包步骤
#######################################################################
*使用eclipse ant build生成jar包，
*创建FlexLicenseLog.bat
	java -jar "C:\FlexLicenseLog\FlexLicenseLog.jar"  C:\FlexLicenseLog  C:\FlexLicenseLog\license.log  0 >> "C:\\FlexLicenseLog\\FlexLicenseLog.out"
*创建FlexLicenseLog.vbs
Set ws = CreateObject("Wscript.Shell")
ws.run "cmd /c C:\FlexLicenseLog\FlexLicenseLog.bat", vbhide	
*创建FlexLicenseLog.sh
	#!/bin/sh
	cd /opt/tomcat/java/FlexLicenseLog/
	java -jar /opt/tomcat/java/FlexLicenseLog/FlexLicenseLog.jar /opt/tomcat/java/FlexLicenseLog/ /share/apps/ansys_inc/shared_files/licensing/license.log 0 > /opt/tomcat/java/FlexLicenseLog/ansysLS.out &
	java -jar /opt/tomcat/java/FlexLicenseLog/FlexLicenseLog.jar /opt/tomcat/java/FlexLicenseLog/ /share/apps/Fluent.Inc/FSLM10.8/lnamd64/license.log 0 > /opt/tomcat/java/FlexLicenseLog/fluentLS.out &
chmod +x FlexLicenseLog.sh

同类产品
#######################################################################
http://www.pbsee.com

