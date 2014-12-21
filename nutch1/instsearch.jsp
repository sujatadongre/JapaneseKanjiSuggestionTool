<%@ page 
  session="false"
  contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"

  import="java.io.*"
  import="java.util.*"
  import="java.net.*"
  import="javax.servlet.http.*"
  import="javax.servlet.*"
  import="java.lang.*"
  import="java.text.DecimalFormat"
  import="java.text.NumberFormat"
  
  import="org.apache.nutch.hmmalgo.*"
%>
<%
	//String myStr = h.parseJapText(request.getParameter("query"));

	request.setCharacterEncoding("UTF-8");
	//Viterbi_New vNew = new Viterbi_New();
	//String myStr = vNew.callExp(request.getParameter("query"));
	//out.println(myStr);
	
	Forward_Alpha fa = new Forward_Alpha();
	String myStr = fa.callExp(request.getParameter("query"));
	out.println(myStr);
%>