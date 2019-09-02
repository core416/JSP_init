package com.iy.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerUsingURI extends HttpServlet {

	// <커맨드, 핸들러인스턴스> 매핑 정보 저장
	private HashMap<String, CommandHandler> commandHandlerMap = new HashMap<String, CommandHandler>();

	public void init() throws ServletException {
		// /WEB-INF/CommandHandler.properties
		String configFile = getInitParameter("configFile"); // xml의 param
		Properties prop = new Properties();
		// /WEB-INF/CommandHandler.properties 절대 경로
		String configFilePath = getServletContext().getRealPath(configFile);

		try (FileReader fis = new FileReader(configFilePath)) {
			prop.load(fis);
		} catch (IOException e) {
			throw new ServletException(e);
		}

		Iterator keyIter = prop.keySet().iterator();

		while (keyIter.hasNext()) {
			String command = (String) keyIter.next(); // key : /simple.do
			String handlerClassName = prop.getProperty(command); // value : handler클래스
			try {
				// SimpleHandler; 선언
				Class<?> handlerClass = Class.forName(handlerClassName);
				// //SimpleHandler handler = new SimpleHandler();
				CommandHandler handlerInstance = (CommandHandler) handlerClass.newInstance(); // 동적 class로딩
				commandHandlerMap.put(command, handlerInstance); // 맵에 .do저장
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new ServletException(e);
			}
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 주소창의 uri정보를 가져온다.
		// uri : /chap18/hello.do
		// 우리에게 필요한 매핑정보는 contextPath(/chap18)를 제외한 부분이므로 string에서 contextPath를 제외하고 가져온다.
		String command = request.getRequestURI(); // /chap18/simple.do
		if (command.indexOf(request.getContextPath()) == 0) {
			command = command.substring(request.getContextPath().length()); // /simple.do
		}
		// key에 해당하는 handler를 가져온다.
		CommandHandler handler = commandHandlerMap.get(command); // 키를 넣으면 클래스가 값으로 반환
		if (handler == null) {
			handler = new NullHandler();
		}
		String viewPage = null;
		try {
			viewPage = handler.process(request, response);
		} catch (Throwable e) {
			throw new ServletException(e);
		}
		if (viewPage != null) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(viewPage);
			dispatcher.forward(request, response);
		}
	}
}
