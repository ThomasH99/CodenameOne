ECHO OFF
ECHO ***** Warning ****
ECHO RChart Visual Builder requires Swing:
ECHO you can download swing at:
ECHO http://java.sun.com/products/jfc/
ECHO *
ECHO *
ECHO ON
jview /p /cp ".;swingall.jar;rchartVB.jar;rchart.jar" com.java4less.rchartVB.RChartVB
