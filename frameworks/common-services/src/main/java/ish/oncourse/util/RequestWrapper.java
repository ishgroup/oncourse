package ish.oncourse.util;

import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class RequestWrapper implements Request{

	Request request;
	
	public RequestWrapper(Request request) {
		this.request = request;
	}
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return request.getAttribute(name);
	}

	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getDateHeader(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getHeader(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getParameter(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getParameters(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Session getSession(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isXHR() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAttribute(String name, Object value) {
		request.setAttribute(name, value);
		
	}

}
