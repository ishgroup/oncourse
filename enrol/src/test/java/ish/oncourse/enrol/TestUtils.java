package ish.oncourse.enrol;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TestUtils {

	public static Request getRequestBy(final Map<String,String> paramters)
	{
		return new Request() {
			@Override
			public Session getSession(boolean create) {
				return null; 
			}

			@Override
			public String getContextPath() {
				return null;  
			}

			@Override
			public List<String> getParameterNames() {
				return new ArrayList<>(paramters.keySet());
			}

			@Override
			public String getParameter(String name) {
				return paramters.get(name);
			}

			@Override
			public String[] getParameters(String name) {
				return new String[0]; 
			}

			@Override
			public String getPath() {
				return null;  
			}

			@Override
			public Locale getLocale() {
				return null;  
			}

			@Override
			public List<String> getHeaderNames() {
				return null;  
			}

			@Override
			public long getDateHeader(String name) {
				return 0;  
			}

			@Override
			public String getHeader(String name) {
				return null;  
			}

			@Override
			public boolean isXHR() {
				return false;  
			}

			@Override
			public boolean isSecure() {
				return false;  
			}

			@Override
			public String getServerName() {
				return null;  
			}

			@Override
			public boolean isRequestedSessionIdValid() {
				return false;  
			}

			@Override
			public Object getAttribute(String name) {
				return null;  
			}

			@Override
			public void setAttribute(String name, Object value) {
				
			}

			@Override
			public String getMethod() {
				return null;  
			}

			@Override
			public int getLocalPort() {
				return 0;  
			}

			@Override
			public int getServerPort() {
				return 0;  
			}
		};
	}
}
