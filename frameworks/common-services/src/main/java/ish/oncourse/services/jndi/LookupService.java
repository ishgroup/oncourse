package ish.oncourse.services.jndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

public class LookupService implements ILookupService {

	private static final Logger logger = Logger.getLogger(LookupService.class);

	@Override
	public Object lookup(String path) {
		try {
			Context ctx = new InitialContext();
			return ctx.lookup("java:comp/env/" + path);
		} catch (NamingException ne) {
			logger.warn(String.format("The object with patch:%s not defined by JNDI", path), ne);
		}
		return null;
	}
}
