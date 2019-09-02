<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.iy.jdbc.ConnectionProvider"%>
<%@ page import="java.sql.DriverManager "%>
<%@ page import="java.sql.Connection" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		Connection conn = null;
		try {
			/* String jdbcDriver = "jdbc:apache:commons:dbcp:manage"; */
			conn = ConnectionProvider.getConnection();
			if (conn != null) {
				out.print("커넥션 풀에 연결 되었습니다.");
			} else {
				out.print("커넥션 풀에 연결 실패하였습니다.");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	%>
</body>
</html>