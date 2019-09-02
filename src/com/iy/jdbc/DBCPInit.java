package com.iy.jdbc;

import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@SuppressWarnings("serial")
public class DBCPInit extends HttpServlet {
	@Override
	public void init() throws ServletException {
		loadJDBCDriver();
		initConnectionPool();
	}
	
	private void loadJDBCDriver(){
		String driverClass = getInitParameter("jdbcDriver");
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//Connection Pool구현
	private void initConnectionPool(){
		try{
			//String jdbcDriver = "jdbc:mysql://localhost:3306/manage?useUnicode=true&characterEncoding=utf8";
			//String user = "root";
			//String pass = "rootroot";
			String jdbcDriver = getInitParameter("jdbcURL");
			String user = getInitParameter("DBUser");
			String pass = getInitParameter("DBPassword");
			
			ConnectionFactory connFactory = new DriverManagerConnectionFactory(jdbcDriver, user, pass);
			
			PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connFactory, null);
			
			//커넥션을 검사할때 사용할 쿼리 설정
			//select 1은 Microsoft SQL Server에서 권장하는 방법
			poolableConnectionFactory.setValidationQuery("select 1");
			
			GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
			//풀에 있는 유휴 커넥션 검사 주기를 설정. 단위 밀리초
			//일반적으로 10~30분 주기로 유휴 커넥션 검사를 하도록 지정
			poolConfig.setTimeBetweenEvictionRunsMillis(1000L * 60L * 5L);
			//true면 유휴 커넥션이 유효한지 검사
			poolConfig.setTestWhileIdle(true);
			poolConfig.setMaxIdle(4);//커넥션 풀이 유지할 최소 유휴 커넥션 개수
			poolConfig.setMaxTotal(50);//풀이 관리하는 커넥션의 최대 개수를 설정
			
			GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory, poolConfig);
			poolableConnectionFactory.setPool(connectionPool);
	        
	        Class.forName("org.apache.commons.dbcp2.PoolingDriver");
	        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
	        //커넥션 풀 등록시, 풀의 이름으로 'manage'를 사용하였음.
	        
	        //커넥션 풀의 이름 (jdbc:apache:commons:dbcp:manage) -> manage, 통상적으로 DB이름과 똑같이 쓴다.
	        String poolName = getInitParameter("poolName");
	        driver.registerPool(poolName, connectionPool);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
}