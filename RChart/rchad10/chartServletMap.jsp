<HTML>
<HEAD>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=windows-1252">
<TITLE>
HTML Test Page
</TITLE>
<%
/* this is the file where we write the donfiguration for the servlet, we should use another file name, not constant */
String filename="dataFile.html";
%>
</HEAD>
<BODY>
<BR>
This is the output of the servlet. The data for the servlet has been calculated in a JSP file.
<BR>
<BR>

<!--  **** VARIABLE DATA, use Java to retrieve series data values from database **** 
 -->

<%

java.sql.Statement st;
java.sql.ResultSet rs;
java.sql.Connection db=null;
int i=1;
String labels="";
String values1="";
String values2="";
String links="";

// connect to database
// open db using a System ODBC entry called "Data" 
try {
  Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
  db = java.sql.DriverManager.getConnection("jdbc:odbc:data", "", "");
}
catch(Exception e) {
  System.err.println("Error eonnecting to DB:" + e.getMessage());
}

try {

st = db.createStatement();
rs = st.executeQuery("Select * from SalesMonth Order by salesMonth DESC");


/* iterate on sales data, up to 6 rows */
while ((i <= 6) && (rs.next()))  {
    /* concatenate | separator */
     if (i>1) {
        values1= "|" + values1;
	links= "|" + links;
	values2= "|" + values2;
	labels= "|" + labels;
     }

      /* concatenate value */
     values1= "" + rs.getString("Services") + values1;
     links= "http://www.java4less.com" + links ;
     values2= "" + rs.getString( "Product") + values2;
     java.text.SimpleDateFormat df= new java.text.SimpleDateFormat("MMM");
     labels= "" + df.format(rs.getDate("salesMonth")) + labels;

     i++;

 }

rs.close();
db.close();

}

catch(Exception e) {
  System.err.println("Error:" + e.getMessage());
}


try {

/* open data file */
java.io.File fi=new java.io.File("..\\webapps\\rchart\\data\\" + filename);
java.io.FileWriter fw=new java.io.FileWriter(fi);


/* write values for serie 1 */
fw.write("SERIE_DATA_1=" +values1 + "\n");
/* write tips for points */
fw.write("SERIE_TIPS_1=" +values1 + "\n");
fw.write("SERIE_LINKS_1=" +links + "\n");

/* write values for serie 2 */
fw.write("SERIE_DATA_2=" +values2+ "\n");

/* write values for labels */
fw.write("XAXIS_LABELS=" +labels+ "\n");


/* write constant data to configuration file*/

fw.write("TITLECHART=Sales 1999\n");
fw.write("LEGEND=TRUE\n");
fw.write("CHART_SHOW_TIPS=TRUE\n");
fw.write("XLABEL=Month\n");
fw.write("YLABEL=Million $\n");
fw.write("SERIE_1=Services\n");
fw.write("SERIE_2=Products\n");
fw.write("SERIE_STYLE_1=0.2|0x663300|LINE\n");
fw.write("SERIE_STYLE_2=0.2|0x99|LINE\n");
fw.write("SERIE_FILL_1=RED\n");
fw.write("SERIE_FILL_2=0x99cc\n");
fw.write("SERIE_FONT_1=Arial|PLAIN|8\n");
fw.write("SERIE_FONT_2=Arial|PLAIN|8\n");
fw.write("SERIE_POINT_1=true\n");
fw.write("SERIE_POINT_2=true\n");
fw.write("SERIE_TYPE_1=LINE\n");
fw.write("SERIE_TYPE_2=LINE\n");
fw.write("CHART_BORDER=0.2|BLACK|LINE\n");
fw.write("CHART_FILL=LIGHTGRAY\n");
fw.write("BIG_TICK_INTERVALX=1\n");
fw.write("BIG_TICK_INTERVALY=1\n");
fw.write("YSCALE_MIN=0\n");
fw.write("TICK_INTERVALY=100\n");
fw.write("LEGEND_BORDER=0.2|BLACK|LINE\n");
fw.write("LEGEND_FILL=WHITE\n");
fw.write("XAXIS_TICKATBASE=true\n");
fw.write("XAXIS_TICKATBASE=true\n");
fw.write("BACK_IMAGE=back13.gif\n");

fw.close();

}

catch(Exception e) {
  System.err.println("Error writing data file:" + e.getMessage());
}

%>

<jsp:include page="servlet/RChartMapServlet" flush="true">
	<jsp:param name="DATAFILE" value="http://localhost:8080/rchart/data/dataFile.html"/>
</jsp:include>

<img src="http://localhost:8080/rchart/servlet/RChartServlet?DATAFILE=http://localhost:8080/rchart/data/<%=filename%>" BORDER=0 ISMAP USEMAP="#CHARTMAP">


</BODY>
</HTML>
