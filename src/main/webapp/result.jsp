<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search Overflow</title>
</head>
<body>
  <h1>
    Results for query
    <%=(String) request.getParameter("q")%></h1>

  <div style="margin: 15px;"></div>
  <p>
    <%=(String) request.getParameter("res")%>
  </p>

</body>
</html>