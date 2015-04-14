package ish.oncourse.services.jndi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class LookupService implements ILookupService {

	private static final Logger logger = LogManager.getLogger();

	@Override
	public Object lookup(String path) {
		try {
			Context ctx = new InitialContext();
			return ctx.lookup("java:comp/env/" + path);
		} catch (NamingException ne) {
			logger.warn("The object with patch:%s not defined by JNDI", path, ne);
		}
		return null;
	}
}
